# 3 · Subsystem Class Diagrams

These are the internal (mostly package-private) subsystems beneath the public API. Class
prefixes name the subsystem: `Nuc_` nucleus, `Syn_`/`Bnd_` transport, `Cryp_` crypto,
`Msg_` wire protocol, `Cfg_` configuration.

## 3.1 The Nucleus — per-cell routing engine (`Nuc_*`)

The `Nuc_Nucleus` is the heart of a cell. It owns the cell's binders, matches incoming
stimuli against subscriptions, filters duplicates, forwards subscriptions to peers, and
runs the send/receive worker threads. **This is what replaces the broker** — every cell
carries its own.

```mermaid
classDiagram
    class Nuc_Nucleus {
        -String cellName
        -UUID cellInstanceID
        -SubscriptionSpecSet subscriptions
        -Nuc_State state
        -Map~UUID,Bnd_Binder~ binders
        -Queue~Msg_NeuPaths~ recvQueue
        -Queue~Nuc_Transmit~ xmitQueue
        -Map~UUID,Nuc_PeerInfo~ peerMap
        -Map subscriptionMap
        -Map forwardedSubscriptions
        -Map~UUID,Long~ stimuliHistory
        -Cryp_Cipher cipher
        -Cell cell
        +addSubscriptions(SubscriptionSpecSet) void
        +setCell(Cell) void
        +start() void
        +stop() void
    }
    class Nuc_State {
        <<enumeration>>
        OFFLINE
        ONLINE
    }
    class Nuc_Transmit
    class Nuc_PeerInfo
    class Nuc_SubscriptionInfo
    class ReceiveThread
    class TransmitThread
    class StimuliHistoryThread
    class SubscriptionTraceThread

    Nuc_Nucleus --> Nuc_State
    Nuc_Nucleus *-- "0..*" Bnd_Binder : one per synapse
    Nuc_Nucleus *-- "1" Cryp_Cipher
    Nuc_Nucleus o-- "*" Nuc_PeerInfo
    Nuc_Nucleus o-- "*" Nuc_SubscriptionInfo
    Nuc_Nucleus *-- Nuc_Transmit : xmit queue
    Nuc_Nucleus ..> ReceiveThread
    Nuc_Nucleus ..> TransmitThread
    Nuc_Nucleus ..> StimuliHistoryThread
    Nuc_Nucleus ..> SubscriptionTraceThread
    Nuc_Nucleus o-- "1" Cell : back-reference
```

**Key responsibilities**

| Field / thread | Responsibility |
|----------------|----------------|
| `binders` | One `Bnd_Binder` per synapse; the actual network I/O endpoints. |
| `recvQueue` / `xmitQueue` + semaphores | Decouple network threads from routing; blocking queues so idle cells sleep. |
| `subscriptionMap` | Local subscriptions → which peer synapses satisfy them. |
| `forwardedSubscriptions` | Subscriptions learned from peers and re-advertised (how interest propagates across the mesh). |
| `stimuliHistory` + `StimuliHistoryThread` | Time-windowed journal of recently-seen stimulus ids → **duplicate filtering**; the window is `duplicateDetectionInterval`. |
| `SubscriptionTraceThread` | Periodically re-advertises subscriptions (`subscriptionRefreshInterval`) so the dynamic mesh stays wired. |
| `cipher` | Encrypts/decrypts each stimulus value on the wire. |

## 3.2 Transport subsystem (`Syn_*` and `Bnd_*`)

Two cooperating hierarchies: **synapses** are the raw channels (an abstraction over a JDK
NIO channel); **binders** manage the peer relationship and message framing over a synapse.

### Synapse channels & addresses

```mermaid
classDiagram
    class Syn_Synapse {
        <<abstract>>
        #Syn_Type type
        #Syn_Mode mode
        #Syn_State state
        #Syn_SessionType sessionType
        +open(Syn_Address) void
        +connect(Syn_Address) void
        +accept() void
        +send(Object) void
        +receive() Object
        +close() void
    }
    class Syn_InetStreamChannel
    class Syn_InetDatagramChannel
    class Syn_UnixStreamChannel

    Syn_Synapse <|-- Syn_InetStreamChannel
    Syn_Synapse <|-- Syn_InetDatagramChannel
    Syn_Synapse <|-- Syn_UnixStreamChannel

    class Syn_Address {
        <<abstract>>
        +getSynapseName() String
    }
    class Syn_InetAddress
    class Syn_UnixAddress
    Syn_Address <|-- Syn_InetAddress
    Syn_Address <|-- Syn_UnixAddress

    class Syn_Factory {
        +createSynapse(String) Syn_Synapse
        +createAddress(String) Syn_Address
    }
    class Syn_Name
    class SynapseSpec

    Syn_Factory ..> Syn_Synapse : creates
    Syn_Factory ..> Syn_Address : creates
    Syn_Factory ..> Syn_Name : parses
    SynapseSpec o-- Syn_Name
```

