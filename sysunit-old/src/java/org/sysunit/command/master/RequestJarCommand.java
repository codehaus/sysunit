package org.sysunit.command.master;

import org.sysunit.command.slave.StoreJarCommand;

public class RequestJarCommand
    extends MasterCommand {

    private String jarName;
    private String path;

    public RequestJarCommand(String jarName,
                             String path) { 
        this.jarName = jarName;
        this.path = path;
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

        getReplyDispatcher().dispatch( new StoreJarCommand( getJarName(),
                                                            bytes,
                                                            server.getName() ) );
    }
}
