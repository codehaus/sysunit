package org.sysunit.command.slave;

import org.sysunit.command.Dispatcher;
import org.sysunit.command.DispatchException;
import org.sysunit.command.master.RequestJarCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Iterator;

public class JarFetcher
    extends Thread {

    private static final Log log = LogFactory.getLog( JarFetcher.class );

    private Dispatcher dispatcher;
    private File dir;
    private Map jarMap;
    private int numStored;
    private SlaveServer slaveServer;
    private boolean failed;
    
    public JarFetcher(Dispatcher dispatcher,
                      File dir,
                      Map jarMap,
                      SlaveServer slaveServer) {
        this.dispatcher = dispatcher;
        this.dir = dir;
        this.jarMap = jarMap;
        this.slaveServer = slaveServer;
        this.failed = false;
    }
    
    public void run() {
        try { 
            fetchJars();
        } catch (DispatchException e) {
            log.error( "failed to transfer all jars",
                       e.getCause() );
        } catch (Exception e) {
            this.failed = true;
            notifyAll();
            log.error( "failed to transfer all jars",
                       e );
        }
    }

    public synchronized void waitFor()
        throws InterruptedException {

        while ( ! this.failed
                &&
                this.numStored != this.jarMap.size() ) {
            wait( 1000 );
        }

        if ( this.failed ) {
            throw new InterruptedException( "failed to transfer all jars" );
        }
    }

    public void fetchJars()
        throws DispatchException {
        if ( ! this.dir.exists() ) {
            this.dir.mkdirs();
        }

        int i = 0;

        for ( Iterator nameIter = this.jarMap.keySet().iterator();
              nameIter.hasNext(); ) {
            String name = (String) nameIter.next();
            String path = (String) this.jarMap.get( name );

            System.err.println( "REQUEST: " + name + " // " + this );
            this.dispatcher.dispatch( new RequestJarCommand( name,
                                                             path,
                                                             this.slaveServer.getName() + " #" + (++i) ) );
        }
    }

    public File[] getJarFiles() {
        return this.dir.listFiles();
    }

    public synchronized void storeJar(String jarName,
                                      byte[] bytes)
        throws Exception {
        File jarFile = new File( this.dir,
                                 jarName );

        
        System.err.println( "CHEESE: " + jarFile );

        jarFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream( jarFile );

        try {
            out.write( bytes );
        } finally {
            out.close();
        }

        ++this.numStored;

        if ( this.numStored == this.jarMap.size() ) {
            notifyAll();
        }
    }
}
