package org.sysunit.plan;

import org.sysunit.model.DistributedSystemTestInfo;

import java.util.Map;
import java.util.HashMap;

public class TestPlan
{
    private static final JvmBinding[] EMPTY_JVMBINDING_ARRAY = new JvmBinding[0];

    private DistributedSystemTestInfo systemTest;

    private Map jvmBindings;

    public TestPlan(DistributedSystemTestInfo systemTest)
    {
        this.systemTest  = systemTest;
        this.jvmBindings = new HashMap();
    }

    public DistributedSystemTestInfo getSystemTest()
    {
        return this.systemTest;
    }

    public void addJvmBinding(JvmBinding jvmBinding)
    {
        this.jvmBindings.put( jvmBinding.getJvmId() + "",
                              jvmBinding );
    }

    public JvmBinding[] getJvmBindings()
    {
        return (JvmBinding[]) this.jvmBindings.values().toArray( EMPTY_JVMBINDING_ARRAY );
    }

    public JvmBinding getJvmBinding(int jvmId)
    {
        return (JvmBinding) this.jvmBindings.get( jvmId + "" );
    }
}
