package org.sysunit.command;

public class StateServer
    extends Server {

    private State state;

    public State getState() {
        return this.state;
    }

    protected void setState(State state) {
        this.state = state;
    }

    protected void enterState(final State state)
        throws Exception {
        setState( state );
        state.enter();
    }
}
