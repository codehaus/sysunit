package org.sysunit;

import org.sysunit.testmesh.master.MasterMain;
import org.sysunit.testmesh.slavehost.SlaveHostMain;

public class Main
{
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length == 0 )
        {
            displayUsage();
            System.exit( 1 );
        }

        String[] subArgs  = new String[ args.length - 1 ];

        for ( int i = 0 ; i < subArgs.length ; ++i )
        {
            subArgs[ i ] = args[ i + 1 ];
        }

        if ( args[0].equals( "master" ) )
        {
            MasterMain.main( subArgs );
        }
        else if ( args[0].equals( "slavehost" ) )
        {
            SlaveHostMain.main( subArgs );
        }
        else
        {
            displayUsage();
            System.exit( 2 );
        }
    }

    public static void displayUsage()
    {
        System.err.println( "usage: java -jar sysunit.jar [slavehost <args>|master <args>]" );
        System.err.println( "" );
        System.err.println( "master <systest> ............ launch a master" );
        System.err.println( "    <systest> ............... specify systest" );
        System.err.println( "" );
        System.err.println( "slavehost [<config>] ........ launch a slavehost" );
        System.err.println( "    <config> ................ specify config" );
        System.err.println( "" );
    }
}
