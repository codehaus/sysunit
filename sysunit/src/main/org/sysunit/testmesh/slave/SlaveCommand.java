package org.sysunit.testmesh.slave;

import org.sysunit.mesh.Command;
import org.sysunit.mesh.Node;

public abstract class SlaveCommand
    extends Command
{
    public void execute(Node node)
        throws Exception
    {
        execute( (SlaveNode) node );
    }

    public abstract void execute(SlaveNode node)
        throws Exception;
}
