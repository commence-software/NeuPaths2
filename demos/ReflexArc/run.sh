#!/bin/bash
# Start the Reflex Arc demo: Zone A + Ops, each cell (group) its own JVM.
# The acoustic sensor and the operator console are NOT started here:
#   - acoustic is hot-plugged later:   ./chaos.sh hotplug
#   - console is interactive:          java -cp $CP ConsoleMain
cd "$(dirname "$0")"

CP="../../source/java/neupaths.jar:."
export CP

mkdir -p out pids /tmp/reflex/zoneA
rm -f /tmp/reflex/*.sock out/*.out pids/*.pid
rm -f /tmp/reflex/zoneA/burst
echo "OPEN" > /tmp/reflex/zoneA/valve.pos

start_proc () {  # <ProcName> [extra java opts...]
  local NAME=$1; shift
  nohup java "$@" -cp "$CP" neupaths.util.CellClusterExec "cfg/Cl_${NAME}.xml" \
      > "out/${NAME}.out" 2>&1 &
  echo $! > "pids/${NAME}.pid"
  echo "started ${NAME} (pid $!)"
}

# Routers/infra first so peers have listeners to join
start_proc ZoneA_Infra
start_proc Ops
sleep 1

start_proc ZA_Pressure
start_proc ZA_Flow
start_proc ZA_Reflex
start_proc ZA_Valve
start_proc ZA_Bridge1
start_proc ZA_Bridge2

echo ""
echo "Reflex Arc demo is starting.  Give subscriptions ~5s to propagate."
echo "  watch zone events:  tail -f out/ZA_events.out"
echo "  watch ops events:   tail -f out/Ops_events.out"
echo "  drive the demo:     ./chaos.sh help"
echo "  operator console:   java -cp \"$CP\" ConsoleMain"
