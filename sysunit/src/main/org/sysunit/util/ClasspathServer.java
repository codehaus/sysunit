package org.sysunit.util;

import org.sysunit.net.Server;
import org.sysunit.net.ServerLoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class ClasspathServer
    implements ServerLoop
{
    private Server server;
    private List mappings;

    public ClasspathServer(int numThreads)
    {
        this.server = new Server( numThreads,
                                  this );

        this.mappings = new ArrayList();

        init();
    }

    Server getServer()
    {
        return this.server;
    }

    public void start()
        throws Exception
    {
        getServer().start();
    }

    public void stop()
        throws InterruptedException
    {
        getServer().stop();
    }

    void init()
    {
        init( System.getProperty( "java.class.path" ) );
    }

    void init(String classpath)
    {
        StringTokenizer tokens = new StringTokenizer( classpath, File.pathSeparator );

        while ( tokens.hasMoreTokens() )
        {
            String component = tokens.nextToken();

            String root = null;
            File   file = new File( component );

            if ( file.isDirectory() )
            {
                root = "/" + file.getName() + "/";
                file = file.getParentFile();
            }
            else
            {
                root = "/" + file.getName();
            }

            Mapping mapping = new Mapping( root,
                                           file );

            mappings.add( mapping );
        }
    }

    public String[] getRelativeUrls()
    {
        String[] urls = new String[ this.mappings.size() ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = ((Mapping)this.mappings.get( i )).getRoot();
        }

        return urls;
    }

    public int getPort()
    {
        return getServer().getPort();
    }

    File getLocalFile(String uri)
    {
        for ( Iterator iter = this.mappings.iterator();
              iter.hasNext(); )
        {
            Mapping mapping = (Mapping) iter.next();

            if ( mapping.matches( uri ) )
            {
                return mapping.getLocalFile( uri );
            }
        }

        return null;
    }

    public void accept(InetAddress remoteAddress,
                       int remotePort,
                       InputStream in,
                       OutputStream out)
        throws Exception
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );

        String line = null;

        String uri = null;

        boolean head = false;

        while ( true )
        {
            line = reader.readLine().trim();

            if ( line.trim().equals( "" ) )
            {
                break;
            }

            if ( line.startsWith( "GET" )
                 ||
                 line.startsWith( "HEAD" ) )
            {
                int firstSpace = line.indexOf( " " );
                int secondSpace = line.indexOf( " ", firstSpace + 1 );

                uri = line.substring( firstSpace + 1,
                                      secondSpace );

                if ( line.startsWith( "HEAD" ) )
                {
                    head = true;
                }
            }
        }

        if ( uri == null )
        {
            out.write( "HTTP/1.1 400 Bad Request\r\n".getBytes() );
            out.write( "\r\n".getBytes() );
            return;
        }


        File local = getLocalFile( uri );

        if ( local == null )
        {
            out.write( "HTTP/1.1 404 Not Found\r\n".getBytes() );
            out.write( "\r\n".getBytes() );

            return;
        }

        out.write( "HTTP/1.1 200\r\n".getBytes() );
        out.write( ("Content-length: " + local.length() + "\r\n").getBytes() );

        if ( local.getName().endsWith( ".zip" )
             ||
             local.getName().endsWith( ".jar" ) )
        {
            out.write( "Content-type: application/java-archive\r\n".getBytes() );
        }
        else
        {
            out.write( "Content-type: application/octect-stream\r\n".getBytes() );
        }

        out.write( "\r\n".getBytes() );

        if ( ! head )
        {
            FileInputStream fileIn = new FileInputStream( local );
            
            byte[] buf = new byte[1024];
            int len = 0;
            
            while ( ( len = fileIn.read( buf,
                                         0,
                                         1024 ) ) > 0 )
            {
                out.write( buf,
                           0,
                           len );
            }
        }
    }
}


class Mapping
{
    private String root;
    private File local;
    
    Mapping(String root,
            File local)
    {
        this.root  = root;
        this.local = local;
    }

    boolean matches(String uri)
    {
        return uri.startsWith( getRoot() );
    }

    String getRoot()
    {
        return this.root;
    }

    File getLocal()
    {
        return this.local;
    }

    File getLocalFile(String uri)
    {
        if ( uri.equals( getRoot() ) )
        {
            return getLocal();
        }

        File localFile = new File( getLocal(),
                                   uri );

        if ( localFile.exists() )
        {
            return localFile;
        }

        return null;
    }
}
