# NeuPaths — Presentation Outline

**Audience:** mixed room — software engineers + technical/product managers
**Length:** 45 min + 15 min Q&A (a 30-minute cut is in §5)
**Covers:** NeuPaths 2.1.0
**Live demo:** `demos/ReflexArc/` — see [`TWO_HOST_RUNBOOK.md`](../demos/ReflexArc/TWO_HOST_RUNBOOK.md)

A mixed room usually fails by pitching to the middle: too much mechanism for the managers, too
much hand-waving for the engineers, and neither group is convinced. This deck avoids that by
picking **one idea**, proving it with a demo before explaining it, and then *explicitly*
splitting the room for one stretch rather than blending the two registers.

---

## 1 · The one idea

Everything in NeuPaths is downstream of a single property: **the firing rule is local.** An
activator fires when all of its receptors hold a stimulus — a predicate one cell can evaluate
alone. No global clock, no central scheduler, no consensus. There is nothing to coordinate,
so there is nothing in the middle.

Carry it through the deck in two registers, and use the same words each time:

| | Phrasing |
|---|---|
| **Engineers** | "No broker, because each cell already knows when it's ready." |
| **Managers** | "Nothing in the middle to fail, operate, license, or scale." |

The manager version is a real TCO argument, not a restatement: a broker tier is a line item, an
on-call rotation, and eventually a scaling project.

**Where the thread surfaces:** state it on slide 3 → prove it on slide 5 → explain it on slide 7
→ monetise it on slide 14.

---

## 2 · Pre-flight

- [ ] `make` in `source/java`, then `make` in `demos/ReflexArc`
- [ ] Ports **30001**, **30002**, **30010** free (plus **30020** if demoing Zone B)
- [ ] Two machines, host B reachable from host A on 30001–30002
- [ ] `./two-host.sh gen <OPS_HOST>` run on host A
- [ ] Firewall cut rehearsed: `cut` → `cut-status` → `heal` → `cut-status`
- [ ] **Backup recording** of the full demo
- [ ] Console running on **host B** (otherwise no `[OPS] Anomaly(...)` line — see runbook §3)
- [ ] Font size checked from the back of the room for `out/*.out` tails

> **The single likeliest way to lose this demo** is a leftover `iptables` DROP rule from a
> rehearsal. Always finish with `./two-host.sh cut-status`.

---

## 3 · The deck at a glance

| # | Slide | Act | Min | For |
|---|-------|-----|-----|-----|
| 1 | Title — "When the network splits, the plant keeps running" | I | 0 | all |
| 2 | The problem: coordination is the fragile part | I | 1 | managers |
| 3 | The claim: there is no middle | I | 3 | all |
| 4 | Demo map (two hosts, two bridges) | I | 4 | all |
| 5 | **LIVE DEMO** — partition and reflex | I | 5 | all |
| 6 | What you just saw | II | 13 | all |
| 7 | The one idea: the dataflow firing rule | II | 15 | engineers |
| 8 | Now the metaphor | II | 18 | all |
| 9 | One screen of code | II | 20 | engineers |
| 10 | Signpost: ten minutes of mechanism | III | 22 | all |
| 11 | The wiring contract | III | 23 | engineers |
| 12 | Cells | III | 26 | engineers |
| 13 | Why redundancy is free | III | 28 | engineers |
| 14 | **Topology is data** (live) | III | 31 | managers |
| 15 | Where it sits | IV | 34 | all |
| 16 | Sharp edges | IV | 37 | all |
| 17 | When *not* to use this | IV | 39 | managers |
| 18 | Observability and ops | IV | 41 | engineers |
| 19 | Maturity and next step | V | 43 | managers |
| 20 | Close | V | 44 | all |

---

## 4 · Slide by slide

### Act I — Problem and proof (0–13 min)

**1 · Title**
"When the network splits, the plant keeps running." Subtitle: *broker-less dataflow for
distributed control · NeuPaths 2.1.0*.

> Deliberately **not** "a nervous system for software." The metaphor is unearned this early and
> reads as whimsy to exactly the people you most need to convince. It arrives on slide 8, after
> they have watched it work.

**2 · The problem** *(managers)*
Distributed systems coordinate through something in the middle. That middle is your failure
domain, your latency floor, your license, and your on-call rotation. Worse: when the network
splits, the edge goes dumb precisely when it matters most.
*Visual:* one broker, spokes, a lightning bolt on the hub.

**3 · The claim**
"There is no middle." Put both translations from §1 on the slide together.
Close with: *"I'm going to show you this working before I explain it."*

**4 · Demo map**
Host A "the plant" / Host B "the brain", two bridge paths, ports 30001–2. Leave it up for the
whole demo — it is the audience's reference frame.

**5 · LIVE DEMO** (8 min) — runbook §5, steps 1–6

