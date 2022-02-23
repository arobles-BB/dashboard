package com.bloobirds.dashboards;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@SpringBootApplication
public class Application implements AppShellConfigurator {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
