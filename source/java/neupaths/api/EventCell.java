// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import neupaths.util.PropertySet;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Processes NeuPaths event stimuli.  This cell will output event data to a
 * {@link java.io.PrintStream} or pass it to a specialization of the
 * {@link EventActivator} class.
 * 
 * @author Aaron Caraveo
 */
public class EventCell extends Cell
{
  /**
   * Creates a new {@code EventCell} object that spools event data to a file.
   * <b>The cell subscribes to event stimuli on the same domain as the synapse</b>.
   * 
   * @param name            The cell's name at runtime.  This name should be
   *                        unique across the entire cell system.
   * @param synapseName     The synapse this cell listens on or connects to.
   * @param outputFileName  The path/name of the output file for event data.
   * @param cryptoKey       The stimulus encryption key.  Specify {@code null}
   *                        to disable encryption using a user-specified
   *                        key.  If disabled, the stimuli will still be
   *                        encrypted as part of NeuPaths protocol encryption.
   */
  public
  EventCell
    (String name,
     String synapseName,
     String outputFileName,
     byte[] cryptoKey)
  {
    super(CellType.EVENT,
          name,
          new PropertySet(),
          synapseName,
          cryptoKey);

    EventActivator activator =
        new InternalEventActivator1(new SynapseSpec(synapseName),
                                    outputFileName);

    addActivator(activator);

    loggingEnabled = new SafeBoolean();  // Event cells can never log
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code EventCell} object that uses a {@link java.io.PrintStream}
   * for output of event data.  <b>The cell subscribes to event stimuli on the same
   * domain as the synapse</b>.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param spooler       The {@link java.io.PrintStream} object that will output
   *                      event data (e.g. {@link java.lang.System#out}).
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  EventCell
    (String      name,
     String      synapseName,
     PrintStream spooler,
     byte[]      cryptoKey)
  {
    super(CellType.EVENT,
          name,
          new PropertySet(),
          synapseName,
          cryptoKey);

    EventActivator activator =
        new InternalEventActivator2(new SynapseSpec(synapseName),
                                    spooler);

    addActivator(activator);

    loggingEnabled = new SafeBoolean();  // Event cells can never log
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code EventCell} object that uses a specialization of
   * the {@link EventActivator} class.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param properties    A dictionary of named properties.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activator     A specialization of the {@link EventActivator}
   *                      class.  It is the user's responsibility to provide
   *                      a valid domain to the activator constructor.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  EventCell
    (String         name,
     PropertySet    properties,
     String         synapseName,
     EventActivator activator,
     byte[]         cryptoKey)
  {
    super(CellType.EVENT,
          name,
          properties,
          synapseName,
          activator,
          cryptoKey);

    loggingEnabled = new SafeBoolean();  // Event cells can never log
  }
  
  //---------------------------------------------------------------------------
  
  /**
   * Creates a new {@code EventCell} object that uses a specialization of
   * the {@link EventActivator} class.
   * 
   * @param name          The cell's name at runtime.  This name should be
   *                      unique across the entire cell system.
   * @param synapseName   The synapse this cell listens on or connects to.
   * @param activator     A specialization of the {@link EventActivator}
   *                      class.  It is the user's responsibility to provide
   *                      a valid domain to the activator constructor.
   * @param cryptoKey     The stimulus encryption key.  Specify {@code null}
   *                      to disable encryption using a user-specified
   *                      key.  If disabled, the stimuli will still be
   *                      encrypted as part of NeuPaths protocol encryption.
   */
  public
  EventCell
    (String         name,
     String         synapseName,
     EventActivator activator,
     byte[]         cryptoKey)
  {
    super(CellType.EVENT,
          name,
          new PropertySet(),
          synapseName,
          activator,
          cryptoKey);

    loggingEnabled = new SafeBoolean();  // Event cells can never log
  }
  
  //===========================================================================
  //  PUBLIC METHODS
  //===========================================================================

  /**
   * Ties off logging for event cells.  Event cells can never log event data.
   */
  @Override
  public final
  void
  enableLogging ()
  {
    // Tied off for Event cells
  }
  
  //===========================================================================
  //  PACKAGE METHODS
  //===========================================================================

  //===========================================================================
  //  PRIVATE MEMBERS
  //===========================================================================

  private static class InternalEventActivator1 extends EventActivator
  {
    public InternalEventActivator1 (SynapseSpec synapseSpec, String outputFileName)
    {
      super(synapseSpec.getDomain());

      try
      {
        spooler = new PrintStream(outputFileName);
      }
      catch (FileNotFoundException fnfe)
      {
        throw new NeuPathsException("Could not open EventCell output file " +
                                       outputFileName + ": " + fnfe.getMessage());
      }
    }
    
    @Override
    protected void processEvent (EventStimulus event)
    {
      spooler.println(event.toString());
    }
    
    private PrintStream spooler;
  }

  private static class InternalEventActivator2 extends EventActivator
  {
    public InternalEventActivator2 (SynapseSpec synapseSpec, PrintStream spooler)
    {
      super(synapseSpec.getDomain());
      this.spooler = spooler;
    }
    
    @Override
    protected void processEvent (EventStimulus event)
    {
      spooler.println(event.toString());
    }
    
    private PrintStream spooler;
  }
}
