package org.sysunit;

public class FailTBean
    extends MockTBean {

    public void run()
        throws Throwable {
        super.run();
        fail( "supposed to fail" );
    }
}