| Beat | Command | Watch |
|---|---|---|
| Steady state | *(nothing)* | `TREND: avg pressure …` on host B |
| Kill one path | `./two-host.sh kill ZA_Bridge1` | telemetry **keeps flowing** |
| Restore | `./two-host.sh start ZA_Bridge1` | dedup lines naming both bridges |
| **Cut the network** | `./two-host.sh cut <OPS_HOST>` *(or pull the cable)* | host B goes quiet |
| Prove nothing died | `./two-host.sh status` | everything still `RUNNING` |
| **Burst** | `./chaos.sh burst` | `REFLEX: closing valve` → `VALVE CLOSED` on host A only |
| Heal | `./two-host.sh heal <OPS_HOST>` | fresh values, no stale backlog flood |

> The line to say while it is cut: **"Both bridge processes are still running. Nothing crashed.
> The network is simply gone."** That sentence is the whole talk.

> Pull the physical cable if the venue allows — same partition, immeasurably better theatre.
> Rehearse with `cut`, perform with the cable, keep `cut` as the fallback.

### Act II — Why it worked (13–22 min)

**6 · What you just saw**
Four bullets, each tied to something they watched:
- the reflex fired with the brain dark
- both bridges stayed alive — this was not a process kill
- duplicates were filtered, which is what made redundancy free
- on heal, fresh values, no replayed backlog

**7 · The one idea** *(engineers — spend real time here)*
An activator fires when **all** its receptors hold a stimulus. That predicate is evaluable by
one cell alone → no coordinator, no clock, no consensus. Everything else in the system —
performance headroom, partition tolerance, hot-plug — is downstream of that locality.

**8 · Now the metaphor**
Cells, synapses, stimuli. A reflex arc is a real neurological structure: the spinal loop that
pulls your hand off a stove before your brain is involved. The biology **generated** the
architecture rather than decorating it, and the demo is that structure made literal.

> This lands here and would not have landed on slide 1. They have already seen it work, so the
> metaphor reads as explanation rather than marketing.

**9 · One screen of code**
One activator beside its cell XML. "That is the programming model."
Resist showing a second. Suggested: `BurstDetector` or `ValveGuard` from the demo they just saw.

### Act III — Mechanism, for the engineers (22–34 min)

**10 · Signpost**
Say it out loud: *"The next ten minutes are mechanism. Managers — your takeaway is slide 14."*
This respects both rooms and stops the drift that kills mixed-audience talks.

**11 · The wiring contract**
`(producing cell C, transmitter T) ⇒ receptor R`, scoped by domain. **C and T are regexes** —
plant that seed now; slide 14 cashes it.

**12 · Cells**
Eight primitive types; show **three** — Injector (code → mesh), Logic (the workhorse), Extractor
(mesh → code). Mention `CUSTOM` for your own subclasses and move on.
*Do not tour all eight.*

**13 · Why redundancy is free**
A time-windowed duplicate filter at every cell. Put the real trace from your own run on screen:

```
Duplicate received: Stimulus[producerCellName=ZA_Pressure, …],
  Trace: ZA_Pressure => ZA_Router (Zone_A) => ZA_Bridge2 (Zone_A) => OpsRouter (Ops)
```

This is why you can run two bridges at all, and it answers "what if a message arrives twice?"
before anyone asks it.

