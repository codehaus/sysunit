package org.sysunit.mesh;

import java.io.Serializable;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public interface NodeInfo
{
    String getName();

    /*
    void execute(Command command)
        throws Exception;
    */

    void reportError(int uid,
                     Throwable thrown)
        throws Exception;

    void reportCompletion(int uid)
        throws Exception;
}
