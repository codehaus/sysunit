package org.sysunit.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

public class PropUtils
{
    public static void setProperty(Object bean,
                                   String propName,
                                   String propValue)
        throws InvalidPropertyException, IllegalAccessException, InvocationTargetException
    {
        String methodName = "set" + propName.substring( 0, 1 ).toUpperCase();

        if ( propName.length() > 1 )
        {
            methodName += propName.substring( 1 );
        }

        Class beanClass = bean.getClass();

        Method[] methods = beanClass.getMethods();

        boolean propertySet = false;

      METHOD_LOOP:
        for ( int i = 0 ; i < methods.length ; ++i )
        {
            if ( methods[ i ].getName().equals( methodName ) )
            {
                if ( Modifier.isPublic( methods[ i ].getModifiers() )
                     &&
                     ! Modifier.isStatic( methods[ i ].getModifiers() ) )
                {
                    if ( methods[ i ].getParameterTypes().length == 1 )
                    {
                        Class paramClass = methods[ i ].getParameterTypes()[0];

                        if ( paramClass.equals( int.class )
                             ||
                             paramClass.equals( Integer.class ) )
                        {
                            Integer value = new Integer( propValue );

                            methods[ i ].invoke( bean,
                                                 new Object[] { value } );
                        }
                        else if (paramClass.equals( float.class )
                                 ||
                                 paramClass.equals( Float.class ) )
                        {
                            Float value = new Float( propValue );

                            methods[ i ].invoke( bean,
                                                 new Object[] { value } );
                        }
                        else if ( paramClass.equals( double.class )
                                  ||
                                  paramClass.equals( Double.class ) )
                        {
                            Double value = new Double( propValue );

                            methods[ i ].invoke( bean,
                                                 new Object[] { value } );
                        }
                        else if ( paramClass.equals( long.class )
                                  ||
                                  paramClass.equals( Long.class ) )
                        {
                            Long value = new Long( propValue );

                            methods[ i ].invoke( bean,
                                                 new Object[] { value } );
                        }
                        else if ( paramClass.equals( boolean.class )
                                  ||
                                  paramClass.equals( Boolean.class ) )
                        {
                            Boolean value = Boolean.FALSE;

                            if ( propValue.equals( "on" )
                                 ||
                                 propValue.equals( "true" )
                                 ||
                                 propValue.equals( "yes" ) )
                            {
                                value = Boolean.TRUE;
                            }
                            
                            methods[ i ].invoke( bean,
                                                 new Object[] { value } );
                        }
                        else if ( paramClass.equals( String.class ) )
                        {
                            methods[ i ].invoke( bean,
                                                 new Object[] { propValue } );
                        }
                    }
                }
            }

            if ( propertySet )
            {
                return;
            }
        }

        throw new InvalidPropertyException( beanClass,
                                            propName );
    }
}
