package org.sysunit.testmesh.slave;

import org.sysunit.testmesh.master.AddSlaveCommand;

import java.net.InetAddress;

public class SlaveMain
{
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length != 3 )
        {
            System.err.println( "usage: java org.sysunit.testmesh.slave.SlaveMain <jvmId> <masterAddress> <masterPort>" );
            System.exit( 1 );
        }

        String jvmIdStr         = args[ 0 ];
        String masterAddressStr = args[ 1 ];
        String masterPortStr    = args[ 2 ];

        int         jvmId         = Integer.parseInt( jvmIdStr );
        InetAddress masterAddress = InetAddress.getByName( masterAddressStr );
        int         masterPort    = Integer.parseInt( masterPortStr );

        final SlaveNode slave = new SlaveNode( jvmId,
                                               masterAddress,
                                               masterPort );
        
        Runtime.getRuntime().addShutdownHook( new Thread()
            {
                public void run()
                {
                    try
                    {
                        slave.stop();
                    }
                    catch (InterruptedException e)
                    {
                        // swallow
                    }
                }
            } );

        slave.start();

        slave.executeOn( slave.getMasterNodeInfo(),
                         new AddSlaveCommand( jvmId ) );
                                              
    }
}
