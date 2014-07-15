package org.vaadin.addons.useractivitytracker.demo;

import org.vaadin.addons.useractivitytracker.UserActivityTracker;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("UserActivityTracker Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private UserActivityTracker userTracker;
    private Label status;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // Simple status label
        status = new Label("You are now active");
        status.setStyleName("status");

        // Initialize our new UI component
        userTracker = UserActivityTracker.track(this, 5000, new UserActivityTracker.Listener() {

            @Override
            public void userInactive() {
                status.setValue("You are now idle");
            }

            @Override
            public void userActive() {
                status.setValue("You are now active");
            }
        });

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(status);
        layout.setComponentAlignment(status, Alignment.MIDDLE_CENTER);
        setContent(layout);

    }

}
