package org.sysunit.mesh;

import org.sysunit.model.TestInfo;
import org.sysunit.model.LogicalMachineInfo;
import org.sysunit.mesh.transport.Transport;

import java.util.List;
import java.util.ArrayList;

public class MeshManager
    extends AbstractMeshNode {

    private TestInfo testInfo;
    private List physicalMachines;
    private List logicalMachines;
    private int logicalMachinesInitialized;

    public MeshManager(Transport transport,
                       TestInfo testInfo) {
        super( transport );
        this.testInfo         = testInfo;
        this.physicalMachines = new ArrayList();
        this.logicalMachines  = new ArrayList();
        this.logicalMachinesInitialized = 0;

        locatePhysicalMachines();
    }

    public TestInfo getTestInfo() {
        return this.testInfo;
    }

    protected void locatePhysicalMachines() {
        String[] physicalMachineIds = getTransport().locatePhysicalMachines();

        for ( int i = 0 ; i < physicalMachineIds.length ; ++i ) {
            addPhysicalMachine( new PhysicalMachineAgent( getTransport(),
                                                          physicalMachineIds[i] ) );
        }

        addLocalPhysicalMachine();
    }

    protected void addLocalPhysicalMachine() {
        PhysicalMachineAgent localPhysicalMachine = new PhysicalMachineAgent( getTransport(),
                                                                              "sysunit.localhost" );
        addPhysicalMachine( localPhysicalMachine );
    }

    public void addPhysicalMachine(PhysicalMachineAgent physicalMachine) {
        this.physicalMachines.add( physicalMachine );
    }

    public PhysicalMachineAgent[] getPhysicalMachines() {
        return (PhysicalMachineAgent[]) this.physicalMachines.toArray( PhysicalMachineAgent.EMPTY_ARRAY );
    }

    public void initializeTest()
        throws Exception {

        PhysicalMachineAgent[] physicalMachines = getPhysicalMachines();

        LogicalMachineInfo[] logicalMachinesInfo = getTestInfo().getLogicalMachinesInfo();

        int physicalMachineIndex = 0;

        for ( int i = 0 ; i < logicalMachinesInfo.length ; ++i ) {
            physicalMachineIndex = launchLogicalMachine( logicalMachinesInfo[i],
                                                         physicalMachines,
                                                         physicalMachineIndex );
        }

        waitForLogicalMachinesToInitialize();
    }

    protected synchronized void waitForLogicalMachinesToInitialize()
        throws InterruptedException {

        while ( this.logicalMachinesInitialized < getTestInfo().getLogicalMachinesInfo().length ) {
            wait();
        }
    }

    protected int launchLogicalMachine(LogicalMachineInfo logicalMachineInfo,
                                       PhysicalMachineAgent[] physicalMachines,
                                       int physicalMachineIndex)
        throws Exception {

        PhysicalMachineAgent selectedPhysicalMachine = physicalMachines[ physicalMachineIndex ];

        launchLogicalMachine( logicalMachineInfo,
                              selectedPhysicalMachine );

        ++physicalMachineIndex;

        if ( physicalMachineIndex > physicalMachines.length ) {
            physicalMachineIndex = 0;
        }

        return physicalMachineIndex;
    }

    protected void launchLogicalMachine(LogicalMachineInfo logicalMachineInfo,
                                        PhysicalMachineAgent physicalMachine)
        throws Exception {

        physicalMachine.launchLogicalMachine( getTestInfo().getId(),
                                              logicalMachineInfo );
    }

    public void logicalMachineLaunched(String logicalMachineId)
        throws Exception {

        LogicalMachineInfo logicalMachineInfo = getTestInfo().getLogicalMachineInfo( logicalMachineId );

        LogicalMachineAgent logicalMachine = new LogicalMachineAgent( getTransport(),
                                                                      logicalMachineInfo );

        this.logicalMachines.add( logicalMachine );

        logicalMachine.dispatch( new InitializeLogicalMachineCommand( getTestInfo().getId(),
                                                                      logicalMachineInfo ) );
    }

    public synchronized void logicalMachineInitialized(String logicalMachineId) {
        ++this.logicalMachinesInitialized;
        notifyAll();
    }

    public LogicalMachineAgent[] getLogicalMachines() {
        return (LogicalMachineAgent[]) this.logicalMachines.toArray( LogicalMachineAgent.EMPTY_ARRAY );
    }

    public void startTest()
        throws Exception {

        LogicalMachineAgent[] logicalMachines = getLogicalMachines();

        for ( int i = 0 ; i < logicalMachines.length ; ++i ) {
            //logicalMachines[i].dispatch( );
        }
    }

    public void waitFor()
        throws Exception {

    }

    public Throwable[] collectErrors()
        throws Exception {
        return null;
    }

    public void kill() {

    }

    public void cleanUp() {

    }

    public void execute(NodeCommand command)
        throws Exception {
        execute( (MeshManagerCommand) command );
    }

    public void execute(MeshManagerCommand command)
        throws Exception {
        command.execute( this );
    }
}
