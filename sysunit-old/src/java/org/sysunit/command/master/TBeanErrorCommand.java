package org.sysunit.command.master;

public class TBeanErrorCommand
    extends RunState.Command {

    private String testServerName;
    private String tbeanId;

    public TBeanErrorCommand(String testServerName,
                             String tbeanId) {
        this.testServerName = testServerName;
        this.tbeanId        = tbeanId;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public void run(RunState state)
        throws Exception {
        state.tbeanError( getTestServerName(),
                          getTBeanId() );
    }
}
