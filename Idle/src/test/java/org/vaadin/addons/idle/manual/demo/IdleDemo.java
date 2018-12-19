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
package org.vaadin.addons.idle.manual.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import org.vaadin.addonhelpers.AbstractTest;
import org.vaadin.addons.idle.Idle;

/**
 *
 * @author Max Schuster
 */
@Theme("valo")
public class IdleDemo extends AbstractTest {

    @Override
    public String getDescription() {
        return "Demo for testing the Idle add-on";
    }

    @Override
    public Component getTestComponent() {
        Label status = new Label("User active");
        status.setId("status");
        
        Idle idle = Idle.track(this);
        idle.addUserActiveListener(e -> status.setValue("User active"));
        idle.addUserInactiveListener(e -> status.setValue("User inactive"));
        idle.setTimeout(3000);
        
        return status;
    }
    
    
    
}
