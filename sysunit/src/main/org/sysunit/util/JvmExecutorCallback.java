package org.sysunit.util;

public interface JvmExecutorCallback
{
    void notifyJvmFinished(JvmExecutor executor,
                           int exitValue);

    void notifyJvmInterrupted(JvmExecutor executor);

    void notifyJvmException(JvmExecutor executor,
                            Exception e);
}
