package org.sysunit.command.master;

import org.sysunit.command.State;

public class MasterState
    extends State {

    private MasterServer server;

    public MasterState(MasterServer server) {
        this.server = server;
    }

    public MasterServer getServer() {
        return this.server;
    }
}
