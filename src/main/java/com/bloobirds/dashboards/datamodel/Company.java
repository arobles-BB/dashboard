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
public class Company {

    final static int BBOBJECT_TYPE=20;

    @EmbeddedId
    private BBObjectID objectID = new BBObjectID();

    private String name;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "SUtenantID", referencedColumnName = "tenantID"),
            @JoinColumn(name = "SUobjectID", referencedColumnName = "BBobjectID")
    })
    private SalesUser assignTo;

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
