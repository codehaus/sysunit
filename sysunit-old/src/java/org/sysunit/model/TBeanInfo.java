package org.sysunit.model;

import org.sysunit.TBeanFactory;

import java.io.Serializable;

public class TBeanInfo
    implements Serializable {

    public static final TBeanInfo[] EMPTY_ARRAY = new TBeanInfo[0];

    private String id;
    private TBeanFactory tbeanFactory;

    public TBeanInfo(String id,
                     TBeanFactory tbeanFactory) {
        this.id = id;
        this.tbeanFactory = tbeanFactory;
    }

    public String getId() {
        return this.id;
    }

    public TBeanFactory getTBeanFactory() {
        return this.tbeanFactory;
    }
}
