package org.sysunit.maven;

import org.sysunit.SysUnitTestBase;

import java.io.File;

public class ClassGeneratorTest
    extends SysUnitTestBase
{
    public void testUnixPath()
    {
        File file = new File( "/usr/local/bin/foo" );

        assertEquals( "escaped file is /usr/local/bin/foo",
                      "/usr/local/bin/foo",
                      ClassGenerator.getPath( file ) );
    }

    public void testWindowsPath()
    {
        File file = new File( "c:\\usr\\local\\bin\\foo" );

        assertEquals( "escaped file is c:\\\\usr\\\\local\\\\bin\\\\foo",
                      "c:\\\\usr\\\\local\\\\bin\\\\foo",
                      ClassGenerator.getPath( file ) );
    }
}
