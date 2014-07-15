package org.vaadin.addons.useractivitytracker;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.UI;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Vaadin extension for tracking user activity / inactivity.
 *
 * Track user inactivity changes. There are two ways to react to user
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
 * The inactivity timeout can be specified using the setTimeout method.
 *
 * @author Sami Ekblad
 */
@JavaScript("useractivitytracker.js")
public class UserActivityTracker extends AbstractJavaScriptExtension {

    private static final long DEFAULT_INACTIVITY_TIMEOUT_MS = 4000;

    private long timeout = DEFAULT_INACTIVITY_TIMEOUT_MS;

    private Listener listener;

    /**
     * Listener interface for user inactivity status changes.
     */
    public interface Listener {

        /**
         * Invoked when user goes inactive based on timeout period.
         *
         * @see UserActivityTracker#setTimeout(long)
         */
        public void userInactive();

        /**
         * Invoked when user becomes active.
         *
         * @see UserActivityTracker#setTimeout(long)
         */
        public void userActive();
    }

    /**
     * Create new user activity tracker for UI with default timeout and no
     * server-side listener.
     *
     * @param ui
     * @return UserActivityTracker
     */
    public static UserActivityTracker track(final UI ui) {
        return new UserActivityTracker(ui);
    }

    /**
     * Create new user activity tracker for UI with timeout and a listener.
     *
     * @param ui
     * @param timeoutMs
     * @param listener
     * @return UserActivityTracker
     */
    public static UserActivityTracker track(final UI ui, final long timeoutMs, final Listener listener) {
        return new UserActivityTracker(ui, timeoutMs, listener);
    }

    private UserActivityTracker(final UI ui) {
        this(ui, DEFAULT_INACTIVITY_TIMEOUT_MS, null);
    }

    private UserActivityTracker(final UI ui, final long timeoutMs, final Listener listener) {
        extend(ui);
        addFunction("onUserInactive", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                if (UserActivityTracker.this.listener != null) {
                    UserActivityTracker.this.listener.userInactive();
                }
            }
        });
        addFunction("onUserActive", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                if (UserActivityTracker.this.listener != null) {
                    UserActivityTracker.this.listener.userActive();
                }
            }
        });
        setListener(listener);
        setTimeout(timeoutMs);
    }

    /**
     * User inactivity timeout in seconds.
     *
     * @return Current timeout in milliseconds.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * User inactivity timeout in seconds.
     *
     * This is the time that needs to pass before user is considered inactive.
     * Default timeout is 4000ms (4 seconds).
     *
     * @param timeout New timeout.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
        if (this.timeout < 0) {
            this.timeout = 0;
        }
        callFunction("setInactivityTimeout", this.timeout);
    }

    /**
     * Listener for user inactivity status changes.
     *
     * @return
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

}
