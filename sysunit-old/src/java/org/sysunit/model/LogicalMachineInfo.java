package org.sysunit.model;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class LogicalMachineInfo
    implements Serializable {

    public static final LogicalMachineInfo[] EMPTY_ARRAY = new LogicalMachineInfo[0];

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private String id;

    private Map tbeansInfo;

    public LogicalMachineInfo(String id) {
        this.id = id;
        this.tbeansInfo = new HashMap();
    }

    public String getId() {
        return this.id;
    }

    public void addTBeanInfo(TBeanInfo tbeanInfo) {
        this.tbeansInfo.put( tbeanInfo.getId(),
                             tbeanInfo );
    }

    public TBeanInfo[] getTBeansInfo() {
        return (TBeanInfo[]) this.tbeansInfo.values().toArray( TBeanInfo.EMPTY_ARRAY );
    }
}
