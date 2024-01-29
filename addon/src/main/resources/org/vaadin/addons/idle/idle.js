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

 /** JavaScript class to track user activity and send events to server. */
class org_vaadin_addons_idle_Idle {

  tracking = false;
  timerId = null;
  timeout = 5000;

  constructor() {
  }

  /** Restart timer */
  timerReset() {
    if (!this.tracking) return;

    // Clear previous timer
    if (typeof this.timerId === "number") {
      clearTimeout(this.timerId);
    }

    // Send 'active' event and change CSS class (only if inactive)
    if (document.body.classList.contains("userinactive") && this.tracking) {
          document.body.classList.replace("userinactive", "useractive");
          const event = new Event("user-active");
          document.body.dispatchEvent(event);
    }


    this.timerId = setTimeout(() => {
      // Timer expired. Send 'inactive' event and change CSS class
      if (!document.body.classList.contains("userinactive") && this.tracking) {
          document.body.classList.replace("useractive", "userinactive");
          const event = new Event("user-inactive");
          document.body.dispatchEvent(event);
      }
    }, this.timeout);
  }


  /** Register event listeners */
  register() {
    if (this.tracking) return; // Avoid registering twice
    document.body.classList.toggle("useractive", true);
    window.addEventListener('mousedown', this.timerReset.bind(this), false);
    window.addEventListener('mousemove', this.timerReset.bind(this), false);
    window.addEventListener('keydown', this.timerReset.bind(this), false);
    this.tracking = true;
    this.timerReset();
  }

  /** Unregister event listeners */
  unregister() {
    this.tracking = false;
    window.removeEventListener('mousedown', this.timerReset, false);
    window.removeEventListener('mousemove', this.timerReset, false);
    window.removeEventListener('keydown', this.timerReset, false);
    document.body.classList.remove("useractive");
    document.body.classList.remove("userinactive");
  }

  /** Reset timer if timeout has changed. */
  setTimeout(newTimeout) {
    document.body.classList.add("useractive");

    if (newTimeout !== this.timeout) {
      this.timeout = newTimeout;
      this.timerReset();
    }
  }
}

// Create instance and register it to window
window.org_vaadin_addons_idle_Idle_instance = new org_vaadin_addons_idle_Idle();
