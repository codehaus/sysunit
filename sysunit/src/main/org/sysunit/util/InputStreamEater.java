package org.sysunit.util;

import java.io.InputStream;
import java.io.IOException;

public class InputStreamEater
    implements Runnable
{
    private InputStream in;

    private StringBuffer buffer;

    private boolean multiplex;

    public InputStreamEater(InputStream in)
    {
        this.in = in;
        this.buffer = new StringBuffer();
    }

    public void setMultiplex(boolean multiplex)
    {
        this.multiplex = multiplex;
    }

    public boolean getMultiplex()
    {
        return this.multiplex;
    }

    public String getOutput()
    {
        return this.buffer.toString();
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
                    buffer.append( new String( buf,
                                               0,
                                               read ) );

                    if ( this.multiplex )
                    {
                        System.err.print( new String( buf, 0, read ) );
                    }
                }
            }
        }
        catch (IOException e)
        {
            // swallow
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                // swallow
            }
        }
    }
}
