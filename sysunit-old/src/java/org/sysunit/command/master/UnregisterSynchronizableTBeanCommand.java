package org.sysunit.command.master;

public class UnregisterSynchronizableTBeanCommand
    extends MasterCommand {

    private String tbeanId;

    public UnregisterSynchronizableTBeanCommand(String tbeanId) {
        this.tbeanId = tbeanId;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.unregisterSynchronizableTBean( getTBeanId() );
    }
}
