package org.sysunit.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.util.Properties;

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

       Properties props = new Properties();

       FileInputStream in = new FileInputStream( source );

       try
       {
           props.load( in );
       }
       finally
       {
           in.close();
       }

       String sysTestName = props.getProperty( "test" );

       String sysTestPath = sysTestName.replace( '.',
                                                 File.separatorChar ) + ".systest";

       File sysTestFile = new File( getBaseDir(),
                                    sysTestPath );
       
       text.append( "package " ).append( packageName ).append( ";\n" );
       text.append( "\n" );
       text.append( "import junit.framework.TestSuite;\n" );
       text.append( "import org.sysunit.DistributedTestCase;\n" );
       text.append( "import org.sysunit.builder.ScenarioInfoBuilder;\n" );
       text.append( "import org.sysunit.builder.DistributedSystemTestInfoBuilder;\n" );
       text.append( "import org.sysunit.model.ScenarioInfo;\n" );
       text.append( "import org.sysunit.model.DistributedSystemTestInfo;\n" );
       text.append( "import java.io.File;\n" );
       text.append( "\n" );
       text.append( "public class " ).append( className ).append( "\n" );
       text.append( "{\n" );
       text.append( "    public static Object suite() throws Exception\n" );
       text.append( "    {\n" );
       text.append( "        TestSuite suite = new TestSuite();\n");
       text.append( "        DistributedSystemTestInfo testInfo = getSystemTestInfo();\n" );
       text.append( "        ScenarioInfo scenarioInfo = getScenarioInfo( testInfo );\n" );
       text.append( "        suite.addTest( new DistributedTestCase( scenarioInfo ) );\n" );
       text.append( "        return suite;\n");
       text.append( "    }\n" );
       text.append( "\n" );
       text.append( "    public static ScenarioInfo getScenarioInfo(DistributedSystemTestInfo testInfo) throws Exception\n" );
       text.append( "    {\n" );
       text.append( "        return ScenarioInfoBuilder.build( testInfo, new File( \"" + source.getPath() + "\" ) );\n" );
       text.append( "    }\n" );
       text.append( "\n" );
       text.append( "    public static DistributedSystemTestInfo getSystemTestInfo() throws Exception\n" );
       text.append( "    {\n" );
       text.append( "        return DistributedSystemTestInfoBuilder.build( new File( \"" + sysTestFile.getPath() + "\" ) );\n" );
       text.append( "    }\n" );
       text.append( "}\n" );

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
