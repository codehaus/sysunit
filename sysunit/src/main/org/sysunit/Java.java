package org.sysunit;

import org.sysunit.util.InputStreamForwarder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Arrays;

public class Java
{
    public static Process execJava(Class mainClass,
                                   String[] args)
        throws IOException
    {
        return execJava( mainClass.getName(),
                         args );
    }

    public static Process execJava(String className,
                                   String[] args)
        throws IOException
    {
        return execJava( new File( System.getProperty( "java.home" ) ),
                         className,
                         args );
    }

    public static Process execJava(File javaHome,
                                   String className,
                                   String[] args)
        throws IOException
    {
        String[] realArgs = new String[ args.length + 6 ];

        File javaBin = new File ( new File( javaHome,
                                            "bin" ),
                                  "java" );

        realArgs[ 0 ] = javaBin.getPath();
        realArgs[ 1 ] = "-classpath";
        realArgs[ 2 ] = System.getProperty( "java.class.path" );
        realArgs[ 3 ] = Java.class.getName();
        realArgs[ 4 ] = System.getProperty( "sysunit.classpath" );
        realArgs[ 5 ] = className;

        for ( int i = 0 ; i < args.length ; ++i )
        {
            realArgs[ i + 6 ] = args[ i ];
        }

        Process process = Runtime.getRuntime().exec( realArgs );

        InputStreamForwarder.forward( process.getInputStream(),
                                      System.out );

        InputStreamForwarder.forward( process.getErrorStream(),
                                      System.err );

        return process;
    }

    public static void main(String[] args)
        throws Throwable
    {
        String classpath = args[ 0 ];
        String className = args[ 1 ];

        String[] realArgs = new String[ args.length - 2  ];

        for ( int i = 0 ; i < realArgs.length ; ++i )
        {
            realArgs[ i ] = args[ i + 2 ];
        }

        StringTokenizer tokens = new StringTokenizer( classpath,
                                                      "|" );

        List urlStrings = new ArrayList();

        while ( tokens.hasMoreTokens() )
        {
            urlStrings.add( tokens.nextToken() );
        }

        URL[] urls = new URL[ urlStrings.size() ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = new URL( (String) urlStrings.get( i ) );
        }

        ClassLoader cl = new URLClassLoader( urls );

        Class mainClass = cl.loadClass( className );

        Method[] methods = mainClass.getMethods();

        Method mainMethod = null;

        for ( int i = 0 ; i < methods.length ; ++i )
        {
            if ( methods[ i ].getName().equals( "main" ) )
            {
                int mod = methods[ i ].getModifiers();

                if ( Modifier.isPublic( mod )
                     &&
                     Modifier.isStatic( mod ) )
                {
                    Class[] paramTypes = methods[ i ].getParameterTypes();

                    if ( paramTypes.length == 1
                         &&
                         paramTypes[ 0 ] == String[].class )
                    {
                        mainMethod = methods[ i ];
                        break;
                    }
                }
            }
        }

        if ( mainMethod != null )
        {
            try
            {
                Thread.currentThread().setContextClassLoader( cl );
                mainMethod.invoke( mainClass,
                                   new Object[] { realArgs } );
            }
            catch (InvocationTargetException e)
            {
                throw e.getTargetException();
            }
        }
        else
        {
            throw new NoSuchMethodError( "public static main(String[]) not found in " + className );
        }
    }
}
