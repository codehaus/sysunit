package org.sysunit.builder;

import org.sysunit.model.DistributedSystemTestInfo;
import org.sysunit.model.JvmInfo;
import org.sysunit.model.TBeanInfo;
import org.sysunit.model.ThreadInfo;

import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DistributedSystemTestInfoBuilder
    extends DefaultHandler
{
    private DistributedSystemTestInfo testInfo;
    private JvmInfo jvmInfo;
    private TBeanInfo tbeanInfo;
    private ThreadInfo threadInfo;

    protected DistributedSystemTestInfoBuilder()
    {
    }

    protected DistributedSystemTestInfo getSystemTestInfo()
    {
        return this.testInfo;
    }

    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes attrs)
        throws SAXException
    {
        if ( localName.equals( "systemTest" ) )
        {
            startSystemTest( attrs );
        }
        else if ( localName.equals( "jvm" ) )
        {
            startJvm( attrs );
        }
        else if ( localName.equals( "tbean" ) )
        {
            startTBean( attrs );
        }
        else
        {
            throw new SAXException( "unknown element: " + localName );
        }
    }

    public void endElement(String uri,
                           String localName,
                           String qName)
        throws SAXException
    {
        if ( localName.equals( "systemTest" ) )
        {
            endSystemTest();
        }
        else if ( localName.equals( "jvm" ) )
        {
            endJvm();
        }
        else if ( localName.equals( "tbean" ) )
        {
            endTBean();
        }
        else
        {
            throw new SAXException( "unknown element: " + localName );
        }
    }

    public void startSystemTest(Attributes attrs)
        throws SAXException
    {
        String name = requiredAttribute( "systemTest",
                                         "name",
                                         attrs );

        this.testInfo = new DistributedSystemTestInfo( name );

        long timeout = optionalAttribute( "timeout",
                                          attrs,
                                          0L );

        this.testInfo.setTimeout( timeout );

    }

    public void endSystemTest()
    {
    }

    public void startJvm(Attributes attrs)
        throws SAXException
    {
        String name = requiredAttribute( "jvm",
                                         "name",
                                         attrs );
        
        int count = optionalAttribute( "count",
                                       attrs,
                                       1 );

        this.jvmInfo = new JvmInfo( name,
                                    count );
    }

    public void endJvm()
    {
        this.testInfo.addJvm( this.jvmInfo );
        this.jvmInfo = null;
    }

    public void startTBean(Attributes attrs)
        throws SAXException
    {
        String className = requiredAttribute( "tbean",
                                              "className",
                                              attrs );

        Properties properties = getProperties( attrs,
                                               new String[] { "className", "count" } );

        int count = optionalAttribute( "count",
                                       attrs,
                                       1 );

        this.tbeanInfo = new TBeanInfo( className,
                                        properties,
                                        count );
    }

    public void endTBean()
    {
        this.jvmInfo.addTBean( this.tbeanInfo );
        this.tbeanInfo = null;
    }

    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

    protected Properties getProperties(Attributes attrs,
                                       String[] exclude)
    {
        Properties properties = new Properties();

        int len = attrs.getLength();

      ATTR_LOOP:
        for ( int i = 0 ; i < len ; ++i )
        {
            String name = attrs.getLocalName( i );

            for ( int j = 0 ; j < exclude.length ; ++j )
            {
                if ( name.equals( exclude[j] ) )
                {
                    continue ATTR_LOOP;
                }
            }

            String value = attrs.getValue( i );

            properties.setProperty( name,
                                    value );
        }

        return properties;
    }

    protected String requiredAttribute(String element,
                                       String name,
                                       Attributes attrs)
        throws SAXException
    {
        String value = attrs.getValue( name );

        if ( value == null
             ||
             value.trim().equals( "" ) )
        {
            throw new SAXException( "attribute '" + name + "' required on <" + element + ">" );
        }

        return value.trim();
    }

    protected String optionalAttribute(String name,
                                       Attributes attrs)
    {
        String value = attrs.getValue( name );

        if ( value == null
             ||
             value.trim().equals( "" ) )
        {
            return null;
        }

        return value.trim();
    }

    protected int optionalAttribute(String name,
                                    Attributes attrs,
                                    int defaultValue)
        throws SAXException
    {
        String intStr = optionalAttribute( name,
                                           attrs );

        if ( intStr == null )
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt( intStr );
        }
        catch (NumberFormatException e)
        {
            throw new SAXException( e );
        }
    }
    
    protected long optionalAttribute(String name,
                                     Attributes attrs,
                                     long defaultValue)
        throws SAXException
    {
        String longStr = optionalAttribute( name,
                                            attrs );

        if ( longStr == null )
        {
            return defaultValue;
        }

        try
        {
            return Long.parseLong( longStr );
        }
        catch (NumberFormatException e)
        {
            throw new SAXException( e );
        }
    }
    
    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------

    public static DistributedSystemTestInfo build(File in)
        throws IOException, SAXException, ParserConfigurationException
    {
        return build( new InputSource( new FileInputStream( in ) ) );
    }

    public static DistributedSystemTestInfo build(InputStream in)
        throws IOException, SAXException, ParserConfigurationException
    {
        return build( new InputSource( in ) );
    }

    public static DistributedSystemTestInfo build(InputSource in)
        throws IOException, SAXException, ParserConfigurationException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        factory.setNamespaceAware( true );

        SAXParser parser = factory.newSAXParser();

        DistributedSystemTestInfoBuilder builder = new DistributedSystemTestInfoBuilder();

        parser.parse( in, builder );

        return builder.getSystemTestInfo();
    }
}
