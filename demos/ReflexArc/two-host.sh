#!/bin/bash
# Two-host Reflex Arc: Zone A on one machine, Ops (the brain) on another, so the
# partition can be a real network cut instead of a process kill.
#
# The demo is TCP end to end, so almost nothing changes between one host and two.
# The ONLY host-dependent value in the whole demo is the address the two bridges
# dial to reach Ops:
#
#   host B (ops)  : ./two-host.sh ops                      # plain cfg/, no gen needed
#   host A (zone) : ./two-host.sh gen <OPS_HOST>           # -> cfg/2h/
#                   ./two-host.sh zone
#                   ./two-host.sh cut <OPS_HOST>           # partition (sudo)
#                   ./two-host.sh heal <OPS_HOST>
#
# See TWO_HOST_RUNBOOK.md.
cd "$(dirname "$0")"

CP="../../source/java/neupaths.jar:."
P1=30001          # Ops path 1 (ZA_Bridge1)
P2=30002          # Ops path 2 (ZA_Bridge2)

# ---------------------------------------------------------------- config gen

gen () {  # <OPS_HOST>
  local OPS_HOST=$1
  if [ -z "$OPS_HOST" ]; then
    echo "usage: ./two-host.sh gen <OPS_HOST>    (hostname or IP of the Ops machine)"; exit 1
  fi
  mkdir -p cfg/2h
  local SRC B
  for SRC in cfg/*.xml; do
    B=$(basename "$SRC")
    case "$B" in
      ZA_Bridge*)
        # The bridges are the only cells that cross the host boundary.
        sed -e "s|#Ops#${P1}#127.0.0.1|#Ops#${P1}#${OPS_HOST}|g" \
            -e "s|#Ops#${P2}#127.0.0.1|#Ops#${P2}#${OPS_HOST}|g" \
            -e "s|<cell_definition>cfg/|<cell_definition>cfg/2h/|g" \
            "$SRC" > "cfg/2h/$B" ;;
      *)
        sed -e "s|<cell_definition>cfg/|<cell_definition>cfg/2h/|g" \
            "$SRC" > "cfg/2h/$B" ;;
    esac
  done
  echo "generated cfg/2h/  (bridges dial Ops at ${OPS_HOST})"
  grep -h 'Peer#Ops' cfg/2h/ZA_Bridge1.xml cfg/2h/ZA_Bridge2.xml | sed 's/^ */  /'
}

# ------------------------------------------------------------------ process

cfg_for () {  # <ProcName> -> cluster file, preferring the generated set
  local NAME=$1
  if [ -f "cfg/2h/Cl_${NAME}.xml" ]; then echo "cfg/2h/Cl_${NAME}.xml"
  else echo "cfg/Cl_${NAME}.xml"; fi
}

start_proc () {  # <ProcName>
  local NAME=$1 CFG
  if [ -f "pids/${NAME}.pid" ] && kill -0 "$(cat pids/${NAME}.pid)" 2>/dev/null; then
    echo "${NAME} already running"; return
  fi
  CFG=$(cfg_for "$NAME")
  if [ ! -f "$CFG" ]; then echo "no cluster file for ${NAME}"; exit 1; fi
  nohup java -cp "$CP" neupaths.util.CellClusterExec "$CFG" > "out/${NAME}.out" 2>&1 &
  echo $! > "pids/${NAME}.pid"
  echo "started ${NAME} (pid $!)  [$CFG]"
}

kill_proc () {  # <ProcName>
  local NAME=$1
  if [ -f "pids/${NAME}.pid" ]; then
    kill "$(cat pids/${NAME}.pid)" 2>/dev/null && echo "killed ${NAME}"
    rm -f "pids/${NAME}.pid"
  else
    echo "${NAME} not running"
  fi
}

# --------------------------------------------------------------- partition

