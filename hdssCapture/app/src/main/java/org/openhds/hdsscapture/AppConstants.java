package org.openhds.hdsscapture;

public class AppConstants {

    public static final int NO = 0, YES = 1, UK = 8, NA = 9, RUR = 1, URB = 2;
    public static final int OTHER_SPECIFY = 66;

    public static final String MARKED_COMPLETE="MARKED AS COMPLETED";
    public static final String MARKED_INCOMPLETE="INCOMPLETE";
    public static final String NOT_DONE="NOT DONE";
    public static final String NOT_ENROLLED = "NOT ENROLLED";

    public static final int NEW_WOMAN = 0 , ACTIVE_WOMAN = 1;

    public static final int NOT_COMPLETE = 0, COMPLETE=1, ELIGIBILITY_COMPLETE=2, ELIGIBILITY_ENROLLED=3;
    public static final int NOSELECT = 0;
    public static final String SPINNER_NOSELECT = "--Select an option--";

    public static final int SCREENING=1, ENROLMENT=2, ANCVISIT=3, DELIVERY=4, PNCVISIT=5, ADHOC=6, CLOSEOUT=7;

    public static final int BEFORE_ENROLMENT = 2, AFTER_ENROLMENT = 1;

    public static final int ANC20 = 20, ANC25 = 25, ANC30 = 30, ANC34 = 34, ANC37 = 37;
    public static final int PNC00 = 0, PNC01 = 1, PNC04 = 4, PNC06 = 6, PNC26 = 26, PNC52 =52;
    public static final int CHILD1 = 1, CHILD2 = 2, CHILD3 = 3, CHILD4 = 4;

    public static final String
            MNH00S = "MNH00 Screening",
            MNH00C = "MNH00 Consenting",
            MNH01 = "Ultrasound Before Enrolment",
            MNH02 = "MNH02 Enrollment Status",
            MNH03 = "MNH03 Socio-demographic",
            MNH04 = "Clinical Status",
            MNH05 = "Maternal Anthropometry",
            MNH06 = "Maternal PoC Diagnostics",
            MNH07 = "Maternal Specimen Collection",
            MNH08 = "Maternal Lab Results",
            MNH09 = "LD Outcome",
            MNH10 = "Maternal Post Delivery Outcome",
            MNH11a = "Newborn Birth Outcome (1st Infant)",
            MNH11b = "Newborn Birth Outcome (2nd Infant)",
            MNH11c = "Newborn Birth Outcome (3rd Infant)",
            MNH11d = "Newborn Birth Outcome (4th Infant)",
            MNH12 = "Maternal PNC Clinical Status",
            MNH13a = "Infant PNC Clinical Status (1st Infant)",
            MNH13b = "Infant PNC Clinical Status (2nd Infant)",
            MNH13c = "Infant PNC Clinical Status (3rd Infant)",
            MNH13d = "Infant PNC Clinical Status (4th Infant)",
            MNH14a = "Infant PoC Diagnotics (1st Infant)",
            MNH14b = "Infant PoC Diagnotics (2nd Infant)",
            MNH14c = "Infant PoC Diagnotics (3rd Infant)",
            MNH14d = "Infant PoC Diagnotics (4th Infant)",
            MNH15a = "Infant Vaccination Status (1st Infant)",
            MNH15b = "Infant Vaccination Status (2nd Infant)",
            MNH15c = "Infant Vaccination Status (3rd Infant)",
            MNH15d = "Infant Vaccination Status (4th Infant)",
            MNH16 = "Exit ANC",
            MNH17 = "Exit Intrapartum",
            MNH18 = "Exit PNC",
            MNH19 = "Maternal Hospitalization",
            MNH20 = "Infant Hospitalization",
            MNH21 = "Adverse Events",
            MNH22 = "Protocol Deviation",
            MNH23 = "Maternal Closeout",
            MNH24a = "Infant Closeout (1st Infant)",
            MNH24b = "Infant Closeout (2nd Infant)",
            MNH24c = "Infant Closeout (3rd Infant)",
            MNH24d = "Infant Closeout (4th Infant)";

    public static final String
            EVENT_MNH00S = "SCREENING",
            EVENT_MNH00C = "CONSENTING",
            EVENT_MNH01 = "ULTRASOUND A (Eligibility Check)",
            EVENT_MNH02 = "ENROLLMENT",
            EVENT_MNH03 = "SOCIO-DEMOGRAPHICS",

    EVENT_MNH04A1 = "ANC 1 CLINICAL",
            EVENT_MNH05A1 = "ANC 1 ANTHROPOMETRY",
            EVENT_MNH06A1 = "ANC 1 PoC DIAGNOSTICS",
            EVENT_MNH07A1 = "ANC 1 SPECIMEN COLLECTION",
            EVENT_MNH08A1 = "ANC 1 LAB RESULTS",

