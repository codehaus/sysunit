package org.sysunit.command.slave;

import java.io.IOException;

public class ProcessRunner
    implements Runnable {

    private String executable;
    private String[] arguments;

    public ProcessRunner(String executable,
                         String[] arguments) {

        this.executable = executable;
        this.arguments  = arguments;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public String[] getCommandArray() {
        String[] commandArray = new String[ this.arguments.length + 1 ];

        commandArray[ 0 ] = getExecutable();

        String[] arguments = getArguments();

        for ( int i = 0 ; i < arguments.length ; ++i ) {
            commandArray[ i + 1 ] = arguments[ i ];
        }

        return commandArray;
    }

    public void run() {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec( getCommandArray() );

            Thread stdoutEater = new Thread( new InputStreamEater( process.getInputStream() ) );
            Thread stderrEater = new Thread( new InputStreamEater( process.getErrorStream() ) );

            stdoutEater.start();
            stderrEater.start();
            
            process.waitFor();

        } catch (IOException e) {
            
        } catch (InterruptedException e) {

        }
    }
}
