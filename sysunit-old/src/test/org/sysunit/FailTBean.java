package org.sysunit;

public class FailTBean
    extends MockTBean {

    public void run()
        throws Exception {
        super.run();
        fail( "supposed to fail" );
    }
}
