package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.mesh.transport.TransportException;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class PhysicalMachineDaemon
    extends AbstractDaemon {

    public PhysicalMachineDaemon(Transport transport) {
        super( transport );
    }

    public PhysicalMachineDaemon(File transportConfig)
        throws IOException, TransportException {
        super( transportConfig );
    }

    public PhysicalMachineDaemon(InputStream transportConfigIn)
        throws IOException, TransportException {
        super( transportConfigIn );
    }

    public void main(String[] args)
        throws Exception {
    }
}
