package org.sysunit.util;

public interface CheckpointCallback {
    void notify(Checkpoint checkpoint)
        throws Exception;
}
