package org.sysunit.testmesh.slave;

import org.sysunit.model.JvmInfo;

public class InitializeJvmCommand
    extends SlaveCommand
{
    private int classpathServerPort;
    private String[] relativeUrls;
    private JvmInfo jvmInfo;

    public InitializeJvmCommand(int classpathServerPort,
                                String[] relativeUrls,
                                JvmInfo jvmInfo)
    {
        this.classpathServerPort = classpathServerPort;
        this.relativeUrls        = relativeUrls;
        this.jvmInfo             = jvmInfo;
    }

    public int getClasspathServerPort()
    {
        return this.classpathServerPort;
    }

    public String[] getRelativeUrls()
    {
        return this.relativeUrls;
    }

    public JvmInfo getJvmInfo()
    {
        return this.jvmInfo;
    }

    public void execute(SlaveNode node)
        throws Exception
    {
        node.initializeJvm( getClasspathServerPort(),
                            getRelativeUrls(),
                            getJvmInfo() );
    }
}
