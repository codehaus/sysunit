package org.sysunit.remote;

import java.util.Set;
import java.util.HashSet;

public class SlaveSession {

    private String sessionId;
    private Set logicalMachines;

    public SlaveSession(String sessionId) {
        this.sessionId = sessionId;
        this.logicalMachines = new HashSet();
    }

    public void addLogicalMachine(LogicalMachine logicalMachine) {
        this.logicalMachines.add( logicalMachine );
    }

    public LogicalMachine[] getLogicalMachines() {
        return (LogicalMachine[]) this.logicalMachines.toArray( LogicalMachine.EMPTY_ARRAY );
    }

    public void start()
        throws InterruptedException {
        startLogicalMachines();
    }

    public void waitFor() 
        throws InterruptedException {
        waitForLogicalMachines();
    }

    public void kill() 
        throws InterruptedException {
        killLogicalMachines();
    };

    protected void startLogicalMachines()
        throws InterruptedException {
        LogicalMachine[] machines = getLogicalMachines();
        
        for ( int i = 0 ; i < machines.length ; ++i ) {
            try {
                machines[i].start();
            } catch (InterruptedException e) {
                for ( int j = 0 ; j < i ; ++j ) {
                    try {
                        machines[j].kill();
                    } catch (InterruptedException ignore) {
                        // swallow
                    }
                }
                throw e;
            }
        }
    }

    protected void waitForLogicalMachines()
        throws InterruptedException {
        LogicalMachine[] machines = getLogicalMachines();

        for ( int i = 0 ; i < machines.length ; ++i ) {
            try {
                machines[i].waitFor();
            } catch (InterruptedException e) {
                for ( int j = i ; j < machines.length ; ++j ) {
                    try {
                        machines[j].kill();
                    } catch (InterruptedException ignore) {
                        // swallow
                    }
                }
                throw e;
            }
        }
    }

    protected void killLogicalMachines()
        throws InterruptedException {
        InterruptedException interrupted = null;

        LogicalMachine[] machines = getLogicalMachines();

        for ( int i = 0 ; i < machines.length ; ++i ) {
            try {
                machines[i].kill();
            } catch (InterruptedException ignore) {
                // swallow
            }
        }

        if ( interrupted != null ) {
            throw interrupted;
        }
    }
}
