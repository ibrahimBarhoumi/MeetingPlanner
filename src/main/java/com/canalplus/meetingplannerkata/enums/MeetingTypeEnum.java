package com.canalplus.meetingplannerkata.enums;

/**
 * @author IBRAHIM
 */
public enum MeetingTypeEnum {
    VC("VC"), SPEC("SPEC"), RS("RS"), RC("RC");

    public final String label;

    MeetingTypeEnum(String label) {
        this.label = label;
    }
}
