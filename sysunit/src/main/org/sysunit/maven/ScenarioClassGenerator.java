package org.sysunit.maven;

import java.io.File;

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

    }
}
