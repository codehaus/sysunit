package org.sysunit.testmesh.slavehost;

import org.sysunit.mesh.Command;
import org.sysunit.mesh.Node;

public abstract class SlaveHostCommand
    extends Command
{
    public void execute(Node node)
        throws Exception
    {
        execute( (SlaveHostNode) node );
    }

    public abstract void execute(SlaveHostNode node)
        throws Exception;
}
