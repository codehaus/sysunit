package org.sysunit.remote;

public class LogicalMachine
    implements Runnable {

    public static final LogicalMachine[] EMPTY_ARRAY = new LogicalMachine[0];

    public static final int JVM_KILLED = -42;

    private SlaveSession session;
    private Process jvmProcess;
    private int exitValue;

    private Boolean isRunning;
    private Boolean hasStopped;

    private Thread jvmThread;

    public LogicalMachine(SlaveSession session) {
        this.session = session;

        this.isRunning  = Boolean.FALSE;
        this.hasStopped = Boolean.FALSE;
    }

    public SlaveSession getSession() {
        return this.session;
    }

    public void start()
        throws InterruptedException {
        this.jvmThread = new Thread( this );

        this.jvmThread.start();

        synchronized ( this.isRunning ) {
            while ( ! this.isRunning.booleanValue() ) {
                this.isRunning.wait();
            }
        }
    }

    public void waitFor() 
        throws InterruptedException {
        synchronized ( this.hasStopped ) {
            while ( ! this.hasStopped.booleanValue() ) {
                this.hasStopped.wait();
            }
        }
    }

    public void kill() 
        throws InterruptedException {
        this.jvmThread.interrupt();
        waitFor();
    }

    public int getExitValue() {
        return this.exitValue;
    }

    public void run() {

        synchronized ( this.isRunning ) {
            this.isRunning = Boolean.TRUE;
            this.isRunning.notifyAll();
        }

        this.jvmProcess = null; // Runtime.exec();

        try {
            this.exitValue = this.jvmProcess.waitFor();
        } catch (InterruptedException e) {
            this.exitValue = JVM_KILLED;
            this.hasStopped = Boolean.TRUE;
            this.hasStopped.notifyAll();
        }

        synchronized ( this.hasStopped ) {
            this.hasStopped = Boolean.TRUE;
            this.hasStopped.notifyAll();
        }
    }
}

