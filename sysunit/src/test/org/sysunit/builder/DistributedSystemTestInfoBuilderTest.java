package org.sysunit.builder;

import org.sysunit.model.DistributedSystemTestInfo;

import java.io.InputStream;

public class DistributedSystemTestInfoBuilderTest
    extends BuilderTestBase
{
    public void testOnlySystemTest()
        throws Exception
    {
        InputStream in = getClass().getResourceAsStream( "OnlySystemTest.xml" );

        DistributedSystemTestInfo testInfo = DistributedSystemTestInfoBuilder.build( in );

        assertNotNull( "testInfo != null",
                       testInfo );

        assertEquals( "testInfo.getName()",
                      "cheese tester",
                      testInfo.getName() );

        assertEquals( "testInfo.getJvms()",
                      0,
                      testInfo.getJvms().length );
    }
}
