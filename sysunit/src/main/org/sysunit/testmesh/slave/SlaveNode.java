package org.sysunit.testmesh.slave;

import org.sysunit.TBean;
import org.sysunit.SynchronizableTBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.mesh.NetworkedNode;
import org.sysunit.mesh.RemoteNodeInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.model.TBeanInfo;
import org.sysunit.testmesh.master.AddSlaveCommand;
import org.sysunit.testmesh.master.SetUpThrewCommand;
import org.sysunit.testmesh.master.RunThrewCommand;
import org.sysunit.testmesh.master.AssertValidThrewCommand;
import org.sysunit.testmesh.master.TearDownThrewCommand;
import org.sysunit.testmesh.master.NotifyFullyBlockedCommand;
import org.sysunit.util.PropUtils;
import org.sysunit.util.TBeanThread;
import org.sysunit.util.TBeanThreadCallback;
import org.sysunit.sync.Synchronizer;
import org.sysunit.sync.SynchronizerCallback;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

public class SlaveNode
    extends NetworkedNode
    implements TBeanThreadCallback, SynchronizerCallback
{
    private static final TBean[] EMPTY_TBEAN_ARRAY = new TBean[0];

    private int jvmId;
    private RemoteNodeInfo masterNodeInfo;

    private TBeanThread[] tbeanThreads;

    private Synchronizer synchronizer;
    private int completed;
    private int unblockSequence;

    private JvmInfo jvmInfo;

    public SlaveNode(int jvmId,
                     InetAddress masterAddress,
                     int masterPort)
    {
        super( "slave" );

        this.jvmId = jvmId;
        this.masterNodeInfo = new RemoteNodeInfo( this,
                                                  "master",
                                                  masterAddress,
                                                  masterPort );
    }

    public RemoteNodeInfo getMasterNodeInfo()
    {
        return this.masterNodeInfo;
    }

    public int getJvmId()
    {
        return this.jvmId;
    }

    void initializeJvm(int classpathServerPort,
                       String[] relativeUrls,
                       JvmInfo jvmInfo)
        throws Exception
    {
        this.jvmInfo = jvmInfo;

        System.err.println( "initialize JVM for " + jvmInfo.getName() );

        ClassLoader cl = initializeClassLoader( classpathServerPort,
                                                relativeUrls );

        TBean[] tbeans = initializeTBeans( cl,
                                           jvmInfo );

        int numSync = 0;

        for ( int i = 0 ; i < tbeans.length ; ++i )
        {
            if ( tbeans[ i ] instanceof SynchronizableTBean )
            {
                ++numSync;
            }
        }

        this.synchronizer = new Synchronizer( numSync,
                                              this );

        for ( int i = 0 ; i < tbeans.length ; ++i )
        {
            if ( tbeans[ i ] instanceof SynchronizableTBean )
            {
                ((SynchronizableTBean)tbeans[i]).setSynchronizer( new TBeanSynchronizer( this.synchronizer ) );
            }
        }

        this.tbeanThreads = new TBeanThread[ tbeans.length ];

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ] = new TBeanThread( tbeans[ i ],
                                                      this );
            this.tbeanThreads[ i ].start();
        }
    }

    void performSetUp()
        throws InterruptedException
    {
        System.err.println( "setup() " + this.jvmInfo.getName() );
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performSetUp();
        }

        waitForThreads();
        System.err.println( "setup() complete " + this.jvmInfo.getName() );
    }

    void performRun()
        throws InterruptedException
    {
        System.err.println( "run() " + this.jvmInfo.getName() );
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performRun();
        }

        waitForThreads();

        System.err.println( "run() complete " + this.jvmInfo.getName() );
    }

    void performAssertValid()
        throws InterruptedException
    {
        System.err.println( "assertValid() " + this.jvmInfo.getName() );
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performAssertValid();
        }

        waitForThreads();
        System.err.println( "assertValid() done " + this.jvmInfo.getName() );
    }

    void performTearDown()
        throws InterruptedException
    {
        System.err.println( "tearDown() " + this.jvmInfo.getName() );
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performTearDown();
        }

        waitForThreads();
        System.err.println( "tearDown() complete " + this.jvmInfo.getName() );
    }

    ClassLoader initializeClassLoader(int port,
                                      String[] relativeUrls)
        throws MalformedURLException
    {
        String host = getMasterNodeInfo().getAddress().getHostName();

        URL[] urls = new URL[ relativeUrls.length ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = new URL( "http://" + host + ":" + port + relativeUrls[ i ] );
        }

        return new ClasspathClassLoader( urls );
    }

    TBean[] initializeTBeans(ClassLoader cl,
                             JvmInfo jvmInfo)
        throws Exception
    {
        List tbeans = new ArrayList();

        TBeanInfo[] tbeanInfos = jvmInfo.getTBeans();

        for ( int i = 0 ; i < tbeanInfos.length ; ++i )
        {
            for ( int j = 0 ; j < tbeanInfos[ i ].getCount() ; ++j )
            {
                tbeans.add( initializeTBean( cl,
                                             tbeanInfos[ i ] ) );
            }
        }

        return (TBean[]) tbeans.toArray( EMPTY_TBEAN_ARRAY );
    }

    TBean initializeTBean(ClassLoader cl,
                          TBeanInfo tbeanInfo)
        throws Exception
    {
        Class tbeanClass = cl.loadClass( tbeanInfo.getClassName() );

        TBean tbean = (TBean) tbeanClass.newInstance();

        Properties props = tbeanInfo.getProperties();

        PropUtils.setProperties( tbean,
                                 props );

        return tbean;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    
    public synchronized void notifySetUp(TBeanThread thread)
    {
        if ( thread.getThrown() != null )
        {
            try
            {
                waitFor( executeOn( getMasterNodeInfo(),
                                    new SetUpThrewCommand( getJvmId(),
                                                           thread.getName(),
                                                           thread.getThrown() ) ) );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        ++this.completed;
        notifyAll();
    }

    public synchronized void notifyRun(TBeanThread thread)
    {
        System.err.println( "notifyRun(" + thread + ") " + this.jvmInfo.getName() );

        if ( thread.getThrown() != null )
        {
            try
            {
                System.err.println( "notifyRun(" + thread + ") " + this.jvmInfo.getName() + " sending error to master" );
                waitFor( executeOn( getMasterNodeInfo(),
                                    new RunThrewCommand( getJvmId(),
                                                         thread.getName(),
                                                         thread.getThrown() ) ) );
                System.err.println( "notifyRun(" + thread + ") " + this.jvmInfo.getName() + " sent error to master" );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            this.synchronizer.setError();
        }

        System.err.println( "notifyRun(" + thread + ") " + this.jvmInfo.getName() + " notify all" );

        this.synchronizer.reduceNumThreads();
        ++this.completed;
        notifyAll();
    }

    public synchronized void notifyAssertValid(TBeanThread thread)
    {
        if ( thread.getThrown() != null )
        {
            try
            {
                waitFor( executeOn( getMasterNodeInfo(),
                                    new AssertValidThrewCommand( getJvmId(),
                                                                 thread.getName(),
                                                                 thread.getThrown() ) ) );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            this.synchronizer.setError();
        }
        ++this.completed;
        notifyAll();
    }

    public synchronized void notifyTearDown(TBeanThread thread)
    {
        if ( thread.getThrown() != null )
        {
            try
            {
                waitFor( executeOn( getMasterNodeInfo(),
                                    new TearDownThrewCommand( getJvmId(),
                                                              thread.getName(),
                                                              thread.getThrown() ) ) );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            this.synchronizer.setError();
        }

        ++this.completed;
        notifyAll();
    }

    synchronized void waitForThreads()
        throws InterruptedException
    {
        while ( this.completed != this.tbeanThreads.length )
        {
            wait();
        }
    }

    synchronized void unblockSynchronizer(int unblockSequence)
        throws Exception
    {
        while ( this.unblockSequence != unblockSequence )
        {
            wait();
        }

        System.err.println( "slave unblocked" );
        ++this.unblockSequence;
        this.synchronizer.unblock();
        notifyAll();
    }

    public synchronized void notifyFullyBlocked(Synchronizer synchronizer)
    {
        System.err.println( "notifyFullyBlocked() " + this.jvmInfo.getName() );
        try
        {
            executeOn( getMasterNodeInfo(),
                       new NotifyFullyBlockedCommand( getJvmId() ) );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void abortTest()
    {
        System.err.println( "abortTest() " + this.jvmInfo.getName() );
        this.synchronizer.setError();
    }

    public synchronized void notifyInconsistent(Synchronizer synchronizer)
    {

    }
}
