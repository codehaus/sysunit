package org.sysunit.command.test;

public class KillCommand
    extends TestCommand {

    public void run(TestServer server) 
        throws Exception {
        server.kill();
    }
}
