// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.api;

import java.io.Serializable;
import java.util.UUID;

/**
 * The base class for all NeuPaths messages.
 *
 * @author Aaron Caraveo
 */
class Msg_NeuPaths implements Serializable
{
  Msg_NeuPaths ()
  {
    arrivalSynapseInstanceID = null;
    departureSynapseInstanceID = null;
    arrivalDomain = null;
  }
  
  UUID arrivalSynapseInstanceID;
  UUID departureSynapseInstanceID;
  String arrivalDomain;

  static final long serialVersionUID = 3287618211388331381L;
}
