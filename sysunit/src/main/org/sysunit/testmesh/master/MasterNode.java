package org.sysunit.testmesh.master;

import org.sysunit.mesh.NodeInfo;
import org.sysunit.mesh.CommandGroup;
import org.sysunit.mesh.StopCommand;
import org.sysunit.model.PhysicalMachineInfo;
import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.ScenarioInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.plan.TestPlan;
import org.sysunit.plan.TestPlanBuilder;
import org.sysunit.plan.JvmBinding;
import org.sysunit.plan.InfeasibleTestPlanException;
import org.sysunit.testmesh.PingPongNode;
import org.sysunit.testmesh.TestMeshManager;
import org.sysunit.testmesh.slavehost.StartSlaveCommand;
import org.sysunit.testmesh.slavehost.CollectOutputsCommand;
import org.sysunit.testmesh.slave.InitializeJvmCommand;
import org.sysunit.testmesh.slave.PerformCommand;
import org.sysunit.testmesh.slave.PerformSetUpCommand;
import org.sysunit.testmesh.slave.PerformRunCommand;
import org.sysunit.testmesh.slave.PerformAssertValidCommand;
import org.sysunit.testmesh.slave.PerformTearDownCommand;
import org.sysunit.testmesh.slave.PerformStopCommand;
import org.sysunit.testmesh.slave.UnblockSynchronizerCommand;
import org.sysunit.testmesh.slave.AbortTestCommand;
import org.sysunit.testmesh.slave.DestroyJvmCommand;
import org.sysunit.util.ClasspathServer;
import org.sysunit.util.Output;

