/*
 * Copyright (C) SpiritSoft, Inc. All rights reserved.
 *
 * This software is published under the terms of the SpiritSoft Software License
 * version 1.0, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * $Id$
 */
package org.sysunit.transport.local;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * A multithreaded blocking queue, useful for implementing producer-consumer style threading patterns.
 *
 * @author James Strachan
 * @version $Revision$
 */
public class MTQueue {
	private static final Log log = LogFactory.getLog(MTQueue.class);

    private LinkedList list = new LinkedList();
    private long defaultTimeout = 10000;
    private Object peekSemaphore = new Object();

    public MTQueue() {
    }

    /**
     * Returns the current number of object in the queue
     */
    public synchronized int size() {
        return list.size();
    }

    /** 
     * adds a new object to the end of the queue
     */
    public synchronized void add(Object object) {
        list.add(object);
        notify();
        synchronized (peekSemaphore) {
            peekSemaphore.notifyAll();
        }
    }

    /** 
     * Removes the first object from the queue, blocking until one is available
     */
    public synchronized Object remove() {
        while (true) {
            if (!list.isEmpty()) {
                Object answer = list.removeFirst();
                if (answer != null) {
                    return answer;
                }
            }
            try {
                wait(defaultTimeout);
            }
            catch (InterruptedException e) {
                log.warn("Thread was interrupted: " + e, e);
            }
        }
    }

    /** 
     * Removes the first object from the queue or waiting until the given timeout 
     * expires
     */
    public synchronized Object remove(long timeout) {
        Object answer = removeNoWait();
        if (answer == null) {
            try {
                wait(timeout);
            }
            catch (InterruptedException e) {
                log.warn("Thread was interrupted: " + e, e);
            }
            answer = removeNoWait();
        }
        return answer;
    }

    /** 
     * Removes the first object from the queue without blocking.
     * @return the first object removed from the queue or null if the
     * queue is empty
     */
    public synchronized Object removeNoWait() {
        if (!list.isEmpty()) {
            return list.removeFirst();
        }
        return null;
    }

    /** 
     * Peeks the first object on the queue without removing it, 
     * blocking until one is available
     */
    public Object peek() {
        while (true) {
            synchronized (this) {
                if (!list.isEmpty()) {
                    Object answer = list.getFirst();
                    if (answer != null) {
                        return answer;
                    }
                }
            }
            synchronized (peekSemaphore) {
                try {
                    peekSemaphore.wait(defaultTimeout);
                }
                catch (InterruptedException e) {
                    log.warn("Thread was interrupted: " + e, e);
                }
            }
        }
    }

    /** 
     * Peeks the first object on the queue without removing it,
     * waiting until the given timeout expires
     */
    public Object peek(long timeout) {
        Object answer = peekNoWait();
        if (answer == null) {
            synchronized (peekSemaphore) {
                try {
                    peekSemaphore.wait(timeout);
                }
                catch (InterruptedException e) {
                    log.warn("Thread was interrupted: " + e, e);
                }
            }
            answer = peekNoWait();
        }
        return answer;
    }

    /** 
     * Peeks the first object on the queue without removing it or
     * blocking.
     * 
     * @return the first object peekd on the queue or null if the
     * queue is empty
     */
    public synchronized Object peekNoWait() {
        if (!list.isEmpty()) {
            return list.getFirst();
        }
        return null;
    }
}
