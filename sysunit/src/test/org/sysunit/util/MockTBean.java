package org.sysunit.util;

import org.sysunit.TBean;

public class MockTBean
    implements TBean
{
    private boolean didSetUp;
    private boolean didRun;
    private boolean didTearDown;
    private boolean didAssertValid;

    private boolean throwSetUp;
    private boolean throwRun;
    private boolean throwTearDown;
    private boolean throwAssertValid;

    public MockTBean()
    {

    }

    public boolean didSetUp()
    {
        return this.didSetUp;
    }

    public boolean didRun()
    {
        return this.didRun;
    }

    public boolean didTearDown()
    {
        return this.didTearDown;
    }

    public boolean didAssertValid()
    {
        return this.didAssertValid;
    }

    public void setThrowSetUp(boolean shouldThrow)
    {
        this.throwSetUp = shouldThrow;
    }

    public void setThrowRun(boolean shouldThrow)
    {
        this.throwRun = shouldThrow;
    }

    public void setThrowAssertValid(boolean shouldThrow)
    {
        this.throwAssertValid = shouldThrow;
    }

    public void setThrowTearDown(boolean shouldThrow)
    {
        this.throwTearDown = shouldThrow;
    }

    public void setUp()
        throws Exception
    {
        this.didSetUp = true;
        if ( this.throwSetUp )
        {
            throw new Exception( "setUp" );
        }
    }

    public void tearDown()
        throws Exception
    {
        this.didTearDown = true;
        if ( this.throwTearDown )
        {
            throw new Exception ( "tearDown" );
        }
    }

    public void run()
        throws Exception
    {
        this.didRun = true;
        if ( this.throwRun )
        {
            throw new Exception( "run" );
        }
    }

    public void assertValid()
        throws Exception
    {
        this.didAssertValid = true;
        if ( this.throwAssertValid )
        {
            throw new Exception( "assertValid" );
        }
    }
}
