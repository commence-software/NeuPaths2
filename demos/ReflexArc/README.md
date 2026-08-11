# Demo: Reflex Arc

A nervous system for a simulated water pump station.  A local control loop (the
*reflex*: pressure spike → close the valve) keeps protecting the plant while the
central analytics and operator console (the *brain*) are partitioned away, and
rewires itself automatically when the partition heals.

Full case study and rationale: [`documents/design/07-case-study-reflex-arc.md`](../../documents/design/07-case-study-reflex-arc.md).

## Features/Techniques Used

- Multiple activators per cell with a loopback subscription (BurstDetector → ValveGuard)
- Dataflow joins as control logic: fusion, hysteresis, and rate limiting by slowest input
- Sensor-liveness **watchdog**: per-sensor trackers + a PulsedActivator sharing cell properties
  (a dataflow gate cannot fire on the *absence* of a stimulus — this closes that gap)
- Two domains (`Zone_A`, `Ops`) joined by **dual redundant BridgeCells** (duplicate-filtered)
- Capability gating: `LeakLocalizer`'s three-way join lights up when the acoustic sensor is hot-plugged
- Regex fleet aggregation: Ops subscribes `.*_Pressure` etc. — cloned zones appear with zero Ops changes
- One killable JVM per cell (group) via `neupaths.util.CellClusterExec`
- Zone parametrization via `-Dreflex.zone` (Zone B configs are sed-generated clones)

## Build

```
javac -cp ../../source/java/neupaths.jar:. *.java        (or: make)
```

Requires `neupaths.jar` built first (`make` in `source/java`).

## Run

```
./run.sh                 # starts Zone A + Ops (8 JVMs)
tail -f out/ZA_events.out   # zone-side narration (reflex, valve audit, watchdog)
tail -f out/Ops_events.out  # ops-side narration (trends, dedup'd telemetry)
java -cp ../../source/java/neupaths.jar:. ConsoleMain    # operator console (open|close|quit)
```

## The runbook (`./chaos.sh <cmd>`)

| # | Command | Watch for | Claim proven |
|---|---------|-----------|--------------|
| 1 | `burst` | `REFLEX: closing valve …` then `VALVE CLOSED` in ZA events | The dataflow reflex |
| 2 | `kill-bridge1` | Telemetry keeps flowing (path 2 + dedup) | Safe redundancy |
| 3 | `kill-flow` | `WATCHDOG: sensor ZA_Flow is DOWN`; burst detection suspends, valve holds | Watchdog + fail-safe stall |
| 4 | `start ZA_Flow` | `WATCHDOG: … RECOVERED`; rewires with no config | Self-wiring mesh |
| 5 | `partition` then `burst` | **Valve closes with the brain dark**; Ops frozen | The reflex arc |
| 6 | `heal` | Fresh trends resume at Ops — no stale backlog flood | Latest-value semantics |
| 7 | `hotplug` | `[OPS] Zone_A: leak signature …` — a capability lit up | Capability gating |
| 8 | `zoneb` | `[OPS] Anomaly(Zone_B …)` with zero Ops-side changes | Regex scale-out |

Also: `status`, `stop-all`, `clear-burst`, `open-valve`, generic `kill <Proc>` / `start <Proc>`.

## Process map

| Process (cluster file) | Cells | Notes |
|------------------------|-------|-------|
| `Cl_ZoneA_Infra` | ZA_Router, ZA_Events | zone backbone + event spool |
| `Cl_ZA_Pressure` / `Cl_ZA_Flow` | one sensor each | 250 ms pulsed sims |
| `Cl_ZA_Acoustic` | ZA_Acoustic | **not started** — hot-plug target |
| `Cl_ZA_Reflex` | ZA_Reflex | 7 activators: detector, guard, localizer, 3 trackers, watchdog |
| `Cl_ZA_Valve` | ZA_Valve | actuator + 1 s state heartbeat |
| `Cl_ZA_Bridge1` / `Cl_ZA_Bridge2` | one bridge each | redundant Zone_A ↔ Ops paths |
| `Cl_Ops` | OpsRouter, OpsTrend, OpsEvents | the brain |
| `Cl_OpsConsole` | OpsConsoleIn, OpsConsoleOut | loaded by `ConsoleMain` |

Simulated plant state lives in `/tmp/reflex/zone<ID>/` (`valve.pos`, `burst`) so the
independently killable JVMs observe the same physics.
