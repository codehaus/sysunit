/*
 * Created on Jul 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.sysunit.command.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.messenger.Messenger;
import org.sysunit.command.Command;
import org.sysunit.command.DispatchException;
import org.sysunit.command.Dispatcher;

/**
 * @author James Strachan
 * @version $Revision$
 */
public class JmsDispatcher implements Dispatcher {
	
	private Messenger messenger;
	private Destination destination;

	public JmsDispatcher() {
	}

	/**
	 * A Pico style constructor
	 * 
	 * @param messenger
	 * @param destination
	 */
	public JmsDispatcher(Messenger messenger, Destination destination) {
		setMessenger(messenger);
		setDestination(destination);
	}

    public void dispatch(Command command) throws DispatchException {
    	try {
            Message message = messenger.createObjectMessage(command);
            messenger.send(destination, message);
        }
        catch (JMSException e) {
        	throw new DispatchException(command, e);
        }
    }
    
	// Properties
	//-------------------------------------------------------------------------    

    /**
     * @return
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     * @param destination
     */
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    /**
     * @return
     */
    public Messenger getMessenger() {
        return messenger;
    }

    /**
     * @param messenger
     */
    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

}
