package org.sysunit.plan;

import org.sysunit.model.JvmInfo;
import org.sysunit.mesh.NodeInfo;

public class JvmBinding
{
    private int jvmId;
    private JvmInfo jvmInfo;
    private NodeInfo nodeInfo;

    public JvmBinding(int jvmId,
                      JvmInfo jvmInfo,
                      NodeInfo nodeInfo)
    {
        this.jvmId    = jvmId;
        this.jvmInfo  = jvmInfo;
        this.nodeInfo = nodeInfo;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    public JvmInfo getJvmInfo()
    {
        return this.jvmInfo;
    }

    public NodeInfo getNodeInfo()
    {
        return this.nodeInfo;
    }
}
