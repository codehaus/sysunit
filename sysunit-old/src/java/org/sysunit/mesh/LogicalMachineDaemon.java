package org.sysunit.mesh;

import org.sysunit.mesh.transport.Transport;
import org.sysunit.mesh.transport.TransportFactory;
import org.sysunit.mesh.transport.TransportException;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class LogicalMachineDaemon
    extends AbstractDaemon {

    public LogicalMachineDaemon(Transport transport) {
        super( transport );
    }

    public LogicalMachineDaemon(File transportConfig)
        throws IOException, TransportException {
        super( transportConfig );
    }

    public LogicalMachineDaemon(InputStream transportConfigIn)
        throws IOException, TransportException {
        super( transportConfigIn );
    }

    public static void main(String[] args)
        throws Exception {

        if ( args.length != 1 ) {
            System.exit( -1 );
        }

        String testId = args[0];

        LogicalMachine logicalMachine = new LogicalMachine( TransportFactory.getInstance().initializeTransport(),
                                                            testId );

        Thread waiter = new Thread() {

                public void run() {
                    while ( true ) {
                        synchronized ( this ) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
            };

        waiter.setDaemon( false );

        waiter.start();
    }
}
