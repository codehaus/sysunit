package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;

public abstract class AbstractMeshNode
    implements MeshNode {

    private Transport transport;

    public AbstractMeshNode(Transport transport) {
        this.transport = transport;
    }

    protected Transport getTransport() {
        return this.transport;
    }
}
