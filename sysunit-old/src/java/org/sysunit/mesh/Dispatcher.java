package org.sysunit.mesh;

public interface Dispatcher {

    void dispatch(NodeCommand command)
        throws Exception;

}
