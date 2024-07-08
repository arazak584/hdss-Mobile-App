package org.openhds.hdsscapture;

public class AppConstants {

    public static final int NO = 2, YES = 1, UK = 8, NA = 9, RUR = 1, URB = 2, LPG =3, non=10;
    public static final int OTHER_SPECIFY = 77;

    public static final String MARKED_COMPLETE="MARKED AS COMPLETED";
    public static final String MARKED_INCOMPLETE="INCOMPLETE";
    public static final String UPDATE="NO MODIFICATION";
    public static final String NOT_DONE="NOT DONE OR UPDATED";
    public static final String SUBMITTED="SUBMITTED";
    public static final String NOT_VISITED = "NOT VISITED";

    public static final int NOT_UPDATED = 0, COMPLETE=1, UPDATED=2;
    public static final int NOSELECT = 0;
    public static final String SPINNER_NOSELECT = "--Select an option--";

    public static final String url="http://localhost.org:8080";
    public static final String Mother = "UNK", Father = "UNK", Location ="0000";

    public static final int IND1 = 1, IND2 = 2, IND3 = 3;

    public static final int CHILD =1;
    public static final int CHILD1 = 1, CHILD2 = 2, CHILD3 = 3, CHILD4 = 4;
    public static final int CHILD5 = 5, CHILD6 = 6, CHILD7 = 7, CHILD8 = 8;
    public static final int RES1 = 1, RES2 = 2;
    public static final int PREG1 = 1, PREG2 = 2,PREG3 = 3;


    public static final String
            EVENT_MIND00S = "INDIVIDUAL",
            EVENT_BASE = "BASELINE",
            EVENT_HDSS1 = "MEMBERSHIP REGISTRATION",
            EVENT_HDSS2 = "VISIT REGISTRATION",
            EVENT_HDSS3 = "CHANGE HOH",
            EVENT_HDSS4 = "DEMOGRAPHICS",
            EVENT_HDSS5 = "SOCIO-DEMOGRAPHICS",
            EVENT_SOCIO = "SOCIO-ECONOMIC PROFILE [SES]",
            EVENT_HDSS6 = "DEATH REGISTRATION",
            EVENT_HDSS7 = "RELATIONSHIP REGISTRATION",
            EVENT_HDSS8 = "OUTMIGRATION REGISTRATION",
            EVENT_HDSS9 = "INMIGRATION REGISTRATION",
            EVENT_HDSS10 = "PREGNANCY OBSERVATION",
            EVENT_HDSS13 = "PREGNANCY OBSERVATION [EXTRA]",
            EVENT_HDSS14 = "AMENDMENT",
            EVENT_HDSS15 = "VACCINATION",
            EVENT_HDSS16 = "DUPLICATE",
            EVENT_HDSS11 = "PREGNANCY OUTCOME",
            EVENT_HDSS12 = "PREGNANCY OUTCOME [EXTRA]",

            EVENT_RESIDENCY = "MEMBERSHIP REGISTRATION",
            EVENT_VISIT = "VISIT REGISTRATION",
            EVENT_RELATIONSHIP = "RELATIONSHIP REGISTRATION",
            EVENT_OBSERVATION = "PREGNANCY OBSERVATION",
            EVENT_OBSERVATION2 = "PREGNANCY OBSERVATION [EXTRA]",
            EVENT_OUTCOME = "PREGNANCY OUTCOME",
            EVENT_OUTCOMES = "PREGNANCY OUTCOME [EXTRA]",
            EVENT_HOUSEHOLD = "CHANGE HOH",
            EVENT_DEATH = "DEATH REGISTRATION",
            EVENT_DEMO = "DEMOGRAPHICS",
            EVENT_DSOCIO = "SOCIO-ECONOMIC PROFILE [SES]",
            EVENT_OMG = "OUTMIGRATION REGISTRATION",
            EVENT_IMG = "INMIGRATION REGISTRATION",
            EVENT_AMEND = "AMENDMENT",
            EVENT_DUP = "DUPLICATE",
            EVENT_VAC = "VACCINATION";

    public static final String DATA_CAPTURE = "file:///android_asset/data_capture_code.html";
    public static final String DATA_SYNC = "file:///android_asset/data_sync.html";
    public static final String DATA_REPORT = "file:///android_asset/data_report.html";
    public static final String DATA_DOWNLOAD = "file:///android_asset/data_download.html";
    public static final String DATA_VIEWS = "file:///android_asset/data_views.html";
    public static final String DATA_QUERY = "file:///android_asset/data_query.html";
    public static final String DOWNLOAD_SES = "file:///android_asset/ses_download.html";
    public static final String DOWNLOAD_IND = "file:///android_asset/ind_download.html";
    public static final String DOWNLOAD_DEMO = "file:///android_asset/demo_download.html";
    public static final String DENO_INFO = "file:///android_asset/deno_views.html";

}
