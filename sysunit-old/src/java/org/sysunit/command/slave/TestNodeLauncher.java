package org.sysunit.command.slave;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.sysunit.command.master.RequestJarCommand;
import org.sysunit.transport.socket.TestNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestNodeLauncher
    extends Thread {

    private static final Log log = LogFactory.getLog(TestNodeLauncher.class);

    private JarFetcher jarFetcher;
    private String[] testArgs;

    private ProcessRunner runner;

    public TestNodeLauncher(JarFetcher jarFetcher,
                            String[] testArgs)
    {
        this.jarFetcher = jarFetcher;
        this.testArgs = testArgs;
    }

    public void run() {
        try {
            waitForJars();
            
            this.runner = ProcessRunner.newJavaProcess( TestNode.class,
                                                        this.testArgs,
                                                        this.jarFetcher.getJarFiles() );
            Thread thread = new Thread( runner );
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void waitForJars()
        throws InterruptedException {
        this.jarFetcher.waitFor();
    }
}
