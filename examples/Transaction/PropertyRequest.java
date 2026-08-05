// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import java.util.UUID;
import neupaths.api.Stimulus;

public final class PropertyRequest extends Stimulus
{
  // Constructor.  Add parameters as necessary to initialize members.
  public PropertyRequest (String propertyName)
  {
    super(TYPE_NAME, TYPE_ID);
    this.propertyName = propertyName;
  }

  // Constructor to be used for creating aliases of this type.
  // Add parameers as necessary to initialize members.
    protected PropertyRequest (String typeName, String propertyName)
  {
    super(typeName, TYPE_ID);
    this.propertyName = propertyName;
  }

  public String toString()
  {
    return TYPE_NAME;
  }

  String propertyName;

  // Add stimulus members here
  public static final String TYPE_NAME = "PropertyRequestStimulus";
  public static final UUID TYPE_ID = UUID.fromString("0171cf82-f6f3-479e-a797-030488d627c9");
}
