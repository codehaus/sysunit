package org.sysunit;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MultiThrowable
    extends Throwable {
    private Throwable[] errors;

    public MultiThrowable(Throwable[] errors) {
        this.errors = errors;
    }

    public Throwable[] getErrors() {
        return this.errors;
    }

    public String getMessage() {
        StringBuffer buf = new StringBuffer();

        for ( int i = 0 ; i < this.errors.length; ++i ) {
            buf.append( this.errors[i].getMessage() );

            if ( (i+1) < this.errors.length  ) {
                buf.append( '\n' );
            }
        }

        return buf.toString();
    }

    public void printStackTrace() {
        for ( int i = 0 ; i < this.errors.length ; ++i ) {
            errors[i].printStackTrace();
        }
    }

    public void printStackTrace(PrintStream out) {
        for ( int i = 0 ; i < this.errors.length ; ++i ) {
            errors[i].printStackTrace( out );
        }
    }

    public void printStackTrace(PrintWriter out) {
        for ( int i = 0 ; i < this.errors.length ; ++i ) {
            errors[i].printStackTrace( out );
        }
    }
}
