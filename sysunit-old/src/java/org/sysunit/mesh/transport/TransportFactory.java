package org.sysunit.mesh.transport;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TransportFactory {

    public static final String TRANSPORT_CONFIG_PROPERTY = "org.sysunit.mesh.transport.config";
    public static final String DEFAULT_TRANSPORT_CONFIG = "org/sysunit/mesh/transport/default.properties";

    private static final TransportFactory INSTANCE = new TransportFactory();

    public static TransportFactory getInstance() {
        return INSTANCE;
    }

    public Transport initializeTransport()
        throws IOException, TransportException {

        String transportConfig = System.getProperty( TRANSPORT_CONFIG_PROPERTY );

        if ( transportConfig == null ) {
            transportConfig = DEFAULT_TRANSPORT_CONFIG;
        }

        InputStream transportConfigIn = getClass().getClassLoader().getResourceAsStream( transportConfig );

        try {
            return initializeTransport( transportConfigIn );
        } finally {
            transportConfigIn.close();
        }
    }

    public Transport initializeTransport(File transportConfig)
        throws IOException, TransportException {

        FileInputStream transportConfigIn = new FileInputStream( transportConfig );

        try {
            return initializeTransport( transportConfigIn );
        } finally {
            transportConfigIn.close();
        }
    }

    public Transport initializeTransport(InputStream transportConfig) 
        throws IOException, TransportException {

        Properties transportProps = new Properties();

        transportProps.load( transportConfig );

        String transportClassName = transportProps.getProperty( "class" );

        try {
            Class transportClass = getClass().getClassLoader().loadClass( transportClassName );
            
            Transport transport = (Transport) transportClass.newInstance();
            
            return transport;
        } catch (ClassNotFoundException e) {
            throw new TransportException( e );
        } catch (InstantiationException e) {
            throw new TransportException( e );
        } catch (IllegalAccessException e) {
            throw new TransportException( e );
        }
    }
}
