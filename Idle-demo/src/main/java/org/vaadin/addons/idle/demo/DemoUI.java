package org.vaadin.addons.idle.demo;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.TreeSet;

import javax.servlet.annotation.WebServlet;

import org.vaadin.addons.idle.Idle;

@Theme("demo")
@Title("Idle Add-on Demo")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class DemoUI extends UI {
    
    // Some test timeouts
    private final TreeSet<Long> timeouts = new TreeSet<>(); {
        timeouts.add(3000L); // 3 seconds
        timeouts.add(10000L); // 10 seconds
        timeouts.add(30000L); // 30 seconds
        timeouts.add(60000L); // 1 minute
        timeouts.add(120000L); // 2 minutes
        timeouts.add(300000L); // 5 minutes
    }

    private Label status;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        
        VerticalLayout wrapperLayout = new VerticalLayout();
        wrapperLayout.setSizeUndefined();
        wrapperLayout.setSpacing(true);

        // Simple status label
        status = new Label("You are now active");
        status.setStyleName("status");
        
        wrapperLayout.addComponent(status);
        wrapperLayout.setComponentAlignment(status, Alignment.MIDDLE_CENTER);

        // Initialize our new UI component
        Idle idle = Idle.track(this, 5000);
        idle.addUserActiveListener(e -> status.setValue("You are now active"));
        idle.addUserInactiveListener(e -> status.setValue("You are now idle"));
        timeouts.add(idle.getTimeout());
        
        // Combobox to change the inactivity timeout
        ComboBox<Long> timeoutComboBox = new ComboBox<>(
                "Inactivity timeout:", timeouts);
        timeoutComboBox.setWidth(250, Unit.PIXELS);
        timeoutComboBox.setValue(idle.getTimeout());
        timeoutComboBox.setEmptySelectionAllowed(false);
        timeoutComboBox.addValueChangeListener(e -> {
            Long timeout = e.getValue();
            Notification.show("Inactivity timeout is now set to:\n" + 
                        timeout + " ms!", "", 
                        Notification.Type.TRAY_NOTIFICATION);
            idle.setTimeout(timeout);
        });
        
        timeoutComboBox.setItemCaptionGenerator(timeout -> timeout + " ms");
        
        wrapperLayout.addComponent(timeoutComboBox);
        wrapperLayout.setComponentAlignment(
                timeoutComboBox, Alignment.MIDDLE_CENTER);
        
        // Show it in the middle of the screen
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(wrapperLayout);
        layout.setComponentAlignment(wrapperLayout, Alignment.MIDDLE_CENTER);
        setContent(layout);

    }

}
