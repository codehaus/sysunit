package org.sysunit.command.master;

public class TBeansSetUpCommand
    extends SetUpState.Command {
    
    private String testServerName;

    public TBeansSetUpCommand(String testServerName) {
        this.testServerName = testServerName;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public void run(SetUpState state)
        throws Exception {
        state.testServerSetUp( getTestServerName() );
    }
}
