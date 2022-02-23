package com.bloobirds.dashboards.ui;

import com.bloobirds.dashboards.datamodel.Company;
import com.bloobirds.dashboards.datamodel.service.CompanyService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "/company", layout = MainView.class)
@PageTitle("Company")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
public class CompanyView extends Div {

    public CompanyView(@Autowired CompanyService service) {
        var grid = new EnhancedGrid<Company>();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(company -> company.getObjectID().getTenantID()).setHeader("TENANT").setAutoWidth(true).setResizable(true);
        grid.addColumn(company -> company.getObjectID().getBBobjectID()).setHeader("ID").setAutoWidth(true).setResizable(true);
        grid.addColumn(Company::getName).setHeader("Company Name").setAutoWidth(true);
        grid.addColumn(Company::getStatus).setHeader("Status").setAutoWidth(true);
        //@TODO This is the worst!! every single company generates 4 different Queries. Need to work on my laziness ;)
        //Hibernate: select company0_.bbobjectid as bbobject1_1_, company0_.tenantid as tenantid2_1_, company0_.suobjectid as suobject5_1_, company0_.sutenantid as sutenant6_1_, company0_.name as name3_1_, company0_.status as status4_1_ from company company0_ limit ?
        //Hibernate: select salesuser0_.bbobjectid as bbobject1_4_0_, salesuser0_.tenantid as tenantid2_4_0_, salesuser0_.email as email3_4_0_, salesuser0_.name as name4_4_0_, salesuser0_.phone_number as phone_nu5_4_0_, salesuser0_.status as status6_4_0_, salesuser0_.surname as surname7_4_0_ from sales_user salesuser0_ where salesuser0_.bbobjectid=? and salesuser0_.tenantid=?
        //Hibernate: select attributes0_.objectid_bbobjectid as objectid1_3_0_, attributes0_.objectid_tenantid as objectid2_3_0_, attributes0_.attribute_name as attribut3_3_0_, attributes0_.attribute_value as attribut4_3_0_, attributes0_.attributes_key as attribut5_0_ from extended_attributes attributes0_ where attributes0_.objectid_bbobjectid=? and attributes0_.objectid_tenantid=?
        //Hibernate: select attributes0_.objectid_bbobjectid as objectid1_3_0_, attributes0_.objectid_tenantid as objectid2_3_0_, attributes0_.attribute_name as attribut3_3_0_, attributes0_.attribute_value as attribut4_3_0_, attributes0_.attributes_key as attribut5_0_ from extended_attributes attributes0_ where attributes0_.objectid_bbobjectid=? and attributes0_.objectid_tenantid=?

        grid.addColumn(company -> company.getAssignTo().getFullName()).setHeader("AssignedTo").setAutoWidth(true);
        grid.setItems(query -> service.findAll(query.getPage(), query.getPageSize()));

        Div messageDiv = new Div();
        grid.asSingleSelect().addValueChangeListener(event -> {
            Company current = event.getValue();
            if (current == null) current = new Company();
            Company old = event.getOldValue();
            if (old == null) old = new Company();
            String message = String.format("Selection changed from [%s|%s] to [%s|%s]",
                    old.getObjectID().getTenantID(), old.getObjectID().getBBobjectID(),
                    current.getObjectID().getTenantID(), current.getObjectID().getBBobjectID());
            messageDiv.setText(message);
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        // add button to generate test data
        Button testDataButton = new Button("Generate Data", e -> generateTestData(service));
        horizontalLayout.add(testDataButton);
        add(horizontalLayout, grid, messageDiv);

        setSizeFull();
    }

    private void generateTestData(CompanyService service) {
    }
}
