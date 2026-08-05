// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

/**
 * Utility for testing synapses and connectivity.
 * <p>
 * This utility can be used to test both sides of a synapse (Listener and Peer).
 * For Network Multicast synapses, which only have peers, the utility can
 * be used to test that multiple peers participate in the multicast group.
 * Peers send "pings" and Listeners respond with "pongs".
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.api.SynapseTestTool <synapseName>}</pre>
 * <p>
 * <i>synapseName</i>: Synapse specification.
 * </p>
 * </ul>
 * <h2>Network Stream Examples</h2>
 * <ul>
 * <h3>IPv4</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Stream#Listener#@#30001}</pre>
 * </p>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Stream#Peer#@#30001#192.168.1.10}</pre>
 * </p>
 * <h3>IPv6</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Stream#Listener#@#30001#6/*}</pre>
 * </p>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Stream#Peer#@#30001#fe80::399e:ac63:b2c4:681c}</pre>
 * </p>
 * </ul>
 * <h2>Network Unicast Examples</h2>
 * <ul>
 * <h3>IPv4</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Unicast#Listener#@#30001}</pre>
 * </p>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Unicast#Peer#@#30001#192.168.1.10}</pre>
 * </p>
 * <h3>IPv6</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Unicast#Listener#@#30001#6/*}</pre>
 * </p>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Unicast#Peer#@#30001#fe80::399e:ac63:b2c4:681c}</pre>
 * </p>
 * </ul>
 * <h2>Network Multicast Example</h2>
 * <ul>
 * <h3>IPv4</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Multicast#Peer#@#30001#224.0.0.10}</pre>
 * </p>
 * <h3>IPv6</h3>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Network#Multicast#Peer#@#30001#ff02::1}</pre>
 * </p>
 * </ul>
 * <h2>Local Stream Examples</h2>
 * <ul>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Local#Stream#Listener#@#/tmp/comm_test}</pre>
 * </p>
 * <p>
 * <pre>{@code java -classpath neupaths.jar neupaths.api.SynapseTestTool Local#Stream#Peer#@#/tmp/comm_test}</pre>
 * </p>
 * </ul>
 *
 * @author Aaron Caraveo
 */
public class SynapseTestTool
{
  private SynapseTestTool ()
  {
    // Construction is not necessary
  }
  
  /**
   * The main routine.  See overview for usage information.
   * 
   * @param args The command line arguments
   */
  public static void main (String[] args)
  {
    if (args.length != 1)
    {
      System.out.println("usage: java -classpath neupaths.jar neupaths.api.SynapseTestTool <synapseName>");
      System.out.println("    synapseName    Synapse specification.");
      System.exit(1);
    }
    
    try
    {
      // Parse the synapse name
      Syn_Name synapseName = new Syn_Name(args[0]);
      
      // Create the synapse address
      Syn_Address synapseAddress = Syn_Factory.createAddress(synapseName);
      
      // Create the synapse
      Syn_Synapse synapse = Syn_Factory.createSynapse(synapseAddress);
      
      if (synapseName.getType() == Syn_Type.STREAM)
      {
        if (synapseName.getMode() == Syn_Mode.LISTENER)
        {
          // Open the synapse (bind to listen address)
          synapse.open(synapseAddress);
          
          // Wait for a connection from a peer
          Syn_Synapse peer = synapse.accept();
          
          // Receive the peer name
          String peerName = (String) peer.receive();
          
          System.out.println("Accepted connection from " + peerName);
          
          while (true)
          {
            // Receive the "ping"
            String msg = (String) peer.receive();
            
            System.out.println(msg);
            
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            // Send the "pong"
            peer.send("Pong from " + peer.getLocalName());
          }
        }
        else
        {
          // Open the synapse
          synapse.open(null);
          
          // Connect to peer
          synapse.connect(synapseAddress);
          
          // Send our name
          synapse.send(synapse.getLocalName());
          
          while (true)
          {
            // Send the "ping"
            synapse.send("Ping from " + synapse.getLocalName());
            
            // Receive the response
            String msg = (String) synapse.receive();
            
            System.out.println(msg);

            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
          }
        }
      }
      else if (synapseName.getType() == Syn_Type.UNICAST)
      {
        if (synapseName.getMode() == Syn_Mode.LISTENER)
        {
          // Open the synapse (bind to listen address)
          synapse.open(synapseAddress);

          // Receive the peer name
          String peerName = (String) synapse.receive();
          
          System.out.println("Accepted connection from " + peerName);
          
          // Create peer synapse
          Syn_Synapse peer = Syn_Factory.createSynapse(peerName);
          
          // Open the peer synapse
          peer.open(null);
          
          // Connect to peer
          peer.connect(Syn_Factory.createAddress(peerName));
          
          // Send our name
          peer.send(peer.getLocalName());
          
          while (true)
          {
            // Receive the "ping"
            String msg = (String) synapse.receive();
            
            System.out.println(msg);
            
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            // Send the "pong"
            peer.send("Pong from " + peer.getLocalName());
          }
        }
        else
        {
          // Open the synapse
          synapse.open(null);
          
          // Connect to peer
          synapse.connect(synapseAddress);
          
          // Create peer synapse
          Syn_Synapse peer = Syn_Factory.createSynapse(synapseAddress);
          
          // Open the peer synapse
          peer.open(null);
          
          // Send our name
          synapse.send(peer.getLocalName());
          
          // Receive the peer name
          String peerName = (String) peer.receive();
          
          System.out.println("Accepted connection from " + peerName);
          
          while (true)
          {
            // Send the "ping"
            synapse.send("Ping from " + synapse.getLocalName());
            
            // Receive the response
            String msg = (String) peer.receive();
            
            System.out.println(msg);

            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
          }
        }
      }
      else // MULTICAST
      {
        // Open the synapse (bind to listen address)
        synapse.open(null);
        
        // Connect to multicast group
        synapse.connect(synapseAddress);
        
        // Create peer synapse
        Syn_Synapse peer = Syn_Factory.createSynapse(synapseAddress);
        
        // Use the synapse type and port from group address to create
        // a listenAddress (i.e. listen on INADDR_ANY for the given port)
        Syn_Address listenAddress =
            Syn_Factory.createAddress(
                Syn_Factory.updateInetNameAddress(synapseAddress.getSynapseName(), "*"));

        // Open the synapse (bind to listen address)
        peer.open(listenAddress);

        // Join the multicast group
        Syn_InetDatagramChannel mcastSynapse =
            (Syn_InetDatagramChannel) peer;
        mcastSynapse.join(synapseAddress, null);
        
        // Send our name
        synapse.send(synapse.getLocalName() + " joined the group");
        
        while (true)
        {
          // Receive the next message
          String msg = (String) peer.receive();
          
          System.out.println(msg);
          
          try { Thread.sleep(2000); } catch (InterruptedException ie) {}
          
          // Send the "ping"
          synapse.send("Ping from " + synapse.getLocalName());
        }
      }
    }
    catch (Excp_NeuPaths be)
    {
      System.out.println("ERROR: Test failed: " + be);
    }
  }
}
