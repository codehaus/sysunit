package org.sysunit.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class JvmExecutor
    implements Runnable {

    private File javaHome;
    private String className;
    private String[] arguments;

    private JvmExecutorCallback callback;

    public JvmExecutor(File javaHome,
                       String className,
                       String[] arguments,
                       JvmExecutorCallback callback)
    {
        this.javaHome  = javaHome;
        this.className = className;
        this.arguments = arguments;
        this.callback  = callback;
    }

    public File getJavaHome()
    {
        return this.javaHome;
    }

    public String getClassName()
    {
        return this.className;
    }

    public String[] getArguments()
    {
        return this.arguments;
    }

    public JvmExecutorCallback getCallback()
    {
        return this.callback;
    }

    public String getJava()
    {
        File javaHome = getJavaHome();

        if ( javaHome != null )
        {
            return new File( new File ( javaHome,
                                        "bin" ),
                             "java" ).getPath();
        }
        else
        {
            return "java";
        }
    }

    public String[] getCommandArray()
    {
        String[] commandArray = new String[ this.arguments.length + 4 ];

        commandArray[ 0 ] = getJava();
        commandArray[ 1 ] = "-classpath";
        commandArray[ 2 ] = System.getProperty( "java.class.path" );
        commandArray[ 3 ] = getClassName();

        String[] arguments = getArguments();

        for ( int i = 0 ; i < arguments.length ; ++i ) {
            commandArray[ i + 4 ] = arguments[ i ];
        }

        return commandArray;
    }

    public String getJavaHomeEnv()
    {
        return "JAVA_HOME=" + getJavaHome().getPath();
    }

    public String getClasspathEnv()
    {
        return "CLASSPATH=" + System.getProperty( "java.class.path" );
    }

    public String[] getEnvArray()
    {
        File javaHome = getJavaHome();

        String[] envArray = null;
        int i = 0;

        if ( javaHome != null )
        {
            envArray = new String[ 2 ];
            envArray[0] = getJavaHomeEnv();
            envArray[1] = getClasspathEnv();
        }
        else
        {
            envArray = new String[ 1 ];
            envArray[0] = getClasspathEnv();
        }

        System.err.println( "ENVIRONMENT:" );
        System.err.println( Arrays.asList( envArray ) );
        System.err.flush();
        return envArray;
    }

    public void run()
    {
        Runtime runtime = Runtime.getRuntime();

        Process process = null;

        try
        {
            process = runtime.exec( getCommandArray(),
                                    null );

            Thread stdoutEater = new Thread( new InputStreamEater( process.getInputStream() ) );
            Thread stderrEater = new Thread( new InputStreamEater( process.getErrorStream() ) );
            
            stdoutEater.start();
            stderrEater.start();
            
            process.waitFor();

            getCallback().notifyJvmFinished( this,
                                             process.exitValue() );
        
        }
        catch (IOException e)
        {
            getCallback().notifyJvmException( this,
                                              e );
        }
        catch (InterruptedException e)
        {
            if ( process != null )
            {
                process.destroy();
                getCallback().notifyJvmInterrupted( this );
            }
        }
    }
}