**14 · Topology is data** *(live — the managers' payoff)*
Run `./chaos.sh zoneb`. A whole new zone appears at Ops with **zero Ops-side changes**, because
Ops subscribes `.*_Pressure` rather than to named producers. Watch for
`[OPS] Anomaly(Zone_B …)` at the console.

Same property, three faces: hot-plug a sensor, clone a zone with `sed`, extend a live
load-balanced pool at runtime. That is a cost-of-scaling story, not a feature list.

### Act IV — Honesty and fit (34–43 min)

**15 · Where it sits**
Name the neighbours before the room does, or they will spend your talk silently pattern-matching
and trusting you a little less each slide.

| Neighbour | Delta |
|---|---|
| Kafka / pub-sub | They route through a broker. There is no middle here. |
| Actors (Akka) | Actors fire per message; activators fire on a **complete set** — the join *is* the scheduler. |
| Erlang / OTP | Supervision trees for fault tolerance; dataflow locality here. |
| **Flow-Based Programming** | Closest relative — same external topology, same port-ignorant components. NeuPaths is peer-to-peer rather than scheduler-mediated, and adds domain scoping. FBP uses bounded buffers with backpressure; NeuPaths does not. |
| LabVIEW / ROS | Domain-specific dataflow; this is general-purpose and broker-less. |

> Naming Morrison and FBP yourself signals command of the field. Being caught not knowing your
> closest relative is expensive.

**16 · Sharp edges** — volunteer all three
- Queues are unbounded `LinkedList`s; **no backpressure**. A slow activator grows its own queue.
- Load balancing **broadcasts** work — every worker receives every item, all but one discard.
  Favours fast local segments over wide-area pools with large payloads.
- A worker that dies mid-job **loses that item — within its pool**. The answer is redundant
  execution, not retry: two pools both answering under the same transaction, first response
  wins, rest filtered. Say the precise version — *"not at-least-once delivery;
  at-least-one-of-N execution"* — because it recovers at the speed of the surviving path
  instead of waiting out a timeout. Demo asset: `examples/Transaction_Filter`.

> An engineer who *finds* a sharp edge you hid discounts everything else you said. One who hears
> you volunteer it extends credit to the rest. This slide buys more than it costs.

**17 · When *not* to use this**
Your highest-credibility slide. Not a high-throughput wide-area pipeline with large payloads.
Not where you need delivery guarantees you have not built. It is for distributed control loops,
edge autonomy, sensor meshes, and systems that must keep deciding when the link is down.

**18 · Observability and ops**
`EventCell` + `EventLogger`, fifteen event levels, per-transmitter stimulus tracing,
`SynapseTestTool`, `CycleDetectorTool` — and the control plane (`CellClusterDaemon`) is built
from ordinary cells with no privileged backchannel.

> "Distributed systems are hard to debug" is the objection that kills adoption *after* the room
> is already sold. Have this slide ready.

### Act V — Close (43–45 min)

**19 · Maturity and next step** *(managers)*
Answer the production-usage question directly, including "not yet — here is the verified demo
and the pilot I'd propose." Do not let this surface first in Q&A.

**20 · Close**
Restate the one idea over the image of the cut cable. Point at:
- `documents/design/` — eight documents, current at 2.1.0, diagrams render-verified
- `demos/ReflexArc/` — everything they just watched
- `examples/` — WorldHello, Transaction, AlarmService, LoadBalance, ClusterDaemon

> The design docs are the **leave-behind**. That is what lets the talk stay deliberately shallow
> without *being* shallow — the depth is one link away and visibly real.

---

## 5 · The 30-minute cut

Drop slides **11, 12, 18** and fold 14 into the main demo. Acts I and IV are load-bearing;
Act III is the part that compresses. Do not cut slide 16 or 17 to save time — losing the honesty
slides costs more credibility than the minutes are worth.

---

## 6 · Q&A preparation

**Engineers**

| Question | Answer |
|---|---|
| "Isn't this just Kafka / pub-sub?" | No broker, and the join is the scheduler. See slide 15. |
| "XML? In 2026?" | The format is incidental; **topology as data** is the point — it is what makes regex subscriptions, zone cloning and runtime pool extension possible. And the pass-through constructor lets a team put wiring in code instead. |
| "No backpressure is disqualifying." | Fair for some workloads — that is slide 17. Bounded by topology and design today; layer your own throttle. Be careful **not** to offer redundancy as the answer: it addresses independent failure, while saturation is common-mode. Two pools fall behind identically at 2× the cost. |
| "So you lose work when a node dies?" | Within one pool, yes. Run two — both answer under the same transaction and the requester filters the duplicate. Slide 16. |
| "How do I debug a mesh?" | Slide 18. |
| "Is the partition demo real, or simulated?" | Two machines, real TCP, real firewall cut (or a pulled cable) — and every process stayed up. |
| "Java only?" | Pure Java SE 17+, no external dependencies. |

**Managers**

| Question | Answer |
|---|---|
| "Who runs this in production?" | Slide 19 — answer directly, do not evade. |
| "What does it replace?" | Usually a broker tier plus the bespoke edge-autonomy code written around it. |
| "Learning curve?" | One concept (the firing rule) and one file format. Slide 9 is the evidence. |
| "What if the author moves on?" | Design docs reconstruct the system from source; no external dependencies; plain Java. |
| "Buy vs. build?" | Commence Software — state the support model you intend to offer. |

---

## 7 · Assets

| Asset | Path |
|---|---|
| Design documentation (8 docs, Mermaid) | [`documents/design/`](design/) |
| Single-page design HTML | [`documents/design/neupaths-design.html`](design/neupaths-design.html) |
| Reflex Arc demo | [`demos/ReflexArc/`](../demos/ReflexArc/) |
| Two-host partition runbook | [`demos/ReflexArc/TWO_HOST_RUNBOOK.md`](../demos/ReflexArc/TWO_HOST_RUNBOOK.md) |
| Load-balancing case study | [`documents/design/06-case-study-load-balancing.md`](design/06-case-study-load-balancing.md) |
| Reflex arc case study | [`documents/design/07-case-study-reflex-arc.md`](design/07-case-study-reflex-arc.md) |
| SDK user guide | [`documents/NeuPaths_SDK_User_Guide.pdf`](NeuPaths_SDK_User_Guide.pdf) |
