package org.sysunit.testmesh.master;

import org.sysunit.mesh.NodeInfo;

public class SlaveInfo
{
    private NodeInfo nodeInfo;
    private int jvmId;

    public SlaveInfo(NodeInfo nodeInfo,
                     int jvmId)
    {
        this.nodeInfo = nodeInfo;
        this.jvmId    = jvmId;
    }

    public NodeInfo getNodeInfo()
    {
        return this.nodeInfo;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }
}
