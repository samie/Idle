/*
 * Copyright 2017 Sami Ekblad.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.idle;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.shared.Registration;
import org.parttio.vaadinjsloader.JSLoader;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

;

/**
 * Vaadin extension for tracking user activity / inactivity.
 * <p>
 * Track user activity/inactivity changes. There are two ways to react to user
 * inactivity: Change the CSS styles or receive notification when user becomes
 * inactive. Both can be used at the same time.
 * <p>
 * When user becomes inactive a style <code>userinactive</code> is application
 * to <code>BODY</code> element. When user comes back active this style is
 * replaced with classname <code>useractive</code>.
 * <p>
 * Furthermore, if listeners have been attached using
 * {@link #addUserActiveListener} and {@link #addUserInactiveListener}, they
 * will be invoked.
 * <p>
 * User activity is tracked by following the mouse and keyboard events on the
 * browser window.
 * <p>
 * The inactivity timeout period can be specified using the
 * {@link #setTimeout(long)} method.
 *
 * @author Sami Ekblad
 */
public class Idle {

    private static final WeakHashMap<UI, Idle> UI_REGISTRATIONS = new WeakHashMap<>();
    public static final long DEFAULT_TIMEOUT = 5000;
    private final List<UserInactiveListener> inactiveListeners = new CopyOnWriteArrayList<>();
    private final List<UserActiveListener> activeListeners = new CopyOnWriteArrayList<>();
    private final WeakReference<UI> ui;
    private long timeout;
    private boolean updateCssClass;
    private DomListenerRegistration activeDomListener;
    private DomListenerRegistration inactiveDomListener;
    private final String jsSingletonName;

    /**
     * Creates a new Idle instance.
     *
     * @param ui UI instance to monitor
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public Idle(UI ui) throws IllegalArgumentException {

        checkNotTracked(ui);

        // Load the JS and CSS
        JSLoader.loadJavaResource(ui, Idle.class, "idle-addon","idle.js", "idle.css");

        // The loaded script creates a single JS instance in window, with unique name instance name
        jsSingletonName = "window." + Idle.class.getCanonicalName().replace('.', '_') + "_instance";

        UI_REGISTRATIONS.put(ui, this);
        this.ui = new WeakReference<>(ui);
        this.trackUI(ui, DEFAULT_TIMEOUT);
    }

    /**
     * Creates a new Idle instance.
     *
     * @param ui        UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public Idle(UI ui, long timeoutMs) throws IllegalArgumentException {
        this(ui);
        setTimeout(timeoutMs);
    }

    /**
     * Creates a new Idle instance.
     *
     * @param ui        UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @param updateCssClass CSS styles are updated when user becomes active/inactive.
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public Idle(UI ui, long timeoutMs, boolean updateCssClass) throws IllegalArgumentException {
        this(ui);
        setTimeout(timeoutMs);
        setUpdateCss(updateCssClass);
    }

    /**
     * Gets the Idle instance a monitored UI.
     *
     * @param ui A monitored UI instance
     * @return Idle instance of the UI or {@code null}, if the UI is not
     * monitored.
     */
    public static Idle get(UI ui) {
        Objects.requireNonNull(ui, "UI must not be null");
        return UI_REGISTRATIONS.get(ui);
    }

    /**
     * Create new user activity tracker for UI with default timeout and no
     * server-side listener.
     *
     * @param ui UI instance to monitor
     * @return Idle Created instance
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public static Idle track(UI ui) throws IllegalArgumentException {
        return new Idle(ui);
    }

    /**
     * Create new user activity tracker for UI with timeout.
     *
     * @param ui        UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @return Idle Created instance
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public static Idle track(UI ui, long timeoutMs)
            throws IllegalArgumentException {
        assert ui != null;
        return new Idle(ui, timeoutMs);
    }

    /**
     * Create new user activity tracker for UI with timeout.
     *
     * @param ui        UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @param updateCssClass CSS styles are updated when user becomes active/inactive.
     * @return Idle Created instance
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public static Idle track(UI ui, long timeoutMs, boolean updateCssClass)
            throws IllegalArgumentException {
        assert ui != null;
        return new Idle(ui, timeoutMs,updateCssClass);
    }

    /** This is an internal method for tracking the UI.
     *
     * @param ui UI instance to track
     * @param timeout Inactivity timeout in milliseconds
     */
    protected void trackUI(UI ui, long timeout) {
        assert ui != null;
        setTimeout(timeout);
        callInstanceMethod("register()");
    }

    /** This is an internal method for calling JS functions.
     *
     * @param methodCall The method call to execute.
     */
    private void callInstanceMethod(String methodCall) {
        UI ui = this.ui.get();
        if (ui != null) {
            ui.getElement().executeJs(jsSingletonName + "." + methodCall + ";");
        }
    }

