package org.sysunit.mesh.transport;

import org.sysunit.SysUnitException;

public class TransportException
    extends SysUnitException {
    
    public TransportException() {

    }

    public TransportException(Throwable rootCause) {
        super( rootCause );
    }
}
