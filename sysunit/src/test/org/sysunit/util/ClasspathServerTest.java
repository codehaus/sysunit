package org.sysunit.util;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import java.net.URLClassLoader;

public class ClasspathServerTest
    extends UtilTestBase
{
    public void testConstruct()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        String[] relativeUrls = server.getRelativeUrls();

        assertContains( "relative urls contains /junit-3.8.1.jar",
                        "/junit-3.8.1.jar",
                        relativeUrls );
    }

    public void testGetPort()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        try
        {
            server.getPort();
            fail( "port not accessible until start()'d" );
        }
        catch (Exception e)
        {
            // expected and correct
        }

        server.start();

        assertTrue( "port not zero",
                    server.getPort() > 0 );

        server.stop();
    }

    public void testGetLocalFile()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        File file = null;

        file = server.getLocalFile( "/junit-3.8.1.jar" );

        assertNotNull( "junit-3.8.1.jar is not null",
                       file );

        assertTrue( "junit-3.8.1.jar exists",
                    file.exists() );

        file = server.getLocalFile( "/classes/org/sysunit/util/ClasspathServer.class" );

        assertNotNull( "ClasspathServer.java is not null",
                       file );

        assertTrue( "ClasspathServer.java exists",
                    file.exists() );

        file = server.getLocalFile( "/classes/org/sysunit/cheese/Spoon" );

        assertNull( "there is no Spoon",
                    file );
    }

    public void testAccept_forJar()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        File junitJarFile = server.getLocalFile( "/junit-3.8.1.jar" );

        assertNotNull( "junit-3.8.1.jar is not null",
                       junitJarFile );

        URL url = new URL( "http://127.0.0.1:" + server.getPort() + "/junit-3.8.1.jar" );

        InputStream in = url.openStream();

        byte[] buf = new byte[1024];
        int len = 0;

        long totalBytes = 0;

        while ( ( len = in.read( buf,
                                 0,
                                 1024 ) ) > 0 )
        {
            totalBytes += len;
        }

        assertEquals( "remote junit.jar is same size as over http",
                      junitJarFile.length(),
                      totalBytes );

        server.stop();
    }

    public void testAccept_forClass()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        File classFile = server.getLocalFile( "/classes/org/sysunit/util/ClasspathServer.class" );

        assertNotNull( "ClasspathServer is not null",
                       classFile );

        URL url = new URL( "http://127.0.0.1:" + server.getPort() + "/classes/org/sysunit/util/ClasspathServer.class" );

        InputStream in = url.openStream();

        byte[] buf = new byte[1024];
        int len = 0;

        long totalBytes = 0;

        while ( ( len = in.read( buf,
                                 0,
                                 1024 ) ) > 0 )
        {
            totalBytes += len;
        }

        assertEquals( "remote class is same size as over http",
                      classFile.length(),
                      totalBytes );
        server.stop();
    }

    public void testAccept_NotFound()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        URL url = new URL( "http://127.0.0.1:" + server.getPort() + "/spoon.jar" );

        try
        {
            url.openStream();
            fail( "should have thrown IOException" );
        }
        catch (IOException e)
        {
            // expected and correct
        }

        server.stop();
    }

    public void testWithClassLoader_BareClass()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        int port = server.getPort();

        String[] relativeUrls = server.getRelativeUrls();

        URL[] urls = new URL[ relativeUrls.length ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = new URL( "http://127.0.0.1:" + port + relativeUrls[ i ] );
        }

        ClassLoader cl = new URLClassLoader( urls,
                                             null );

        Class theClass = cl.loadClass( "org.sysunit.util.ClasspathServer" );

        assertNotEquals( "ClasspathServer.class different from loaded through http",
                         ClasspathServer.class,
                         theClass );

        server.stop();
    }

    public void testWithClassLoader_ClassInJar()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        int port = server.getPort();

        String[] relativeUrls = server.getRelativeUrls();

        URL[] urls = new URL[ relativeUrls.length ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = new URL( "http://127.0.0.1:" + port + relativeUrls[ i ] );
        }

        ClassLoader cl = new URLClassLoader( urls,
                                             null );

        Class theClass = cl.loadClass( "junit.framework.TestCase" );

        assertNotEquals( "TestCase.class different from loaded through http",
                         junit.framework.TestCase.class,
                         theClass );

        server.stop();
    }

    public void testWithClassLoader_Resource()
        throws Exception
    {
        ClasspathServer server = new ClasspathServer( 1 );

        server.start();

        int port = server.getPort();

        String[] relativeUrls = server.getRelativeUrls();

        URL[] urls = new URL[ relativeUrls.length ];

        for ( int i = 0 ; i < urls.length ; ++i )
        {
            urls[ i ] = new URL( "http://127.0.0.1:" + port + relativeUrls[ i ] );
        }

        ClassLoader cl = new URLClassLoader( urls,
                                             null );

        URL theResource = cl.getResource( "junit/framework/TestCase.class" );

        URL theResourceHere = getClass().getResource( "junit/framework/TestCase.class" );

        assertNotEquals( "TestCase.class different from loaded through http",
                         theResource,
                         theResourceHere );
    }
}
