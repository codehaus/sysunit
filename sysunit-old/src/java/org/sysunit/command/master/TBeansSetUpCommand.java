package org.sysunit.command.master;

public class TBeansSetUpCommand
    extends MasterCommand {

    private String testServerName;

    public TBeansSetUpCommand(String testServerName) {
        this.testServerName = testServerName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.tbeansSetUp( getTestServerName() );
    }
}
