// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.util.UUID;
import java.util.Date;

/**
 * Stimulus type for NeuPaths log events.
 * 
 * @author Aaron Caraveo
 */
public final class EventStimulus extends Stimulus
{
  EventStimulus
    (EventType type,
     String    source,
     String    details)
  {
    super(TYPE_NAME, TYPE_ID);
    
    this.timestamp = new Date();
    this.type = type;
    this.source = source;
    this.details = details;
  }

  EventStimulus
    (EventType type,
     String    source,
     String    details,
     int       size)
  {
    super(TYPE_NAME, TYPE_ID);
    
    this.timestamp = new Date();
    this.type = type;
    this.source = source;

    if (details != null && details.length() > size)
      this.details = details.substring(0, size - 1) + "...";
    else
      this.details = details;
  }

  /**
   * Returns the date/time at which the event was logged.
   * 
   * @return The event date/time.
   */
  public
  Date
  getTimestamp ()
  {
    return timestamp;
  }
  
  /**
   * Returns the event type.
   * 
   * @return The event type.
   */
  public
  EventType
  getType ()
  {
    return type;
  }

  /**
   * Returns the source of the event.
   * 
   * @return The name of the entity that logged the event.
   */
  public
  String
  getSource ()
  {
    return source;
  }
  
  /**
   * Returns the event details.
   * 
   * @return The event details.
   */
  public
  String
  getDetails ()
  {
    return details;
  }

  @Override
  public
  String
  toString ()
  {
    String timestampStr =
        String.format("%tD %tT.%tN",
                      timestamp,
                      timestamp,
                      timestamp);
    
    String image = "\nType:   " + type +
                   "\nTime:   " + timestampStr +
                   "\nSource: " + source +
                   "\nDetails:\n" + details;

    return image;
  }

  private Date timestamp;
  private EventType type;
  private String source;
  private String details;

  /**
   * The stimulus type name.  Used in event recordings.
   */
  static final String TYPE_NAME = "EventStimulus";
  
  /**
   * The stimulus type ID.  Used in receptor and transmitter specifications.
   */
  static final UUID TYPE_ID = UUID.fromString("dcf39afe-5498-48d1-920f-8954122936e2");

  static final long serialVersionUID = 4083495567141928266L;
}
