package com.bitespeed.identityRec.Classes;

public class LinkPrecedence {
    public static final String SECONDARY = "secondary";
    public static final String PRIMARY = "primary";


    public static boolean isValid(String value) {
        return value.equals(SECONDARY) || value.equals(PRIMARY);
    }
}