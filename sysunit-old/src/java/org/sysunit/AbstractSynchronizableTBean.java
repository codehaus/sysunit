package org.sysunit;

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

/**
 * Abstract <code>SynchronizableTBean</code> base class.
 *
 * <p>
 * This abstract base class provides management methods for
 * <code>TBeanSynchronizer</code> control.
 * </p>
 *
 * @see #setSynchronizer
 * @see #getSynchronizer
 * @see #sync
 *
 * @author Bob McWhirter
 */
public abstract class AbstractSynchronizableTBean
    extends AbstractTBean
    implements SynchronizableTBean {

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** The synchronizer. */
    private TBeanSynchronizer synchronizer;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     */
    public AbstractSynchronizableTBean() {
        // intentionally left blank
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * @see SynchronizableTBean
     */
    public void setSynchronizer(TBeanSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    /**
     * Retrieve the <code>TBeanSynchronizer</code>.
     *
     *  @return The synchronizer.
     */
    public TBeanSynchronizer getSynchronizer() {
        return this.synchronizer;
    }

    /**
     * Synchronize at a syncpoint.
     *
     * <p>
     * Synchronize at a named sync-point by blocking
     * until all other synchronizable TBeans have synchronized
     * or terminated.
     * </p>
     *
     * @param syncPoint The sync-point id.
     *
     * @throws SynchronizationException If an error occurs while
     *         blocking for synchronization.
     * @throws InterruptedException If an interrupted signal is
     *         received while blocking for synchronization.
     */
    protected void sync(String syncPoint)
        throws SynchronizationException, InterruptedException {
        getSynchronizer().sync( syncPoint );
    }
}