| Type | Transport |
|------|-----------|
| `Syn_InetStreamChannel` | TCP (`SocketChannel` / `ServerSocketChannel`) |
| `Syn_InetDatagramChannel` | UDP unicast and multicast (`DatagramChannel`) |
| `Syn_UnixStreamChannel` | Unix-domain stream socket (`AF_UNIX`) |
| `Syn_InetAddress` | IP host + port |
| `Syn_UnixAddress` | filesystem socket path |

### Enumerations that make up a synapse name

```mermaid
classDiagram
    class Syn_Scope {
        <<enumeration>>
        NETWORK
        LOCAL
    }
    class Syn_Type {
        <<enumeration>>
        STREAM
        UNICAST
        MULTICAST
    }
    class Syn_Mode {
        <<enumeration>>
        PEER
        LISTENER
    }
    class Syn_SessionType {
        <<enumeration>>
        CONNECTIONED
        CONNECTIONLESS
    }
    class Syn_State {
        <<enumeration>>
        UNINITIALIZED
        INITIALIZED
        OPEN
        LISTENING
        CONNECTED
        CLOSED
        RECLAIM
    }
```

**Synapse name grammar** (from `package-info`):

```
scope # type # mode # domain [ # opt1 [ # opt2 ... ] ]

  scope   = Network | Local
  type    = Stream (TCP) | Unicast (UDP) | Multicast (UDP)
  mode    = Listener | Peer
  domain  = case-sensitive name, or "@" for the global domain
  opts    = type-specific (e.g. port, host/IP, multicast group, socket path)

Examples:
  Network#Stream#Listener#@#30001
  Network#Unicast#Peer#D1#30001#192.168.1.10
  Network#Multicast#Peer#@#30001#224.0.0.10
  Local#Stream#Listener#@#/tmp/neupaths.sock
```

### Binders — the peer relationship over a synapse

```mermaid
classDiagram
    class Bnd_Binder {
        <<abstract>>
        -Syn_Name synName
        -Map~UUID,Bnd_PeerInfo~ peerInfoMap
        +start() void
        +stop() void
        +send(UUID, Msg_NeuPaths) void
        #sendMessage() void
        #receiveMessage() Msg_NeuPaths
    }
    class Bnd_StreamPeer
    class Bnd_StreamListener
    class Bnd_UnicastPeer
    class Bnd_UnicastListener
    class Bnd_MulticastPeer
    class Bnd_Factory {
        +createBinder(String, UUID, Syn_Name, SubscriptionSpecSet) Bnd_Binder
    }
    class Bnd_PeerInfo {
        -Syn_Synapse fromPeerSynapse
        -Syn_Synapse toPeerSynapse
        -Thread peerThread
    }

    Bnd_Binder <|-- Bnd_StreamPeer
    Bnd_Binder <|-- Bnd_StreamListener
    Bnd_Binder <|-- Bnd_UnicastPeer
    Bnd_Binder <|-- Bnd_UnicastListener
    Bnd_Binder <|-- Bnd_MulticastPeer
    Bnd_Factory ..> Bnd_Binder : creates
    Bnd_Binder o-- "*" Bnd_PeerInfo
    Bnd_PeerInfo *-- "2" Syn_Synapse : send + receive
```

- **Listener vs Peer.** A `Listener` binder waits; when a `Peer` joins, the listener
  **spawns a new peer synapse** dedicated to that connection — the same "grow the mesh on
  contact" pattern for TCP, UDP-unicast and Unix streams.
- **Design patterns:** *Abstract Factory* (`Syn_Factory`, `Bnd_Factory`), *Strategy* (the
  synapse/binder subclasses per transport), *State* (`Syn_State` lifecycle), and the JDK
  *Cleaner* pattern for deterministic channel cleanup.

