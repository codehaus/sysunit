package org.sysunit.testmesh.slavehost;

import org.sysunit.model.PhysicalMachineInfo;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class SlaveHostConfiguration
{
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private List tags;
    private Map jdks;

    public SlaveHostConfiguration()
    {
        this.tags = new ArrayList();
        this.jdks = new HashMap();
    }

    public void addTag(String tag)
    {
        this.tags.add( tag );
    }

    public String[] getTags()
    {
        return (String[]) this.tags.toArray( EMPTY_STRING_ARRAY );
    }

    public void addJdk(String name,
                       File javaHome)
    {
        this.jdks.put( name,
                       javaHome );
    }

    public String[] getJdks()
    {
        return (String[]) this.jdks.keySet().toArray( EMPTY_STRING_ARRAY );
    }

    public File getJavaHome(String jdk)
    {
        return (File) this.jdks.get( jdk );
    }

    public PhysicalMachineInfo getPhysicalMachineInfo()
    {
        return new PhysicalMachineInfo( getTags(),
                                        getJdks() );
    }

    public static SlaveHostConfiguration build(InputStream stream)
        throws IOException, SlaveHostConfigurationException
    {
        InputStreamReader reader = new InputStreamReader( stream );

        return build( reader );
    }

    public static SlaveHostConfiguration build(File file)
        throws IOException, SlaveHostConfigurationException
    {
        FileReader reader = new FileReader( file );

        try
        {
            return build( reader );
        }
        finally
        {
            reader.close();
        }
    }

    public static SlaveHostConfiguration build(Reader reader)
        throws IOException, SlaveHostConfigurationException
    {
        BufferedReader in = new BufferedReader( reader );

        String line = null;

        boolean processingTags = false;
        boolean processingJdks = false;

        SlaveHostConfiguration config = new SlaveHostConfiguration();

        while ( ( line = in.readLine() ) != null )
        {
            int hashLoc = line.indexOf( "#" );

            if ( hashLoc >= 0 )
            {
                line = line.substring( 0,
                                       hashLoc );
            }

            line = line.trim();

            if ( line.equals( "" ) )
            {
                continue;
            }

            if ( line.equals( "[tags]" ) )
            {
                processingTags = true;
                processingJdks = false;
                continue;
            }

            if ( line.equals( "[jdks]" ) )
            {
                processingTags = false;
                processingJdks = true;
                continue;
            }

            if ( processingTags )
            {
                config.addTag( line );
            }
            else if ( processingJdks )
            {
                int equalLoc = line.indexOf( "=" );

                if ( equalLoc < 0 )
                {
                    throw new SlaveHostConfigurationException( line );
                }

                String jdk = line.substring( 0,
                                             equalLoc ).trim();

                File javaHome = new File( line.substring( equalLoc + 1 ).trim() );

                config.addJdk( jdk,
                               javaHome );
            }
            else
            {
                throw new SlaveHostConfigurationException( line );
            }
        }

        return config;
    }
}
