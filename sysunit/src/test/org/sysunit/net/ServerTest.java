package org.sysunit.net;

public class ServerTest
    extends NetTestBase
{
    public void testStartStop()
        throws Exception
    {
        MockServerLoop loop = new MockServerLoop();
        
        Server server = new Server( 1,
                                    loop );
        
        server.start();
        server.stop();
    }
}
