package org.sysunit;

public class ValidationFailTBean
    extends MockTBean {

    public void assertValid()
        throws Exception {
        fail( "should fail" );
    }
}