    /**
     * Checks if the given UI is already monitored by Idle.
     *
     * @param ui UI instance to check
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    protected void checkNotTracked(UI ui) throws IllegalArgumentException {
        assert ui != null;
        if (UI_REGISTRATIONS.containsKey(ui)) {
            throw new IllegalArgumentException(
                    "This UI is already monitored by Idle");
        }
    }

    /**
     * User inactivity timeout in milliseconds.
     *
     * @return Current timeout in milliseconds.
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * User inactivity timeout in milliseconds.
     * <p>
     * This is the time that needs to pass before user is considered inactive.
     * Default timeout is 5000ms (5 seconds).
     *
     * @param timeout New timeout.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout >= 0 ? timeout: 0;
        callInstanceMethod("setTimeout(%d)".formatted(this.timeout));
    }

    /** Do we apply css class when user becomes active/inactive.
     *
     * @return If true, the CSS styles are updated when user becomes active/inactive.
     */
    public boolean isUpdateCss() {
        return updateCssClass;
    }

    /**
     * Apply css class to body when user becomes active/inactive.
     *
     * @param updateCssClass If true, the CSS styles are updated when user becomes
     */
    public void setUpdateCss(boolean updateCssClass) {
        this.updateCssClass = updateCssClass;
        callInstanceMethod("setUpdateCssClass(%b)".formatted(this.updateCssClass));
    }

    /**
     * Fires an {@link UserActiveEvent}.
     */
    protected void fireUserActive() {
        activeListeners.forEach(l -> l.userActive(new UserActiveEvent(this)));
    }

    /**
     * Adds the user active listener.
     *
     * @param listener the Listener to be added.
     * @return A registration object for removing the listener.
     * @see Registration
     */
    public Registration addUserActiveListener(UserActiveListener listener) {
        activeListeners.add(listener);

        // Attach DOM event listener
        if (activeDomListener == null) {
            UI ui = this.ui.get();
            if (ui != null) {
                activeDomListener = ui.getElement()
                        .addEventListener("user-active", e -> fireUserActive());
            }
        }
        return () -> {
            activeListeners.remove(listener);
            // Remove DOM event listener
            if (activeListeners.isEmpty()) {
                activeDomListener.remove();
                activeDomListener = null;
            }
        };
    }

    /**
     * Fires an {@link UserInactiveEvent}.
     */
    protected void fireUserInactive() {
        inactiveListeners.forEach(l -> l.userInactive(new UserInactiveEvent(this)));
    }

    /**
     * Adds the user inactive listener.
     *
     * @param listener The Listener to be added.
     * @return A registration object for removing the listener.
     * @see Registration
     */
    public Registration addUserInactiveListener(UserInactiveListener listener) {
        inactiveListeners.add(listener);

        // Attach DOM event listener
        if (inactiveDomListener == null) {
            UI ui = this.ui.get();
            if (ui != null) {
                inactiveDomListener = ui.getElement()
                        .addEventListener("user-inactive", e -> fireUserInactive());
            }
        }
        return () -> {
            inactiveListeners.remove(listener);
            // Remove DOM event listener
            if (inactiveListeners.isEmpty()) {
                inactiveDomListener.remove();
                inactiveDomListener = null;
            }
        };

    }

    /** Stop tracking the UI.
     *
     */
    public void untrackUI() {
        UI ui = this.ui.get();
        callInstanceMethod("unregister()");
        UI_REGISTRATIONS.remove(ui);
        this.ui.clear();
    }

    /**
     * Listener interface for user activity status changes to active.
     */
    @FunctionalInterface
    public interface UserActiveListener extends Serializable {

        /**
         * Invoked when user becomes active.
         *
         * @param event Event
         * @see Idle#setTimeout(long)
         */
        void userActive(UserActiveEvent event);

    }


    /**
     * Listener interface for user activity status changes to inactive.
     */
    @FunctionalInterface
    public interface UserInactiveListener extends Serializable {

        /**
         * Invoked when user goes inactive based on timeout period.
         *
         * @param event Event
         * @see Idle#setTimeout(long)
         */
        void userInactive(UserInactiveEvent event);

    }

    /**
     * Basic Idle add-on event
     */
    public static class IdleEvent extends EventObject {

        /**
         * New instance of idle event.
         *
         * @param idle The source of the event.
         */
        public IdleEvent(Idle idle) {
            super(idle);
        }

        /**
         * Gets the Idle instance that triggered this event.
         *
         * @return Idle instance that triggered this event.
         */
        public Idle getIdle() {
            return (Idle) getSource();
        }

    }

    /**
     * User active event.
     * <p>
     * This event is triggered when the user becomes active.
     */
    public static class UserActiveEvent extends IdleEvent {

        /**
         * New instance of user active event.
         *
         * @param idle The source of the event.
         */
        public UserActiveEvent(Idle idle) {
            super(idle);
        }

    }

    /**
     * User inactive event.
     * <p>
     * This event is triggered when the user becomes inactive.
     */
    public static class UserInactiveEvent extends IdleEvent {

        /**
         * New instance of user inactive event.
         *
         * @param idle The source of the event.
         */
        public UserInactiveEvent(Idle idle) {
            super(idle);
        }

    }
}
