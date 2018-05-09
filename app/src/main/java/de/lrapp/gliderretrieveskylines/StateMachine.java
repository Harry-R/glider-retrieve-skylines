package de.lrapp.gliderretrieveskylines;

/**
 * "main" class, handles the state machine
 *
 * Probably this is a overkill and a relatively complex if- else construct would had done the
 * job, too. The state machine was implemented for learning by doing.
 *
 */
public class StateMachine {

    private StateContext sc;

    /**
     * Constructor, creates new state context
     * @param initialHeight pilot's initial height above GND at the time of state machine creation
     */
    StateMachine(int initialHeight) {
        sc = new StateContext(initialHeight);
    }

    /**
     * runs a state machine action
     * @param height pilot's actual height above GND
     */
    public void run(int height) {
        sc.check(height);
    }
}

/**
 *  Interface do define the state structure
 */
interface Statelike {
    /**
     * Checks, if state changes or sending a notification is necessary
     * @param context the state context
     * @param height pilot's actual height above GND
     */
    void check(StateContext context, int height);

    /**
     * Sends a notification, content is implementation specific.
     * Called by {@link #check(StateContext, int)} method, if necessary.
     * @param height pilot's actual height above GND
     */
    void doNotify(int height);
}

/**
 * The state context saves the actual state and initiates the initial state.
 * It provides access to the state's check method, so an other method can call StateContext.check
 * from outside without having to know the actualState.
 */
class StateContext {
    private Statelike actualState;

    /**
     * Constructor determines the initial state
     * @param initialHeight pilot's initial height above GND at the time of state machine creation
     */
    StateContext(int initialHeight) {
        if (initialHeight < 10) {
            setState(new StateOnGnd());
        } else if (initialHeight < 200) {
            setState(new StateBelow200());
        } else if (initialHeight < 400) {
            setState(new StateBelow400());
        } else if (initialHeight < 600) {
            setState(new StateBelow600());
        } else if (initialHeight < 800) {
            setState(new StateBelow800());
        } else if (initialHeight > 800) {
            setState(new StateAbove800());
        }
     }

    /**
     * Setter method for the actual state, called on state change
     * @param newState the new state to change into
     */
    void setState(final Statelike newState) {
        actualState = newState;
    }

    /**
     * Calls the actual state*s check method
     * @param height pilot's actual height above GND
     */
    public void check(int height) {
        actualState.check(this, height);
    }

}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateOnGnd implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height > 10) {
            doNotify(height);
            context.setState(new StateBelow200());
        }
    }

    @Override
    public void doNotify(int height) {
        // pilot takeoff
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow200 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 10) {
            doNotify(height);
            context.setState(new StateOnGnd());
        } else if (height >= 200) {
            context.setState(new StateBelow400());
        }
    }

    @Override
    public void doNotify(int height) {
        // Pilot landed
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow400 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 200) {
            doNotify(height);
            context.setState(new StateBelow200());
        } else if (height >= 400) {
            context.setState(new StateBelow600());
        }
    }

    @Override
    public void doNotify(int height) {
        // below 200
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow600 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 400) {
            doNotify(height);
            context.setState(new StateBelow400());
        } else if (height >= 600) {
            context.setState(new StateBelow800());
        }
    }

    @Override
    public void doNotify(int height) {
        // below 400
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow800 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 600) {
            doNotify(height);
            context.setState(new StateBelow600());
        } else if (height >= 800) {
            context.setState(new StateAbove800());
        }
    }

    @Override
    public void doNotify(int height) {
        // below 600
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateAbove800 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 800) {
            doNotify(height);
            context.setState(new StateBelow200());
        }
    }

    @Override
    public void doNotify(int height) {
        // below 800
    }
}