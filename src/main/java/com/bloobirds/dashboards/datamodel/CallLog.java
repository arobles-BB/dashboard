package com.bloobirds.dashboards.datamodel;


import com.bloobirds.dashboards.datamodel.abstraction.Activity;
import com.bloobirds.dashboards.datamodel.abstraction.BBObjectID;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class CallLog implements Activity {

    public static final int CALL_RESULT_NO_STATUS = 0;
    public static final int CALL_RESULT_ON_PROSPECTION = 1;
    public static final int CALL_RESULT_CONTACTED = 2;
    public static final int CALL_RESULT_ENGAGED = 3;
    public static final int CALL_RESULT_MEETING = 4;
    public static final int CALL_RESULT_ACCOUNT = 5;
    public static final int CALL_RESULT_NURTURING = 6;
    public static final int CALL_RESULT_DISCARDED = 7;

    @EmbeddedId
    private BBObjectID objectID = new BBObjectID();

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    // bug de hibernate y postgres https://shred.zone/cilla/page/299/string-lobs-on-postgresql-with-hibernate-36.html
    private String transcript = "";
    private String origin = "";
    private String destination = "";
    private LocalDate dateCall = null;
    private Integer seconds = 0;
    private Integer callResult = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            joinColumns = {@JoinColumn(name = "objectID.BBObjectID"), @JoinColumn(name = "objectID.tenantID")}
    )
    @ToString.Exclude
    private Map<String, String> attributes = new HashMap<>();

    public void addAttribute(String name, String value) {
        String key=MessageFormat.format("{0}.{1,number}.{2}",objectID.getTenantID(), objectID.getBBobjectID(), name);
        attributes.put(key, value);
    }

    public String getAttribute(String name) {
        String key=MessageFormat.format("{0}.{1,number}.{2}",objectID.getTenantID(), objectID.getBBobjectID(), name);
        return attributes.get(key);
    }

    @Override
    public int getActivityType() {
        return Activity.TYPE_CALL;
    }
}
