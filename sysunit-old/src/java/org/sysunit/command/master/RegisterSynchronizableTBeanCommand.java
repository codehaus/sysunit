package org.sysunit.command.master;

public class RegisterSynchronizableTBeanCommand
    extends MasterCommand {

    private String tbeanId;

    public RegisterSynchronizableTBeanCommand(String tbeanId) {
        this.tbeanId = tbeanId;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.registerSynchronizableTBean( getTBeanId() );
    }
}
