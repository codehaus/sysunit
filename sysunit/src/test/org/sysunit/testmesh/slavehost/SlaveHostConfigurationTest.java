package org.sysunit.testmesh.slavehost;

import java.io.InputStream;

public class SlaveHostConfigurationTest
    extends SlaveHostTestBase
{
    public void testValid()
        throws Exception
    {
        InputStream in = getClass().getResourceAsStream( "valid.conf" );

        SlaveHostConfiguration config = SlaveHostConfiguration.build( in );

        assertNotNull( "config is not null",
                       config );

        assertLength( "1 tag",
                      1,
                      config.getTags() );

        assertEquals( "has linux tag",
                      "linux",
                      config.getTags()[0] );

        assertLength( "2 jdks",
                      2,
                      config.getJdks() );

        assertContains( "contains sun-1.3.1",
                        "sun-1.3.1",
                        config.getJdks() );

        assertContains( "contains ibm-1.3.1",
                        "ibm-1.3.1",
                        config.getJdks() );
                      
    }

    public void testInvalid()
        throws Exception
    {
        InputStream in = getClass().getResourceAsStream( "invalid.conf" );

        try
        {
            SlaveHostConfiguration.build( in );

            fail( "should have thrown SlaveHostConfigurationException" );
        }
        catch (SlaveHostConfigurationException e)
        {
            // expected and correct
        }

    }
}
