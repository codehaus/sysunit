package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.mesh.transport.TransportFactory;
import org.sysunit.mesh.transport.TransportException;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public abstract class AbstractDaemon {

    private Transport transport;

    public AbstractDaemon(Transport transport) {
        this.transport = transport;
    }

    public AbstractDaemon(File transportConfig)
        throws IOException, TransportException {
        this( TransportFactory.getInstance().initializeTransport( transportConfig ) );
    }

    public AbstractDaemon(InputStream transportConfigIn)
        throws IOException, TransportException {
        this( TransportFactory.getInstance().initializeTransport( transportConfigIn ) );
    }
}
