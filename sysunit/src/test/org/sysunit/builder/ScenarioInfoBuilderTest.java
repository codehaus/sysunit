package org.sysunit.builder;

import org.sysunit.model.ScenarioInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.model.DistributedSystemTestInfo;

import java.util.Properties;

public class ScenarioInfoBuilderTest
    extends BuilderTestBase
{
    private Properties props;

    public void setUp()
        throws Exception
    {
        super.setUp();
        this.props = new Properties();
    }

    public void tearDown()
        throws Exception
    {
        this.props = null;
        super.tearDown();
    }

    public void testTestClass()
        throws Exception
    {
        setProperty( "test.class",
                     "org.sysunit.tests.NoOpTest" );

        ScenarioInfo scenarioInfo = build();

        assertNotNull( "System test-info exists",
                       scenarioInfo.getSystemTestInfo() );

        assertEquals( "test is named 'no-op'",
                      "no-op",
                      scenarioInfo.getSystemTestInfo().getName() );
    }

    public void testTag()
        throws Exception
    {
        setProperty( "test.class",
                     "org.sysunit.tests.NoOpTest" );

        setProperty( "jvm.one.tag",
                     "client" );

        ScenarioInfo scenarioInfo = build();

        DistributedSystemTestInfo testInfo = scenarioInfo.getSystemTestInfo();

        JvmInfo jvmInfo = testInfo.getJvm( "one" );

        assertNotNull( "JVM 'one' exists",
                       jvmInfo );

        assertEquals( "jvm 'one' is tagged as 'client'",
                      "client",
                      scenarioInfo.getTag( jvmInfo ) );
    }

    public void testJdk()
        throws Exception
    {
        setProperty( "test.class",
                     "org.sysunit.tests.NoOpTest" );

        setProperty( "jvm.one.jdk",
                     "sun" );

        ScenarioInfo scenarioInfo = build();

        DistributedSystemTestInfo testInfo = scenarioInfo.getSystemTestInfo();

        JvmInfo jvmInfo = testInfo.getJvm( "one" );

        assertNotNull( "JVM 'one' exists",
                       jvmInfo );

        assertEquals( "jvm 'one' is using jdk 'sun'",
                      "sun",
                      scenarioInfo.getJdk( jvmInfo ) );
    }

    public void testTagAndJdk()
        throws Exception
    {
        setProperty( "test.class",
                     "org.sysunit.tests.NoOpTest" );

        setProperty( "jvm.one.jdk",
                     "sun" );

        setProperty( "jvm.one.tag",
                     "client" );

        ScenarioInfo scenarioInfo = build();

        DistributedSystemTestInfo testInfo = scenarioInfo.getSystemTestInfo();

        JvmInfo jvmInfo = testInfo.getJvm( "one" );

        assertNotNull( "JVM 'one' exists",
                       jvmInfo );

        assertEquals( "jvm 'one' is tagged as 'client'",
                      "client",
                      scenarioInfo.getTag( jvmInfo ) );

        assertEquals( "jvm 'one' is using jdk 'sun'",
                      "sun",
                      scenarioInfo.getJdk( jvmInfo ) );
    }

    void setProperty(String name,
                     String value)
    {
        this.props.setProperty( name,
                                value );
    }

    ScenarioInfo build()
        throws Exception
    {
        return ScenarioInfoBuilder.build( this.props );
    }
}
