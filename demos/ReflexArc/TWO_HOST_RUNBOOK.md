# Two-Host Reflex Arc — Presentation Runbook

The single-host demo ([`README.md`](README.md)) partitions Zone A from Ops by **killing the
bridge processes**. That proves the reflex works, but an audience can reasonably object that
killing a process is not a network partition.

This runbook splits the demo across two machines joined by **real TCP**, so the partition is a
genuine network cut with every process still running. It is the same demo, same code, same
activators — only the Zone_A ↔ Ops synapses change.

> **Why it matters on stage.** When you cut the network and the bridges *stay alive* while the
> valve still closes, there is nothing left to attribute the result to. The zone is deciding
> alone because it can, not because you killed the thing that would have decided for it.

---

## 1 · What actually changes

**Almost nothing.** The demo is `Network#Stream` end to end, so the single-host and two-host
builds are the same configuration. Exactly one value differs:

| | Single-host | Two-host |
|---|---|---|
| Zone_A internal mesh | `Network#Stream#…#Zone_A#30010#127.0.0.1` | **unchanged** (cells stay co-located on host A) |
| `OpsRouter` listeners | `Network#Stream#Listener#Ops#3000{1,2}` | **unchanged** (binds `0.0.0.0`) |
| Ops-internal peers | `Network#Stream#Peer#Ops#30001#127.0.0.1` | **unchanged** (co-located on host B) |
| `ZA_Bridge1/2` → Ops | `…#Ops#3000{1,2}#127.0.0.1` | `…#Ops#3000{1,2}#`**`<OPS_HOST>`** |

Ops-internal cells stay on **loopback** deliberately: the firewall cut must sever Zone↔Ops
without also severing OpsTrend and OpsEvents from their own router.

Two consequences worth knowing:

- **Host B needs no generated configs at all.** It runs the committed `cfg/` unchanged — and so
  does `ConsoleMain`, with no patch and no argument.
- **`gen` is only run on host A**, where it rewrites the two bridge files into `cfg/2h/` and
  leaves the committed `cfg/` untouched.

## 2 · Hosts and ports

| | Host A — "the plant" | Host B — "the brain" |
|---|---|---|
| Processes | ZoneA_Infra, ZA_Pressure, ZA_Flow, ZA_Reflex, ZA_Valve, ZA_Bridge1, ZA_Bridge2 | Ops (OpsRouter, OpsTrend, OpsEvents) + the operator console |
| Plant state | `/tmp/reflex/zoneA/` (`valve.pos`, `burst`) | — |
| Local ports | 30010 (Zone_A backbone), 30020 if Zone B is cloned | 30001, 30002 |
| Inbound from the other host | — | **30001**, **30002** |

Only host B needs ports open to the network; the bridges dial out.

The simulated physics lives only on host A, so `./chaos.sh burst` must be run **on host A**.

Both hosts need `neupaths.jar` built and the demo compiled:

```bash
cd source/java && make                       # once
cd demos/ReflexArc && make                   # or: javac -cp ../../source/java/neupaths.jar:. *.java
```

Open **30001–30002** on host B before you start, or the bridges will sit retrying:

```bash
sudo ufw allow from <HOST_A_IP> to any port 30001:30002 proto tcp
```

## 3 · Bring-up

**Host B (brain) first** — the bridges are peers and need a listener to join. No `gen` here:

```bash
./two-host.sh ops
tail -f out/Ops_events.out
java -cp ../../source/java/neupaths.jar:. ConsoleMain     # unmodified
```

**Host A (plant):**

```bash
./two-host.sh gen <OPS_HOST>
./two-host.sh zone
tail -f out/ZA_events.out
```

Allow ~5 s for subscriptions to propagate. Confirm the hop is real before you go any further:

```bash
ss -tnp | grep -E '30001|30002'       # host A: two ESTAB, one per bridge
```

You should see Ops narrating trends within a few seconds:

```
TREND: avg pressure 6.00 bar, avg flow 119.7 m3/h over 40 samples
```

…and, if `logging_enabled` is on, dedup warnings naming the path each copy took:

```
Duplicate received: Stimulus[producerCellName=ZA_Pressure, …],
  Trace: ZA_Pressure => ZA_Router (Zone_A) => ZA_Bridge2 (Zone_A) => OpsRouter (Ops)
```

**Put that trace line on screen.** It is the dual-bridge redundancy and the duplicate filter
proving themselves in one line, and it pre-empts "what happens if a message arrives twice?"

### The operator console

Run it on **host B**, unmodified:

```bash
java -cp ../../source/java/neupaths.jar:. ConsoleMain
```

`OpsConsoleIn`/`OpsConsoleOut` peer to `Ops#30001#127.0.0.1`, and `OpsRouter` is on host B with
them, so the committed config is already correct. (This is the main thing the all-TCP
conversion bought: on a mixed Unix/TCP build the console would have needed either a patched
cluster path or a config swap.)

It matters for the demo because the `Anomaly` stimulus is consumed by `OpsConsoleOut` — **not**
by the `Ops` cluster. Without the console running you will see `TREND:` lines in
`out/Ops_events.out` but never `[OPS] Anomaly(...)`, which looks like a broken partition demo
and is not.

