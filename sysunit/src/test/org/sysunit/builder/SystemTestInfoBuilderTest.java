package org.sysunit.builder;

import org.sysunit.SystemTestCase;
import org.sysunit.model.SystemTestInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.model.ThreadInfo;
import org.sysunit.model.TBeanFactoryInfo;

public class SystemTestInfoBuilderTest
    extends BuilderTestBase
{
    public void testMostBasic()
        throws Exception
    {
        SystemTestCase testCase = new MostBasicTestCase( 500 );

        SystemTestInfo testInfo = SystemTestInfoBuilder.build( testCase );

        assertSame( "testInfo.getSystemTestCase()",
                    testCase,
                    testInfo.getSystemTestCase() );
    }
}
