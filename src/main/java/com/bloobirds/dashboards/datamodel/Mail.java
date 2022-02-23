package com.bloobirds.dashboards.datamodel;

import lombok.Data;

@Data
public class Mail {

    public final static String CAT_POSITIVE="Positive";
    public final static String CAT_TEST="Test";

    public final static int SENTIMENT_VERY_POSITIVE=5;
    public final static int SENTIMENT_POSITIVE=3;
    public final static int SENTIMENT_NEUTRAL=0;
    public final static int SENTIMENT_NEGATIVE=-3;
    public final static int SENTIMENT_VERY_NEGATIVE=-5;

    // email ID
    String id;
    // email Subject
    String subject;
    // email message_Text
    String message_text;
    // email from
    String[] from;
    // email to
    String[] to;
    // email cc
    String[] cc;

    // Avoiding mime_attachments in order to simplify scenario

    // Derived attributes

    // email language as in ISO 639
    String language;

    // name on the email signature
    String signatureName;
    // Signature Job Title
    String signatureJobTitle;
    // Signature Company
    String signatureCompany;
    // Signature Phone
    String signaturePhoneNumber;

    // is a reply to a previous message?
    boolean isReply;
    // is forwarding a previous message?
    boolean isForwarding;
    // is an automated reply, like "meeting acceptance/refusal" "no such destination", "out of office"
    boolean isAutomated;
    // is an "out of office"
    boolean isOOO;
    // either contains an invitation to continue chatting (like asking questions) or the overall sentiment is clearly positive
    boolean showsInterest;


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

    /**
     * sales mail structure
     *
     * subject: Call to Action
     *
     * body:
     * - introduction
     * - Hook to sales pitch
     * - problems, solution and value
     * - closing
     *
     *
     */
}