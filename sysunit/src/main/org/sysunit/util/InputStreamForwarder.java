package org.sysunit.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class InputStreamForwarder
    implements Runnable
{
    private InputStream in;
    private OutputStream out;

    public InputStreamForwarder(InputStream in,
                                OutputStream out)
    {
        this.in  = in;
        this.out = out;
    }

    public void run()
    {
        byte[] buf = new byte[1024];
        int read = 0;

        try
        {
            while ( (read = this.in.read( buf,
                                          0,
                                          buf.length ) ) >= 0 )
            {
                if ( read > 0 )
                { 
                    out.write( buf,
                               0,
                               read );
                }
            }
        }
        catch (IOException e)
        {
            // swallow
        }
    }

    public static Thread forward(InputStream in,
                                 OutputStream out)
    {
        InputStreamForwarder forwarder = new InputStreamForwarder( in,
                                                                   out );

        Thread thread = new Thread( forwarder );

        thread.setDaemon( true );

        thread.start();

        return thread;
    }
}
