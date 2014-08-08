package org.vaadin.addons.idle.demo;

import com.vaadin.annotations.PreserveOnRefresh;
import org.vaadin.addons.idle.Idle;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("Idle Add-on Demo")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private Label status;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        
        VerticalLayout wrapperLayout = new VerticalLayout();

        // Simple status label
        status = new Label("You are now active");
        status.setStyleName("status");

        // Initialize our new UI component
        Idle.track(this, 5000, new Idle.Listener() {

            @Override
            public void userInactive() {
                status.setValue("You are now idle");
            }

            @Override
            public void userActive() {
                status.setValue("You are now active");
            }
        });
        
        wrapperLayout.addComponent(status);
        wrapperLayout.setComponentAlignment(status, Alignment.MIDDLE_CENTER);
        
        Button restartButton = new Button("Restart Application");
        restartButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Page.getCurrent().setLocation("?restartApplication");
            }
        });
        wrapperLayout.addComponent(restartButton);
        wrapperLayout.setComponentAlignment(
                restartButton, Alignment.MIDDLE_CENTER);
        
        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(wrapperLayout);
        layout.setComponentAlignment(wrapperLayout, Alignment.MIDDLE_CENTER);
        setContent(layout);

    }

}
