package org.sysunit.command.master;

import org.sysunit.command.State;
import org.sysunit.command.StateCommand;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.slave.LaunchTestNodeCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class LaunchState
    extends MasterState {

    private static final Log log = LogFactory.getLog( LaunchState.class );

    public static abstract class Command
        extends StateCommand {

        public void run(State state)
            throws Exception {
            if ( state instanceof LaunchState ) {
                run ( (LaunchState) state );
            }
        }

        public abstract void run(LaunchState state)
            throws Exception;
    }

    private List testNodeInfos;
    private Dispatcher[] slaveNodeDispatchers;
    private String xml;
    private String[] jvmNames;

    public LaunchState(MasterServer server,
                       Dispatcher[] slaveNodeDispatchers,
                       String xml,
                       String[] jvmNames) {
        super( server );
        this.slaveNodeDispatchers = slaveNodeDispatchers;
        this.xml                  = xml;
        this.jvmNames             = jvmNames;
        this.testNodeInfos        = new ArrayList();
    }

    public void enter()
        throws Exception {

        Map jarMap = getJarMap();

        log.debug( jvmNames.length + " jvms" );

        for ( int i = 0, j = 0 ; i < jvmNames.length ; ++i, ++j ) {

            j = i % this.slaveNodeDispatchers.length;

            log.debug( jvmNames[i] + " to " + this.slaveNodeDispatchers[j] );

            this.slaveNodeDispatchers[j].dispatch( new LaunchTestNodeCommand( this.xml,
                                                                              jvmNames[i],
                                                                              getServer().getName(),
                                                                              jarMap ) );
		}
    }

    protected Map getJarMap()
        throws IOException {
        Properties  jarMap = new Properties();
        InputStream jarsIn = getClass().getClassLoader().getResourceAsStream( "sysunit-jars.properties" );
        if ( jarsIn != null ) {
            try {
                jarMap.load( jarsIn );
            } finally {
                jarsIn.close();
            }
        }

        return jarMap;
    }

    public void addLaunchedTestNode(String testServerName,
                                    int numSynchronizableTBeans,
                                    Dispatcher dispatcher)
        throws Exception {

        TestNodeInfo testNodeInfo = new TestNodeInfo( testServerName,
                                                      numSynchronizableTBeans,
                                                      dispatcher );

        this.testNodeInfos.add( testNodeInfo );

        if ( this.testNodeInfos.size() == this.jvmNames.length ) {
            getServer().exitState( this );
        }
    }

    public TestNodeInfo[] getTestNodeInfos() {
        return (TestNodeInfo[]) this.testNodeInfos.toArray( TestNodeInfo.EMPTY_ARRAY );
    }
}
