package org.sysunit;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

public class SysUnitTestCase
    extends TestCase {

    public void assertEmpty(Object[] array) {
        assertLength( 0,
                      array );
    }

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
