// Required Notice: Copyright (c) 2024 Commence Software LLC
// Required Notice: This software is licensed under PolyForm Shield License 1.0.0 (https://polyformproject.org/licenses/shield/1.0.0)
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0
package neupaths.util;

import java.io.*;
import java.util.UUID;

/**
 * Utility for creating a new stimulus type.
 * <p>
 * Generates a Java source file for a class derived from {@link neupaths.api.Stimulus}
 * to be used as a new stimulus type.  A unique {@code TYPE_ID} is generated
 * for the new class.
 * </p>
 * <ul>
 * <pre>{@code usage: java -classpath neupaths.jar neupaths.util.GenerateStimulusType <stimulusTypeName>}</pre>
 * <p>
 * <i>stimulusTypeName</i>: The name of the class that is generated in a
 * file named <i>stimulusTypeName</i>.java.
 * </p>
 * </ul>
 * <p>
 * It is advisable to generate a serialVersionUID for the new stimulus type using the
 * JDK {@code serialver} tool.  Refer to {@link java.io.Serializable} for a discussion
 * on serialization/de-serialization.
 * </p>
 * 
 * @author Aaron Caraveo
 */
public class GenerateStimulusType
{
  private GenerateStimulusType ()
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
      System.out.println("usage: java -classpath neupaths.jar neupaths.util.GenerateStimulusType <stimulusTypeName>");
      System.exit(1);
    }

    System.out.println("Generating source code for " + args[0] + " ...");

    try
    {
      File sourceFile = new File(args[0] + ".java");
      PrintWriter sourceOutput = new PrintWriter(new FileWriter(sourceFile));

      sourceOutput.println("package your_package_name;");
      sourceOutput.println("import java.util.UUID;");
      sourceOutput.println("import neupaths.api.Stimulus;\n");
  
      sourceOutput.println("public final class " + args[0] + " extends Stimulus");
      sourceOutput.println("{");
      sourceOutput.println("  // Constructor.  Add parameters as necessary to initialize members.");
      sourceOutput.println("  public " + args[0] + " ()");
      sourceOutput.println("  {");
      sourceOutput.println("    super(TYPE_NAME, TYPE_ID);");
      sourceOutput.println("    // Initialize members here");
      sourceOutput.println("  }\n");
      sourceOutput.println("  // Constructor to be used for creating aliases of this type.");
      sourceOutput.println("  // Add parameers as necessary to initialize members.");
      sourceOutput.println("  protected " + args[0] + " (String typeName)");
      sourceOutput.println("  {");
      sourceOutput.println("    super(typeName, TYPE_ID);");
      sourceOutput.println("    // Initialize members here");
      sourceOutput.println("  }\n");
      sourceOutput.println("  public String toString()");
      sourceOutput.println("  {");
      sourceOutput.println("    return TYPE_NAME;");
      sourceOutput.println("  }\n");
      sourceOutput.println("  // Add stimulus members here");
      sourceOutput.println("  public static final String TYPE_NAME = \"" + args[0] + "\";");
      sourceOutput.println("  public static final UUID TYPE_ID = UUID.fromString(\"" + UUID.randomUUID() + "\");");
      sourceOutput.println("}");

      sourceOutput.flush();
      sourceOutput.close();
    }
    catch (IOException ioe)
    {
      System.out.println("error: could not generate file: " + ioe);
    }

   System.out.println("done.");
  }
}
