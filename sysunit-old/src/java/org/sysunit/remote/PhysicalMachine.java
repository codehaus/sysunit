package org.sysunit.remote;

public class PhysicalMachine {

    public static PhysicalMachine[] EMPTY_ARRAY = new PhysicalMachine[0];

    private String id;

    public PhysicalMachine(String id) {
        this.id = id;
    }

    public void prepare(MasterSession session) {
        // send jars, etc.
    }
    
}
