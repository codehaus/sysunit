package org.sysunit.util;

public interface TBeanThreadCallback
{
    void notifySetUp(TBeanThread thread);
    void notifyRun(TBeanThread thread);
    void notifyAssertValid(TBeanThread thread);
    void notifyTearDown(TBeanThread thread);
}
