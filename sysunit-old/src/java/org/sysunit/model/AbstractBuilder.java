package org.sysunit.model;

import org.sysunit.TBeanFactory;

public abstract class AbstractBuilder
    implements Builder {

    private TestInfo testInfo;
    private LogicalMachineInfo currentLogicalMachineInfo;

    public AbstractBuilder() {
    }

    protected void newTestInfo(String id) {
        this.testInfo = new TestInfo( id );
    }

    protected TestInfo getTestInfo() {
        return this.testInfo;
    }

    protected void newLogicalMachineInfo(String id) {
        LogicalMachineInfo logicalMachineInfo = new LogicalMachineInfo( id );
        getTestInfo().addLogicalMachineInfo( logicalMachineInfo );
        this.currentLogicalMachineInfo = logicalMachineInfo;
    }

    protected LogicalMachineInfo getCurrentLogicalMachineInfo() {
        return this.currentLogicalMachineInfo;
    }

    protected void newTBeanInfo(String id,
                                TBeanFactory tbeanFactory) {
        
        TBeanInfo tbeanInfo = new TBeanInfo( id,
                                             tbeanFactory );

        LogicalMachineInfo logicalMachineInfo = getCurrentLogicalMachineInfo();

        if ( logicalMachineInfo == null ) {
            newLogicalMachineInfo( "default" );
        }

        logicalMachineInfo = getCurrentLogicalMachineInfo();

        logicalMachineInfo.addTBeanInfo( tbeanInfo );
    }

}
