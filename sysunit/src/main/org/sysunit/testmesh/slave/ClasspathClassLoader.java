package org.sysunit.testmesh.slave;

import java.net.URL;
import java.net.URLClassLoader;

public class ClasspathClassLoader
    extends URLClassLoader
{
    public ClasspathClassLoader(URL[] urls)
    {
        super( urls );
    }

    public Class loadClass(String name)
        throws ClassNotFoundException
    {
        return loadClass( name,
                          true );
    }
}

