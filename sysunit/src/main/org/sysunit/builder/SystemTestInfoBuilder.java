package org.sysunit.builder;

import org.sysunit.SystemTestCase;
import org.sysunit.TBean;
import org.sysunit.model.SystemTestInfo;
import org.sysunit.model.ThreadInfo;
import org.sysunit.model.TBeanFactoryInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SystemTestInfoBuilder
{
    public static SystemTestInfo build(SystemTestCase systemTestCase)
    {
        SystemTestInfo systemTestInfo = new SystemTestInfo( systemTestCase );

        buildThreads( systemTestInfo,
                      systemTestCase );

        buildTBeanFactories( systemTestInfo,
                             systemTestCase );

        return systemTestInfo;
    }

    protected static void buildThreads(SystemTestInfo systemTestInfo,
                                       SystemTestCase systemTestCase)
    {
        Class testCaseClass = systemTestCase.getClass();

        Method[] methods = testCaseClass.getMethods();

        for ( int i = 0 ; i < methods.length ; ++i )
        {
            if ( methods[i].getName().startsWith( "thread" ) )
            {
                if ( Modifier.isPublic( methods[i].getModifiers() )
                     &&
                     ! Modifier.isStatic( methods[i].getModifiers() )
                     &&
                     methods[i].getReturnType().equals( Void.TYPE ) )
                {
                    systemTestInfo.addThread( new ThreadInfo( systemTestCase,
                                                              methods[i] ) );
                }
            }
        }
    }

    protected static void buildTBeanFactories(SystemTestInfo systemTestInfo,
                                              SystemTestCase systemTestCase)
    {
        Class testCaseClass = systemTestCase.getClass();

        Method[] methods = testCaseClass.getMethods();
        
        for ( int i = 0 ; i < methods.length ; ++i )
        {
            if ( methods[i].getName().startsWith( "tbean" ) )
            {
                if ( Modifier.isPublic( methods[i].getModifiers() )
                     &&
                     ! Modifier.isStatic( methods[i].getModifiers() )
                     &&
                     methods[i].getReturnType().equals( TBean.class ) )
                {
                    systemTestInfo.addTBeanFactory( new TBeanFactoryInfo( systemTestCase,
                                                                          methods[i] ) );
                }
            }
        }
    }
}
