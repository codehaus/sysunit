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

    public static ScenarioInfo build(File file)
        throws Exception
    {
        FileInputStream in = new FileInputStream( file );

        try
        {
            return build( in );
        }
        finally
        {
            in.close();
        }
    }


    public static ScenarioInfo build(InputStream in)
        throws Exception
    {
        Properties props = new Properties();

        props.load( in );

        return build( props );
    }

    public static ScenarioInfo build(Properties props)
        throws Exception
    {
        String testClassName = props.getProperty( "test.class" );

        String scenarioName = props.getProperty( "scenario.name" );

        Class testClass = Class.forName( testClassName );

        Method method = testClass.getMethod( "getTestInfo",
                                             EMPTY_CLASS_ARRAY );

        DistributedSystemTestInfo testInfo = (DistributedSystemTestInfo) method.invoke( testClass,
                                                                                        EMPTY_OBJECT_ARRAY );

        ScenarioInfo scenarioInfo = new ScenarioInfo( scenarioName,
                                                      testInfo );

        JvmInfo[] jvms = testInfo.getJvms();

        for ( int i = 0 ; i < jvms.length ; ++i )
        {
            String tag = props.getProperty( "jvm." + jvms[i].getName() + ".tag" );
            String jdk = props.getProperty( "jvm." + jvms[i].getName() + ".jdk" );

            if ( tag != null )
            {
                tag = tag.trim();
                scenarioInfo.setTag( jvms[ i ],
                                     tag );
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
