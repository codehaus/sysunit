package org.sysunit.testmesh.slavehost;

import org.sysunit.mesh.NodeInfo;
import org.sysunit.mesh.RemoteNodeInfo;
import org.sysunit.util.JvmExecutor;
import org.sysunit.util.JvmExecutorCallback;
import org.sysunit.util.Output;
import org.sysunit.testmesh.slave.SlaveMain;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

class JvmManager
    implements JvmExecutorCallback
{
    private static final JvmExecutor[] EMPTY_JVMEXECUTOR_ARRAY = new JvmExecutor[0];

    private SlaveHostNode node;

    private Map threads;
    private Map masters;
    private Map jvms;
    private Map jvmsById;

    public JvmManager(SlaveHostNode node)
    {
        this.node = node;
        this.threads = new HashMap();
        this.masters = new HashMap();
        this.jvms = new HashMap();
        this.jvmsById = new HashMap();
    }

    synchronized Thread startJvm(int jvmId,
                                 File javaHome,
                                 RemoteNodeInfo master)
    {
        final JvmExecutor executor = new JvmExecutor( jvmId,
                                                      javaHome,
                                                      SlaveMain.class.getName(),
                                                      new String[] { "" + jvmId,
                                                                     master.getAddress().getHostAddress(),
                                                                     "" + master.getPort() },
                                                      this );
        
        
        Thread thread = new Thread( executor );
        
        this.threads.put( executor,
                          thread );

        this.masters.put( executor,
                          master );

        this.jvmsById.put( "" + jvmId,
                           executor );

        List jvms = (List) this.jvms.get( master );

        if ( jvms == null )
        {
            jvms = new ArrayList();
            this.jvms.put( master,
                           jvms );
        }

        jvms.add( executor );
        
        thread.start();

        return thread;
    }

    void removeJvm(JvmExecutor executor)
    {
        List jvms = (List) this.jvms.get( getMaster( executor ) );

        jvms.remove( executor );

        this.threads.remove( executor );
        this.masters.remove( executor );
        this.jvmsById.remove( "" + executor.getJvmId() );
    }

    JvmExecutor[] getJvmExecutors(NodeInfo master)
    {
        return (JvmExecutor[]) ((List)this.jvms.get( master )).toArray( EMPTY_JVMEXECUTOR_ARRAY );
    }


    JvmExecutor getJvmExecutor(int id)
    {
        return (JvmExecutor) this.jvmsById.get( "" + id );
    }

    Thread getThread(JvmExecutor executor)
    {
        return (Thread) this.threads.get( executor );
    }

    NodeInfo getMaster(JvmExecutor executor)
    {
        return (NodeInfo) this.masters.get( executor );
    }

    public synchronized void notifyJvmFinished(JvmExecutor jvm,
                                               int exitValue)
    {
        removeJvm( jvm );
        this.node.notifyJvmFinished( jvm,
                                     exitValue );
    }

    public synchronized void notifyJvmInterrupted(JvmExecutor jvm)
    {
        removeJvm( jvm );
        this.node.notifyJvmInterrupted( jvm );
    }

    public synchronized void notifyJvmException(JvmExecutor jvm,
                                                Exception e)
    {
        removeJvm( jvm );
        this.node.notifyJvmException( jvm,
                                      e );
    }

    Output[] getOutputs(NodeInfo master)
    {
        JvmExecutor[] executors = getJvmExecutors( master );

        Output[] outputs = new Output[ executors.length ];

        for ( int i = 0 ; i < executors.length ; ++i )
        {
            outputs[ i ] = new Output( executors[ i ].getJvmId(),
                                       executors[ i ].getStdout(),
                                       executors[ i ].getStderr() );
        }

        return outputs;
    }

    synchronized void destroyAll()
    {
        JvmExecutor[] executors = (JvmExecutor[]) this.threads.keySet().toArray( EMPTY_JVMEXECUTOR_ARRAY );

        for ( int i = 0 ; i < executors.length ; ++i )
        {
            try
            {
                Thread thr = getThread( executors[ i ] );
                thr.interrupt();
                executors[ i ].destroy();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    synchronized void destroyAll(NodeInfo master)
    {
        JvmExecutor[] executors = getJvmExecutors( master );

        for ( int i = 0 ; i < executors.length ; ++i )
        {
            try
            {
                Thread thr = getThread( executors[ i ] );
                thr.interrupt();
                executors[ i ].destroy();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
