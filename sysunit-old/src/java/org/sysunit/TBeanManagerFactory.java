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

import org.sysunit.local.LocalTBeanManager;

/**
 * Entry-point for creating a <code>TBeanMaager</code>.
 *
 * <p>
 * The property <code>org.sysunit.TBeanManager</code> is inspected
 * and if non-null, the value is used as the default <code>TBeanManager</code>
 * class for running tests.
 * </p>
 *
 * <p>
 * If the <code>org.sysunit.TBeanManager</code> property is not set,
 * then the default <code>LocalTBeanManager</code. class is used.
 * </p.
 *
 * @see TBeanManager
 *
 * @author Bob McWhirter
 *
 * @version $Id$
 */
public class TBeanManagerFactory {

    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    /** Default <code>TBeanManager</code> class name. */
    public static final String DEFAULT_CLASSNAME = LocalTBeanManager.class.getName();

    // ----------------------------------------------------------------------
    //     Class Methods
    // ----------------------------------------------------------------------

    /**
     * Create a new <code>TBeanManager</code>.
     *
     * <p>
     * Inspects the value of <code>org.sysunit.TBeanManager</code> property
     * if set, and returns a new instance of the specified class, otherwise
     * the {@link #DEFAULT_CLASSNAME} specifies that <code>LocalTBeanManager</code>
     * will be used.
     * </p>
     *
     * @return The new <code>TBeanManager</code> instance.
     *
     * @throws Exception If an error occurs while attempting to lookup
     *         the class and instantiate the object.
     */
    public static TBeanManager newTBeanManager()
        throws Exception {

        String classname = System.getProperty( TBeanManager.class.getName() );

        if ( classname == null
             ||
             "".equals( classname.trim() ) ) {
            classname = DEFAULT_CLASSNAME;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if ( cl == null ) {
            cl = TBeanManagerFactory.class.getClassLoader();
        }

        Class managerClass = cl.loadClass( classname );

        TBeanManager manager = (TBeanManager) managerClass.newInstance();

        return manager;
    }
}
