#!/bin/bash
# The demo runbook, automated.  See README.md for the full script.
cd "$(dirname "$0")"

CP="../../source/java/neupaths.jar:."

start_proc () {  # <ProcName> [extra java opts...]
  local NAME=$1; shift
  if [ -f "pids/${NAME}.pid" ] && kill -0 "$(cat pids/${NAME}.pid)" 2>/dev/null; then
    echo "${NAME} already running"; return
  fi
  nohup java "$@" -cp "$CP" neupaths.util.CellClusterExec "cfg/Cl_${NAME}.xml" \
      > "out/${NAME}.out" 2>&1 &
  echo $! > "pids/${NAME}.pid"
  echo "started ${NAME} (pid $!)"
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

gen_zone_b () {
  mkdir -p cfg/gen /tmp/reflex/zoneB
  rm -f /tmp/reflex/zoneB/burst
  echo "OPEN" > /tmp/reflex/zoneB/valve.pos
  for F in ZA_Router ZA_Events ZA_Pressure ZA_Flow ZA_Acoustic ZA_Reflex ZA_Valve \
           ZA_Bridge1 ZA_Bridge2 \
           Cl_ZoneA_Infra Cl_ZA_Pressure Cl_ZA_Flow Cl_ZA_Acoustic Cl_ZA_Reflex \
           Cl_ZA_Valve Cl_ZA_Bridge1 Cl_ZA_Bridge2; do
    local G
    G=$(echo "$F" | sed -e 's/ZA_/ZB_/' -e 's/ZoneA/ZoneB/')
    sed -e 's/ZA_/ZB_/g' -e 's/Zone_A/Zone_B/g' -e 's/ZoneA/ZoneB/g' \
        -e 's|/tmp/reflex/za\.sock|/tmp/reflex/zb.sock|g' \
        -e 's|cfg/Z|cfg/gen/Z|g' \
        "cfg/${F}.xml" > "cfg/gen/${G}.xml"
  done
}

start_zb () {  # <ClusterBase>  (e.g. ZB_Pressure -> cfg/gen/Cl_ZB_Pressure.xml)
  local NAME=$1
  if [ -f "pids/${NAME}.pid" ] && kill -0 "$(cat pids/${NAME}.pid)" 2>/dev/null; then
    echo "${NAME} already running"; return
  fi
  nohup java -Dreflex.zone=B -cp "$CP" neupaths.util.CellClusterExec "cfg/gen/Cl_${NAME}.xml" \
      > "out/${NAME}.out" 2>&1 &
  echo $! > "pids/${NAME}.pid"
  echo "started ${NAME} (pid $!)"
}

case "$1" in
  burst)        touch /tmp/reflex/zoneA/burst; echo "ZONE A: pipe burst injected" ;;
  clear-burst)  rm -f /tmp/reflex/zoneA/burst; echo "ZONE A: burst cleared (valve stays as commanded)" ;;
  open-valve)   echo "OPEN" > /tmp/reflex/zoneA/valve.pos; echo "ZONE A: valve forced OPEN (sim-level)" ;;

  kill)         kill_proc "$2" ;;
  start)        start_proc "$2" ;;

  kill-bridge1) kill_proc ZA_Bridge1 ;;
  kill-flow)    kill_proc ZA_Flow ;;

  partition)    kill_proc ZA_Bridge1; kill_proc ZA_Bridge2
                echo "PARTITION: Zone A is cut off from Ops (the brain is dark)" ;;
  heal)         start_proc ZA_Bridge1; start_proc ZA_Bridge2
                echo "HEALED: bridges restarting; subscriptions rewire in ~1.5s" ;;

  hotplug)      start_proc ZA_Acoustic
                echo "HOT-PLUG: acoustic sensor joining; LeakLocalizer lights up" ;;

  zoneb)        gen_zone_b
                start_zb ZoneB_Infra; sleep 1
                start_zb ZB_Pressure; start_zb ZB_Flow; start_zb ZB_Reflex
                start_zb ZB_Valve; start_zb ZB_Bridge1; start_zb ZB_Bridge2
                echo "ZONE B is launching - watch it appear at Ops with zero Ops changes" ;;

  status)       for P in pids/*.pid; do
                  [ -f "$P" ] || continue
                  N=$(basename "$P" .pid)
                  if kill -0 "$(cat "$P")" 2>/dev/null; then echo "RUNNING  $N"; else echo "DEAD     $N"; fi
                done
                echo "valve.pos: $(cat /tmp/reflex/zoneA/valve.pos 2>/dev/null || echo '?')" \
                     " burst: $( [ -f /tmp/reflex/zoneA/burst ] && echo ACTIVE || echo none )" ;;

  stop-all)     for P in pids/*.pid; do [ -f "$P" ] || continue; kill "$(cat "$P")" 2>/dev/null; rm -f "$P"; done
                echo "all processes stopped" ;;

  help|*)       cat << 'EOF'
usage: ./chaos.sh <command>
  burst | clear-burst | open-valve     drive the simulated plant
  kill-bridge1                         runbook 2: kill one redundant path
  kill-flow / start ZA_Flow            runbook 3-4: watchdog + fail-safe stall
  partition / heal                     runbook 5-6: the reflex arc
  hotplug                              runbook 7: acoustic sensor -> LeakLocalizer
  zoneb                                runbook 8: clone a whole zone
  kill <Proc> | start <Proc>           generic (name = cfg/Cl_<Proc>.xml)
  status | stop-all
EOF
                ;;
esac
