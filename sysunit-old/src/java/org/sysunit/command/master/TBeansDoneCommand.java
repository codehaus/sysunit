package org.sysunit.command.master;

public class TBeansDoneCommand
    extends MasterCommand {

    private String testServerName;
    private Throwable[] errors;

    public TBeansDoneCommand(String testServerName,
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

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.tbeansDone( getTestServerName(),
                                 getErrors() );
    }
}
