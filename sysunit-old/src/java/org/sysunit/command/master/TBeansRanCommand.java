package org.sysunit.command.master;

public class TBeansRanCommand
    extends MasterCommand {

    private String testServerName;

    public TBeansRanCommand(String testServerName) {
        this.testServerName = testServerName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.tbeansRan( getTestServerName() );
    }
}
