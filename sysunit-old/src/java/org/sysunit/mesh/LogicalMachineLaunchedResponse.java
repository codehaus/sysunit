package org.sysunit.mesh;

public class LogicalMachineLaunchedResponse
    extends MeshManagerCommand {

    private String id;

    public LogicalMachineLaunchedResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void execute(MeshManager meshManager)
        throws Exception {
        meshManager.logicalMachineLaunched( getId() );
    }
}
