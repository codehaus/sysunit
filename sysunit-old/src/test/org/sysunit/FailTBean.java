package org.sysunit;

public class FailTBean
    extends AbstractTBean {

    public void run()
        throws Exception {
        fail( "supposed to fail" );
    }
}
