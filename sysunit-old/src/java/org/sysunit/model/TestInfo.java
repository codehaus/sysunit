package org.sysunit.model;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class TestInfo
    implements Serializable {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private String id;
    private long timeoutMs;
    private Map logicalMachinesInfo;

    public TestInfo(String id) {
        this.id = id;
        this.timeoutMs = 0;
        this.logicalMachinesInfo = new HashMap();
    }

    public String getId() {
        return this.id;
    }

    public void setTimeout(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public long getTimeout() {
        return this.timeoutMs;
    }

    public void addLogicalMachineInfo(LogicalMachineInfo logicalMachineInfo) {
        this.logicalMachinesInfo.put( logicalMachineInfo.getId(),
                                      logicalMachineInfo );
    }

    public LogicalMachineInfo[] getLogicalMachinesInfo() {
        return (LogicalMachineInfo[]) this.logicalMachinesInfo.values().toArray( LogicalMachineInfo.EMPTY_ARRAY );
    }

    public LogicalMachineInfo getLogicalMachineInfo(String id) {
        return (LogicalMachineInfo) this.logicalMachinesInfo.get( id );
    }

    public String[] getLogicalMachineIds() {
        return (String[]) this.logicalMachinesInfo.keySet().toArray( EMPTY_STRING_ARRAY );
    }
}
