// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

/**
 * Provides the APIs for creating and running a NeuPaths cellular system.
 * <p>
 * A NeuPaths cellular system consists of <i>Cells</i> interconnected by
 * <i>Synapses</i> that process <i>Stimuli</i>.
 * </p>
 * <h2>Cells</h2>
 * <p>
 * There are eight primitive cell types:
 * </p>
 * <ul>
 * <li>{@link BridgeCell} - Transports stimuli across cell domains by maintaining
 * a presence in multiple domains and using subscriptions to pull stimuli
 * to the bridge.</li>
 * <li>{@link EventCell} - Processes log events ({@link EventStimulus}) in a
 * particular domain.  The {@link neupaths.util.EventLogger} utility uses an
 * {@link EventCell} to spool log events to standard output or a designated
 * file.</li>
 * <li>{@link ExtractorCell} - Extracts stimuli from a cell system.  Used by
 * conventional programs to interact with a cell system.</li>
 * <li>{@link InjectorCell} - Injects stimuli into a cell system.  Used by
 * conventional programs to interact with a cell system.</li>
 * <li>{@link LogicCell} - Uses one or more {@link Activator}s to process
 * stimuli and optionally produce new stimuli.</li>
 * <li>{@link LoadBalancedCell} - Uses a {@link LoadBalancedActivator} to
 * participate in a pool of cells that share the workload.  Similar to
 * a {@link LogicCell}, can include additional {@link Activator}s for
 * complex computational tasks.</li>
 * <li>{@link LoadControllerCell} - Acts as the coordinator for
 * {@link LoadBalancedCell}s, which register with the controller.</li>
 * <li>{@link RouterCell} - Routes stimuli over intersted synapses.</li>
 * </ul>
 * <p>
 * </p>
 * <h2>Stimuli</h2>
 * <p>
 * Stimuli are atomic data elements that are transported by synapses.  A
 * stimulus is a unique instance of a stimulus type derived from the
 * {@link Stimulus} class.
 * </p>
 * <h2>Synapses</h2>
 * <p>
 * Synapses bind together the cells in a system.  They are an abstraction for
 * the underlying technologies used to transport stimuli.  Synapses participate
 * in a single domain and will only transport stimuli that satisy a
 * subscription in that domain.  The one exception is that any synapse will
 * transport stimuli that match a subscription in the global domain.  A Peer
 * synapse can only join a Listener in the same domain.
 * </p>
 * <p>
 * Synapses are specified by string-encoded names with the following format:
 * <ul>
 * <i>scope</i>#<i>type</i>#<i>mode</i>#<i>domain</i>[#<i>opt1</i>[#<i>opt2</i> ... [#<i>optN</i>]]]
 * <p>
 * <table cellspacing=10>
 * <tr>
 * <td><i>scope</i></td>
 * <td>Currently supported values: Network, Local.</td>
 * </tr>
 * <tr>
 * <td><i>type</i></td>
 * <td>Currently supported values: Stream, Unicast, Multicast.</td>
 * </tr>
 * <tr>
 * <td><i>mode</i></td>
 * <td>Currently supported values: Listener, Peer.</td>
 * </tr>
 * <tr>
 * <td><i>domain</i></td>
 * <td>Case-sensitive name or "@" for the global domain.</td>
 * </tr>
 * <tr>
 * <td><i>opt<sub>i</sub></i></td>
 * <td>Synapse type-specific option.</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * <h3>Network Stream Synapses</h3>
 * <p>
 * Network Stream synapses use TCP/IP.  They are specified as follows:
 * <ul>
 * <p>
 * Network#Stream#Listener#<i>domain</i>#<i>port</i>#<i>host_name</i><br>
 * Network#Stream#Listener#<i>domain</i>#<i>port</i>#<i>host_address</i><br>
 * Network#Stream#Listener#<i>domain</i>#<i>port</i>
 * </p>
 * <p>
 * Network#Stream#Peer#<i>domain</i>#<i>port</i>#<i>host_name</i><br>
 * Network#Stream#Peer#<i>domain</i>#<i>port</i>#<i>host_address</i><br>
 * Network#Stream#Peer#<i>domain</i>#<i>port</i>
 * </p>
 * <table cellspacing=10>
 * <tr valign=top>
 * <td><i>domain</i></td>
 * <td>Case-sensitive name or "@" for the global domain.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>port</i></td>
 * <td>IP port.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>host_name</i></td>
 * <td>Name of the host.  The system will attempt to resolve the host name to
 * an IPv4 or IPv6 address, depending on the system configuration.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>host_address</i></td>
 * <td>IPv4 or IPv6 address of the host.  For Listener synapses, the IP address
 * accepting connections.  For Peer synapses, the IP address to contact.  Addresses
 * have an optional "4/" or "6/" prefix to denote IPv4 or IPv6 respectively.</td>
 * </tr>
 * </table>
 * </ul>
 * </p>
 * <p>
 * These synapses operate in two modes: Listener and Peer.  Listener mode waits
 * for Peers to join.  When a Peer joins the Listener, a new Peer synapse is
 * created to communicate with the requester.
 * </p>
 * <p>
 * For Listener synapses, you may omit the IP address, specify "*" or specify
 * "4/*" to use IPv4 INADDR_ANY.  You may specify "6/*" to use IPv6 INADDR_ANY.
 * </p>
 * <p>
 * For Peer synapses, you may omit the IP address, specify "*" or specify
 * "4/*" to use the first valid local IPv4 address.  You may specify "6/*" to
 * use the first valid local IPv6 address.
 * </p>
 * <h3>Network Unicast Synapses</h3>
 * <p>
 * Network Unicast synapses use UDP/IP.  They are specified as follows:
 * <ul>
 * <p>
 * Network#Unicast#Listener#<i>domain</i>#<i>port</i>#<i>host_name</i><br>
 * Network#Unicast#Listener#<i>domain</i>#<i>port</i>#<i>host_address</i><br>
 * Network#Unicast#Listener#<i>domain</i>#<i>port</i>
 * </p>
 * <p>
 * Network#Unicast#Peer#<i>domain</i>#<i>port</i>#<i>host_name</i><br>
 * Network#Unicast#Peer#<i>domain</i>#<i>port</i>#<i>host_address</i><br>
 * Network#Unicast#Peer#<i>domain</i>#<i>port</i>
 * </p>
 * <table cellspacing=10>
 * <tr valign=top>
 * <td><i>domain</i></td>
 * <td>Case-sensitive name or "@" for the global domain.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>port</i></td>
 * <td>IP port.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>host_name</i></td>
 * <td>Name of the host.  The system will attempt to resolve the host name to
 * an IPv4 or IPv6 address, depending on the system configuration.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>host_address</i></td>
 * <td>IPv4 or IPv6 address of the host.  For Listener synapses, the IP address
 * accepting connections.  For Peer synapses, the IP address to contact.  Addresses
 * have an optional "4/" or "6/" prefix to denote IPv4 or IPv6 respectively.</td>
 * </tr>
 * </table>
 * </ul>
 * </p>
 * <p>
 * These synapses operate in two modes: Listener and Peer.  Listener mode waits
 * for Peers to join.  When a Peer joins the Listener, a new Peer synapse is
 * created to communicate with the requester.
 * </p>
 * <p>
 * For Listener synapses, you may omit the IP address, specify "*" or specify
 * "4/*" to use IPv4 INADDR_ANY.  You may specify "6/*" to use IPv6 INADDR_ANY.
 * </p>
 * <p>
 * For Peer synapses, you may omit the IP address, specify "*" or specify
 * "4/*" to use the first valid local IPv4 address.  You may specify "6/*" to
 * use the first valid local IPv6 address.
 * </p>
 * <h3>Network Multicast Synapses</h3>
 * <p>
 * Network Multicast synapses use Multicast UDP/IP.  They are specified as follows:
 * <ul>
 * <p>
 * Network#Multicast#Peer#<i>domain</i>#<i>port</i>#<i>multicast_group</i><br>
 * </p>
 * <table cellspacing=10>
 * <tr valign=top>
 * <td><i>domain</i></td>
 * <td>Case-sensitive name or "@" for the global domain.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>port</i></td>
 * <td>IP port for the multicast group.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>multicast_group</i></td>
 * <td>IPv4 or IPv6 address of the multicast group.
 * </tr>
 * </table>
 * </ul>
 * </p>
 * <h3>Local Stream Synapses</h3>
 * <p>
 * Local Stream synapses use Unix-domain streams.  They are specified as follows:
 * <ul>
 * <p>
 * Local#Stream#Listener#<i>domain</i>#<i>filesystem_path</i>
 * </p>
 * <p>
 * Local#Stream#Peer#<i>domain</i>#<i>filesystem_path</i>
 * </p>
 * <table cellspacing=10>
 * <tr valign=top>
 * <td><i>domain</i></td>
 * <td>Case-sensitive name or "@" for the global domain.</td>
 * </tr>
 * <tr valign=top>
 * <td><i>filesystem_path</i></td>
 * <td>File system path for the Unix-domain socket.  For Listener synapses,
 * the file system path accepting connections.  For Peer synapses, the
 * file system path to contact.</td>
 * </tr>
 * </table>
 * </ul>
 * <p>
 * These synapses operate in two modes: Listener and Peer.  Listener mode waits
 * for Peers to join.  When a Peer joins the Listener, a new Peer synapse is
 * created to communicate with the requester.
 * </p>
 * </ul>
 * <h2>Activators</h2>
 * <p>
 * {@link Activator}s consume and produce stimuli.  NeuPaths developers will
 * create custom activators with specialized logic.  Think of an activator
 * as a black box with inputs and outputs.  The inputs are specified as a
 * set of receptors, while the outputs are specified as a set of transmitters.
 * Subscriptions pull stimuli from producing cells to the receptors.  An
 * activator evaluates stimuli once all of its receptors contain stimuli.
 * </p>
 * <h2>Receptors</h2>
 * <p>
 * Receptors are named, type-safe receptacles for stimuli.  They consume stimuli
 * that satisfy a subscription.  See {@link ReceptorSpec} for details.
 * </p>
 * <h2>Transmitters</h2>
 * <p>
 * Transmitters are named, type-safe emitters of stimuli.  They forward stimuli
 * to interested synapses.  See {@link TransmitterSpec} for details.
 * </p>
 * <h2>Subscriptions</h2>
 * <p>
 * Subscriptions express interest in stimuli that are emitted by transmitters.
 * A subscription specifies the producing cell (C), producing transmitter (T) and
 * consuming receptor (R).  In essence, a subscription is a contract:
 * (C, T) =&gt; (R).  This contract implies that the transmitter and receptor
 * stimulus types match.  The producing cell and producing transmitter names
 * may specify regular expressions, making it possible to aggregate stimuli
 * from multiple cells.  A subscription also speficies the domain in which the
 * stimuli will be transported.  The global domain indicates that stimuli should
 * pass to all domains.
 * </p>
 * <p>
 * Five of the primitive cell types use subscriptions:
 * </p>
 * <ul>
 * <li>
 * {@link BridgeCell}s - A bridge subscription (see
 * {@link BridgeSubscriptionSpec}) pulls stimuli to the bridge cell from a
 * particular domain.  Upon arrival, the stimuli can be forwarded to other domains
 * according to subscriptions the bridge cell has learned.  A bridge cell does not
 * evaluate stimuli and therefore does not use receptors.
 * </li>
 * <li>
 * {@link ExtractorCell}s - An extractor subscription (see
 * {@link ExtractorSubscriptionSpec}) pulls stimuli to the extractor cell, which
 * is essentially a receptor used to extract data from a cell system (hence the
 * omission of a <i>Receptor Name</i>.)
 * </li>
 * <li>
 * {@link LogicCell}s - Logic subscriptions pull stimuli to a logic cell for
 * evaluation.  Refer to {@link LogicSubscriptionSpec},
 * {@link LogicLoopbackSubscriptionSpec} and
 * {@link LogicMapSubscriptionSpec}.
 * </li>
 * <li>
 * {@link LoadBalancedCell}s - Logic subscriptions pull stimuli to a load-balanced
 * cell for evaluation by {@link LoadBalancedActivator}s.  Refer to {@link LogicSubscriptionSpec},
 * {@link LogicLoopbackSubscriptionSpec} and
 * {@link LogicMapSubscriptionSpec}.
 * </li>
 * <li>
 * {@link LoadControllerCell}s - Logic subscriptions are used to intercept stimuli
 * for corresponding {@link LoadBalancedActivator}s.  The controller determines
 * which {@link LoadBalancedCell} will process the next set of stimuli.  Refer to
 * {@link LogicSubscriptionSpec}, {@link LogicLoopbackSubscriptionSpec} and
 * {@link LogicMapSubscriptionSpec}.
 * </li>
 * </ul>
 * <h2>Domains</h2>
 * <p>
 * Cells can be logically grouped together by named domains.  Synapses and
 * subscriptions are tagged with a domain name, and stimuli will only be
 * transported over synapses with a domain that matches the subscription
 * domain.  The one exception to this rule is that stimuli matching
 * subscriptions in the global domain will be transported over any
 * applicable synapse.  As cells can have any number of synapses, and
 * each synapse has a domain, a cell can partipate in multiple domains.
 * </p>
 */
package neupaths.api;
