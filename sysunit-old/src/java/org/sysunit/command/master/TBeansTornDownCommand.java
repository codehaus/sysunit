package org.sysunit.command.master;

public class TBeansTornDownCommand
    extends TearDownState.Command {

    private String testServerName;
    private Throwable[] errors;

    public TBeansTornDownCommand(String testServerName,
                                 Throwable[] errors) {
        this.testServerName = testServerName;
        this.errors = errors;
    }

    public String getTestServerName() {
        return this.testServerName;
    }

    public Throwable[] getErrors() {
        return this.errors;
    }

    public void run(TearDownState state)
        throws Exception {
        state.testServerTornDown( getTestServerName(),
                                  getErrors() );
    }
}
