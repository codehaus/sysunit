package org.sysunit.command.master;

import org.sysunit.command.slave.StoreJarCommand;

public class RequestJarCommand
    extends MasterCommand {

    private String jarName;
    private String path;
    private String grist;

    public RequestJarCommand(String jarName,
                             String path,
                             String grist) { 
        this.jarName = jarName;
        this.path = path;
        this.grist = grist;
    }

    public String getJarName() {
        return this.jarName;
    }

    public String getPath() {
        return this.path;
    }

    public void run(MasterServer server)
        throws Exception {
        byte[] bytes = server.requestJar( getJarName(),
                                          getPath().replace('@', '\\' ) );

        System.err.println( "FETCH JAR: " + getJarName() + " // " + this + " // " + this.grist);

        getReplyDispatcher().dispatch( new StoreJarCommand( getJarName(),
                                                            bytes,
                                                            server.getName() ) );
    }
}
