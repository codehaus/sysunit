package org.sysunit.util;

import java.util.Properties;

public class PropUtilsTest
    extends UtilTestBase
{
    public void testSetProperty_Primitives()
        throws Exception
    {
        SimpleBean bean = new SimpleBean();

        PropUtils.setProperty( bean,
                               "float",
                               "42.42" );

        assertEquals( "float property set to 42.42",
                      42.42,
                      bean.getFloat(),
                      0.1 );

        PropUtils.setProperty( bean,
                               "double",
                               "84.84" );

        assertEquals( "double property set to 84.84",
                      84.84,
                      bean.getDouble(),
                      0.1 );

        PropUtils.setProperty( bean,
                               "int",
                               "42" );

        assertEquals( "integer property set to 42",
                      42,
                      bean.getInt() );

        PropUtils.setProperty( bean,
                               "long",
                               "84" );

        assertEquals( "long property set to 84",
                      84,
                      bean.getLong() );

        PropUtils.setProperty( bean,
                               "boolean",
                               "true" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );

        bean.setBoolean( false );
                      
        PropUtils.setProperty( bean,
                               "boolean",
                               "yes" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );

        bean.setBoolean( false );
                      
        PropUtils.setProperty( bean,
                               "boolean",
                               "on" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );

    }

    public void testSetProperty_Objects()
        throws Exception
    {
        SimpleBean bean = new SimpleBean();

        PropUtils.setProperty( bean,
                               "floatObject",
                               "42.42" );

        assertEquals( "float property set to 42.42",
                      42.42,
                      bean.getFloat(),
                      0.1 );

        PropUtils.setProperty( bean,
                               "doubleObject",
                               "84.84" );

        assertEquals( "double property set to 84.84",
                      84.84,
                      bean.getDouble(),
                      0.1 );

        PropUtils.setProperty( bean,
                               "intObject",
                               "42" );

        assertEquals( "integer property set to 42",
                      42,
                      bean.getInt() );

        PropUtils.setProperty( bean,
                               "longObject",
                               "84" );

        assertEquals( "long property set to 84",
                      84,
                      bean.getLong() );

        PropUtils.setProperty( bean,
                               "booleanObject",
                               "true" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );

        bean.setBoolean( false );
                      
        PropUtils.setProperty( bean,
                               "booleanObject",
                               "yes" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );

        bean.setBoolean( false );
                      
        PropUtils.setProperty( bean,
                               "booleanObject",
                               "on" );

        assertTrue( "boolean value set to true",
                    bean.getBoolean() );


        PropUtils.setProperty( bean,
                               "string",
                               "cheese" );

        assertEquals( "string property set to 'cheese'",
                      "cheese",
                      bean.getString() );
    }

    public void testSetProperties_Primities()
        throws Exception
    {
        Properties props = new Properties();

        props.setProperty( "float", "42.42" );
        props.setProperty( "double", "84.84" );
        props.setProperty( "int", "42" );
        props.setProperty( "long", "84" );
        props.setProperty( "boolean", "true" );
        props.setProperty( "string", "cheese" );

        SimpleBean bean = new SimpleBean();

        PropUtils.setProperties( bean,
                                 props );

        
        assertEquals( "float property set to 42.42",
                      42.42,
                      bean.getFloat(),
                      0.1 );

        assertEquals( "double property set to 84.84",
                      84.84,
                      bean.getDouble(),
                      0.1 );

        assertEquals( "integer property set to 42",
                      42,
                      bean.getInt() );

        assertEquals( "long property set to 84",
                      84,
                      bean.getLong() );

        assertTrue( "boolean property set to true",
                    bean.getBoolean() );

        assertEquals( "string property set to 'cheese'",
                      "cheese",
                      bean.getString() );

    }

}
