package org.sysunit.command.master;

public class SyncCommand
    extends MasterCommand {

    private String tbeanId;
    private String syncPointName;

    public SyncCommand(String tbeanId,
                       String syncPointName) {
        this.tbeanId = tbeanId;
        this.syncPointName = syncPointName;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public String getSyncPointName() {
        return this.syncPointName;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.sync( this );
    }
}
