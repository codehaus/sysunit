package org.sysunit.mesh;

public interface MeshNode {
    void execute(NodeCommand command)
        throws Throwable;
}
