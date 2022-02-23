package com.bloobirds.dashboards.datamodel.abstraction;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class BBObjectID implements Serializable {
    private String tenantID;
    private long BBobjectID;
}
