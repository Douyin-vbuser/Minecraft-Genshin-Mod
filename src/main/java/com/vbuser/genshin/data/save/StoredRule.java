package com.vbuser.genshin.data.save;

public class StoredRule {
    public final String key;
    public final RuleType type;
    public final String value;

    public StoredRule(String key, RuleType type, String value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public boolean asBoolean(boolean def) {
        if (value == null) return def;
        String v = value.trim().toLowerCase();
        if ("true".equals(v) || "1".equals(v) || "yes".equals(v)) return true;
        if ("false".equals(v) || "0".equals(v) || "no".equals(v)) return false;
        return def;
    }

    public int asInt(int def) {
        try { return Integer.parseInt(value.trim()); } catch (Exception e) { return def; }
    }

    public double asDouble(double def) {
        try { return Double.parseDouble(value.trim()); } catch (Exception e) { return def; }
    }

    public String asString(String def) {
        return value != null ? value : def;
    }
}
