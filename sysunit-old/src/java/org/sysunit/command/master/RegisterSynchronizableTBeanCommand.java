package org.sysunit.command.master;

public class RegisterSynchronizableTBeanCommand
    extends RunState.Command {

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

    public void run(RunState state)
        throws Exception {
        state.registerSynchronizableTBean( getTestServerName(),
                                           getTBeanId() );
    }
}
