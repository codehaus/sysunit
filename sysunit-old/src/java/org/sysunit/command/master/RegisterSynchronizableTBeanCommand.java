package org.sysunit.command.master;

public class RegisterSynchronizableTBeanCommand
    extends MasterCommand {

    private String testServerName;
    private String tbeanId;

    public RegisterSynchronizableTBeanCommand(String testServerName,
                                              String tbeanId) {
        this.testServerName = testServerName;
        this.tbeanId = tbeanId;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.registerSynchronizableTBean( this );
    }
}
