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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.impl.CollectionTag;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * Creates a TBean to add to the current JVM.
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version   $Revision$
 */
public class TBeanTag extends UseBeanTag {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(TBeanTag.class);

    protected static final Object[] EMPTY_ARGUMENTS = {};

    /** the name of the property to create */
    private String tagName;

    /** the name of the adder method */
    protected String addMethodName;

    /** if present this is used to call a doit method when the bean is constructed */
    private Method invokeMethod;


    public TBeanTag() {
        this(null, "bean", null);
    }

    public TBeanTag(Class defaultClass, String tagName) {
        this(defaultClass, tagName, null);
    }

    public TBeanTag(Class defaultClass, String tagName, Method invokeMethod) {
        super(defaultClass);
        this.tagName = tagName;
        this.invokeMethod = invokeMethod;
        
        if (tagName.length() > 0) {
            addMethodName = "add" 
                + tagName.substring(0,1).toUpperCase() 
                + tagName.substring(1);
        }
    }

    /**
     * @return the local name of the XML tag to which this tag is bound
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Output the tag as a named variable. If the parent bean has an adder or setter
     * method then invoke that to register this bean with its parent.
     */
    protected void processBean(String var, Object bean) throws JellyTagException {
        if (var != null) {
            context.setVariable(var, bean);
        }
        
        // now lets try set the parent property via calling the adder or the setter method
        if (bean != null) {
            Tag parent = this;
            
            while (true) {
                parent = parent.getParent();
                if (parent == null) {
                    break;
                }
                
                if (parent instanceof BeanSource) {
                    BeanSource source = (BeanSource) parent;
                    Object parentObject = source.getBean();
                    if (parentObject != null) {
                        if (parentObject instanceof Collection) {
                            Collection collection = (Collection) parentObject;
                            collection.add(bean);
                        }
                        else {
                            // lets see if there's a setter method...
                            Method method = findAddMethod(parentObject.getClass(), bean.getClass());
                            if (method != null) {
                                Object[] args = { bean };
                                try {
                                    method.invoke(parentObject, args);
                                }
                                catch (Exception e) {
                                    throw new JellyTagException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                                }
                            }
                            else {
                                try {
                                  BeanUtils.setProperty(parentObject, tagName, bean);
                                } catch (IllegalAccessException e) {
                                    throw new JellyTagException(e);
                                } catch (InvocationTargetException e) {
                                    throw new JellyTagException(e);
                                }
                            }
                        }
                    }
                    else {
                        log.warn("Cannot process null bean for tag: " + parent);
                    }
                }
                else if (parent instanceof CollectionTag) {
                    CollectionTag tag = (CollectionTag) parent;
                    tag.addItem(bean);
                }
                else {
                    continue;
                }
                break;
            }

            if (invokeMethod != null) {
                Object[] args = { bean };
                try {
                    invokeMethod.invoke(bean, EMPTY_ARGUMENTS);
                }
                catch (Exception e) {
                    throw new JellyTagException( "failed to invoke method: " + invokeMethod + " on bean: " + bean + " reason: " + e, e );
                }
            }
            else {
                if (parent == null && var == null) { 
                    //warn if the bean gets lost in space
                    log.warn( "Could not add bean to parent for bean: " + bean );
                }
            }
        }
    }

    /**
     * Finds the Method to add the new bean
     */
    protected Method findAddMethod(Class beanClass, Class valueClass) {
        if (addMethodName == null) {
            return null;
        }
        Class[] argTypes = { valueClass };
        return MethodUtils.getAccessibleMethod(
            beanClass, addMethodName, argTypes
        );
    }
        
        
    /**
     * @return the parent bean object
     */
    protected Object getParentObject() throws JellyTagException {
        BeanSource tag = (BeanSource) findAncestorWithClass(BeanSource.class);
        if (tag != null) {
            return tag.getBean();
        }
        return null;
    }        
}
