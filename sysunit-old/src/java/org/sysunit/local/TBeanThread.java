package org.sysunit.local;

/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The SpiritSoft Software License, Version 1.0
 *
 * Copyright (c) 2003 SpiritSoft, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        SpiritSoft, Inc (http://www.spiritsoft.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "SysUnit" and "SpiritSoft" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission. 
 *    For written permission, please contact info@spiritsoft.com.
 *
 * 5. Products derived from this software may not be called "SysUnit"
 *    nor may "SysUnit" appear in their names without prior written
 *    permission of the SpiritSoft, Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL SPIRITSOFT, INC. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the SpiritSoft, Inc.  For more
 * information on the SpiritSoft, Inc, please see
 * <http://www.spiritsoft.com/>.
 *
 */

import org.sysunit.SynchronizableTBean;
import org.sysunit.TBean;
import org.sysunit.TBeanSynchronizer;
import org.sysunit.Synchronizer;
import org.sysunit.SynchronizationException;

public class TBeanThread
    extends Thread {

    public static final TBeanThread[] EMPTY_ARRAY = new TBeanThread[0];

    private String tbeanId;
    private TBean tbean;
    private Synchronizer synchronizer;
    private Barrier beginBarrier;
    private Barrier endBarrier;
    private Throwable error;
    private boolean isDone;

    public TBeanThread(String tbeanId,
                       TBean tbean,
                       Synchronizer synchronizer,
                       Barrier beginBarrier,
                       Barrier endBarrier) {
        super( "tbean-thread." + tbeanId );
        this.tbeanId      = tbeanId;
        this.tbean        = tbean;
        this.synchronizer = synchronizer;
        this.beginBarrier = beginBarrier;
        this.endBarrier   = endBarrier;
        this.isDone       = false;
    }

    public String getTBeanId() {
        return this.tbeanId;
    }

    public TBean getTBean() {
        return this.tbean;
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }

    public Barrier getBeginBarrier() {
        return this.beginBarrier;
    }

    public Barrier getEndBarrier() {
        return this.endBarrier;
    }
    
    public void run() {
        setUpSynchronizer();

        try {
            getBeginBarrier().block();
        } catch (InterruptedException e) {
            return;
        }

        try {
            tbean.setUp();
            tbean.run();
        } catch (Throwable e) {
            setError( e );
            getSynchronizer().error( getTBeanId() );
        } finally {
            if ( getTBean() instanceof SynchronizableTBean ) {
                try {
                    getSynchronizer().unregisterSynchronizableTBean( getTBeanId() );
                } catch (SynchronizationException e) {
                    setError( e );
                }
            }
            synchronized ( this ) {
                this.isDone = true;
            }
            try {
                tbean.tearDown();
            } catch (Throwable t) {
                setError( t );
            }
            try {
                getEndBarrier().block();
            } catch (InterruptedException e) {
                // swallow and exit
            }
        }
    }

    public synchronized boolean isDone() {
        if ( ! isAlive() ) {
            return false;
        }

        return this.isDone;
    }

    public boolean hasError() {
        return ( this.error != null );
    }

    public Throwable getError() {
        return this.error;
    }

    protected void setError(Throwable error) {
        this.error = error;
    }

    protected void setUpSynchronizer() {
        if ( getTBean() instanceof SynchronizableTBean ) {
            ((SynchronizableTBean)getTBean()).setSynchronizer( new TBeanSynchronizer( getTBeanId(),
                                                                                      getSynchronizer() ) );
        }
    }
}
