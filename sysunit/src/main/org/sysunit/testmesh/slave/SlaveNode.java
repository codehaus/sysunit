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

    private ClassLoader cl;

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

    public void stop()
        throws InterruptedException
    {
        //System.err.println( "SlaveNode::stop()" );
        super.stop();
        //System.err.println( "SlaveNode::stop() complete" );
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

        ClassLoader cl = initializeClassLoader( classpathServerPort,
                                                relativeUrls );

        Thread.currentThread().setContextClassLoader( cl );

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

            this.tbeanThreads[ i ].setContextClassLoader( this.cl );

            this.tbeanThreads[ i ].start();
        }
    }

    void performSetUp()
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performSetUp();
        }

        waitForThreads();
    }

    void performRun()
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performRun();
        }

        waitForThreads();
    }

    void performAssertValid()
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performAssertValid();
        }

        waitForThreads();
    }

    void performTearDown()
        throws InterruptedException
    {
        this.completed = 0;

        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performTearDown();
        }

        waitForThreads();
    }

    void performStop()
        throws InterruptedException
    {
        for ( int i = 0 ; i < this.tbeanThreads.length ; ++i )
        {
            this.tbeanThreads[ i ].performStop();
        }
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

        StringBuffer classpath = new StringBuffer();

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            if ( i > 0 )
            {
                classpath.append( "|" );
            }

            classpath.append( urls[i].toExternalForm() );
        }

        System.setProperty( "sysunit.classpath",
                            classpath.toString() );

        this.cl = new ClasspathClassLoader( urls );

        return cl;
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
        if ( thread.getThrown() != null )
        {
            try
            {
                waitFor( executeOn( getMasterNodeInfo(),
                                    new RunThrewCommand( getJvmId(),
                                                         thread.getName(),
                                                         thread.getThrown() ) ) );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            this.synchronizer.setError();
        }

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

        ++this.unblockSequence;
        this.synchronizer.unblock();
        notifyAll();
    }

    public synchronized void notifyFullyBlocked(Synchronizer synchronizer)
    {
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
        this.synchronizer.setError();
    }

    void destroyJvm()
    {
        System.exit( 0 );
    }

    public synchronized void notifyInconsistent(Synchronizer synchronizer)
    {

    }
}
