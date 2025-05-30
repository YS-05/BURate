package com.coursegrade.CourseGraderBackend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
public enum HubRequirement {

    PLM("PLM", "Philosophical Inquiry and Life's Meanings", "PHILOSOPHICAL, AESTHETIC, AND HISTORICAL INTERPRETATION", 1),
    AEX("AEX", "Aesthetic Exploration", "PHILOSOPHICAL, AESTHETIC, AND HISTORICAL INTERPRETATION", 1),
    HCO("HCO", "Historical Consciousness", "PHILOSOPHICAL, AESTHETIC, AND HISTORICAL INTERPRETATION", 1),

    SI1("SI1", "Scientific Inquiry I", "SCIENTIFIC AND SOCIAL INQUIRY", 1),
    SO1("SO1", "Social Inquiry I", "SCIENTIFIC AND SOCIAL INQUIRY", 1),
    SI2("SI2", "Scientific Inquiry II", "SCIENTIFIC AND SOCIAL INQUIRY", 1), // Note: SI2 OR SO2
    SO2("SO2", "Social Inquiry II", "SCIENTIFIC AND SOCIAL INQUIRY", 1),     // Note: SI2 OR SO2

    QR1("QR1", "Quantitative Reasoning I", "QUANTITATIVE REASONING", 1),
    QR2("QR2", "Quantitative Reasoning II", "QUANTITATIVE REASONING", 1),

    IIC("IIC", "The Individual in Community", "DIVERSITY, CIVIC ENGAGEMENT, AND GLOBAL CITIZENSHIP", 1),
    GCI("GCI", "Global Citizenship and Intercultural Literacy", "DIVERSITY, CIVIC ENGAGEMENT, AND GLOBAL CITIZENSHIP", 2),
    ETR("ETR", "Ethical Reasoning", "DIVERSITY, CIVIC ENGAGEMENT, AND GLOBAL CITIZENSHIP", 1),

    FYW("FYW", "First-Year Writing Seminar", "COMMUNICATION", 1),
    WRI("WRI", "Writing, Research, and Inquiry", "COMMUNICATION", 1),
    WIN("WIN", "Writing-Intensive Course", "COMMUNICATION", 2),
    OSC("OSC", "Oral and/or Signed Communication", "COMMUNICATION", 1),
    DME("DME", "Digital/Multimedia Expression", "COMMUNICATION", 1),

    CRT("CRT", "Critical Thinking", "INTELLECTUAL TOOLKIT", 2),
    RIL("RIL", "Research and Information Literacy", "INTELLECTUAL TOOLKIT", 2),
    TWC("TWC", "Teamwork/Collaboration", "INTELLECTUAL TOOLKIT", 2),
    CRI("CRI", "Creativity/Innovation", "INTELLECTUAL TOOLKIT", 2);

    private final String code;
    private final String name;
    private final String category;
    private final int reqCount;

    HubRequirement(String code, String name, String category, int reqCount) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.reqCount = reqCount;
    }

}
