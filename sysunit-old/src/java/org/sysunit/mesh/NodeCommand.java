package org.sysunit.mesh;

import java.io.Serializable;

public interface NodeCommand
    extends Serializable {
    
    void execute(MeshNode node) 
        throws Throwable;
}
