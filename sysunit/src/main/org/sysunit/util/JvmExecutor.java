package org.sysunit.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class JvmExecutor
    implements Runnable {

    private int jvmId;
    private File javaHome;
    private String className;
    private String[] arguments;

    private JvmExecutorCallback callback;

    private Process process;

    private Thread stdoutEaterThread;
    private Thread stderrEaterThread;

    private InputStreamEater stdoutEater;
    private InputStreamEater stderrEater;

    public JvmExecutor(int jvmId,
                       File javaHome,
                       String className,
                       String[] arguments,
                       JvmExecutorCallback callback)
    {
        this.jvmId = jvmId;
        this.javaHome  = javaHome;
        this.className = className;
        this.arguments = arguments;
        this.callback  = callback;
    }

    public int getJvmId()
    {
        return this.jvmId;
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

    public String getStdout()
    {
        return  ( ( this.stdoutEater != null )
                  ? ( this.stdoutEater.getOutput() )
                  : "" );
    }

    public String getStderr()
    {
        return ( ( this.stderrEater != null )
                 ? ( this.stderrEater.getOutput() )
                 : "" );
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

    public void run()
    {
        Runtime runtime = Runtime.getRuntime();

        try
        {
            this.process = runtime.exec( getCommandArray(),
                                         null );

            this.stdoutEater = new InputStreamEater( process.getInputStream() );
            this.stderrEater = new InputStreamEater( process.getErrorStream() );

            this.stdoutEater.setMultiplex( true );
            this.stderrEater.setMultiplex( true );

            this.stdoutEaterThread = new Thread( this.stdoutEater );
            this.stderrEaterThread = new Thread( this.stderrEater );
            
            this.stdoutEaterThread.setDaemon( true );
            this.stderrEaterThread.setDaemon( true );

            this.stdoutEaterThread.start();
            this.stderrEaterThread.start();
            
            this.process.waitFor();

            getCallback().notifyJvmFinished( this,
                                             this.process.exitValue() );
        
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
                this.process.destroy();
                getCallback().notifyJvmInterrupted( this );
            }
        }
    }

    public void destroy()
        throws Exception
    {
        this.process.destroy();
        this.stdoutEaterThread.interrupt();
        this.stderrEaterThread.interrupt();
    }
}
