package com.bloobirds.dashboards;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class User {

    private static final Properties messages=new Properties();

    private static final VaadinSession session=VaadinSession.getCurrent();

    public static void setAttribute(String name, Object value, UI ui){
        ui.access(() -> session.setAttribute(name,value));
    }

    public static Object getAttribute(String name,UI ui){
        String message = getMessage(name);
        if (message!=null) return message;
        AtomicReference<Object> result= new AtomicReference<>();
        ui.access(() -> result.set(session.getAttribute(name)));
        return result;

    }

    public static String getMessage(String key) {
        if (messages.isEmpty()) initialize();
        return messages.getProperty(key);
    }

    /**
     * FAKE INIT!
     */
    private static void initialize() {
        try {
            User.messages.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("vaadin-i18n.properties"));
        } catch (IOException e) {
            log.error(e);
        }



        User.messages.setProperty("tenant","TENANT");
        User.messages.setProperty("id","ID");

        User.messages.setProperty("salesuser.name","Name");
        User.messages.setProperty("salesuser.surname","Surname");
        User.messages.setProperty("salesuser.phone","Phone");
        User.messages.setProperty("salesuser.email","email");
        User.messages.setProperty("salesuser.status","Status");
        User.messages.setProperty("salesuser.status.nostatus","NO STATUS");
        User.messages.setProperty("salesuser.status.active","ACTIVE");
        User.messages.setProperty("salesuser.status.inactive","INACTIVE");

        User.messages.setProperty("salesuser.status.3","DELETED");

        User.messages.setProperty("salesuser.datamodel.fields", String.valueOf(2));

        User.messages.setProperty("salesuser.field.0","Role");
        User.messages.setProperty("salesuser.field.1","Function");


        User.messages.setProperty("contact.name","Name");
        User.messages.setProperty("contact.surname","Surname");
        User.messages.setProperty("contact.role","Role");
        User.messages.setProperty("contact.email","email");
        User.messages.setProperty("contact.status","Status");
        User.messages.setProperty("contact.assignto","Assigned To");

        User.messages.setProperty("contact.status.nostatus","NO STATUS");
        User.messages.setProperty("contact.status.onprospection","ON PROSPECTION");
        User.messages.setProperty("contact.status.contacted","CONTACTED");
        User.messages.setProperty("contact.status.engaged","ENGAGED");
        User.messages.setProperty("contact.status.meeting","MEETING");
        User.messages.setProperty("contact.status.account","ACCOUNT");
        User.messages.setProperty("contact.status.nurturing","NURTURING");
        User.messages.setProperty("contact.status.discarded","DISCARDED");
        User.messages.setProperty("contact.status.8","NEW");

        User.messages.setProperty("contact.datamodel.fields", String.valueOf(2));

        User.messages.setProperty("contact.field.0","Rate");
        User.messages.setProperty("contact.field.1","Source");


    }

}
