package org.sysunit.testmesh.master;

import org.sysunit.mesh.Command;
import org.sysunit.mesh.Node;

public abstract class MasterCommand
    extends Command
{
    public void execute(Node node)
        throws Exception
    {
        execute( (MasterNode) node );
    }

    public abstract void execute(MasterNode node)
        throws Exception;
}
