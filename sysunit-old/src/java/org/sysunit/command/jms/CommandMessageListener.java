/*
 * Created on Jul 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.sysunit.command.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sysunit.command.Command;
import org.sysunit.command.NodeContext;

/**
 * A JMS MessageListener which will consume Command objects and execute them against
 * a given context
 * 
 * @author James Strachan
 * @version $Revision$
 */
public class CommandMessageListener implements MessageListener {

    private static final Log log = LogFactory.getLog(CommandMessageListener.class);

    private NodeContext context;

	public CommandMessageListener(NodeContext context) {
		this.context = context;
	}
	
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                Object value = objectMessage.getObject();
                if (value instanceof Command) {
                    onCommand((Command) value);
                }
                else {
                    log.error("Received object which was not a command: " + value);
                }
            }
            catch (Exception e) {
                log.error("Could not process message: " + message + ". Reason: " + e, e);
            }
        }
        else {
            log.error("Uknown message type: " + message);
        }
    }

    // Implementation methods
    //-------------------------------------------------------------------------    

    /**
     * @param command
     */
    protected void onCommand(Command command) throws Exception {
        command.run(context);
    }

}
