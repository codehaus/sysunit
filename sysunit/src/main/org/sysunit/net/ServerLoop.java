package org.sysunit.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public interface ServerLoop
{
    void accept(InetAddress remoteAddress,
                int remotePort,
                InputStream in,
                OutputStream out)
        throws Exception;
}
