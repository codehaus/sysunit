package org.sysunit.testmesh.master;

public class MasterMain
{
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length != 1 )
        {
            System.err.println( "systest argument required" );
            System.exit( 1 );
        }

        String systest = args[0];

        final MasterNode master = new MasterNode();
        
        Runtime.getRuntime().addShutdownHook( new Thread()
            {
                public void run()
                {
                    try
                    {
                        master.stop();
                    }
                    catch (InterruptedException e)
                    {
                        // swallow
                    }
                }
            } );

        master.start();
    }
}
