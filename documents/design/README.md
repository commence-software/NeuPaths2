# NeuPaths Software Design Documentation

**Version 2.0.2 · Generated from `source/java` · UML diagrams in [Mermaid](https://mermaid.js.org/) (renders natively on GitHub)**

This documentation reconstructs the software design of the NeuPaths framework from its
source code. NeuPaths is a **broker-less framework for event-driven, distributed and
parallel dataflow programming** written in pure Java SE (JDK 17+, no external dependencies).

The system is a metaphor for the human nervous system: autonomous **cells** (neurons)
are wired together by **synapses** and communicate by passing **stimuli** (signals).
There is no central broker — routing, subscription and duplicate-filtering happen
*at each cell*, in parallel, driven by the arrival of data.

## Reading order

| # | Document | Diagrams | What it covers |
|---|----------|----------|----------------|
| 1 | [Architecture Overview](01-architecture.md) | Package · Component · Deployment | The big picture: packages, the cell/synapse/stimulus mesh, runtime layering, and the core design principles. |
| 2 | [Domain Model — Core Class Diagrams](02-domain-model.md) | Class | The public API model: `Cell` hierarchy, `Activator`, `Stimulus`, receptors/transmitters, and the subscription spec model. |
| 3 | [Subsystem Class Diagrams](03-subsystems.md) | Class | Internals: the `Nucleus` routing engine, the synapse/binder transport, cryptography, the wire protocol, XML configuration, and utilities/daemon. |
| 4 | [Behavioral Diagrams](04-behavior.md) | Sequence · State · Activity | How it *runs*: cell lifecycle, dataflow evaluation, peer join handshake, stimulus propagation, injector/extractor round-trips, cycle detection and load balancing. |
| 5 | [Execution Model — Threads & Dataflow](05-execution-model.md) | Pipeline · Activity | The multi-threaded model: the staged producer/consumer pipeline, the full thread inventory, the receptor-completion firing gate, and concurrency guarantees and sharp edges. |

## The vocabulary at a glance

| Concept | Type in code | Meaning |
|---------|--------------|---------|
| **Stimulus** | `neupaths.api.Stimulus` | An atomic, typed, serializable unit of data that flows through the system. |
| **Cell** | `neupaths.api.Cell` | An autonomous processing node — a neuron. Eight primitive types. |
| **Synapse** | `neupaths.api.Syn_Synapse` | A transport-abstracting connection between cells (TCP, UDP, multicast, Unix socket). |
| **Nucleus** | `neupaths.api.Nuc_Nucleus` | The per-cell routing engine: manages synapses, subscriptions, queues and duplicate filtering. |
| **Activator** | `neupaths.api.Activator` | User-written logic. Fires only when **all** its receptors hold a stimulus (dataflow). |
| **Receptor / Transmitter** | `Rx_Receptor` / `Tx_Transmitter` | Named, type-safe inputs / outputs of an activator. |
| **Subscription** | `neupaths.api.SubscriptionSpec` | A wiring contract: `(producing cell C, transmitter T) ⇒ receptor R`. |
| **Domain** | (string tag) | A namespace scoping where stimuli may travel; `@` is the global domain. |
| **Cellular system** | `CellCluster` / mesh | A broker-less mesh of cells bound by synapses. |

## Package map

```
neupaths.api    — the framework core: cells, activators, stimuli, synapses,
                  binders, nucleus, configuration, crypto, wire protocol (≈150 types)
neupaths.stim   — concrete Stimulus types for Java primitives (Boolean, Byte, …, String)
neupaths.util   — command-line tools and the cluster daemon / control plane
```

> **Diagram conventions.** These documents use Mermaid `classDiagram`, `sequenceDiagram`,
> `stateDiagram-v2` and `flowchart` blocks. Inheritance is drawn `Base <|-- Derived`,
> interface realization `Interface <|.. Impl`, composition `Whole *-- Part`, and plain
> association/dependency `A --> B`. Package-private types are noted where relevant; the
> class prefixes (`Syn_`, `Bnd_`, `Nuc_`, `Cfg_`, `Cryp_`, `Msg_`, `Rx_`, `Tx_`, `Stim_`,
> `Actv_`, `Excp_`) denote the subsystem a type belongs to.
