package org.sysunit.mesh;

public class ReportCompletionCommand
    extends Command
{
    private int reportingUid;

    ReportCompletionCommand(int reportingUid)
    {
        this.reportingUid = reportingUid;
    }

    public int getReportingUid()
    {
        return this.reportingUid;
    }

    public void execute(Node node)
        throws Exception
    {
        node.reportCompletion( getReportingUid() );
    }
}
