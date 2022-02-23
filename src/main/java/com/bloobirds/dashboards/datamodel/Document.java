package com.bloobirds.dashboards.datamodel;


import lombok.Data;

@Data
public class Document {

    // Only working for text documents
    public final static int DOC_TYPE_BOOK=0;
    public final static int DOC_TYPE_PRESENTATION=1;
    public final static int DOC_TYPE_PAPER=2;
    public final static int DOC_TYPE_SNIPPET=3;
    public final static int DOC_TYPE_EMAIL_TEMPLATE=4;


    // Document ID
    String id;
    // Document type
    int doc_type;
    // Document main language as in ISO 639
    String language;
    //Document text content
    String content;
    // sentiment from -5 to +5
    int sentiment;
    // category analysis, for a full list of supported categories see....
    String[] categories;
    // ner PERSON
    String[] persons;
    // ner ORG
    String[] organizations;
    // ner LOC
    String[] localizations;
    // other ner [TYPE1][[VALUE1],[VALUE2],[VALUE3],...]
    String [][] other_ner;

}