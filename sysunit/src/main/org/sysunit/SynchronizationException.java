package org.sysunit;

public class SynchronizationException
    extends SysUnitException
{

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public SynchronizationException() {
        // intentionally left blank
    }

    public SynchronizationException(Throwable rootCause) {
        super( rootCause );
    }
}
