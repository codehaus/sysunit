package org.sysunit.command.master;

public class SyncCommand
    extends RunState.Command {

    private String testServerName;
    private String tbeanId;
    private String syncPointName;

    public SyncCommand(String testServerName,
                       String tbeanId,
                       String syncPointName) {
        this.testServerName = testServerName;
        this.tbeanId = tbeanId;
        this.syncPointName = syncPointName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public String getSyncPointName() {
        return this.syncPointName;
    }

    public void run(RunState state)
        throws Exception {
        state.sync( getTestServerName(),
                    getTBeanId(),
                    getSyncPointName() );
    }
}
