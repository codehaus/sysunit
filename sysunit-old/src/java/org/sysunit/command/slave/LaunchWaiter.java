package org.sysunit.command.slave;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.sysunit.command.master.RequestJarCommand;
import org.sysunit.transport.jms.TestNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LaunchWaiter
    extends Thread {

    private static final Log log = LogFactory.getLog(LaunchWaiter.class);
    private File dir;
    private LaunchTestNodeCommand command;
    private String[] testArgs;

    public LaunchWaiter(File dir,
                        LaunchTestNodeCommand command,
                        String[] testArgs)
    {
        this.dir = dir;
        this.command = command;
        this.testArgs = testArgs;
    }

    public void run() {
        try {
            setUpClasspath();
            waitForJars();
            
            ProcessRunner runner = ProcessRunner.newJavaProcess(TestNode.class,
                                                                this.testArgs,
                                                                getJarFiles() );
            Thread thread = new Thread(runner);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setUpClasspath()
        throws Exception {
        if ( this.dir.exists() ) {
            log.info( "#$#$#$#$#$ DIR EXISTS" );
            return;
        } else {
            log.info( "####### SETTING UP CLASSPATH" );
        }

        this.dir.mkdirs();

        this.dir.deleteOnExit();

        String masterId = this.command.getMasterID();
        Map jarMap = this.command.getJarMap();
        
        for ( Iterator nameIter = jarMap.keySet().iterator();
              nameIter.hasNext(); ) {
            String name = (String) nameIter.next();
            String path = (String) jarMap.get( name );

            log.info( "fetching " + name + " from " + path );

            command.getReplyDispatcher().dispatch( new RequestJarCommand( name,
                                                                          path ) );
        }
    }

    protected synchronized void waitForJars()
        throws InterruptedException {
        while ( getJarFiles().length != command.getJarMap().size() ) {
            log.info( getJarFiles().length + " of " + command.getJarMap().size() );
            wait( 500 );
        }
    }

    protected synchronized void storeJar(String jarName,
                                         byte[] bytes)
        throws Exception {

        File jarFile = new File( this.dir,
                                 jarName );

        jarFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream( jarFile );

        try {
            out.write( bytes );
        } finally {
            out.close();
        }

        notifyAll();
    }

    public File[] getJarFiles() {
        return this.dir.listFiles();
    }
}
