package org.sysunit.testmesh.slavehost;

import org.sysunit.model.PhysicalMachineInfo;

import java.io.File;

public class SlaveHostDaemon
{
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length > 1 )
        {
            System.err.println( "usage: slavehostd [<config>]" );
            System.exit( 1 );
        }

        SlaveHostConfiguration config = null;

        if ( args.length == 1 )
        {
            config = SlaveHostConfiguration.build( new File( args[ 0 ] ) );
        }
        else
        {
            config = new SlaveHostConfiguration();
            config.addTag( "*" );
        }

        final SlaveHostNode slaveHost = new SlaveHostNode( config );

        Runtime.getRuntime().addShutdownHook( new Thread()
            {
                public void run()
                {
                    try
                    {
                        slaveHost.stop();
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            } );

        slaveHost.start();
    }
}
