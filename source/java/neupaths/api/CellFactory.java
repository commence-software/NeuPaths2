// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Creates cells according to cell definition files.
 * <p>
 * A cell definition file contains an XML schema for one of the NeuPaths cell
 * types: {@link BridgeCell}, {@link EventCell}, {@link ExtractorCell},
 * {@link InjectorCell}, {@link LogicCell} or {@link RouterCell}.  The
 * {@link #createCell} method instantiates a NeuPaths cell according to the
 * definition, returning a reference to the specific NeuPaths cell type.
 * For example, a cell definition file that specifies a Logic cell would be
 * used as follows:
 * </p>
 * <ul>
 * {@code LogicCell myCell = CellFactory.createCell(myCellDefinition);}
 * </ul>
 * <p>
 * If the cell type is unknown at runtime, a less specific instantiation
 * would be:
 * </p>
 * <ul>
 * {@code Cell myCell = CellFactory.createCell(myCellDefinition);}
 * </ul>
 * <h2>Bridge Cells</h2>
 * <p>
 * A {@link BridgeCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <bridge_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <subscriptions>}
 * {@literal         <subscription>}
 * {@literal             <cell_name></cell_name>}
 * {@literal             <transmitter_name></transmitter_name>}
 * {@literal             <domain></domain>}
 * {@literal         </subscription>}
 * {@literal         ...}
 * {@literal     </subscriptions>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </bridge_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Notes</b>:
 * <ol>
 * <li>Subscriptions are optional.  When no subscriptions are provided, the
 * cell operates as a {@link RouterCell}.</li>
 * <li>Only Bridge subscriptions are accepted (see {@link BridgeSubscriptionSpec}).
 * In this case, the {@literal <subscription>} element is an alias for
 * {@literal <bridge_subscription>}.</li>
 * </ol>
 * </p>
 * <h2>Event Cells</h2>
 * <p>
 * An {@link EventCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <event_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapse></synapse>}
 * {@literal     <output_file></output_file>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </event_cell>}</pre>
 * </ul>
 * </p>
 * <h2>Extractor Cells</h2>
 * <p>
 * An {@link ExtractorCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <extractor_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <subscription>}
 * {@literal         <cell_name></cell_name>}
 * {@literal         <transmitter_name></transmitter_name>}
 * {@literal         <domain></domain>}
 * {@literal         <filter_transactions>enabled|disabled</filter_transactions>}
 * {@literal     </subscription>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </extractor_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Note</b>: Only an Extractor subscription is accepted (see {@link ExtractorSubscriptionSpec}).
 * In this case, the {@literal <subscription>} element is an alias for
 * {@literal <extractor_subscription>}.
 * </p>
 * <h2>Injector Cells</h2>
 * <p>
 * An {@link InjectorCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <injector_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <transmitter>}
 * {@literal         <name></name>}
 * {@literal         <stimulus_class></stimulus_class>}
 * {@literal         <trace>enabled|disabled</trace>}
 * {@literal     </transmitter>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </injector_cell>}</pre>
 * </ul>
 * </p>
 * <h2>Load-Balanced Cells</h2>
 * <p>
 * A {@link LoadBalancedCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <load_balanced_cell>}
 * {@literal     <name></name>}
 * {@literal     <properties>}
 * {@literal         <property>}
 * {@literal             <name></name>}
 * {@literal             <value></value>}
 * {@literal         </property>}
 * {@literal         ...}
 * {@literal     </properties>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <activators>}
 * {@literal         <activator>}
 * {@literal             <class></class>}
 * {@literal             <transaction_history_window_ms>n</transaction_history_window_ms>}
 * {@literal         </activator>}
 * {@literal         ...}
 * {@literal     </activators>}
 * {@literal     <controller_name></controller_name>}
 * {@literal     <controller_domain></controller_domain>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <pulse_interval_ms>n</pulse_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal <load_balanced_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Note</b>: Cell properties are optional.
 * </p>
 * <h2>Load-Controller Cells</h2>
 * <p>
 * A {@link LoadControllerCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <load_controller_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <receptors>}
 * {@literal         <receptor>}\
 * {@literal             <name></name>}
 * {@literal             <mode>buffered|non_buffered</mode>}
 * {@literal             <stimulus_class></stimulus_class>}
 * {@literal         </receptor>}
 * {@literal         ...}
 * {@literal     </receptors>}
 * {@literal     <subscriptions>}
 * {@literal         <subscription>}
 * {@literal             <cell_name></cell_name>}
 * {@literal             <transmitter_name></transmitter_name>}
 * {@literal             <receptor_name></receptor_name>}
 * {@literal             <domain></domain>}
 * {@literal         </subscription>}
 * {@literal         <logic_subscription>}
 * {@literal             <cell_name></cell_name>}
 * {@literal             <transmitter_name></transmitter_name>}
 * {@literal             <receptor_name></receptor_name>}
 * {@literal             <domain></domain>}
 * {@literal         </logic_subscription>}
 * {@literal         <loopback_subscription>}
 * {@literal             <transmitter_name></transmitter_name>}
 * {@literal             <receptor_name></receptor_name>}
 * {@literal         </loopback_subscription>}
 * {@literal         <map_subscription>}
 * {@literal             <cell_name></cell_name>}
 * {@literal             <transmitter_name></transmitter_name>}
 * {@literal             <domain></domain>}
 * {@literal         </map_subscription>}
 * {@literal         ...}
 * {@literal     </subscriptions>}
 * {@literal     <cell_name_prefix></cell_name_prefix>}
 * {@literal     <cell_domain></cell_domain>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal <load_controller_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Note</b>: Only Logic-based subscriptions are accepted (see {@link LogicSubscriptionSpec}).
 * In this case, the {@literal <subscription>} element is an alias for
 * {@literal <logic_subscription>}.  Loopback ({@literal <loopback_subscription>}) and
 * Map ({@literal <map_subscription>}) subscriptions are also accepted.
 * </p>
 * <h2>Logic Cells</h2>
 * <p>
 * A {@link LogicCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <logic_cell>}
 * {@literal     <name></name>}
 * {@literal     <properties>}
 * {@literal         <property>}
 * {@literal             <name></name>}
 * {@literal             <value></value>}
 * {@literal         </property>}
 * {@literal         ...}
 * {@literal     </properties>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <activators>}
 * {@literal         <activator>}
 * {@literal             <class></class>}
 * {@literal             <transaction_history_window_ms>n</transaction_history_window_ms>}
 * {@literal         </activator>}
 * {@literal         ...}
 * {@literal     </activators>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <pulse_interval_ms>n</pulse_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </logic_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Note</b>: Cell properties are optional.
 * </p>
 * <h2>Router Cells</h2>
 * <p>
 * A {@link RouterCell} is specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <router_cell>}
 * {@literal     <name></name>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <crypto_key_file></crypto_key_file>}
 * {@literal </router_cell>}</pre>
 * </ul>
 * </p>
 * <h2>Specialized Cells</h2>
 * <p>
 * A cell derived from {@link LogicCell} or {@link LoadBalancedCell} is
 * specified as follows:
 * </p>
 * <p>
 * <ul>
 * <pre>
 * {@literal <specialized_cell>}
 * {@literal     <class></class>}
 * {@literal     <name></name>}
 * {@literal     <properties>}
 * {@literal         <property>}
 * {@literal             <name></name>}
 * {@literal             <value></value>}
 * {@literal         </property>}
 * {@literal         ...}
 * {@literal     </properties>}
 * {@literal     <synapses>}
 * {@literal         <synapse></synapse>}
 * {@literal         ...}
 * {@literal     </synapses>}
 * {@literal     <activators>}
 * {@literal         <activator>}
 * {@literal             <class></class>}
 * {@literal             <transaction_history_window_ms>n</transaction_history_window_ms>}
 * {@literal         </activator>}
 * {@literal         ...}
 * {@literal     </activators>}
 * {@literal     <logging_enabled>true|false</logging_enabled>}
 * {@literal     <trace_logging_enabled>true|false</trace_logging_enabled>}
 * {@literal     <debug_logging_enabled>true|false</debug_logging_enabled>}
 * {@literal     <runtime_logging_enabled>true|false</runtime_logging_enabled>}
 * {@literal     <propagate_global_subscriptions>true|false</propagate_global_subscriptions>}
 * {@literal     <subscription_refresh_interval_ms>n</subscription_refresh_interval_ms>}
 * {@literal     <duplicate_detection_interval_ms>n</duplicate_detection_interval_ms>}
 * {@literal     <subscription_trace_interval_ms>n</subscription_trace_interval_ms>}
 * {@literal     <pulse_interval_ms>n</pulse_interval_ms>}
 * {@literal     <crypto_key_file>pathname</crypto_key_file>}
 * {@literal </specialized_cell>}</pre>
 * </ul>
 * </p>
 * <p>
 * <b>Notes</b>:
 * <ul>
 * <li>Cell properties are optional.</li>
 * <li>The custom cell class must have a constructor like the following:
 * <p>
 * <pre> public class MyCustomCell extends LogicCell
 * {
 *   public MyCustomCell (String      name,
 *                        String[]    synapseNames,
 *                        Activator[] activators,
 *                        byte[]      cryptoKey)
 *   {
 *     ...
 *   }
 * }
 * </pre>
 * </p>
 * </li>
 * <li>The custom cell class and constructor must be {@code public}.</li>
 * </ul>
 * </p>
 * <h2>Cell Definition Element Types</h2>
 * <p>
 * <table border=1>
 * <tr align=center valign=bottom>
 * <td><b>Element Name</b></td>
 * <td><b>Description</b></td>
 * <td><b>Element Path</b></td>
 * <td><b>Element Type</b></td>
 * <td><b>Opt?</b></td>
 * <td><b>Default</b></td>
 * <td><b>Example</b></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>cell_name</td>
 * <td>Subscription producer cell name</td>
 * <td>
 * /bridge_cell/subscriptions/subscription/cell_name<br>
 * /extractor_cell/subscription/cell_name<br>
 * /load_controller_cell/subscriptions/subscription/cell_name
 * </td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>My_Cell</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>class</td>
 * <td>Fully-qualified name of specialized cells's class type.</td>
 * <td>/specialized_cell/class</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>myPackage.MyCustomCell</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>class</td>
 * <td>Fully-qualified name of activator's class type.</td>
 * <td>/(logic|load_balanced|specialized)_cell/activators/activator/class</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>myPackage.MyCustomActivator</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>cell_domain</td>
 * <td>Domain name of the load-balanced cells</td>
 * <td>/load_controller_cell/cell_domain</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>LBDomain</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>cell_name_prefix</td>
 * <td>Prefix of load-balanced cell names</td>
 * <td>/load_controller_cell/cell_name_prefix</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>LBCell_</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>controller_domain</td>
 * <td>Domain name of the load-controller cell</td>
 * <td>/load_balanced_cell/controller_domain</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>LBDomain</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>controller_name</td>
 * <td>Name of the load-controller cell</td>
 * <td>/load_balanced_cell/controller_name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>LBController</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>crypto_key_file</td>
 * <td>Pathname of file containing crypto secret key.</td>
 * <td>/*_cell/crypto_key_file</td>
 * <td>String</td>
 * <td>Yes</td>
 * <td>null</td>
 * <td>cfg/stim_key.dat</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>debug_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#DEBUG} events is enabled.</td>
 * <td>/(bridge|extractor|injector|logic|load_balanced|load_controller|route|specialized)_cell/debug_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>domain</td>
 * <td>Subscription domain</td>
 * <td>
 * /bridge_cell/subscriptions/subscription/domain<br>
 * /extractor_cell/subscription/domain<br>
 * /load_controller_cell/subscriptions/subscription/domain
 * </td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>Domain1</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>duplicate_detection_interval_ms</td>
 * <td>Length of duplicate detection window (millisecs).</td>
 * <td>/*_cell/duplicate_detection_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>1000</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>filter_transactions</td>
 * <td>Indicates if duplicate transaction responses should be filtered.</td>
 * <td>/extractor_cell/subscription/filter_transactions</td>
 * <td>TransactionFilter: "enabled" or "disabled"</td>
 * <td>Yes</td>
 * <td>disabled</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>logging_enabled</td>
 * <td>Indicates if cell logging is enabled.</td>
 * <td>(bridge|extractor|injector|logic|load_balanced|load_controller|route|specialized)_cell/logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>true</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>mode</td>
 * <td>Receptor mode</td>
 * <td>/load_controller_cell/receptors/receptor/mode</td>
 * <td>ReceptorMode: "buffered" or "non_buffered"</td>
 * <td>Yes</td>
 * <td>non_buffered</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>name</td>
 * <td>Cell name</td>
 * <td>/*_cell/name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>My_Cell</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>name</td>
 * <td>Name of property</td>
 * <td>/(logic|load_balanced|specialized)_cell/properties/property/name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>Property1</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>name</td>
 * <td>Receptor name</td>
 * <td>/load_controller_cell/receptors/receptor/name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>A_Receptor</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>name</td>
 * <td>Transmitter name</td>
 * <td>/injector_cell/transmitter/name</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>A_Transmitter</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>output_file</td>
 * <td>Pathname of event data output file.</td>
 * <td>/event_cell/output_file</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>/tmp/domain1_events.txt</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>propagate_global_subscriptions</td>
 * <td>Indicates if cell should forward global subscriptions.</td>
 * <td>/*_cell/propagate_global_subscriptions</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>true</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>pulse_interval_ms</td>
 * <td>Interval between pulses (millisecs).</td>
 * <td>/(logic|load_balanced|specialized)_cell/pulse_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>0</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>receptor_name</td>
 * <td>Subscription consumer receptor name</td>
 * <td>
 * /load_controller_cell/subscriptions/subscription/receptor_name<br>
 * /load_controller_cell/subscriptions/logic_subscription/receptor_name<br>
 * /load_controller_cell/subscriptions/loopback_subscription/receptor_name
 * </td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>A_Receptor</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>runtime_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#RUNTIME} events is enabled.</td>
 * <td>/(bridge|extractor|injector|logic|load_balanced|load_controller|route|specialized)_cell/runtime_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>stimulus_class</td>
 * <td>Fully-qualified name of receptors's stimulus type.</td>
 * <td>/load_controller_cell/receptors/receptor/stimulus_class</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>neupaths.api.IntegerStimulus</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>stimulus_class</td>
 * <td>Fully-qualified name of transmitter's stimulus type.</td>
 * <td>/injector_cell/transmitter/stimulus_class</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>neupaths.api.IntegerStimulus</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>subscription_refresh_interval_ms</td>
 * <td>Interval at which cell's subscriptions are refreshed (millisecs).</td>
 * <td>/(bridge|event|extractor|logic|load_balanced|load_controller|specialized)_cell/subscription_refresh_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>1500</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>subscription_trace_interval_ms</td>
 * <td>Interval at which cell's current subscriptions are reported via {@link EventType#TRACE} events (millisecs).</td>
 * <td>/*_cell/subscription_trace_interval_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>0</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>synapse</td>
 * <td>Synapse name specification</td>
 * <td>
 * /(bridge|extractor|injector|logic|load_balanced|load_controller|route|specialized)_cell/synapses/synapse<br>
 * /event_cell/synapse
 * </td>
 * <td>String.  See {@link Cell} for details.</td>
 * <td>No</td>
 * <td></td>
 * <td>Network#Stream#Peer#Domain1#30000</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>trace</td>
 * <td>Indicates if stimulus path trace should be reported via {@link EventType#TRACE} events.</td>
 * <td>/injector_cell/transmitter/trace</td>
 * <td>StimulusTrace: "enabled" or "disabled"</td>
 * <td>Yes</td>
 * <td>enabled</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>trace_logging_enabled</td>
 * <td>Indicates if logging of {@link EventType#TRACE} events is enabled.</td>
 * <td>/(bridge|extractor|injector|logic|load_balanced|load_controller|route|specialized)_cell/trace_logging_enabled</td>
 * <td>Boolean: "true" or "false"</td>
 * <td>Yes</td>
 * <td>false</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>transaction_history_window_ms</td>
 * <td>Length of transaction history window used to detect duplicate transacton responses (millisecs).</td>
 * <td>/(logic|load_balanced|specialized)_cell/activators/activator/transaction_history_window_ms</td>
 * <td>Numeric (long)</td>
 * <td>Yes</td>
 * <td>30000</td>
 * <td></td>
 * </tr>
 * <tr align=left valign=top>
 * <td>transmitter_name</td>
 * <td>Subscription producer transmitter name</td>
 * <td>
 * /(bridge|load_controller)_cell/subscriptions/subscription/transmitter_name<br>
 * /extractor_cell/subscription/transmitter_name
 * </td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>A_Transmitter</td>
 * </tr>
 * <tr align=left valign=top>
 * <td>value</td>
 * <td>Value of property</td>
 * <td>/(logic|load_balanced|specialized)_cell/properties/property/value</td>
 * <td>String</td>
 * <td>No</td>
 * <td></td>
 * <td>property_value</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Aaron Caraveo
 */
public class CellFactory
{
  private CellFactory ()
  {
    // Construction not necessary
  }

  /**
   * Instantiates a NeuPaths cell as specified in the {@code cellDefinitionFile}.
   *
   * @param cellDefinitionFile Pathname of the cell definition file.
   *
   * @return NeuPaths cell object.
   */
  @SuppressWarnings("unchecked")
  public static
  <T extends Cell> T createCell (String cellDefinitionFile)
  {
    return (T)(internalCreateCell(null, cellDefinitionFile));
  }

  /**
   * Instantiates a NeuPaths cluster cell as specified in the
   * {@code cellDefinitionFile}.
   *
   * @param clusterHandler     Cell cluster configuration handler.
   * @param cellDefinitionFile Pathname of the cell definition file.
   *
   * @return NeuPaths cell object.
   */
  @SuppressWarnings("unchecked")
  static
  <T extends Cell> T createCell (Cfg_CellClusterHandler clusterHandler,
                                 String                 cellDefinitionFile)
  {
    return (T)(internalCreateCell(clusterHandler,
                                  cellDefinitionFile));
  }

  private static
  Cell internalCreateCell (Cfg_CellClusterHandler clusterHandler,
                           String                 cellDefinitionFile)
  {
    Cell newCell = null;
    Syn_Name clusterSynapse = null;

    if (clusterHandler != null)
    {
      LinkedList<String> clusterOptions = new LinkedList<>();
      clusterOptions.addLast(clusterHandler.synapseDirectory + File.separator +
                             "cluster_" +
                             Math.abs(clusterHandler.clusterName.hashCode()) +
                             ".syn");
  
      try
      {
        clusterSynapse =
            new Syn_Name(Syn_Scope.LOCAL,
                         Syn_Type.STREAM,
                         Syn_Mode.PEER,
                         "Cluster:Ctrl",
                         clusterOptions);
      }
      catch (Excp_SynapseFatal sfe)
      {
        throw new NeuPathsException("Could not create synapse name: " + sfe);
      }
    }

    try
    {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

      documentBuilder.setErrorHandler(new XMLErrorHandler());
      
      Document document = documentBuilder.parse(cellDefinitionFile);
      
      NodeList nodes = document.getChildNodes();
      
      if (nodes.getLength() == 1)
      {
        Node root = nodes.item(0);

        if (root.getNodeType() == Node.ELEMENT_NODE)
        {
          String nodeName = root.getNodeName().toUpperCase();
          Cfg_CellDefinitionParser parser = null;

          switch (nodeName)
          {
            case "BRIDGE_CELL":
              parser = new Cfg_BridgeCellParser(cellDefinitionFile);
              break;
            case "EVENT_CELL":
              parser = new Cfg_EventCellParser(cellDefinitionFile);
              break;
            case "EXTRACTOR_CELL":
              parser = new Cfg_ExtractorCellParser(cellDefinitionFile);
              break;
            case "INJECTOR_CELL":
              parser = new Cfg_InjectorCellParser(cellDefinitionFile);
              break;
            case "LOAD_BALANCED_CELL":
              parser = new Cfg_LoadBalancedCellParser(cellDefinitionFile);
              break;
            case "LOAD_CONTROLLER_CELL":
              parser = new Cfg_LoadControllerCellParser(cellDefinitionFile);
              break;
            case "LOGIC_CELL":
              parser = new Cfg_LogicCellParser(cellDefinitionFile);
              break;
            case "ROUTER_CELL":
              parser = new Cfg_RouterCellParser(cellDefinitionFile);
              break;
            case "CUSTOM_CELL":
              parser = new Cfg_CustomCellParser(cellDefinitionFile);
              break;
            default:
              throw new NeuPathsException("Cell definition file " +
                                          cellDefinitionFile +
                                          " contains invalid top-level element:" +
                                          nodeName);
          }

          parser.parseNode(root);
          parser.validate();
          newCell = parser.instantiate();
        }
      }
      else
      {
        throw new NeuPathsException("Cell definition file " +
                                    cellDefinitionFile +
                                    " has invalid format");
      }
    }
    catch (ParserConfigurationException pce)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, pce);
    }
    catch (SAXException se)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, se);
    }
    catch (IOException ioe)
    {
      throw new NeuPathsException("Could not parse cell definition file " +
                                  cellDefinitionFile, ioe);
    }

    return newCell;
  }

  private static class XMLErrorHandler implements ErrorHandler
  {
    public void error (SAXParseException exception) throws SAXException
    {
      throw exception;
    }

    public void fatalError (SAXParseException exception) throws SAXException
    {
      throw exception;
    }

    public void warning (SAXParseException exception) throws SAXException
    {
      throw exception;
    }
  }
}
