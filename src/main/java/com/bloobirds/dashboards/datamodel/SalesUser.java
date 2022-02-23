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
public class SalesUser {

    final static int BBOBJECT_TYPE=30;

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;

    public static final int STATUS_NO_STATUS = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 2;

    private int status;

    @EmbeddedId
    private BBObjectID objectID = new BBObjectID();

    public String getFullName(){
        return MessageFormat.format("{0} {1}",name,surname);
    }


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

}