## 3.3 Cryptography subsystem (`Cryp_*`)

A pluggable cipher encrypts each stimulus value into a `javax.crypto.SealedObject` before
it crosses a synapse. The cipher is chosen by the **first byte of the crypto key**.

```mermaid
classDiagram
    class Cryp_Cipher {
        <<interface>>
        +encrypt(Object) Object
        +decrypt(Object) Object
    }
    class Cryp_AES
    class Cryp_Blowfish
    class Cryp_Null
    class Cryp_Stim
    class Cryp_Factory {
        +createCipher(byte[]) Cryp_Cipher
    }
    class Cryp {
        <<constants>>
        +int AES_CIPHER
        +int BLOWFISH_CIPHER
    }

    Cryp_Cipher <|.. Cryp_AES
    Cryp_Cipher <|.. Cryp_Blowfish
    Cryp_Cipher <|.. Cryp_Null
    Cryp_AES <|-- Cryp_Stim
    Cryp_Factory ..> Cryp_Cipher : creates
    Cryp_Factory ..> Cryp : reads type byte
    Nuc_Nucleus *-- Cryp_Cipher : uses
```

| Cipher | Algorithm |
|--------|-----------|
| `Cryp_AES` | `AES/CBC/PKCS5Padding`; first 16 key bytes are the IV. |
| `Cryp_Blowfish` | Blowfish. |
| `Cryp_Null` | Pass-through (no encryption). |
| `Cryp_Stim` | Default stimulus cipher — a fixed-key `Cryp_AES` used when no key is supplied. |

- The factory reads a type byte from the key: `Cryp.AES_CIPHER` (1) or
  `Cryp.BLOWFISH_CIPHER` (2). Supply a real key via `GenerateCryptoKey`.
- *Strategy + Factory* patterns; `Excp_Cipher` wraps all `javax.crypto` checked exceptions.

## 3.4 Wire protocol (`Msg_*`)

Everything crossing a synapse is a `Msg_NeuPaths`. One message type carries application
data (`Msg_Stimulus`); the rest form the **control plane** for the broker-less mesh:
peer join, subscription advertisement, and departure.

```mermaid
classDiagram
    class Msg_NeuPaths {
        <<abstract>>
        -UUID arrivalSynapseInstanceID
        -UUID departureSynapseInstanceID
        -String arrivalDomain
    }
    class Msg_Stimulus {
        -String producerCellName
        -String producerTransmitterName
        -UUID typeID
        -UUID instanceID
        -UUID transactionID
        -Object value
        -List~Stim_Trace~ trace
    }
    class Msg_JoinRequest {
        -Msg_JoinPhase joinPhase
        -UUID cellInstanceID
        -String cellName
        -String synapseDomain
    }
    class Msg_JoinAcknowledge {
        -UUID requesterInstanceID
    }
    class Msg_Subscription {
        -String producerCellName
        -String producerTransmitterName
        -String consumerReceptorName
        -String domain
    }
    class Msg_Leave {
        -UUID cellInstanceID
        -String cellName
    }
    class Msg_JoinPhase {
        <<enumeration>>
        PHASE_1
        PHASE_2
    }

    Msg_NeuPaths <|-- Msg_Stimulus
    Msg_NeuPaths <|-- Msg_JoinRequest
    Msg_NeuPaths <|-- Msg_JoinAcknowledge
    Msg_NeuPaths <|-- Msg_Subscription
    Msg_NeuPaths <|-- Msg_Leave
    Msg_JoinRequest --> Msg_JoinPhase
    Msg_Stimulus o-- "*" Stim_Trace
```

| Message | Plane | Role |
|---------|-------|------|
| `Msg_Stimulus` | data | Carries a user stimulus (value + trace) across a synapse. |
| `Msg_JoinRequest` | control | A peer advertises join intent and its send/receive synapse endpoints. |
| `Msg_JoinAcknowledge` | control | The listener reciprocates its endpoints. |
| `Msg_Subscription` | control | Declares interest in a producer's transmitter for a receptor. |
| `Msg_Leave` | control | Notifies the mesh of a departing cell so peers clean up. |

