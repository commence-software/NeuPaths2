// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import java.util.Date;
import java.util.UUID;
import neupaths.api.Stimulus;

public class WorldHelloSalutation extends Stimulus
{  
  public WorldHelloSalutation (String cityName, String salutation)
  {
    super(TYPE_NAME, TYPE_ID);
    
    this.cityName = cityName;
    this.salutation = salutation;
    date = new Date();
  }

  @Override
  public String toString()
  {
    return "\"" + salutation + "\" from " + cityName + " on " + date;
  }

  public String cityName;
  public String salutation;
  public Date   date;
  
  public static final String TYPE_NAME = "WorldHelloSalutation";
  public static final UUID TYPE_ID = UUID.fromString("8c55f6e4-942f-4e3b-a822-2562425c1a94");

  static final long serialVersionUID = -2731912013491025319L;
}
