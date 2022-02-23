package com.bloobirds.dashboards.ui;

import com.bloobirds.dashboards.datamodel.CallLog;
import com.bloobirds.dashboards.datamodel.abstraction.BBObjectID;
import com.bloobirds.dashboards.datamodel.service.CallLogService;
import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.badge.Badge;

import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

@Route(value = "", layout = MainView.class)
@PageTitle("CallLog")
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
public class CallsView extends Div {

    private static final SerializableBiConsumer<Badge, CallLog> statusComponentUpdater = (badge, call) -> {
        Integer status = call.getCallResult();
        if (status == null) status = 0;

        switch (status) {
            case CallLog.CALL_RESULT_ON_PROSPECTION -> { badge.setText("ON PROSPECTION"); badge.setVariant(Badge.BadgeVariant.CONTRAST); }
            case CallLog.CALL_RESULT_CONTACTED -> { badge.setText("CONTACTED"); badge.setVariant(Badge.BadgeVariant.SUCCESS); }
            case CallLog.CALL_RESULT_ENGAGED -> { badge.setText("ENGAGED"); badge.setVariant(Badge.BadgeVariant.SUCCESS); }
            case CallLog.CALL_RESULT_MEETING -> { badge.setText("MEETING"); badge.setVariant(Badge.BadgeVariant.SUCCESS); }
            case CallLog.CALL_RESULT_ACCOUNT -> { badge.setText("ACCOUNT"); badge.setVariant(Badge.BadgeVariant.NORMAL); }
            case CallLog.CALL_RESULT_NURTURING -> { badge.setText("NURTURING"); badge.setVariant(Badge.BadgeVariant.CONTRAST); }
            case CallLog.CALL_RESULT_DISCARDED -> { badge.setText("DISCARDED"); badge.setVariant(Badge.BadgeVariant.ERROR); }
            default -> { badge.setText("NO STATUS"); badge.setVariant(Badge.BadgeVariant.CONTRAST); }

        }
    };

