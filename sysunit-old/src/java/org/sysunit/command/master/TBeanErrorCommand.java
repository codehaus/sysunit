package org.sysunit.command.master;

public class TBeanErrorCommand
    extends MasterCommand {

    private String tbeanId;

    public TBeanErrorCommand(String tbeanId) {
        this.tbeanId = tbeanId;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public void run(MasterServer masterServer)
        throws Exception {
        masterServer.error( getTBeanId() );
    }
}
