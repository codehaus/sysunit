package org.sysunit.command.slave;

public class StoreJarCommand
    extends SlaveCommand {

    private String jarName;
    private byte[] bytes;
    private String masterID;

    public StoreJarCommand(String jarName,
                           byte[] bytes,
                           String masterID) {
        this.jarName = jarName;
        this.bytes = bytes;
        this.masterID = masterID;
    }

    public String getMasterID() {
        return this.masterID;
    }

    public String getJarName() {
        return this.jarName;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void run(SlaveServer server)
        throws Exception {
        System.err.println( "STORE JAR: " + getJarName() + " // " + this );
        server.storeJar( getMasterID(),
                         getJarName(),
                         getBytes() );
    }
}
