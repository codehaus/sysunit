package org.sysunit.mesh;

public class LogicalMachineInitializedResponse
    extends MeshManagerCommand {

    private String id;

    public LogicalMachineInitializedResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void execute(MeshManager meshManager)
        throws Exception {
        meshManager.logicalMachineInitialized( getId() );
    }
}
