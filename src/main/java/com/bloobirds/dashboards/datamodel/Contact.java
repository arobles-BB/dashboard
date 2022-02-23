package com.bloobirds.dashboards.datamodel;

import com.bloobirds.dashboards.datamodel.abstraction.BBObjectID;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class Contact {
    final static int BBOBJECT_TYPE=40;

    private String name;
    private String surname;
    private String jobTitle;
    private String linkedIn;
    private String phoneNumber;
    private String email;

    public static final int STATUS_NO_STATUS = 0;
    public static final int STATUS_ON_PROSPECTION = 1;
    public static final int STATUS_CONTACTED = 2;
    public static final int STATUS_ENGAGED = 3;
    public static final int STATUS_MEETING = 4;
    public static final int STATUS_ACCOUNT = 5;
    public static final int STATUS_NURTURING = 6;
    public static final int STATUS_DISCARDED = 7;


    private int status;

    @EmbeddedId
    private BBObjectID objectID = new BBObjectID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SUtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "SUobjectID", referencedColumnName = "BBobjectID")
    })
    private SalesUser assignTo;

    public String getAssignToFullName(){
        if (assignTo == null) return "";
        else return assignTo.getFullName();
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            joinColumns = {@JoinColumn(name = "objectID.BBobjectID"), @JoinColumn(name = "objectID.tenantID")}
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
}
