package org.sysunit.util;

public class ExitValueMain
{
    public static void main(String[] args)
        throws Exception
    {
        if ( args.length != 2 )
        {
            System.exit( 42 );
        }

        int arg1 = Integer.parseInt( args[0] );
        int arg2 = Integer.parseInt( args[1] );

        System.exit( arg1 + arg2 );
    }
}
