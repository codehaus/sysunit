package org.sysunit.remote;

import java.util.List;
import java.util.ArrayList;

public class MasterNode {

    private MasterSession session;
    private List physicalMachines;

    public MasterNode() {
        this.physicalMachines = new ArrayList();
        this.session = new MasterSession();
    }

    public MasterSession getSession() {
        return this.session;
    }

    public void addPhysicalMachine(PhysicalMachine physicalMachine) {
        this.physicalMachines.add( physicalMachine );
    }

    public PhysicalMachine[] getPhysicalMachines() {
        return (PhysicalMachine[]) this.physicalMachines.toArray( PhysicalMachine.EMPTY_ARRAY );
    }

    protected void preparePhysicalMachines() {
        PhysicalMachine[] machines = getPhysicalMachines();

        for ( int i = 0 ; i < machines.length ; ++i ) {
            machines[i].prepare( getSession() );
        }
    }
}
