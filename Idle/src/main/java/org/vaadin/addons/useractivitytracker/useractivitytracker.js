window.org_vaadin_addons_useractivitytracker_UserActivityTracker = function() {

    var self = this;
    this.timer = null;
    this.timeout = 4000;

    this.addEvent = function(ob, type, fn) {
        if (typeof window.addEventListener !== "undefined") {
            ob.addEventListener(type, fn, false);
        } else if (typeof window.attachEvent !== "undefined") {
            ob.attachEvent('on' + type, fn);
        }
    };

    this.setInactivityTimeout = function(timeoutMs) {
        self.timeout = timeoutMs;
        self.resetTimer();
    };

    this.timerReset = function() {
        if (self.timer) {
            clearTimeout(self.timer);
            if (document.body.className.indexOf("userinactive") >= 0) {
                document.body.className = document.body.className.replace("userinactive", "useractive");
                if (self.getState().enabled) {
                    self.onUserActive();
                }
            }
        }

        self.timer = setTimeout(function() {
            if (document.body.className.indexOf("useractive") >= 0) {
                document.body.className = document.body.className.replace("useractive", "userinactive");
                if (self.getState().enabled) {
                    self.onUserInactive();
                }
            }
        }, self.timeout);

    };


    if (document.body.className.indexOf("useractive") < 0) {
        document.body.className += " useractive";
    }
    
    self.addEvent(window, 'mousedown', self.timerReset);
    self.addEvent(window, 'mousemove', self.timerReset);
    self.addEvent(window, 'keydown', self.timerReset);


}
