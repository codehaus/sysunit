package org.sysunit.mesh;

public class ReportExceptionCommand
    extends Command
{
    private int reportingUid;
    private Exception exception;

    public ReportExceptionCommand(int reportingUid,
                                  Exception exception)
    {
        this.reportingUid       = reportingUid;
        this.exception = exception;
    }

    public int getReportingUid()
    {
        return this.reportingUid;
    }

    public Exception getException()
    {
        return this.exception;
    }

    public void execute(Node node)
    {
        node.reportException( getReportingUid(),
                              getException() );
    }
}
