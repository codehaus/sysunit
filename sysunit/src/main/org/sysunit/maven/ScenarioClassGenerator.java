package org.sysunit.maven;

import java.io.File;
import java.io.FileWriter;

public class ScenarioClassGenerator
    extends ClassGenerator
{
    public ScenarioClassGenerator()
    {
        super( "scenario" );
    }

    public void generateClass(String packageName,
                              String className,
                              File source,
                              File destination)
        throws Exception
    {
       StringBuffer text = new StringBuffer();

        text.append( "package " ).append( packageName ).append( ";\n" );
        text.append( "\n" );
        text.append( "import junit.framework.TestSuite;\n" );
        text.append( "import org.sysunit.DistributedTestCase;\n" );
        text.append( "import org.sysunit.builder.ScenarioInfoBuilder;\n" );
        text.append( "import org.sysunit.model.ScenarioInfo;\n" );
        text.append( "import java.io.File;\n" );
        text.append( "\n" );
        text.append( "public class " ).append( className ).append( "\n" );
        text.append( "{\n" );
        text.append( "    public static Object suite() throws Exception\n" );
        text.append( "    {\n" );
        text.append( "        TestSuite suite = new TestSuite();\n");
        text.append( "        ScenarioInfo scenarioInfo = getScenarioInfo();\n" );
        text.append( "        suite.addTest( new DistributedTestCase( scenarioInfo ) );\n" );
        text.append( "        return suite;\n");
        text.append( "    }\n" );
        text.append( "\n" );
        text.append( "    public static ScenarioInfo getScenarioInfo() throws Exception\n" );
        text.append( "    {\n" );
        text.append( "        return ScenarioInfoBuilder.build( new File( \"" + source.getPath() + "\" ) );\n" );
        text.append( "    }\n" );
        text.append( "}" );

        FileWriter out = new FileWriter( destination );

        try
        {
            out.write( text.toString() );
        }
        finally
        {
            out.close();
        }
    }
}
