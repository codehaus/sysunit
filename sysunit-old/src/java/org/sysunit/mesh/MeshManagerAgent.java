package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;

public class MeshManagerAgent
    implements Dispatcher {

    private Transport transport;
    private String testId;

    public MeshManagerAgent(Transport transport,
                            String testId) {
        this.transport = transport;
        this.testId    = testId;
    }

    protected Transport getTransport() {
        return this.transport;
    }

    public String getTestId() {
        return this.testId;
    }

    public void dispatch(NodeCommand command)
        throws Exception {
        getTransport().send( getTestId(),
                             command );
                             
    }

    public void sync(String tbeanId,
                     String syncPointId)
        throws InterruptedException {

    }
}
