package org.sysunit.command.slave;

import org.sysunit.command.Dispatcher;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MasterSession {

    private JarFetcher jarFetcher;
    private List launchers;

    public MasterSession(Dispatcher dispatcher,
                         File dir,
                         Map jarMap,
                         SlaveServer slaveServer) {
        this.launchers = new ArrayList();
        this.jarFetcher = new JarFetcher( dispatcher,
                                          dir,
                                          jarMap,
                                          slaveServer );

        this.jarFetcher.start();
    }

    public synchronized void addTestNode(String[] testArgs) {
        TestNodeLauncher launcher = new TestNodeLauncher( this.jarFetcher,
                                                          testArgs );

        this.launchers.add( launcher );
        launcher.start();
    }

    public void storeJar(String jarName,
                         byte[] bytes)
        throws Exception {
        this.jarFetcher.storeJar( jarName,
                                  bytes );
    }

}