## 4 · The partition

```bash
./two-host.sh cut  <OPS_HOST>     # host A, needs sudo
./two-host.sh heal <OPS_HOST>
./two-host.sh cut-status
```

This installs `iptables` DROP rules on ports 30001–30002 in both directions. **DROP, not
REJECT** — a silent blackhole is what a pulled cable looks like; REJECT sends an immediate RST
and is a different (easier) failure for the mesh to detect.

If the venue allows it, **pull the physical cable instead.** It is the same partition and
immeasurably better theatre. Rehearse with `cut`, perform with the cable, and keep `cut` as the
fallback if you end up on conference WiFi.

Say the quiet part out loud while it is cut: *"Both bridge processes are still running. Nothing
crashed. The network is simply gone."* Then `./two-host.sh status` on host A to show everything
still RUNNING.

## 5 · The sequence

Rehearse this end to end at least twice. Timings assume ~5 s of settle after bring-up.

| # | Say | Do | Watch for |
|---|-----|-----|-----------|
| 1 | "Steady state — the plant is reporting to the brain." | *(nothing)* | `TREND: avg pressure …` on host B |
| 2 | "Two independent network paths, and the mesh notices duplicates." | `./two-host.sh kill ZA_Bridge1` *(host A)* | Telemetry **keeps flowing** on host B |
| 3 | "Bring it back." | `./two-host.sh start ZA_Bridge1` | Dedup trace lines naming both bridges |
| 4 | "Now I cut the network. Nothing is being killed." | `./two-host.sh cut <OPS_HOST>` | Host B goes quiet; host A `status` all RUNNING |
| 5 | **"The pipe bursts while the brain is dark."** | `./chaos.sh burst` *(host A)* | `REFLEX: closing valve …` → `VALVE CLOSED` on **host A only** |
| 6 | "The plant protected itself with no central anything." | `cat /tmp/reflex/zoneA/valve.pos` | `CLOSED` |
| 7 | "Reconnect." | `./two-host.sh heal <OPS_HOST>` | Host B resumes with **fresh** values, no stale flood |
| 8 | "A capability appears at runtime." | `./chaos.sh hotplug` | `LeakLocalizer` lights up |
| 9 | "And a whole new zone, with zero changes at Ops." | `./chaos.sh zoneb` *(host A)* | `Anomaly(Zone_B …)` at Ops via regex subscriptions |

Steps 4–6 are the talk. Everything else is supporting evidence — cut 8 and 9 first if you are
short on time.

Between runs: `./two-host.sh stop-all` on both hosts, and `./two-host.sh heal <OPS_HOST>` on
host A (**always verify with `cut-status`** — a leftover DROP rule is the single most likely way
to lose the demo).

## 6 · Rehearsing on one machine

Because the transport is already TCP, the ordinary single-host demo *is* the rehearsal:

```bash
./run.sh
java -cp ../../source/java/neupaths.jar:. ConsoleMain
```

Everything except the network cut behaves identically — same sockets, same bridges, same
duplicate filtering. Verified on one host: listeners on 30001/30002/30010, telemetry reaching
`OpsTrend`, dedup naming both bridge paths, `./chaos.sh burst` → `REFLEX: closing valve` →
`VALVE CLOSED`, `kill-bridge1` with telemetry still flowing, and `./chaos.sh zoneb` producing
`[OPS] Anomaly(Zone_B …)` at the console with no Ops-side change.

Substitute `./two-host.sh kill ZA_Bridge1 && ./two-host.sh kill ZA_Bridge2` for the firewall cut
when rehearsing solo — it is the weaker claim you are replacing, but it exercises the same
recovery path. The firewall cut itself is the one step that must be rehearsed on real hardware.

## 7 · Failure modes, ranked by how likely they are to bite you

| Symptom | Cause | Fix |
|---|---|---|
| Host B silent, host A healthy | Port 30001/30002 blocked | Open the ports; check `ss -tln` on host B |
| Bridges never connect | `gen` run with the wrong `<OPS_HOST>` | Re-run `gen`; verify with `grep 'Peer#Ops' cfg/2h/ZA_Bridge1.xml` |
| Still partitioned after `heal` | Leftover `iptables` rule | `./two-host.sh cut-status`, then `heal` again |
| No `[OPS] Anomaly` line | Console not running on host B | See §3 — `Anomaly` goes to `OpsConsoleOut`, not the Ops cluster |
| Nothing starts, `Address already in use` | A previous run is still up, or something else holds 30001/30002/30010 | `./two-host.sh stop-all`; check with `ss -tlnp \| grep -E '3000[12]\|3001[0]'` |
| Ops sees nothing after `zoneb` | Zone B needs its own bridges | `zoneb` starts them; check `out/ZB_Bridge*.out` |
| `run './two-host.sh gen ...' first` | `gen` not run on host A | `./two-host.sh gen <OPS_HOST>` |

`cfg/2h/` is generated and disposable — delete and regenerate freely. The committed `cfg/` is
never modified (except the optional console swap in §3, which keeps a backup in `cfg/orig/`).
