package org.sysunit.command.test;

public class KillCommand
    extends TestCommand {

    public void run(TestServer server) 
        throws Exception {
        System.err.println( "EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT EXIT ");
        System.exit( 1 );
    }
}
