package org.sysunit.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PropUtils
{
    public static void setProperty(Object bean,
                                   String propName,
                                   String propValue)
        throws InvalidPropertyException
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

                        if ( paramClass.equals( Integer.TYPE )
                             ||
                             paramClass.equals( Integer.class ) )
                        {
                            
                        }
                        else if (paramClass.equals( Float.TYPE )
                                 ||
                                 paramClass.equals( Float.class ) )
                        {

                        }
                        else if ( paramClass.equals( Long.TYPE )
                                  ||
                                  paramClass.equals( Long.class ) )
                        {

                        }
                        else if ( paramClass.equals( Boolean.TYPE )
                                  ||
                                  paramClass.equals( Boolean.class ) )
                        {

                        }
                        else if ( paramClass.equals( String.class ) )
                        {

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
