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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sysunit.AlreadySynchronizedException;
import org.sysunit.SynchronizationException;
import org.sysunit.Synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Single-JVM <code>Synchronizer</code> implementation.
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class LocalSynchronizer
    implements Synchronizer {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    private static final Log log = LogFactory.getLog(LocalSynchronizer.class);

    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** <code>TBean</code> <code>String</code> identifiers. */
    private Set tbeanIds;

    /** <code>TBean</code> <code>String</code> identifiers that are waiting
     *  in a sync() call. */
    private Set waitingTBeanIds;

    private int waitCounter;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public LocalSynchronizer() {
        this.tbeanIds        = new HashSet();
        this.waitingTBeanIds = new HashSet();
        this.waitCounter     = 0;
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * Register a synchronizable <code>TBean</code> identifier.
     *
     * <p>
     * Registration is required for proper accounting of
     * sync cycles.
     * </p>
     *
     * @param tbeanId The identifier of the synchronizable TBean.
     */
    public synchronized void registerSynchronizableTBean(String tbeanId) {
        this.tbeanIds.add( tbeanId );
    }

    /**
     * Unregister a synchronizable <code>TBean</code> identifier.
     *
     * <p>
     * Unregistration should occur when the <code>TBean</code>
     * terminates (for any reason) in order to maintain proper
     * accounting on sync cycles.
     * </p>
     *
     * @param tbeanId The identifier of the synchronizable TBean.
     */
    public synchronized void unregisterSynchronizableTBean(String tbeanId) {
        this.tbeanIds.remove( tbeanId );
        this.waitingTBeanIds.remove( tbeanId );

        checkUnblock();
    }

    protected String[] getRegisteredTBeans() {
        return (String[]) this.tbeanIds.toArray( EMPTY_STRING_ARRAY );
    }

    protected String[] getWaitingTBeans() {
        return (String[]) this.waitingTBeanIds.toArray( EMPTY_STRING_ARRAY );
    }

    protected synchronized boolean isWaitingTBean(String tbeanId) {
        return this.waitingTBeanIds.contains( tbeanId );
    }

    protected synchronized void addWaitingTBean(String tbeanId) {
        this.waitingTBeanIds.add( tbeanId );
    }


    /**
     * @see Synchronizer
     */
    public synchronized void sync(String tbeanId,
                                  String syncPointName)
        throws SynchronizationException, InterruptedException {
        
        if ( isWaitingTBean( tbeanId ) ) {
            throw new AlreadySynchronizedException( tbeanId,
                                                    syncPointName );
        }
            
        addWaitingTBean( tbeanId );

        int waiter = this.waitCounter;

        checkUnblock();
            
        while ( waiter == this.waitCounter ) {
            wait();
        }
    }

    private void checkUnblock() {
        if ( shouldUnblock() ) {
            unblockAll();
        }
    }

    private boolean shouldUnblock() {
        return this.waitingTBeanIds.size() == tbeanIds.size();
    }

    public synchronized void unblockAll() {
        ++this.waitCounter;
        this.waitingTBeanIds.clear();
        notifyAll();
    }

    public void error(String tbeanId) {
        unregisterSynchronizableTBean( tbeanId );
    }
}