    public CallsView(@Autowired CallLogService service) {
        var grid = new EnhancedGrid<CallLog>();

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.addColumn(call -> call.getObjectID().getTenantID()).setHeader("TENANT").setAutoWidth(true).setResizable(true);
        grid.addColumn(call -> call.getObjectID().getBBobjectID()).setHeader("ID").setAutoWidth(true).setResizable(true);
        grid.addColumn(CallLog::getDateCall).setHeader("Date");
        grid.addColumn(createStatusComponentRenderer()).setHeader("Call Result").setAutoWidth(true);
        grid.addColumn(CallLog::getOrigin).setHeader("FROM");
        grid.addColumn(CallLog::getDestination).setHeader("TO").setAutoWidth(true);
        grid.addColumn(CallLog::getSeconds).setHeader("Duration").setAutoWidth(true);

//        grid.setPageSize(10); throws https://github.com/vaadin/flow-components/issues/1283

        grid.setItems(query -> service.findAll(query.getPage(), query.getPageSize()));

        // add column with more button, to invoke when editing item
        grid.addComponentColumn(person -> {
            Button details = new Button("...More");

            details.addClickListener(e -> {
                Dialog dialog = new Dialog(); //@todo not nice
                dialog.getElement().setAttribute("aria-label", "Contact Details");
                VerticalLayout dialogLayout = createDialogLayout(person, dialog);
                dialog.add(dialogLayout);
                dialog.setModal(false);
                dialog.setDraggable(true);
                dialog.open();
            });
            details.setEnabled(true);
            return details;
        });

        Div messageDiv = new Div();
        grid.asSingleSelect().addValueChangeListener(event -> {
            CallLog current = event.getValue();
            if (current == null) current = new CallLog();
            CallLog old = event.getOldValue();
            if (old == null) old = new CallLog();
            String message = String.format("Selection changed from [%s|%s] to [%s|%s]",
                    old.getObjectID().getTenantID(), old.getObjectID().getBBobjectID(),
                    current.getObjectID().getTenantID(), current.getObjectID().getBBobjectID());
            messageDiv.setText(message);
        });

        // add layout for buttons
        var filter = new DatePicker();
        filter.addValueChangeListener(event -> {
            if (filter.getValue() == null)
                grid.setItems(query -> service.findAll(query.getPage(), query.getPageSize()));
            else grid.setItems(query -> service.findAll(filter.getValue(), query.getPage(), query.getPageSize()));
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        horizontalLayout.add(filter);
        // add button to clear all selected filters
        Button clearFiltersButton = new Button("Clear Filters", e -> {grid.clearAllFilters(); filter.clear();});
        horizontalLayout.add(clearFiltersButton);
        // add button to generate test data
        Button testDataButton = new Button("Generate Data", e -> generateTestData(service));
        horizontalLayout.add(testDataButton);

        add(horizontalLayout, grid, messageDiv);
        setSizeFull();
    }

    private VerticalLayout createDialogLayout(CallLog person, Dialog dialog) {
        H2 headline = new H2("CallLog Details");
        headline.getStyle().set("margin", "0").set("font-size", "1.5em")
                .set("font-weight", "bold");
        HorizontalLayout header = new HorizontalLayout(headline);
        header.getElement().getClassList().add("draggable");
        header.setSpacing(false);
        header.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("cursor", "move");
        header.getStyle()
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("margin",
                        "calc(var(--lumo-space-s) * -1) calc(var(--lumo-space-l) * -1) 0");

        TextField nameF = new TextField("Rate");
        nameF.setValue(person.getAttribute("rate"));
        nameF.setEnabled(false);
        VerticalLayout fieldLayout = new VerticalLayout(nameF);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        Button saveButton = new Button("Close", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        buttonLayout
                .setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(header, fieldLayout,
                buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }

    private static ComponentRenderer<Badge, CallLog> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Badge::new, statusComponentUpdater);
    }

    private void generateTestData(CallLogService callLogService){

        BBObjectID BBid= new BBObjectID();
        Random rand= new Random();
        BBid.setTenantID("bloobirds");
        CallLog callLog= new CallLog();

        BBid.setBBobjectID(rand.nextLong(10000));
        callLog.setObjectID(BBid);
        callLog.setDateCall(LocalDate.now().minus(Period.ofDays(rand.nextInt(15))));
        callLog.setCallResult(rand.nextInt(6));
        callLog.setSeconds(rand.nextInt(700));
        callLog.setOrigin(rPG());
        callLog.setDestination(rPG());
        callLog.addAttribute("rate",rates[rand.nextInt(7)]);
        callLog = callLogService.save(callLog);

        BBid.setBBobjectID(rand.nextLong(10000));
        callLog.setObjectID(BBid);
        callLog.setDateCall(LocalDate.now().minus(Period.ofDays(rand.nextInt(15))));
        callLog.setCallResult(rand.nextInt(6));
        callLog.setSeconds(rand.nextInt(700));
        callLog.setOrigin(rPG());
        callLog.setDestination(rPG());
        callLog.addAttribute("rate",rates[rand.nextInt(7)]);
        callLog = callLogService.save(callLog);

        BBid.setBBobjectID(rand.nextLong(10000));
        callLog.setObjectID(BBid);
        callLog.setDateCall(LocalDate.now().minus(Period.ofDays(rand.nextInt(15))));
        callLog.setCallResult(rand.nextInt(6));
        callLog.setSeconds(rand.nextInt(700));
        callLog.setOrigin(rPG());
        callLog.setDestination(rPG());
        callLog.addAttribute("rate",rates[rand.nextInt(7)]);
        callLog = callLogService.save(callLog);

        BBid.setBBobjectID(rand.nextLong(10000));
        callLog.setObjectID(BBid);
        callLog.setDateCall(LocalDate.now().minus(Period.ofDays(rand.nextInt(15))));
        callLog.setCallResult(rand.nextInt(6));
        callLog.setSeconds(rand.nextInt(700));
        callLog.setOrigin(rPG());
        callLog.setDestination(rPG());
        callLog.addAttribute("rate",rates[rand.nextInt(7)]);
        callLogService.save(callLog);
    }

    private String rPG() {
        String digits = "0123456789";
        Random rand= new Random();
        StringBuilder result = new StringBuilder();
        for (int i=0; i < 8; i++) result.append(digits.charAt(rand.nextInt(10)));

        return result.toString();
    }
    String[] rates = {"A++","A+","A","B+","B","C","D"};

}
