package org.sysunit.testmesh.slavehost;

import org.sysunit.model.JvmInfo;
import org.sysunit.model.PhysicalMachineInfo;
import org.sysunit.mesh.NodeInfo;
import org.sysunit.testmesh.PingPongNode;
import org.sysunit.testmesh.master.JvmErrorCommand;
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
    private Thread mcastPingPongThread;
    private Thread bcastPingPongThread;

    private SlaveHostConfiguration config;

    private Map jvms;
    private Map masters;

    public SlaveHostNode(String name,
                         SlaveHostConfiguration config)
    {
        super( name );
        this.config  = config;
        this.jvms    = new HashMap();
        this.masters = new HashMap();
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
        this.mcastPingPongThread.interrupt();
        this.bcastPingPongThread.interrupt();

        super.stop();

        synchronized ( this )
        {
            for ( Iterator jvmIter = this.jvms.keySet().iterator();
                  jvmIter.hasNext() ; )
            {
                Thread jvmThread = (Thread) this.jvms.get( jvmIter.next() );

                jvmThread.interrupt();
            }
        }
    }

    void startSlave(String jdk,
                    int jvmId,
                    InetAddress masterAddress,
                    int masterPort,
                    NodeInfo master)
        throws Exception
    {
        File javaHome = getConfiguration().getJavaHome( jdk );

        final JvmExecutor jvm = new JvmExecutor( javaHome,
                                                 SlaveMain.class.getName(),
                                                 new String[] { "" + jvmId,
                                                                masterAddress.getHostAddress(),
                                                                "" + masterPort },
                                                 this );
        
        
        Thread jvmThread = new Thread( jvm );

        synchronized ( this )
        {
            this.jvms.put( jvm,
                           jvmThread );
            this.masters.put( jvm,
                              master );
        }

        jvmThread.start();
    }

    public synchronized void notifyJvmFinished(JvmExecutor jvm,
                                               int exitValue)
    {
        this.jvms.remove( jvm );

        if ( exitValue != 0 )
        {
            NodeInfo master = (NodeInfo) this.masters.get( jvm );
            try
            {
                System.err.println( "JVM ERROR" );
                executeOn( master,
                           new JvmErrorCommand() );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.masters.remove( jvm );
    }

    public synchronized void notifyJvmInterrupted(JvmExecutor jvm)
    {
        this.jvms.remove( jvm );
        this.masters.remove( jvm );
    }

    public synchronized void notifyJvmException(JvmExecutor jvm,
                                                Exception e)
    {
        System.err.println( "JVM EXCEPTION" );
        this.jvms.remove( jvm );
        this.masters.remove( jvm );
    }
}
