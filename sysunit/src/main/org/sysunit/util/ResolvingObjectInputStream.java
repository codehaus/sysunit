package org.sysunit.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.IOException;

public class ResolvingObjectInputStream
    extends ObjectInputStream
{
    private ClassLoader classLoader;

    public ResolvingObjectInputStream(InputStream in,
                                      ClassLoader classLoader)
        throws IOException
    {
        super( in );
        this.classLoader = classLoader;
    }

    public Class resolveClass(ObjectStreamClass classDesc)
        throws IOException, ClassNotFoundException
    {
        return this.classLoader.loadClass( classDesc.getName() );
    }
}
