package org.sysunit.command.slave;

import org.sysunit.command.Dispatcher;
import org.sysunit.command.DispatchException;
import org.sysunit.command.master.RequestJarCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Iterator;

public class JarFetcher
    extends Thread {

    private Dispatcher dispatcher;
    private File dir;
    private Map jarMap;
    private int numStored;
    private SlaveServer slaveServer;

    public JarFetcher(Dispatcher dispatcher,
                      File dir,
                      Map jarMap,
                      SlaveServer slaveServer) {
        this.dispatcher = dispatcher;
        this.dir = dir;
        this.jarMap = jarMap;
        this.slaveServer = slaveServer;
    }

    public void run() {
        try { 
            fetchJars();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void waitFor()
        throws InterruptedException {

        while ( this.numStored != this.jarMap.size() ) {
            wait( 1000 );
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
