window.org_vaadin_addons_idle_Idle = function() {

    var self = this;
    this.timer = null;
    this.timeout = null;

    this.addEvent = function(ob, type, fn) {
        if (ob.addEventListener) {
            ob.addEventListener(type, fn, false);
        } else if (ob.attachEvent) {
            ob.attachEvent('on' + type, fn);
        } else {
            type = 'on' + type;
            if (typeof ob[type] === 'function') {
                fn = (function(f1, f2) {
                    return function() {
                        f1.apply(this, arguments);
                        f2.apply(this, arguments);
                    };
                })(ob[type], fn);
            }
            ob[type] = fn;
            return true;
        }
        return false;
    };

    this.timerReset = function() {
        if (self.timer) {
            clearTimeout(self.timer);
            if (document.body.className.indexOf("userinactive") >= 0) {
                document.body.className = document.body.className.replace("userinactive", "useractive");
                if (self.getState().active) {
                    self.onUserActive();
                }
            }
        }

        self.timer = setTimeout(function() {
            if (document.body.className.indexOf("useractive") >= 0) {
                document.body.className = document.body.className.replace("useractive", "userinactive");
                if (self.getState().active) {
                    self.onUserInactive();
                }
            }
        }, self.timeout);

    };
    
    this.onStateChange = function() {
        var newTimeout = self.getState().timeout;
        // Reset timer if timeout has changed
        if (newTimeout !== self.timeout) {
            self.timeout = newTimeout;
            self.timerReset();
        }
    };

    if (document.body.className.indexOf("useractive") < 0) {
        document.body.className += " useractive";
    }

    self.addEvent(window, 'mousedown', self.timerReset);
    self.addEvent(window, 'mousemove', self.timerReset);
    self.addEvent(window, 'keydown', self.timerReset);

};
