package org.sysunit.mesh;

public class ReportErrorCommand
    extends Command
{
    private int reportingUid;
    private Throwable thrown;

    public ReportErrorCommand(int reportingUid,
                              Throwable thrown)
    {
        this.reportingUid = reportingUid;
        this.thrown       = thrown;
    }

    public int getReportingUid()
    {
        return this.reportingUid;
    }

    public Throwable getThrown()
    {
        return this.thrown;
    }

    public void execute(Node node)
    {
        node.reportError( getReportingUid(),
                          getThrown() );
    }
}
