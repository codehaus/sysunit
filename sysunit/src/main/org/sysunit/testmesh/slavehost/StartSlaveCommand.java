package org.sysunit.testmesh.slavehost;

import org.sysunit.mesh.RemoteNodeInfo;
import org.sysunit.model.JvmInfo;

import java.net.InetAddress;

public class StartSlaveCommand
    extends SlaveHostCommand
{
    private String jdk;
    private int jvmId;

    public StartSlaveCommand(String jdk,
                             int jvmId)
    {
        this.jdk   = jdk;
        this.jvmId = jvmId;
    }

    public String getJdk()
    {
        return this.jdk;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    public void execute(SlaveHostNode node)
        throws Exception
    {
        node.startSlave( getJvmId(),
                         getJdk(),
                         getOrigin() );
    }
}
