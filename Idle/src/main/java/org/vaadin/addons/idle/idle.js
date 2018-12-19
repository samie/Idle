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
window.org_vaadin_addons_idle_Idle = function() {

    var self = this;
    
    this.timer = null;
    this.timeout = null;
    this.hasUserActiveListener = false;
    this.hasUserInactiveListener = false;

    this.timerReset = function() {
        if (self.timer) {
            clearTimeout(self.timer);
            if (document.body.classList.contains("userinactive")) {
                document.body.classList.remove("userinactive");
                document.body.classList.add("useractive");
                if (self.hasUserActiveListener) {
                    self.onUserActive();
                }
            }
        }

        self.timer = setTimeout(function() {
            if (document.body.classList.contains("useractive")) {
                document.body.classList.remove("useractive");
                document.body.classList.add("userinactive");
                if (self.hasUserInactiveListener) {
                    self.onUserInactive();
                }
            }
        }, self.timeout);

    };
    
    this.onStateChange = function() {
        var state = self.getState(),
                listeners = state.registeredEventListeners;
        
        self.hasUserActiveListener = listeners && listeners.indexOf("user-active") > -1;
        self.hasUserInactiveListener = listeners && listeners.indexOf("user-inactive") > -1;
        
        var newTimeout = state.timeout;
        // Reset timer if timeout has changed
        if (newTimeout !== self.timeout) {
            self.timeout = newTimeout;
            self.timerReset();
        }
    };
    
    this.onUnregister = function() {
        window.removeEventListener('mousedown', this.timerReset, false);
        window.removeEventListener('mousemove', this.timerReset, false);
        window.removeEventListener('keydown', this.timerReset, false);
        
        document.body.classList.remove("useractive");
        document.body.classList.remove("userinactive");
    };

    if (!document.body.classList.contains("useractive")) {
        document.body.classList.add("useractive");
    }

    window.addEventListener('mousedown', this.timerReset, false);
    window.addEventListener('mousemove', this.timerReset, false);
    window.addEventListener('keydown', this.timerReset, false);

};
