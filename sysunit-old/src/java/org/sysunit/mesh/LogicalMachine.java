package org.sysunit.mesh;

import org.sysunit.TBean;
import org.sysunit.TBeanFactory;
import org.sysunit.mesh.transport.Transport;
import org.sysunit.model.LogicalMachineInfo;
import org.sysunit.model.TBeanInfo;
import org.sysunit.util.Barrier;
import org.sysunit.util.Blocker;

import java.util.List;
import java.util.ArrayList;

public class LogicalMachine
    extends AbstractMeshNode {
    
    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    private String testId;
    private LogicalMachineInfo logicalMachineInfo;

    private Barrier beginBarrier;
    private Blocker blocker;
    private Barrier endBarrier;

    private List tbeanThreads;

    private MeshManagerAgent meshManager;

    public LogicalMachine(Transport transport,
                          String testId) {
        super( transport );

        this.testId             = testId;
        this.beginBarrier       = new Barrier( logicalMachineInfo.getTBeansInfo().length + 1 );
        this.blocker            = new Blocker();
        this.endBarrier         = new Barrier( logicalMachineInfo.getTBeansInfo().length + 1 );

        this.tbeanThreads       = new ArrayList();
    }

    public String getTestId() {
        return this.testId;
    }

    public void initialize(LogicalMachineInfo logicalMachineInfo)
        throws Throwable {
        setInfo( logicalMachineInfo );
        initializeTBeans();
    }

    public void setInfo(LogicalMachineInfo logicalMachineInfo) {
        this.logicalMachineInfo = logicalMachineInfo;
    }

    public LogicalMachineInfo getInfo() {
        return this.logicalMachineInfo;
    }

    protected Barrier getBeginBarrier() {
        return this.beginBarrier;
    }

    protected Blocker getBlocker() {
        return this.blocker;
    }

    protected Barrier getEndBarrier() {
        return this.endBarrier;
    }

    public void initializeTBeans()
        throws Throwable {
        TBeanInfo[] tbeansInfo = getInfo().getTBeansInfo();
        
        for ( int i = 0 ; i < tbeansInfo.length ; ++i ) {
            initializeTBean( tbeansInfo[i] );
        }

        getBeginBarrier().block();
    }

    protected TBeanThread[] getTBeanThreads() {
        return (TBeanThread[]) this.tbeanThreads.toArray( TBeanThread.EMPTY_ARRAY );
    }

    public void startTBeans() {
        getBlocker().unblock();
    }

    public void waitFor(long timeoutMs) {

    }

    public Throwable[] collectErrors() {
        List errors = new ArrayList();

        TBeanThread[] threads = getTBeanThreads();

        for ( int i = 0 ; i < threads.length ; ++i ) {
            if ( threads[i].hasError() ) {
                errors.add( threads[i].getError() );
            }
        }

        return (Throwable[]) errors.toArray( EMPTY_THROWABLE_ARRAY );
    }

    public void kill() {
        TBeanThread[] threads = getTBeanThreads();

        for ( int i = 0 ; i < threads.length ; ++i ) {
            threads[i].interrupt();
        }
    }

    protected void initializeTBean(TBeanInfo tbeanInfo)
        throws Throwable {

        TBeanFactory tbeanFactory = tbeanInfo.getTBeanFactory();
        TBean tbean = tbeanFactory.newTBean();

        TBeanThread thread = new TBeanThread( tbeanInfo,
                                              tbean,
                                              getBeginBarrier(),
                                              getBlocker(),
                                              getEndBarrier() );

        tbeanThreads.add( thread );

        thread.start();
    }

    protected void sync(String tbeanId,
                        String syncPoint)
        throws InterruptedException {
        getMeshManager().sync( tbeanId,
                               syncPoint );
    }

    protected MeshManagerAgent getMeshManager() {
        synchronized ( this ) { 
            if ( this.meshManager == null ) {
                this.meshManager = new MeshManagerAgent( getTransport(),
                                                         getTestId() );
            }
        }
        return this.meshManager;
    }

    public void execute(NodeCommand command)
        throws Throwable {
        execute( (LogicalMachineCommand) command );
    }

    public void execute(LogicalMachineCommand command)
        throws Throwable {
        command.setTransport( getTransport() );
        command.execute( this );
    }
}


