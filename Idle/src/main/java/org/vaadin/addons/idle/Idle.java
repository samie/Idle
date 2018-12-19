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

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.Extension;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Objects;
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
 * Furthermore, if listeners have been attached using
 * {@link #addUserActiveListener} and {@link #addUserInactiveListener}, they
 * will be invoked.
 *
 * User activity is tracked by following the mouse and keyboard events on the
 * browser window.
 *
 * The inactivity timeout period can be specified using the
 * {@link #setTimeout(long)} method.
 *
 * @author Sami Ekblad
 */
@JavaScript("idle.js")
public class Idle extends AbstractJavaScriptExtension {

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
     * Listener interface for user activity status changes to active.
     */
    @FunctionalInterface
    public interface UserActiveListener extends Serializable {
        
        public static final Method USER_ACTIVE_METHOD
                = ReflectTools.findMethod(UserActiveListener.class,
                        "userActive", UserActiveEvent.class);
        
        /**
         * Invoked when user becomes active.
         *
         * @param event Event
         * @see Idle#setTimeout(long)
         */
        public void userActive(UserActiveEvent event);
        
    }
    
    /**
     * User active event.
     * 
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
     * Listener interface for user activity status changes to inactive.
     */
    @FunctionalInterface
    public interface UserInactiveListener extends Serializable {
        
        public static final Method USER_INACTIVE_METHOD
                = ReflectTools.findMethod(UserInactiveListener.class,
                        "userInactive", UserInactiveEvent.class);
        
        /**
         * Invoked when user goes inactive based on timeout period.
         *
         * @param event Event
         * @see Idle#setTimeout(long)
         */
        public void userInactive(UserInactiveEvent event);
        
    }
    
    /**
     * User inactive event.
     * 
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
    
    /**
     * Gets the Idle instance a monitored UI.
     * 
     * @param ui A monitored UI instance
     * @return Idle instance of the UI or {@code null}, if the UI is not
     * monitored.
     */
    public static Idle get(UI ui) {
        Objects.requireNonNull(ui, "UI must not be null");
        for (Extension extension : ui.getExtensions()) {
            if (extension instanceof Idle) {
                return (Idle) extension;
            }
        }
        return null;
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
     * @param ui UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @return Idle Created instance
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    public static Idle track(UI ui, long timeoutMs)
            throws IllegalArgumentException {
        return new Idle(ui, timeoutMs);
    }
    
    /**
     * Creates a new Idle instance.
     * 
     * @param ui UI instance to monitor
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    protected Idle(UI ui) throws IllegalArgumentException {
        checkNotTracked(ui);
        extend(ui);
        addFunction("onUserInactive", args -> fireUserInactive());
        addFunction("onUserActive", args -> fireUserActive());
    }

    /**
     * Creates a new Idle instance.
     * 
     * @param ui UI instance to monitor
     * @param timeoutMs Inactivity timeout in milliseconds
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    protected Idle(UI ui, long timeoutMs) throws IllegalArgumentException {
        this(ui);
        setTimeout(timeoutMs);
    }
    
    /**
     * Checks if the given UI is already monitored by Idle.
     * 
     * @param ui UI instance to check
     * @throws IllegalArgumentException If the UI is already monitored by Idle
     */
    protected void checkNotTracked(UI ui) throws IllegalArgumentException {
        if (Idle.get(ui) != null) {
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
        return getState(false).timeout;
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

    @Override
    protected IdleState getState() {
        return (IdleState) super.getState();
    }
    
    @Override
    protected IdleState getState(boolean markAsDirty) {
        return (IdleState) super.getState(markAsDirty);
    }
    
    /**
     * Fires an {@link UserActiveEvent}.
     */
    protected void fireUserActive() {
        fireEvent(new UserActiveEvent(this));
    }
    
    /**
     * Adds the user active listener.
     * 
     * @param listener the Listener to be added.
     * @return A registration object for removing the listener.
     * @see Registration
     */
    public Registration addUserActiveListener(UserActiveListener listener) {
        return addListener("user-active", UserActiveEvent.class, listener,
                UserActiveListener.USER_ACTIVE_METHOD);
    }
    
    /**
     * Fires an {@link UserInactiveEvent}.
     */
    protected void fireUserInactive() {
        fireEvent(new UserInactiveEvent(this));
    }
    
    /**
     * Adds the user inactive listener.
     * 
     * @param listener The Listener to be added.
     * @return A registration object for removing the listener.
     * @see Registration
     */
    public Registration addUserInactiveListener(UserInactiveListener listener) {
        return addListener("user-inactive", UserInactiveEvent.class, listener,
                UserInactiveListener.USER_INACTIVE_METHOD);
    }
    
}