import junit.framework.AssertionFailedError;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class MasterNode
    extends PingPongNode
{
    private static NodeInfo[] EMPTY_NODEINFO_ARRAY = new NodeInfo[0];
    private static SlaveInfo[] EMPTY_SLAVEINFO_ARRAY = new SlaveInfo[0];

    private TestMeshManager testMeshManager;
    private List slaves;
    private Set slaveHosts;

    private Map jvms;

    private ScenarioInfo scenarioInfo;

    private ClasspathServer classpathServer;

    private List thrown;

    private int numBlocked;

    private int blockSequence;

    private boolean jvmError;

    public MasterNode()
    {
        this( new ScenarioInfo( "none",
                                new DistributedSystemTestInfo( "none" ) ) );
    }

    public MasterNode(DistributedSystemTestInfo systemTestInfo)
    {
        this( new ScenarioInfo( "none",
                                systemTestInfo ) );
    }

    public MasterNode(ScenarioInfo scenarioInfo)
    {
        super( "master" );

        this.testMeshManager = new TestMeshManager();
        this.slaves          = new ArrayList();
        this.slaveHosts      = new HashSet();
        this.jvms            = new HashMap();

        this.scenarioInfo    = scenarioInfo;

        this.classpathServer = new ClasspathServer( 2 );

        this.thrown          = new ArrayList();
    }

    public long getListenTime()
    {
        return 5000;
    }

    public void start()
        throws Exception
    {
        super.start();

        getClasspathServer().start();

        sendPing();

        Thread.sleep( getListenTime() );

        if ( getSlaveHosts().length == 0 )
        {
            disableMulticast();
            sendPing();
            Thread.sleep( getListenTime() );
        }
    }

    public ClasspathServer getClasspathServer()
    {
        return this.classpathServer;
    }

    void sendPing()
        throws Exception
    {
        int port = getPort();

        String message = PING_PREFIX + port;

        byte[] pingBytes = message.getBytes();

        DatagramSocket pingSocket = null;
        DatagramPacket ping = null;

        if ( getPingAddress() != null )
        {
            try
            {
                MulticastSocket mcastSocket = new MulticastSocket( getPingPort() );
                mcastSocket.joinGroup( getPingAddress() );
                
                pingSocket = mcastSocket;
                
                ping = new DatagramPacket( pingBytes,
                                           pingBytes.length,
                                           getPingAddress(),
                                           getPingPort() );
            }
            catch (IOException e)
            {
                // swallow, we'll retry below
            }
        }

        if ( pingSocket == null )
        {
            pingSocket = new DatagramSocket(); 
            
            ping = new DatagramPacket( pingBytes,
                                       pingBytes.length,
                                       InetAddress.getLocalHost(),
                                       getPingPort() + 1 );
        }

        pingSocket.send( ping );
        pingSocket.close();
    }

    public void stop()
        throws InterruptedException
    {
        super.stop();
        getClasspathServer().stop();
    }

    public void addSlaveHost(NodeInfo slaveHost,
                             PhysicalMachineInfo physicalMachineInfo)
    {
        getTestMeshManager().addSlaveHost( slaveHost,
                                           physicalMachineInfo );
    }

    public NodeInfo[] getSlaveHosts()
    {
        return getTestMeshManager().getSlaveHosts();
    }

    public TestMeshManager getTestMeshManager()
    {
        return this.testMeshManager;
    }

    public void addSlave(NodeInfo slave,
                         int jvmId)
    {
        synchronized ( this.slaves )
        {
            this.slaves.add( new SlaveInfo( slave,
                                            jvmId ) );
            
            if ( this.slaves.size() == getSystemTestInfo().getTotalJvms() )
            {
                this.slaves.notifyAll();
            }
        }
    }

    public synchronized SlaveInfo[] getSlaves()
    {
        return (SlaveInfo[]) this.slaves.toArray( EMPTY_SLAVEINFO_ARRAY );
    }
    
    public DistributedSystemTestInfo getSystemTestInfo()
    {
        return getScenarioInfo().getSystemTestInfo();
    }

    public ScenarioInfo getScenarioInfo()
    {
        return this.scenarioInfo;
    }

    public Throwable[] runTest()
        throws Exception
    {
        TestPlanBuilder builder = new TestPlanBuilder( getSystemTestInfo(),
                                                       getScenarioInfo(),
                                                       getTestMeshManager() );

        TestPlan plan = builder.buildTestPlan();

        JvmBinding[] jvmBindings = plan.getJvmBindings();

        for ( int i = 0 ; i < jvmBindings.length ; ++i )
        {
            this.jvms.put( jvmBindings[ i ].getJvmId() + "",
                           jvmBindings[ i ].getJvmInfo() );

            executeOn( jvmBindings[ i ].getNodeInfo(),
                       new StartSlaveCommand( getScenarioInfo().getJdk( jvmBindings[ i ].getJvmInfo() ),
                                              jvmBindings[ i ].getJvmId() ) );

            this.slaveHosts.add( jvmBindings[ i ].getNodeInfo() );
        }

        waitForSlaves();

        if ( ! this.jvmError )
        {
            initializeJvms( plan );

            if ( getFundamentalErrors().length == 0 )
            {
                performSetUp();
                
                if ( ! hasThrown() )
                {
                    performRun();
                    
                    if ( ! hasThrown() )
                    {
                        performAssertValid();
                    }
                }
                
                performTearDown();
            }
        }

        performStop();
        destroyJvms();
        collectOutputs();

        Throwable[] fundamentalErrors = getFundamentalErrors();
            
        Throwable[] thrown = new Throwable[ this.thrown.size() + fundamentalErrors.length ];

        for ( int i = 0 ; i < this.thrown.size() ; ++i )
        {
            ThrowEntry entry = (ThrowEntry) this.thrown.get( i );

            thrown[ i ] = entry.getThrown();

            if ( thrown[ i ] instanceof AssertionFailedError )
            {
                thrown[ i ] = new SlaveAssertionFailedError( entry.getJvmInfo(),
                                                             entry.getTBeanId(),
                                                             (AssertionFailedError) thrown[ i ] );
            }
            else
            {
                thrown[ i ] = new SlaveThrowable( entry.getJvmInfo(),
                                                  entry.getTBeanId(),
                                                  thrown[ i ] );
            }
        }

        for ( int i = 0; i < fundamentalErrors.length ; ++i )
        {
            thrown[ i + this.thrown.size() ] = fundamentalErrors[ i ];
        }

        return thrown;
    }

    void initializeJvms(TestPlan plan)
        throws Exception
    {
        SlaveInfo[] slaves = getSlaves();
        
        CommandGroup commandGroup = newCommandGroup();
        
        for ( int i = 0 ; i < slaves.length ; ++i )
        {
            JvmBinding binding = plan.getJvmBinding( slaves[ i ].getJvmId() );
            
            commandGroup.add( executeOn( slaves[ i ].getNodeInfo(),
                                         new InitializeJvmCommand( getClasspathServer().getPort(),
                                                                   getClasspathServer().getRelativeUrls(),
                                                                   binding.getJvmInfo() ) ) );
        }
        
        commandGroup.waitFor();
    }

    void perform(PerformCommand command)
        throws Exception
    {
        SlaveInfo[] slaves = getSlaves();
        
        CommandGroup commandGroup = newCommandGroup();
        
        for ( int i = 0 ; i < slaves.length ; ++i )
        {
            commandGroup.add( executeOn( slaves[ i ].getNodeInfo(),
                                         command ) );
        }
        
        commandGroup.waitFor();
    }

    void performSetUp()
        throws Exception
    {
        perform( new PerformSetUpCommand() );
    }

    synchronized void setUpThrew(int jvmId,
                                 String tbeanId,
                                 Throwable thrown)
    {
        this.thrown.add( new ThrowEntry( (JvmInfo) this.jvms.get( jvmId + "" ),
                                         tbeanId,
                                         thrown ) );
    }

    void performRun()
        throws Exception
    {
        perform( new PerformRunCommand() );
    }

    synchronized void runThrew(int jvmId,
                               String tbeanId,
                               Throwable thrown)
        throws Exception
    {
        this.thrown.add( new ThrowEntry( (JvmInfo) this.jvms.get( jvmId + "" ),
                                         tbeanId,
                                         thrown ) );
        abortTest();
    }

    void performAssertValid()
        throws Exception
    {
        perform( new PerformAssertValidCommand() );
    }

    synchronized void assertValidThrew(int jvmId,
                                       String tbeanId,
                                       Throwable thrown)
    {
        this.thrown.add( new ThrowEntry( (JvmInfo) this.jvms.get( jvmId + "" ),
                                         tbeanId,
                                         thrown ) );
    }

    void performTearDown()
        throws Exception
    {
        perform( new PerformTearDownCommand() );
    }

    synchronized void tearDownThrew(int jvmId,
                                    String tbeanId,
                                    Throwable thrown)
        throws Exception
    {
        this.thrown.add( new ThrowEntry( (JvmInfo) this.jvms.get( jvmId + "" ),
                                         tbeanId,
                                         thrown ) );
    }

    void performStop()
        throws Exception
    {
        perform( new PerformStopCommand() );

        SlaveInfo[] slaves = getSlaves();
        
        CommandGroup commandGroup = newCommandGroup();

        /*
        StopCommand command = new StopCommand();
        
        for ( int i = 0 ; i < slaves.length ; ++i )
        {
            commandGroup.add( executeOn( slaves[ i ].getNodeInfo(),
                                         command ) );
        }
        
        commandGroup.waitFor();
        */
    }

    boolean hasThrown()
    {
        return ( ! this.thrown.isEmpty() );
    }
    
    void waitForSlaves()
        throws InterruptedException
    {
        synchronized ( this.slaves )
        {
            while ( this.slaves.size() != getSystemTestInfo().getTotalJvms()
                    &&
                    ! this.jvmError )
            {
                this.slaves.wait();
            }
        }
    }

    void abortTest()
        throws Exception
    {
        SlaveInfo[] slaves = getSlaves();

        CommandGroup commandGroup = newCommandGroup();

        AbortTestCommand command = new AbortTestCommand();
        
        for ( int i = 0 ; i < slaves.length ; ++i )
        {
            commandGroup.add( executeOn( slaves[ i ].getNodeInfo(),
                                         command ) );
        }
        
        commandGroup.waitFor();
    }

    synchronized void notifyFullyBlocked(int jvmId)
    {
        ++this.numBlocked;

        if ( this.numBlocked == this.slaves.size() )
        {
            this.numBlocked = 0;

            UnblockSynchronizerCommand unblockCommand = new UnblockSynchronizerCommand( this.blockSequence );

            ++this.blockSequence;

            SlaveInfo[] slaves = getSlaves();

            for ( int i = 0 ; i < slaves.length ; ++i )
            {
                try
                {
                    executeOn( slaves[ i ].getNodeInfo(),
                               unblockCommand );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized void jvmError()
    {
        this.jvmError = true;
        notifyAll();
    }

    void destroyJvms()
        throws Exception
    {
        SlaveInfo[] slaves = getSlaves();
        
        DestroyJvmCommand command = new DestroyJvmCommand();
        
        for ( int i = 0 ; i < slaves.length ; ++i )
        {
            executeOn( slaves[ i ].getNodeInfo(),
                       command  );
        }
    }

    void collectOutputs()
        throws Exception
    {
        NodeInfo[] slaveHosts = (NodeInfo[]) this.slaveHosts.toArray( EMPTY_NODEINFO_ARRAY );
        
        CollectOutputsCommand command = new CollectOutputsCommand();

        CommandGroup commandGroup = newCommandGroup();

        for ( int i = 0 ; i < slaveHosts.length ; ++i )
        {
            commandGroup.add( executeOn( slaveHosts[ i ],
                                         command  ) );
        }

        commandGroup.waitFor();
    }

    void reportOutputs(Output[] outputs)
    {
        synchronized ( System.out )
        {
            for ( int i = 0 ; i < outputs.length ; ++i )
            {
                System.out.println( "----------------------------------------------" );
                System.out.println( "JVM: " + ((JvmInfo)this.jvms.get( outputs[ i ].getJvmId() + "" )).getName() + "(" + outputs[ i ].getJvmId() + ")" );
                System.out.println( "----------------------------------------------" );
                System.out.println( outputs[ i ].getStdout() );
            }
            System.out.println( "----------------------------------------------" );
        }
        synchronized ( System.err )
        {
            for ( int i = 0 ; i < outputs.length ; ++i )
            {
                System.err.println( "----------------------------------------------" );
                System.err.println( "JVM: " + ((JvmInfo)this.jvms.get( outputs[ i ].getJvmId() + "" )).getName() + "(" + outputs[ i ].getJvmId() + ")" );
                System.err.println( "----------------------------------------------" );
                System.err.println( outputs[ i ].getStderr() );
            }
            System.err.println( "----------------------------------------------" );
        }
    }
}
