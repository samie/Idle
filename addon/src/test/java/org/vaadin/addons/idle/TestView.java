package org.vaadin.addons.idle;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class TestView extends VerticalLayout {

    private Idle idle;
    private Paragraph status = new Paragraph("Server-side state: User active");

    private Paragraph timeout = new Paragraph("Timeout: "+Idle.DEFAULT_TIMEOUT+"ms");

    public TestView() {

        idle = Idle.track(UI.getCurrent(),1000);

        idle.addUserActiveListener(e -> status.setText("Server-side state: User active"));
        idle.addUserInactiveListener(e -> status.setText("Server-side state: User inactive"));

        Button enable = new Button("Enable idle tracking", (e) -> idle = Idle.track(UI.getCurrent()));
        Button disable = new Button("Disable idle tracking", (e) -> idle.untrackUI());
        Button update = new Button("Change timeout", (e) -> {

            long newTimeout = idle.getTimeout() + 4000;
            if (newTimeout > 10000) newTimeout = 1000;
            idle.setTimeout(newTimeout);
            timeout.setText("Timeout "+ newTimeout +"ms");
        });

        Button disableCss = new Button("Disable CSS classes", (e) -> {
            idle.setUpdateCss(e.getSource().getText().contains("Enable"));
            e.getSource().setText(idle.isUpdateCss()? "Disable CSS": "Enable CSS");
        });

        // For testing purposes
        status.setTitle("status");

        add(status, timeout, enable, disable, update, disableCss);
    }
}
