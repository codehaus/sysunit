package org.sysunit.testmesh.slavehost;

import org.sysunit.model.JvmInfo;
import org.sysunit.model.PhysicalMachineInfo;
import org.sysunit.mesh.NodeInfo;
import org.sysunit.mesh.RemoteNodeInfo;
import org.sysunit.testmesh.PingPongNode;
import org.sysunit.testmesh.master.JvmErrorCommand;
import org.sysunit.testmesh.master.ReportOutputsCommand;
import org.sysunit.testmesh.slave.SlaveMain;
import org.sysunit.util.JvmExecutor;
import org.sysunit.util.JvmExecutorCallback;

import java.io.File;
import java.net.InetAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class SlaveHostNode
    extends PingPongNode
    implements JvmExecutorCallback
{
    private static final JvmInfo[] EMPTY_JVMINFO_ARRAY = new JvmInfo[0];

    private Thread mcastPingPongThread;
    private Thread bcastPingPongThread;

    private SlaveHostConfiguration config;

    private JvmManager jvmManager;

    public SlaveHostNode(String name,
                         SlaveHostConfiguration config)
    {
        super( name );
        this.config  = config;
        this.jvmManager = new JvmManager( this );
    }

    public SlaveHostNode(SlaveHostConfiguration config)
    {
        this( "slave-host",
              config );
    }

    public PhysicalMachineInfo getPhysicalMachineInfo()
    {
        return this.config.getPhysicalMachineInfo();
    }

    public SlaveHostConfiguration getConfiguration()
    {
        return this.config;
    }

    public synchronized void start()
        throws Exception
    {
        super.start();

        this.mcastPingPongThread = new PingPongThread( this,
                                                       getPingAddress() );
        this.mcastPingPongThread.start();

        this.bcastPingPongThread = new PingPongThread( this,
                                                       null );
        this.bcastPingPongThread.start();
    }

    public synchronized void stop()
        throws InterruptedException
    {
        //System.err.println( "SlaveHostNode::stop()" );
        this.mcastPingPongThread.interrupt();
        this.bcastPingPongThread.interrupt();

        super.stop();

        this.jvmManager.destroyAll();

        /*
        synchronized ( this )
        {
            for ( Iterator jvmIter = this.jvms.keySet().iterator();
                  jvmIter.hasNext() ; )
            {
                Thread jvmThread = (Thread) this.jvms.get( jvmIter.next() );

                jvmThread.interrupt();
            }
        }
        */
        //System.err.println( "SlaveHostNode::stop() complete" );
    }

    void startSlave(int jvmId,
                    String jdk,
                    NodeInfo master)
        throws Exception
    {
        File javaHome = getConfiguration().getJavaHome( jdk );

        this.jvmManager.startJvm( jvmId,
                                  javaHome,
                                  (RemoteNodeInfo) master );
    }

    void killSlave(int jvmId)
    {
        
    }

    void collectOutputs(NodeInfo master)
        throws Exception
    {
        executeOn( master,
                   new ReportOutputsCommand( this.jvmManager.getOutputs( master ) ) );
    }

    public synchronized void notifyJvmFinished(JvmExecutor jvm,
                                               int exitValue)
    {
        if ( exitValue != 0 )
        {
            NodeInfo master = this.jvmManager.getMaster( jvm );
            try
            {
                executeOn( master,
                           new JvmErrorCommand() );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void notifyJvmInterrupted(JvmExecutor jvm)
    {
    }

    public synchronized void notifyJvmException(JvmExecutor jvm,
                                                Exception e)
    {
    }
}
