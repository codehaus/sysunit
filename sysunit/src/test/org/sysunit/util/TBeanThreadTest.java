package org.sysunit.util;

public class TBeanThreadTest
    extends UtilTestBase
    implements TBeanThreadCallback
{
    private TBeanThread thr;
    private String expectedThrown;

    public void setUp()
        throws Exception
    {
        this.expectedThrown = null;
    }

    public void tearDown()
        throws Exception
    {
        Thread.sleep( 500 );
        this.expectedThrown = null;
        this.thr.perfromStop();
        this.thr.join();
    }
    public void testNormal()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );

        thr.performSetUp();
        thr.performRun();
        thr.performAssertValid();
        thr.performTearDown();
    }

    public void testSetUpException()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );

        tbean.setThrowSetUp( true );
        expectedThrowable( "setUp" );

        thr.performSetUp();

        Thread.sleep( 500 );
        expectedNoThrowable();

        thr.performRun();
        thr.performAssertValid();
        thr.performTearDown();
    }

    public void testRunException()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );
        
        thr.performSetUp();
        
        Thread.sleep( 500 );

        tbean.setThrowRun( true );
        expectedThrowable( "run" );
        
        thr.performRun();

        Thread.sleep( 500 );
        expectedNoThrowable();

        thr.performAssertValid();
        thr.performTearDown();
    }

    public void testAssertValidException()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );

        thr.performSetUp();
        thr.performRun();

        Thread.sleep( 500 );
        tbean.setThrowAssertValid( true );
        expectedThrowable( "assertValid" );

        thr.performAssertValid();

        Thread.sleep( 500 );
        expectedNoThrowable();

        thr.performTearDown();
    }

    public void testTearDownException()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );

        thr.performSetUp();
        thr.performRun();
        thr.performAssertValid();

        Thread.sleep( 500 );
        tbean.setThrowTearDown( true );
        expectedThrowable( "tearDown" );

        thr.performTearDown();
    }

    public void testAllException()
        throws Exception
    {
        MockTBean tbean = new MockTBean();
        TBeanThread thr = startTBean( tbean );


        Thread.sleep( 500 );
        tbean.setThrowSetUp( true );
        expectedThrowable( "setUp" );

        thr.performSetUp();

        Thread.sleep( 500 );
        expectedNoThrowable();

        tbean.setThrowRun( true );
        expectedThrowable( "run" );

        thr.performRun();

        Thread.sleep( 500 );
        expectedNoThrowable();

        tbean.setThrowAssertValid( true );
        expectedThrowable( "assertValid" );

        thr.performAssertValid();

        Thread.sleep( 500 );
        expectedNoThrowable();

        tbean.setThrowTearDown( true );
        expectedThrowable( "tearDown" );

        thr.performTearDown();
    }

    TBeanThread startTBean(MockTBean tbean)
        throws InterruptedException
    {
        this.thr = new TBeanThread( tbean,
                                    this );

        this.thr.start();

        Thread.sleep( 500 );

        assertDoneNothing( this.thr );

        return this.thr;
    }

    void expectedThrowable(String message)
    {
        this.expectedThrown = message;
    }

    void expectedNoThrowable()
    {
        this.expectedThrown = null;
    }

    void assertDoneNothing(TBeanThread thread)
    {
        assertFalse( "not setUp()",
                     ((MockTBean)thread.getTBean()).didSetUp() );

        assertFalse( "not run()",
                     ((MockTBean)thread.getTBean()).didRun() );

        assertFalse( "not assertValid()",
                     ((MockTBean)thread.getTBean()).didAssertValid() );

        assertFalse( "not tearDown()",
                     ((MockTBean)thread.getTBean()).didTearDown() );

        assertExpectedThrowables( thread );
    }

    // --

    public void notifySetUp(TBeanThread thread)
    {
        assertTrue( "has setUp()",
                    ((MockTBean)thread.getTBean()).didSetUp() );

        assertFalse( "not run()",
                     ((MockTBean)thread.getTBean()).didRun() );

        assertFalse( "not assertValid()",
                     ((MockTBean)thread.getTBean()).didAssertValid() );

        assertFalse( "not tearDown()",
                     ((MockTBean)thread.getTBean()).didTearDown() );

        assertExpectedThrowables( thread );
    }

    public void notifyRun(TBeanThread thread)
    {
        assertTrue( "has setUp()",
                    ((MockTBean)thread.getTBean()).didSetUp() );

        assertTrue( "has run()",
                    ((MockTBean)thread.getTBean()).didRun() );
        
        assertFalse( "not assertValid()",
                     ((MockTBean)thread.getTBean()).didAssertValid() );
        
        assertFalse( "not tearDown()",
                     ((MockTBean)thread.getTBean()).didTearDown() );

        assertExpectedThrowables( thread );
    }

    public void notifyAssertValid(TBeanThread thread)
    {
        assertTrue( "has setUp()",
                    ((MockTBean)thread.getTBean()).didSetUp() );

        assertTrue( "has run()",
                    ((MockTBean)thread.getTBean()).didRun() );

        assertTrue( "has assertValid()",
                    ((MockTBean)thread.getTBean()).didAssertValid() );
        
        assertFalse( "not tearDown()",
                     ((MockTBean)thread.getTBean()).didTearDown() );

        assertExpectedThrowables( thread );
    }

    public void notifyTearDown(TBeanThread thread)
    {
        assertTrue( "has setUp()",
                    ((MockTBean)thread.getTBean()).didSetUp() );

        assertTrue( "has run()",
                    ((MockTBean)thread.getTBean()).didRun() );
        
        assertTrue( "has assertValid()",
                    ((MockTBean)thread.getTBean()).didAssertValid() );
        
        assertTrue( "has tearDown()",
                    ((MockTBean)thread.getTBean()).didTearDown() );

        assertExpectedThrowables( thread );
    }

    void assertExpectedThrowables(TBeanThread thread)
    {
        Throwable thrown = thread.getThrown();

        if ( this.expectedThrown == null
             &&
             thrown != null )
        {
            fail( "no throw expected, but foudn <" + thrown.getMessage() + ">" );
        }


        if ( this.expectedThrown != null
             &&
             thrown == null )
        {
            fail( "expected <" + this.expectedThrown + "> but none thrown" );
        }

        if ( this.expectedThrown == null
             &&
             thrown == null )
        {
            return;
        }

        if ( this.expectedThrown == thrown.getMessage() )
        {
            return;
        }

        fail( "expected <" + this.expectedThrown + "> but found <" + thrown.getMessage() + ">" );
    }
}