The join handshake sequence is drawn in [§4.4](04-behavior.md#44-peer-join-handshake-sequence).

## 3.5 Configuration subsystem (`Cfg_*`)

Clusters and cells are declared in **XML** and parsed with a **DOM**, recursive-descent set
of handlers. `CellFactory` drives it. Handler *interfaces* (`*HandlerInt`) are callback
contracts by which a child handler pushes its parsed result up to its parent — a
Chain-of-Responsibility / Composite arrangement mirroring the XML nesting.

```mermaid
classDiagram
    class Cfg_ConfigHandler {
        <<abstract>>
        #String xPath
        #processNode(Node) void
        #processElement() void
    }
    class Cfg_Utils {
        <<utility>>
        +processElement() void
        +getNodeText(Node) String
        +cryptoKeyBytes(String) byte[]
    }
    class Cfg_CellClusterHandler
    class Cfg_CellDefinitionsHandler
    class Cfg_CellHandler {
        <<abstract>>
    }
    class Cfg_LogicCellHandler
    class Cfg_BridgeCellHandler
    class Cfg_EventCellHandler
    class Cfg_ExtractorCellHandler
    class Cfg_InjectorCellHandler
    class Cfg_LoadBalancedCellHandler
    class Cfg_LoadControllerCellHandler
    class Cfg_RouterCellHandler
    class Cfg_SpecializedCellHandler

    Cfg_ConfigHandler <|-- Cfg_CellClusterHandler
    Cfg_ConfigHandler <|-- Cfg_CellDefinitionsHandler
    Cfg_ConfigHandler <|-- Cfg_CellHandler
    Cfg_CellHandler <|-- Cfg_LogicCellHandler
    Cfg_CellHandler <|-- Cfg_BridgeCellHandler
    Cfg_CellHandler <|-- Cfg_EventCellHandler
    Cfg_CellHandler <|-- Cfg_ExtractorCellHandler
    Cfg_CellHandler <|-- Cfg_InjectorCellHandler
    Cfg_CellHandler <|-- Cfg_LoadBalancedCellHandler
    Cfg_CellHandler <|-- Cfg_LoadControllerCellHandler
    Cfg_CellHandler <|-- Cfg_RouterCellHandler
    Cfg_CellHandler <|-- Cfg_SpecializedCellHandler
```

`Cfg_CellClusterHandler` parses the `<Cell_Cluster>` root and delegates to
`Cfg_CellDefinitionsHandler` for the list of cell-definition files. Each `Cfg_CellHandler`
subclass parses one cell type, capturing its name, properties, synapses and logging config.

**Handler callback interfaces** (child ⇒ parent aggregation):

```mermaid
classDiagram
    class Cfg_ActivatorsHandlerInt {
        <<interface>>
        +addActivator(Activator) void
    }
    class Cfg_ReceptorsHandlerInt {
        <<interface>>
        +addReceptor(ReceptorSpec) void
    }
    class Cfg_SubscriptionsHandlerInt {
        <<interface>>
        +addSubscription(SubscriptionSpec) void
    }
    class Cfg_TransmitterHandlerInt {
        <<interface>>
        +addTransmitter(TransmitterSpec) void
    }
    class Cfg_SynapsesHandlerInt {
        <<interface>>
        +addSynapses(Set) void
    }

    Cfg_ActivatorsHandlerInt <|.. Cfg_LogicCellHandler
    Cfg_ActivatorsHandlerInt <|.. Cfg_LoadBalancedCellHandler
    Cfg_ActivatorsHandlerInt <|.. Cfg_SpecializedCellHandler
    Cfg_SubscriptionsHandlerInt <|.. Cfg_BridgeCellHandler
    Cfg_SubscriptionsHandlerInt <|.. Cfg_LoadControllerCellHandler
    Cfg_ReceptorsHandlerInt <|.. Cfg_LoadControllerCellHandler
    Cfg_TransmitterHandlerInt <|.. Cfg_InjectorCellHandler
    Cfg_SynapsesHandlerInt <|.. Cfg_CellHandler
```

The nested element handlers (`Cfg_CellPropertiesHandler` → `Cfg_CellPropertyHandler`,
`Cfg_CellSubscriptionsHandler` → `Cfg_CellSubscriptionHandler`,
`Cfg_CellActivatorsHandler` → `Cfg_CellActivatorHandler`,
`Cfg_CellReceptorsHandler` → `Cfg_CellReceptorHandler`,
`Cfg_CellTransmitterHandler`) follow the same container→item pattern and use **reflection**
(`Class.forName`) to instantiate `Activator`s and to read each stimulus type's `TYPE_ID`.

## 3.6 Utilities & the daemon control plane (`neupaths.util`)

Command-line tools and a background **daemon** for operating clusters. The daemon itself is
built *from NeuPaths cells* — the control plane eats its own dog food.

```mermaid
classDiagram
    class CellClusterExec
    class CellClusterDaemon
    class IssueCommandToDaemon
    class EventLogger
    class GenerateCryptoKey
    class GenerateStimulusType
    class CommandLine
    class PropertySet

    class DaemonCmdActv {
        -Map~String,CellCluster~ clusters
    }
    class DaemonCmdStim
    class DaemonCmdRespStim
    class DaemonCmdType {
        <<enumeration>>
    }
    class DaemonCellInfo
    class Daemon {
        <<constants>>
    }

    Activator <|-- DaemonCmdActv
    Stimulus <|-- DaemonCmdStim
    Stimulus <|-- DaemonCmdRespStim
    DaemonCmdActv --> DaemonCmdType : dispatches on
    DaemonCmdActv o-- "*" CellCluster : manages
    DaemonCmdRespStim o-- "*" DaemonCellInfo
    CellClusterDaemon *-- DaemonCmdActv : hosts in a LogicCell
    IssueCommandToDaemon ..> DaemonCmdStim : injects
    IssueCommandToDaemon ..> DaemonCmdRespStim : extracts
```

The six CLI entry points (each has a `main()`): `CellClusterExec`, `CellClusterDaemon`,
`IssueCommandToDaemon`, `EventLogger`, `GenerateCryptoKey`, `GenerateStimulusType`.
`DaemonCmdStim`/`DaemonCmdRespStim` are `Stimulus` subclasses; `DaemonCmdActv` is an
`Activator`; `PropertySet` is a thread-safe, serializable, iterable name/value map used for
arguments and cell properties throughout.

**Control-plane flow.** `CellClusterDaemon` hosts a `LogicCell` whose `DaemonCmdActv`
listens for `DaemonCmdStim` commands (discover / deploy / start / pause / stop / logging —
33 in `DaemonCmdType`). `IssueCommandToDaemon` uses an `InjectorCell` to send a command and
an `ExtractorCell` to receive the `DaemonCmdRespStim` — i.e. the operator tooling is itself
an ordinary NeuPaths client talking over synapses, with no privileged backchannel.

## 3.7 Exception hierarchy

Two roots: a checked `Excp_NeuPaths` family used internally by subsystems, and a single
unchecked `NeuPathsException` surfaced to API callers.

```mermaid
classDiagram
    class Exception
    class RuntimeException
    class Excp_NeuPaths
    class Excp_Synapse
    class Excp_SynapseFatal
    class Excp_SynapseNonFatal
    class Excp_Binder
    class Excp_Cell
    class Excp_Cipher
    class Excp_Nucleus
    class Excp_Receptor
    class Excp_Transmitter
    class Excp_Service
    class NeuPathsException

    Exception <|-- Excp_NeuPaths
    Excp_NeuPaths <|-- Excp_Synapse
    Excp_Synapse <|-- Excp_SynapseFatal
    Excp_Synapse <|-- Excp_SynapseNonFatal
    Excp_NeuPaths <|-- Excp_Binder
    Excp_NeuPaths <|-- Excp_Cell
    Excp_NeuPaths <|-- Excp_Cipher
    Excp_NeuPaths <|-- Excp_Nucleus
    Excp_NeuPaths <|-- Excp_Receptor
    Excp_NeuPaths <|-- Excp_Transmitter
    Excp_NeuPaths <|-- Excp_Service
    RuntimeException <|-- NeuPathsException
```

- Internal subsystems throw specific checked `Excp_*` types; boundaries (e.g. `Cell`,
  `Activator`) translate failures into the unchecked `NeuPathsException` for callers.
- `Excp_SynapseFatal` vs `Excp_SynapseNonFatal` lets the transport distinguish a dead
  connection (tear down) from a recoverable hiccup (retry) — key to mesh resilience.

Continue to the [Behavioral diagrams →](04-behavior.md)
