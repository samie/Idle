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
package org.vaadin.addons.idle.demo;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import org.vaadin.addons.idle.Idle;

import java.util.TreeSet;

@PreserveOnRefresh
@Route
@PageTitle("Idle Add-on Demo")
public class DemoView extends Div {
    
    // Some test timeouts
    private final TreeSet<Long> timeouts = new TreeSet<>(); {
        timeouts.add(3000L); // 3 seconds
        timeouts.add(10000L); // 10 seconds
        timeouts.add(30000L); // 30 seconds
        timeouts.add(60000L); // 1 minute
        timeouts.add(120000L); // 2 minutes
        timeouts.add(300000L); // 5 minutes
    }

    private Span status;

    public DemoView() {
        
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setSizeUndefined();
        wrapperLayout.setSpacing(true);

        // Simple status label
        status = new Span("You are now active");
        status.setClassName("status");
        
        wrapperLayout.add(status);
        wrapperLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Initialize our new UI component
        Idle idle = Idle.track(UI.getCurrent(), 5000);
        idle.addUserActiveListener(e -> status.setText("You are now active"));
        idle.addUserInactiveListener(e -> status.setText("You are now idle"));
        timeouts.add(idle.getTimeout());
        
        // Combobox to change the inactivity timeout
        ComboBox<Long> timeoutComboBox = new ComboBox<>(
                "Inactivity timeout:", timeouts);
        timeoutComboBox.setWidth("250px");
        timeoutComboBox.setValue(idle.getTimeout());
        timeoutComboBox.addValueChangeListener(e -> {
            Long timeout = e.getValue();
            Notification.show("Inactivity timeout is now set to:\n" +
                        timeout + " ms!");
            idle.setTimeout(timeout);
        });
        
        timeoutComboBox.setItemLabelGenerator(timeout -> timeout + " ms");
        wrapperLayout.add(timeoutComboBox);
        wrapperLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        
        // Show it in the middle of the screen
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.add(wrapperLayout);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);
    }

}
