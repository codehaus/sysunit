package org.sysunit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SysUnit
{

    public static Process execJava(Class mainClass,
                                   String[] args)
        throws IOException
    {
        return Java.execJava( mainClass,
                              args );
    }

    public static Process execJava(String className,
                                   String[] args)
        throws IOException
    {
        return Java.execJava( className,
                              args );
    }

    public static Process execJava(File javaHome,
                                   String className,
                                   String[] args)
        throws IOException
    {
        return Java.execJava( javaHome,
                              className,
                              args );
    }

    public static File getResourceAsFile(String resourcePath)
        throws IOException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if ( cl == null )
        {
            cl = SysUnit.class.getClassLoader();
        }

        InputStream in = cl.getResourceAsStream( resourcePath );

        File resourceFile = File.createTempFile( "sysunit",
                                                 "tmp" );

        resourceFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream( resourceFile );

        byte[] buf = new byte[1024];

        int len = 0;

        try
        {
            while ( ( len = in.read( buf,
                                     0,
                                     buf.length ) ) >= 0 )
            {
                out.write( buf,
                           0,
                           len );
            }
        }
        finally
        {
            out.close();
        }

        return resourceFile;
    }
}
