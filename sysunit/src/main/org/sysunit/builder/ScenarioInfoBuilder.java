package org.sysunit.builder;

import org.sysunit.model.ScenarioInfo;
import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.JvmInfo;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class ScenarioInfoBuilder
{

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Class[0];

    public static ScenarioInfo build(DistributedSystemTestInfo testInfo,
                                     File file)
        throws Exception
    {
        FileInputStream in = new FileInputStream( file );

        try
        {
            return build( testInfo,
                          in );
        }
        finally
        {
            in.close();
        }
    }


    public static ScenarioInfo build(DistributedSystemTestInfo testInfo,
                                     InputStream in)
        throws Exception
    {
        Properties props = new Properties();

        props.load( in );

        return build( testInfo,
                      props );
    }

    public static ScenarioInfo build(DistributedSystemTestInfo testInfo,
                                     Properties props)
        throws Exception
    {
        String scenarioName = props.getProperty( "name" );

        ScenarioInfo scenarioInfo = new ScenarioInfo( scenarioName,
                                                      testInfo );

        JvmInfo[] jvms = testInfo.getJvms();

        String defaultTag = props.getProperty( "jvm.*.tag" );
        String defaultJdk = props.getProperty( "jvm.*.jdk" );
                
        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            String tag = props.getProperty( "jvm." + jvms[i].getName() + ".tag" );
            String jdk = props.getProperty( "jvm." + jvms[i].getName() + ".jdk" );

            if ( tag == null )
            {
                tag = defaultTag;
            }

            if ( tag != null )
            {
                tag = tag.trim();
                scenarioInfo.setTag( jvms[ i ],
                                     tag );
            }

            if ( jdk == null )
            {
                jdk = defaultJdk;
            }

            if ( jdk != null )
            {
                jdk = jdk.trim();
                scenarioInfo.setJdk( jvms[ i ],
                                     jdk );
            }
        }

        return scenarioInfo;
    }
}
