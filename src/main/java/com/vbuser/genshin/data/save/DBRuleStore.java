package com.vbuser.genshin.data.save;

import com.vbuser.database.operate.Init;
import com.vbuser.database.operate.New;
import com.vbuser.database.operate.Select;
import com.vbuser.database.operate.Update;
import com.vbuser.database.operate.Delete;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public final class DBRuleStore {
    private DBRuleStore() {}

    private static final String DB_NAME = "genshin_data";
    private static final String TABLE = "rules";
    private static File dataBaseDir;
    private static boolean inited = false;

    public static synchronized void initForWorld(World world) throws IOException {
        if (world.isRemote) return;
        if (world.provider.getDimension() != 0) return;
        File worldDir = world.getSaveHandler().getWorldDirectory();
        File dbDir = new File(worldDir, DB_NAME);

        if (!new File(dbDir, "tables.txt").exists()) {
            dataBaseDir = Init.initBase(DB_NAME, worldDir);
        } else {
            dataBaseDir = dbDir;
        }

        ensureTable();
        inited = true;
    }

    public static synchronized void clear() {
        dataBaseDir = null;
        inited = false;
    }

    private static void ensureTable() {
        try {
            New.createTable(TABLE, new String[]{"key","type","value"}, dataBaseDir);
        } catch (Throwable ignored) {}
    }

    public static synchronized Map<String, StoredRule> loadAll() {
        checkReady();
        List<String> rows = Select.queryTable(dataBaseDir, TABLE, new String[0]);
        Map<String, StoredRule> result = new LinkedHashMap<>();

        boolean isFirstLine = true;
        for (String line : rows) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            Map<String, String> kv = parseRow(line);
            String key = kv.get("key");
            String type = kv.get("type");
            String value = kv.get("value");
            if (key != null) {
                result.put(key, new StoredRule(key, RuleType.from(type), value));
            }
        }
        return result;
    }

    public static synchronized boolean existsKey(String key) {
        checkReady();
        List<String> rows = Select.queryTable(dataBaseDir, TABLE, new String[]{"key=" + key});
        return rows.size() > 1;
    }

    public static synchronized void insert(StoredRule rule) {
        checkReady();
        New.insert(TABLE,
                new String[]{"key","type","value"},
                new String[]{rule.key, rule.type.id(), rule.value},
                dataBaseDir);
    }

    public static synchronized void upsert(StoredRule rule) {
        if (existsKey(rule.key)) {
            Update.updateTable(dataBaseDir, TABLE,
                    new String[]{"type","value"}, new String[]{rule.type.id(), rule.value},
                    new String[]{"key=" + rule.key});
        } else {
            insert(rule);
        }
    }

    public static synchronized void delete(String key) throws IOException {
        checkReady();
        Delete.deleteFrom(dataBaseDir, TABLE, new String[]{"key=" + key});
    }

    private static void checkReady() {
        if (!inited || dataBaseDir == null) {
            throw new IllegalStateException("DBRuleStore not initialized or world not loaded.");
        }
    }

    private static Map<String, String> parseRow(String line) {
        Map<String, String> map = new HashMap<>();
        if (line == null) return map;

        String[] parts = line.split(">");
        if (parts.length >= 3) {
            map.put("key", parts[0].trim());
            map.put("type", parts[1].trim());
            map.put("value", parts[2].trim());
        }
        return map;
    }

    private static String stripQuotes(String v) {
        if (v == null) return null;
        if ((v.startsWith("\"") && v.endsWith("\"")) || (v.startsWith("'") && v.endsWith("'"))) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }
}