package org.sysunit.command.slave;

import java.io.InputStream;
import java.io.IOException;

public class InputStreamEater
    implements Runnable {

    private InputStream in;

    public InputStreamEater(InputStream in) {
        this.in = in;
    }

    public void run() {

        byte[] buf = new byte[1024];
        int read = 0;

        try {
            while ( (read = this.in.read( buf,
                                          0,
                                          buf.length ) ) >= 0 ) {
                // discard
            }
        } catch (IOException e) {
            // swallow
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // swallow
            }
        }
    }
}
