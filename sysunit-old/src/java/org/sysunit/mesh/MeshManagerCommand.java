package org.sysunit.mesh;

public abstract class MeshManagerCommand
    implements NodeCommand {

    public void execute(MeshNode node)
        throws Exception {
        execute( (MeshManager) node );
    }

    public abstract void execute(MeshManager meshManager)
        throws Exception;
}