    EVENT_MNH04A2 = "ANC 2 CLINICAL",
            EVENT_MNH05A2 = "ANC 2 ANTHROPOMETRY",
            EVENT_MNH06A2 = "ANC 2 PoC DIAGNOSTICS",
            EVENT_MNH07A2 = "ANC 2 SPECIMEN COLLECTION",
            EVENT_MNH08A2 = "ANC 2 LAB RESULTS",

    EVENT_MNH04A3 = "ANC 3 CLINICAL STATUS",
            EVENT_MNH05A3 = "ANC 3 ANTHROPOMETRY",
            EVENT_MNH06A3 = "ANC 3 PoC DIAGNOSTICS",
            EVENT_MNH07A3 = "ANC 3 SPECIMEN COLLECTION",
            EVENT_MNH08A3 = "ANC 3 LAB RESULTS",

    EVENT_MNH04A4 = "ANC 4 CLINICAL STATUS",
            EVENT_MNH05A4 = "ANC 4 ANTHROPOMETRY",
            EVENT_MNH06A4 = "ANC 4 PoC DIAGNOSTICS",
            EVENT_MNH07A4 = "ANC 4 SPECIMEN COLLECTION",
            EVENT_MNH08A4 = "ANC 4 LAB RESULTS",

    EVENT_MNH32 = "ULTRASOUND B (After Enrolment)",

            EVENT_MNH04A5 = "ANC 5 CLINICAL STATUS",
            EVENT_MNH05A5 = "ANC 5 ANTHROPOMETRY",
            EVENT_MNH06A5 = "ANC 5 PoC DIAGNOSTICS",
            EVENT_MNH07A5 = "ANC 5 SPECIMEN COLLECTION",
            EVENT_MNH08A5 = "ANC 5 LAB RESULTS",


    EVENT_MNH16 = "EXIT ANTENATAL CARE",

    EVENT_MNH09 = "MATERNAL DELIVERY",
            EVENT_MNH10 = "POST DELIVERY",
            EVENT_MNH11a = "NEWBORN BIRTH OUTCOME (1st Infant)",
            EVENT_MNH11b = "NEWBORN BIRTH OUTCOME (2nd Infant)",
            EVENT_MNH11c = "NEWBORN BIRTH OUTCOME (3rd Infant)",
            EVENT_MNH11d = "NEWBORN BIRTH OUTCOME (4th Infant)",

    EVENT_MNH17 = "EXIT INTRAPARTUM",

    EVENT_MNH12P0 = "PNC 0 MATERNAL CLINICAL STATUS",
            EVENT_MNH06P0 = "PNC 0 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH13P0a = "PNC 0 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P0b = "PNC 0 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P0c = "PNC 0 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P0d = "PNC 0 INFANT CLINICAL STATUS (4th Infant)",

            EVENT_MNH14P0a = "PNC 0 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P0b = "PNC 0 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P0c = "PNC 0 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P0d = "PNC 0 INFANT DIAGNOSTICS (4th Infant)",

            EVENT_MNH15P0a = "PNC 0 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P0b = "PNC 0 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P0c = "PNC 0 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P0d = "PNC 0 INFANT VACCINATION (4th Infant)",

    EVENT_MNH12P1 = "PNC 1 MATERNAL CLINICAL STATUS",
            EVENT_MNH06P1 = "PNC 1 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH13P1a = "PNC 1 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P1b = "PNC 1 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P1c = "PNC 1 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P1d = "PNC 1 INFANT CLINICAL STATUS (4th Infant)",

    EVENT_MNH14P1a = "PNC 1 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P1b = "PNC 1 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P1c = "PNC 1 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P1d = "PNC 1 INFANT DIAGNOSTICS (4th Infant)",

    EVENT_MNH15P1a = "PNC 1 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P1b = "PNC 1 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P1c = "PNC 1 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P1d = "PNC 1 INFANT VACCINATION (4th Infant)",

    EVENT_MNH12P2 = "PNC 2 MATERNAL CLINICAL STATUS",
            EVENT_MNH06P2 = "PNC 2 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH13P2a = "PNC 2 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P2b = "PNC 2 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P2c = "PNC 2 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P2d = "PNC 2 INFANT CLINICAL STATUS (4th Infant)",

    EVENT_MNH14P2a = "PNC 2 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P2b = "PNC 2 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P2c = "PNC 2 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P2d = "PNC 2 INFANT DIAGNOSTICS (4th Infant)",

