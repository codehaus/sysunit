package org.sysunit;

public class SysUnitException
    extends Exception
{
    private Throwable rootCause;

    public SysUnitException()
    {

    }

    public SysUnitException(String message)
    {
        super( message );
    }

    public SysUnitException(Throwable rootCause)
    {
        this.rootCause = rootCause;
    }
}
