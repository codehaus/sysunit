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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Creates a nested property via calling a beans createFoo() method then
 * either calling the setFoo(value) or addFoo(value) methods in a similar way
 * to how Ant tags construct themselves.
 * 
 * @author <a href="mailto:james.strachan@spiritsoft.com">James Strachan</a>
 * @version $Revision$
 */
public class TBeanPropertyTag extends TBeanTag {

    /** empty arguments constant */
    private static final Object[] EMPTY_ARGS = {};
    
    /** empty argument types constant */
    private static final Class[] EMPTY_ARG_TYPES = {};

    /** the name of the create method */
    private String createMethodName;

    
    public TBeanPropertyTag(String tagName) {
        super(Object.class, tagName);

        if (tagName.length() > 0) {
            createMethodName = "create" 
                + tagName.substring(0,1).toUpperCase() 
                + tagName.substring(1);
        }
    }
    
    /**
     * Creates a new instance by calling a create method on the parent bean
     */
    protected Object newInstance(Class theClass, Map attributes, XMLOutput output) throws JellyTagException {
        Object parentObject = getParentObject();
        if (parentObject != null) {
            // now lets try call the create method...
            Class parentClass = parentObject.getClass();
            Method method = findCreateMethod(parentClass);
            if (method != null) {
                try {
                    return method.invoke(parentObject, EMPTY_ARGS);
                }
                catch (Exception e) {
                    throw new JellyTagException( "failed to invoke method: " + method + " on bean: " + parentObject + " reason: " + e, e );
                }
            }
            else {
                Class tagClass = theClass;
                if(tagClass == Object.class)
                    tagClass = findAddMethodClass(parentClass);
                if(tagClass == null)
                    throw new JellyTagException("unable to infer element class for tag "+getTagName());

                return super.newInstance(tagClass, attributes, output) ;
            }
        }
        throw new JellyTagException("The " + getTagName() + " tag must be nested within a tag which maps to a BeanSource implementor");
    }

    /**
     * finds the parameter type of the first public method in the parent class whose name
     * matches the add{tag name} pattern, whose return type is void and which takes
     * one argument only.
     * @param parentClass
     * @return the class of the first and only parameter
     */
    protected Class findAddMethodClass(Class parentClass) {
        Method[] methods = parentClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if(Modifier.isPublic(method.getModifiers())) {
                Class[] args = method.getParameterTypes();
                if (method.getName().equals(addMethodName)
                      && java.lang.Void.TYPE.equals(method.getReturnType())
                      && args.length == 1
                      && !java.lang.String.class.equals(args[0])
                      && !args[0].isArray()
                      && !args[0].isPrimitive())
                    return args[0];
            }
        }
        return null;
    }

    /**
     * Finds the Method to create a new property object
     */
    protected Method findCreateMethod(Class theClass) {
        if (createMethodName == null) {
            return null;
        }
        return MethodUtils.getAccessibleMethod(
            theClass, createMethodName, EMPTY_ARG_TYPES
        );
    }    
}
