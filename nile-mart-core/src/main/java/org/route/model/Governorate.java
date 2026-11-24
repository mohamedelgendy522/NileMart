package org.route.model;

public enum Governorate {
    CAIRO(Zone.GREATER_CAIRO, "Cairo"),
    GIZA(Zone.GREATER_CAIRO, "Giza"),
    QALYUBIA(Zone.GREATER_CAIRO, "Qalyubia"),

    ALEXANDRIA(Zone.DELTA, "Alexandria"),
    BEHEIRA(Zone.DELTA, "Beheira"),
    KAFR_EL_SHEIKH(Zone.DELTA, "Kafr El Sheikh"),
    DAKAHLIA(Zone.DELTA, "Dakahlia"),
    DAMIETTA(Zone.DELTA, "Damietta"),
    GHARBIA(Zone.DELTA, "Gharbia"),
    MONUFIA(Zone.DELTA, "Monufia"),
    SHARQIA(Zone.DELTA, "Sharqia"),

    PORT_SAID(Zone.CANAL, "Port Said"),
    ISMAILIA(Zone.CANAL, "Ismailia"),
    SUEZ(Zone.CANAL, "Suez"),

    FAIYUM(Zone.UPPER_EGYPT, "Faiyum"),
    BENI_SUEF(Zone.UPPER_EGYPT, "Beni Suef"),
    MINYA(Zone.UPPER_EGYPT, "Minya"),
    ASYUT(Zone.UPPER_EGYPT, "Asyut"),
    SOHAG(Zone.UPPER_EGYPT, "Sohag"),
    QENA(Zone.UPPER_EGYPT, "Qena"),
    LUXOR(Zone.UPPER_EGYPT, "Luxor"),
    ASWAN(Zone.UPPER_EGYPT, "Aswan"),

    MATROUH(Zone.REMOTE, "Matrouh"),
    NORTH_SINAI(Zone.REMOTE, "North Sinai"),
    SOUTH_SINAI(Zone.REMOTE, "South Sinai"),
    RED_SEA(Zone.REMOTE, "Red Sea"),
    NEW_VALLEY(Zone.REMOTE, "New Valley");

    private final Zone zone;
    private final String label;

    Governorate(Zone zone, String label) {
        this.zone = zone;
        this.label = label;
    }

    public Zone getZone() {
        return zone;
    }

    public String getLabel() {
        return label;
    }
}