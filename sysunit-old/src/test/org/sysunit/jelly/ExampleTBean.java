/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.jelly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sysunit.AbstractSynchronizableTBean;

public class ExampleTBean extends AbstractSynchronizableTBean {

	private static List singletonExtent = Collections.synchronizedList(new ArrayList());
	
    private String foo;
    private long sleepTime;
    private boolean hasRun = false;

	/**
	 * @return the extent of all instances of this class
	 */
	public static List extent() {
		return singletonExtent;
	}

    public ExampleTBean() {
        extent().add(this);
    }

    public void run() throws Exception {
        Thread.sleep(this.sleepTime);
        this.hasRun = true;
    }

    public boolean hasRun() {
        return this.hasRun;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String toString() {
        return super.toString() + "[foo=" + foo + ";sleepTime=" + sleepTime + "]";
    }
}