    EVENT_MNH15P2a = "PNC 2 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P2b = "PNC 2 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P2c = "PNC 2 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P2d = "PNC 2 INFANT VACCINATION (4th Infant)",

    EVENT_MNH12P3 = "PNC 3 MATERNAL CLINICAL STATUS",
            EVENT_MNH05P3 = "PNC 3 ANTHROPOMETRY",
            EVENT_MNH06P3 = "PNC 3 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH07P3 = "PNC 3 SPECIMEN COLLECTION",
            EVENT_MNH08P3 = "PNC 3 LAB RESULTS",
            EVENT_MNH13P3a = "PNC 3 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P3b = "PNC 3 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P3c = "PNC 3 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P3d = "PNC 3 INFANT CLINICAL STATUS (4th Infant)",

    EVENT_MNH14P3a = "PNC 3 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P3b = "PNC 3 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P3c = "PNC 3 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P3d = "PNC 3 INFANT DIAGNOSTICS (4th Infant)",

    EVENT_MNH15P3a = "PNC 3 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P3b = "PNC 3 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P3c = "PNC 3 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P3d = "PNC 3 INFANT VACCINATION (4th Infant)",

    EVENT_MNH12P4 = "PNC 4 MATERNAL CLINICAL STATUS",
            EVENT_MNH05P4 = "PNC 4 ANTHROPOMETRY",
            EVENT_MNH06P4 = "PNC 4 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH07P4 = "PNC 4 SPECIMEN COLLECTION",
            EVENT_MNH08P4 = "PNC 4 LAB RESULTS",
            EVENT_MNH13P4a = "PNC 4 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P4b = "PNC 4 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P4c = "PNC 4 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P4d = "PNC 4 INFANT CLINICAL STATUS (4th Infant)",

    EVENT_MNH14P4a = "PNC 4 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P4b = "PNC 4 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P4c = "PNC 4 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P4d = "PNC 4 INFANT DIAGNOSTICS (4th Infant)",

    EVENT_MNH15P4a = "PNC 4 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P4b = "PNC 4 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P4c = "PNC 4 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P4d = "PNC 4 INFANT VACCINATION (4th Infant)",

    EVENT_MNH12P5 = "PNC 5 MATERNAL CLINICAL STATUS",
            EVENT_MNH05P5 = "PNC 5 ANTHROPOMETRY",
            EVENT_MNH06P5 = "PNC 5 MATERNAL PoC DIAGNOSTICS",
            EVENT_MNH13P5a = "PNC 5 INFANT CLINICAL STATUS (1st Infant)",
            EVENT_MNH13P5b = "PNC 5 INFANT CLINICAL STATUS (2nd Infant)",
            EVENT_MNH13P5c = "PNC 5 INFANT CLINICAL STATUS (3rd Infant)",
            EVENT_MNH13P5d = "PNC 5 INFANT CLINICAL STATUS (4th Infant)",

    EVENT_MNH14P5a = "PNC 5 INFANT DIAGNOSTICS (1st Infant)",
            EVENT_MNH14P5b = "PNC 5 INFANT DIAGNOSTICS (2nd Infant)",
            EVENT_MNH14P5c = "PNC 5 INFANT DIAGNOSTICS (3rd Infant)",
            EVENT_MNH14P5d = "PNC 5 INFANT DIAGNOSTICS (4th Infant)",

    EVENT_MNH15P5a = "PNC 5 INFANT VACCINATION (1st Infant)",
            EVENT_MNH15P5b = "PNC 5 INFANT VACCINATION (2nd Infant)",
            EVENT_MNH15P5c = "PNC 5 INFANT VACCINATION (3rd Infant)",
            EVENT_MNH15P5d = "PNC 5 INFANT VACCINATION (4th Infant)",

    EVENT_MNH18 = "EXIT POST NATAL CARE",

    EVENT_MNH19 = "MATERNAL HOSPITALIZATION",
            EVENT_MNH20 = "INFANT HOSPITALIZATION",
            EVENT_MNH21 = "MATERNAL ADVERSE EVENT",
            EVENT_MNH21I = "INFANT ADVERSE EVENT",
            EVENT_MNH22 = "PROTOCOL DEVIATION",
            EVENT_MNH23 = "MATERNAL CLOSEOUT",

    EVENT_MNH24a = "INFANT CLOSEOUT (1st Infant)",
            EVENT_MNH24b = "INFANT CLOSEOUT (2nd Infant)",
            EVENT_MNH24c = "INFANT CLOSEOUT (3rd Infant)",
            EVENT_MNH24d = "INFANT CLOSEOUT (4th Infant)";


}
