package org.sysunit;

import junit.framework.Assert;

public abstract class AbstractTBean
    extends Assert
    implements TBean
{
    
    /**
     * @see org.sysunit.TBean
     */
    public void assertValid()
        throws Throwable
    {
        // intentionally left blank
    }

    /**
     * @see org.sysunit.TBean
     */
    public void setUp()
        throws Exception
    {
        // intentionally left blank
    }

    /**
     * @see org.sysunit.TBean
     */
    public void tearDown()
        throws Exception
    {
        // intentionally left blank
    }
}
