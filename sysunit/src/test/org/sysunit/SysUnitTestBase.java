package org.sysunit;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collection;

public abstract class SysUnitTestBase
    extends TestCase
{
    public void assertUnique(String description,
                             Object[] objs)
    {

        for ( int i = 0 ; i < objs.length ; ++i )
        {
            for ( int j = 0 ; j < objs.length ; ++j )
            {
                if ( j != i )
                {
                    if ( objs[ i ].equals( objs[ j ] ) )
                    {
                        fail( description + ": objects {[" + i + "]<" + objs[ i ] + ">} and {[" + j + "]<" + objs[ j ] + ">} are equal" );
                    }
                }
            }
        }
    }

    public void assertNotEquals(String description,
                                Object object1,
                                Object object2)
    {
        if ( object1.equals( object2 ) )
        {
            fail( "equal but expected not to be: <" + object1 + ">, <" + object2 + ">" );
        }
    }

    public void assertContains(Object expected,
                               Object[] actual)
    {
        assertContains( null,
                        expected,
                        actual );
    }

    public void assertContains(String description,
                               Object expected,
                               Object[] actual)
    {
        for ( int i = 0 ; i < actual.length ; ++i )
        {
            if ( actual[i].equals( expected ) )
            {
                return;
            }
        }

        if ( description != null )
        {
            fail( description + ": " + expected + " not in " + Arrays.asList( actual ) );
        }
        else
        {
            fail( expected + " not in " + Arrays.asList( actual ) );
        }
    }

    public void assertLength(String description,
                             int length,
                             Object[] array)
    {
        if ( array.length == length )
        {
            return;
        }

        fail( description + ": expected length of <" + length + "> but found <" + array.length + ">" );
    }

    public void assertLength(String description,
                             int length,
                             Collection collection)
    {
        if ( collection.size() == length )
        {
            return;
        }

        fail( description + ": expected length of <" + length + "> but found <" + collection.size() + ">" );
    }
}
