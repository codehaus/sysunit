package org.sysunit.mesh.transport;

import java.io.Serializable;

public interface Transport {

    void send(String endPoint,
              Serializable object)
        throws Exception;

    String[] locatePhysicalMachines();
}
