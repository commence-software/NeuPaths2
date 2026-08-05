// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
import neupaths.api.*;
import neupaths.stim.*;

public class FibonacciActivator extends PulsedActivator
{
  public FibonacciActivator ()
  {
    super("FibonacciActivator",
          new ReceptorSpec[] {
            new ReceptorSpec("Fn_1",
                             ReceptorMode.NON_BUFFERED,
                             IntegerStimulus.TYPE_ID),
            new ReceptorSpec("Fn_2",
                             ReceptorMode.NON_BUFFERED,
                             IntegerStimulus.TYPE_ID) },
          new TransmitterSpec[] {
            new TransmitterSpec("Fn_1",
                                IntegerStimulus.TYPE_ID),
            new TransmitterSpec("Fn_2",
                                IntegerStimulus.TYPE_ID),
            new TransmitterSpec("Result",
                                IntegerStimulus.TYPE_ID) },
          new LogicSubscriptionSpec[] {
            new LogicLoopbackSubscriptionSpec("Fn_1",
                                              "Fn_1"),
            new LogicLoopbackSubscriptionSpec("Fn_2",
                                              "Fn_2") });
  }

  public void start ()
  {
    setStimulus("Fn_1", new IntegerStimulus(1));
    setStimulus("Fn_2", new IntegerStimulus(0));
  }

  public void evaluate ()
  {
    IntegerStimulus Fn_1 = getStimulus("Fn_1");
    IntegerStimulus Fn_2 = getStimulus("Fn_2");

    int Fn = Fn_1.get() + Fn_2.get();

    setStimulus("Result", new IntegerStimulus(Fn));
    setStimulus("Fn_1", new IntegerStimulus(Fn));
    setStimulus("Fn_2", new IntegerStimulus(Fn_1.get()));
  }
}
