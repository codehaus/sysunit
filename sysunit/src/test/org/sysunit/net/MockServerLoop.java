package org.sysunit.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class MockServerLoop
    implements ServerLoop
{
    private int numAccepted;

    public MockServerLoop()
    {
        this.numAccepted = 0;
    }

    public void accept(InetAddress remoteAddress,
                       int port,
                       InputStream in,
                       OutputStream out)
        throws Exception
    {
        ++this.numAccepted;
    }

    public int getNumAccepted()
    {
        return this.numAccepted;
    }
}
