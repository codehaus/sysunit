package org.sysunit.model;

import org.sysunit.SystemTestCase;

import java.util.Set;
import java.util.HashSet;

public class SystemTestInfo
{
    private static final TBeanFactoryInfo[] EMPTY_TBEANFACTORYINFO_ARRAY = new TBeanFactoryInfo[0];
    private static final ThreadInfo[] EMPTY_THREADINFO_ARRAY = new ThreadInfo[0];

    private SystemTestCase systemTestCase;

    private Set tbeanFactories;
    private Set threads;

    public SystemTestInfo(SystemTestCase systemTestCase)
    {
        this.systemTestCase = systemTestCase;
        this.threads = new HashSet();
        this.tbeanFactories = new HashSet();
    }

    public SystemTestCase getSystemTestCase()
    {
        return this.systemTestCase;
    }

    public void addTBeanFactory(TBeanFactoryInfo tbeanFactoryInfo)
    {
        this.tbeanFactories.add( tbeanFactoryInfo );
    }

    public TBeanFactoryInfo[] getTBeanFactories()
    {
        return (TBeanFactoryInfo[]) this.tbeanFactories.toArray( EMPTY_TBEANFACTORYINFO_ARRAY );
    }

    public void addThread(ThreadInfo threadInfo)
    {
        this.threads.add( threadInfo );
    }

    public ThreadInfo[] getThreads()
    {
        return (ThreadInfo[]) this.threads.toArray( EMPTY_THREADINFO_ARRAY );
    }

}
