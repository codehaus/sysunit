package org.sysunit;

import junit.framework.TestCase;

import java.util.Arrays;

public class SysUnitTestCase
    extends TestCase {

    public void assertLength(int length,
                             Object[] array) {
        assertEquals( length,
                      array.length );
    }

    public void assertContainsEquals(Object object,
                                     Object[] array) {
        for ( int i = 0 ; i < array.length ; ++i ) {
            if ( array[i].equals( object ) ) {
                return;
            }
        }

        fail( object + " not contained in " + Arrays.asList( array ) );
    }
}
