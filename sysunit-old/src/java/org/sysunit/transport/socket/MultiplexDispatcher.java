package org.sysunit.transport.socket;

import org.sysunit.command.Command;
import org.sysunit.command.Dispatcher;
import org.sysunit.command.DispatchException;

public class MultiplexDispatcher
    implements Dispatcher {

    private Dispatcher[] dispatchers;

    public MultiplexDispatcher(Dispatcher[] dispatchers) {
        this.dispatchers = dispatchers;
    }

    public void start()
        throws Exception {

    }

    public void stop()
        throws Exception {

    }

    public void dispatch(Command command)
        throws DispatchException {
        for ( int i = 0 ; i < this.dispatchers.length ; ++i ) {
            this.dispatchers[i].dispatch( command );
        }
    }
}
