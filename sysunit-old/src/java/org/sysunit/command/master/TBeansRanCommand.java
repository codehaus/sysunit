package org.sysunit.command.master;

public class TBeansRanCommand
    extends RunState.Command {

    private String testServerName;

    public TBeansRanCommand(String testServerName) {
        this.testServerName = testServerName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public void run(RunState state)
        throws Exception {
        state.tbeansRan( getTestServerName() );
    }
}
