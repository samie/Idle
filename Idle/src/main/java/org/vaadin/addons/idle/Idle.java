package org.vaadin.addons.idle;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import org.json.JSONArray;
import org.json.JSONException;
import org.vaadin.addons.idle.client.IdleState;

/**
 * Vaadin extension for tracking user activity / inactivity.
 *
 * Track user activity/inactivity changes. There are two ways to react to user
 * inactivity: Change the CSS styles or receive notification when user becomes
 * inactive. Both can be used at the same time.
 *
 * When user becomes inactive a style <code>userinactive</code> is application
 * to <code>BODY</code> element. When user comes back active this style is
 * replaced with classname <code>useractive</code>.
 *
 * Furthermore, if a non-null listener is given with setListner, it will be
 * invoked.
 *
 * User activity is tracked by following the mouse and keyboard events on the
 * browser window.
 *
 * The inactivity timeout period can be specified using the setTimeout method.
 *
 * @author Sami Ekblad
 */
@JavaScript("idle.js")
public class Idle extends AbstractJavaScriptExtension {

    private static final long DEFAULT_INACTIVITY_TIMEOUT_MS = 5000;

    private Listener listener;

    /**
     * Listener interface for user inactivity status changes.
     */
    public interface Listener {

        /**
         * Invoked when user goes inactive based on timeout period.
         *
         * @see Idle#setTimeout(long)
         */
        public void userInactive();

        /**
         * Invoked when user becomes active.
         *
         * @see Idle#setTimeout(long)
         */
        public void userActive();
    }

    /**
     * Create new user activity tracker for UI with default timeout and no
     * server-side listener.
     *
     * @param ui UI instance to monitor
     * @return Idle Created instance
     */
    public static Idle track(final UI ui) {
        return new Idle(ui);
    }

    /**
     * Create new user activity tracker for UI with timeout.
     *
     * @param ui UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @return Idle Created instance
     */
    public static Idle track(final UI ui, final long timeoutMs) {
        return new Idle(ui, timeoutMs, null);
    }    
    
    /**
     * Create new user activity tracker for UI with timeout and a listener.
     *
     * @param ui UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @param listener Listener that receives the events
     * @return Idle Created instance
     */
    public static Idle track(final UI ui, final long timeoutMs, final Listener listener) {
        return new Idle(ui, timeoutMs, listener);
    }

    private Idle(final UI ui) {
        this(ui, DEFAULT_INACTIVITY_TIMEOUT_MS, null);
    }

    private Idle(final UI ui, final long timeoutMs, final Listener listener) {
        extend(ui);
        addFunction("onUserInactive", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                if (Idle.this.listener != null) {
                    Idle.this.listener.userInactive();
                }
            }
        });
        addFunction("onUserActive", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                if (Idle.this.listener != null) {
                    Idle.this.listener.userActive();
                }
            }
        });
        setListener(listener);
        setTimeout(timeoutMs);
    }

    /**
     * User inactivity timeout in milliseconds.
     *
     * @return Current timeout in milliseconds.
     */
    public long getTimeout() {
        return getState().timeout;
    }

    /**
     * User inactivity timeout in milliseconds.
     *
     * This is the time that needs to pass before user is considered inactive.
     * Default timeout is 5000ms (5 seconds).
     *
     * @param timeout New timeout.
     */
    public void setTimeout(long timeout) {
        getState().timeout = timeout;
    }

    /**
     * Listener for user inactivity status changes.
     *
     * @return Listener instance or null.
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Set a new listener for user inactivity status changes.
     *
     * Setting this null avoids server notifications to be sent. This is useful
     * if only CSS changes are desired.
     *
     * @param listener New listener, or null if no server notifications are
     * needed.
     */
    public void setListener(Listener listener) {
        this.listener = listener;
        getState().enabled = this.listener != null;
    }

    @Override
    protected IdleState getState() {
        return (IdleState) super.getState();
    }
    
}