firewall () {  # <add|del> <OPS_HOST> <label>
  local OP=$1 OPS_HOST=$2 FLAG
  if [ -z "$OPS_HOST" ]; then echo "usage: ./two-host.sh ${3} <OPS_HOST>"; exit 1; fi
  [ "$OP" = add ] && FLAG=-A || FLAG=-D
  # DROP, not REJECT: a silent blackhole is what a pulled cable looks like.
  sudo iptables $FLAG OUTPUT -p tcp -d "$OPS_HOST" --dport ${P1}:${P2} -j DROP
  sudo iptables $FLAG INPUT  -p tcp -s "$OPS_HOST" --sport ${P1}:${P2} -j DROP
}

# -------------------------------------------------------------------- main

case "$1" in
  gen)   gen "$2" ;;

  zone)  if [ ! -d cfg/2h ]; then
           echo "run './two-host.sh gen <OPS_HOST>' first"; exit 1
         fi
         mkdir -p out pids /tmp/reflex/zoneA
         rm -f /tmp/reflex/zoneA/burst
         echo "OPEN" > /tmp/reflex/zoneA/valve.pos
         start_proc ZoneA_Infra
         sleep 1
         for P in ZA_Pressure ZA_Flow ZA_Reflex ZA_Valve ZA_Bridge1 ZA_Bridge2; do
           start_proc "$P"
         done
         echo ""
         echo "Zone A up (router on 30010).  Give subscriptions ~5s to reach Ops."
         echo "  watch:  tail -f out/ZA_events.out"
         echo "  drive:  ./chaos.sh burst | ./chaos.sh hotplug | ./chaos.sh zoneb" ;;

  ops)   mkdir -p out pids
         start_proc Ops
         echo ""
         echo "Ops up, listening on ${P1} and ${P2}."
         echo "  watch:    tail -f out/Ops_events.out"
         echo "  console:  java -cp \"$CP\" ConsoleMain" ;;

  cut)   firewall add "$2" cut
         echo "PARTITION: Zone_A <-> Ops severed at the network (bridges still running)" ;;

  heal)  firewall del "$2" heal
         echo "HEALED: firewall rules removed; bridges reconnect on their own" ;;

  cut-status)
         sudo iptables -L OUTPUT -n | grep -E "dpts?:${P1}" || echo "no cut rules active" ;;

  kill)  kill_proc "$2" ;;
  start) start_proc "$2" ;;

  status)
         for P in pids/*.pid; do
           [ -f "$P" ] || continue
           N=$(basename "$P" .pid)
           if kill -0 "$(cat "$P")" 2>/dev/null; then echo "RUNNING  $N"; else echo "DEAD     $N"; fi
         done
         echo "valve.pos: $(cat /tmp/reflex/zoneA/valve.pos 2>/dev/null || echo 'n/a')" \
              " burst: $( [ -f /tmp/reflex/zoneA/burst ] && echo ACTIVE || echo none )" ;;

  stop-all)
         for P in pids/*.pid; do [ -f "$P" ] || continue; kill "$(cat "$P")" 2>/dev/null; rm -f "$P"; done
         echo "all processes stopped" ;;

  help|*)
cat << 'EOF'
usage: ./two-host.sh <command>

  host B (the brain) -- no config generation needed:
    ops                   OpsRouter + OpsTrend + OpsEvents, listening on 30001/30002
                          (then: java -cp ... ConsoleMain)

  host A (the plant):
    gen <OPS_HOST>        point the two bridges at the Ops machine -> cfg/2h/
    zone                  ZoneA_Infra + sensors + reflex + valve + 2 bridges
    cut <OPS_HOST>        blackhole 30001-30002 to/from Ops (sudo)
    heal <OPS_HOST>       remove the rules
    cut-status            is a cut currently active?

  also: kill <Proc> | start <Proc> | status | stop-all

Ports: Ops 30001/30002, Zone_A 30010, Zone_B 30020 (generated by ./chaos.sh zoneb).
Only host B needs inbound ports open; the bridges dial out.
Drive the simulated plant with ./chaos.sh on host A.
EOF
         ;;
esac
