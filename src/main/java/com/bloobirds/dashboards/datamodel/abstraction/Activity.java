package com.bloobirds.dashboards.datamodel.abstraction;

public interface Activity {

    final static int TYPE_CALL=10;
    final static int TYPE_EMAIL=11;
    final static int TYPE_LINKEDIN=12;
    final static int TYPE_MEETING=13;

    public int getActivityType();
}
