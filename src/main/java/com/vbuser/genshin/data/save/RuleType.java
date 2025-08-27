package com.vbuser.genshin.data.save;

public enum RuleType {
    BOOLEAN, INTEGER, DOUBLE, STRING;

    public static RuleType from(String s) {
        if (s == null) return STRING;
        switch (s.toLowerCase()) {
            case "bool":
            case "boolean": return BOOLEAN;
            case "int":
            case "integer": return INTEGER;
            case "double":
            case "float": return DOUBLE;
            case "string":
            default: return STRING;
        }
    }

    public String id() {
        switch (this) {
            case BOOLEAN: return "boolean";
            case INTEGER: return "integer";
            case DOUBLE:  return "double";
            case STRING:
            default: return "string";
        }
    }
}
