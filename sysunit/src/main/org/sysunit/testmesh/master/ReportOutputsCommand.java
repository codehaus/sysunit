package org.sysunit.testmesh.master;

import org.sysunit.util.Output;

public class ReportOutputsCommand
    extends MasterCommand
{
    private Output[] outputs;

    public ReportOutputsCommand(Output[] outputs)
    {
        this.outputs = outputs;
    }

    public Output[] getOutputs()
    {
        return this.outputs;
    }

    public void execute(MasterNode node)
        throws Exception
    {
        node.reportOutputs( getOutputs() );
    }
}
