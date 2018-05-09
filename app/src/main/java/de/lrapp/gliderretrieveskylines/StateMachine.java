package de.lrapp.gliderretrieveskylines;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

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
    StateMachine(int initialHeight, Context activityContext) {
        sc = new StateContext(initialHeight, activityContext);
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
    void doNotify(StateContext sc, int height);
}

/**
 * The state context saves the actual state and initiates the initial state.
 * It provides access to the state's check method, so an other method can call StateContext.check
 * from outside without having to know the actualState.
 */
class StateContext {
    private Statelike actualState;
    private Context activityContext;

    /**
     * Constructor determines the initial state
     * @param initialHeight pilot's initial height above GND at the time of state machine creation
     */
    StateContext(int initialHeight, Context lActivityContext) {
        activityContext = lActivityContext;
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
        buildNotification("Tracking started", "Your pilot is " + initialHeight + "m high.");
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

    /**
     * Builds and sends a notification
     * @param titleText Notification title
     * @param contentText Notification content
     */
    void buildNotification(String titleText, String contentText) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activityContext, "channelId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titleText)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activityContext);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(42, mBuilder.build());


    }

}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateOnGnd implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height > 10) {
            doNotify(context, height);
            context.setState(new StateBelow200());
        }
    }

    @Override
    public void doNotify(StateContext sc, int height) {
        // pilot takeoff
        sc.buildNotification("Takeoff", "Your pilot is in the air.");
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow200 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 10) {
            doNotify(context, height);
            context.setState(new StateOnGnd());
        } else if (height >= 200) {
            context.setState(new StateBelow400());
        }
    }

    @Override
    public void doNotify(StateContext context, int height) {
        // Pilot landed
        context.buildNotification("Landed", "Your pilot has landed.");
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow400 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 200) {
            doNotify(context, height);
            context.setState(new StateBelow200());
        } else if (height >= 400) {
            context.setState(new StateBelow600());
        }
    }

    @Override
    public void doNotify(StateContext context, int height) {
        // below 200
        context.buildNotification("Below 200m","Your pilot is below 200m and will probably land soon.");
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow600 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 400) {
            doNotify(context, height);
            context.setState(new StateBelow400());
        } else if (height >= 600) {
            context.setState(new StateBelow800());
        }
    }

    @Override
    public void doNotify(StateContext context, int height) {
        // below 400
        context.buildNotification("Below 400m", "Your pilot is below 400m, get ready to retrieve him / her.");
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateBelow800 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 600) {
            doNotify(context, height);
            context.setState(new StateBelow600());
        } else if (height >= 800) {
            context.setState(new StateAbove800());
        }
    }

    @Override
    public void doNotify(StateContext context, int height) {
        // below 600
        context.buildNotification("Below 600m", "Your pilot is below 600m, not so high anymore...");
    }
}

/**
 * For not implementation specific documentation, see {@link StateContext}
 */
class StateAbove800 implements Statelike {

    @Override
    public void check(StateContext context, int height) {
        if (height < 800) {
            doNotify(context, height);
            context.setState(new StateBelow200());
        }
    }

    @Override
    public void doNotify(StateContext context, int height) {
        // below 800
        context.buildNotification("Below 800m", "Your pilot is below 800m, keep an eye open at the live tracking.");
    }
}