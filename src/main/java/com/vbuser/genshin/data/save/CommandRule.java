package com.vbuser.genshin.data.save;

import net.minecraft.command.CommandBase;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.*;
import java.util.stream.Collectors;

public class CommandRule extends CommandBase {

    @Override public String getName() { return "rule"; }
    @Override public String getUsage(ICommandSender sender) { return "/rule <list|get|set>"; }
    @Override public int getRequiredPermissionLevel() { return 2; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Usage: /rule list | get <key> | set <key> <value> [type]"));
            return;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "list": {
                List<String> lines = RuleManagerDump.dumpServer()
                        .stream().sorted().collect(Collectors.toList());
                if (lines.isEmpty()) {
                    sender.sendMessage(new TextComponentString("No rules."));
                } else {
                    for (String l : lines) sender.sendMessage(new TextComponentString(l));
                }
                break;
            }
            case "get": {
                if (args.length < 2) throw new WrongUsageException("/rule get <key>");
                String key = args[1];
                String val = RuleManager.getString(key, null);
                if (val == null) {
                    sender.sendMessage(new TextComponentString("Rule '" + key + "' not found."));
                } else {
                    sender.sendMessage(new TextComponentString(key + " = " + val));
                }
                break;
            }
            case "set": {
                if (args.length < 3) throw new WrongUsageException("/rule set <key> <value> [type]");
                String key = args[1];
                String value = join(args, 2);
                RuleType type;

                String last = args[args.length - 1].toLowerCase(Locale.ROOT);
                RuleType explicit = tryParseType(last);
                if (explicit != null) {
                    type = explicit;
                    value = join(Arrays.copyOfRange(args, 2, args.length - 1), 0);
                } else {
                    String existing = RuleManager.getString(key, null);
                    if (existing != null) {
                        type = RuleManagerDump.getServerType(key);
                        if (type == null) type = RuleType.STRING;
                    } else {
                        type = RuleType.STRING;
                    }
                }

                RuleManager.setOnServer(key, type, value);
                sender.sendMessage(new TextComponentString("Set " + key + " = " + value + " (" + type.id() + ")"));
                break;
            }
            default:
                throw new WrongUsageException(getUsage(sender));
        }
    }

    private static String join(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }
    private static String join(String[] args, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private static RuleType tryParseType(String s) {
        switch (s) {
            case "boolean":
            case "bool": return RuleType.BOOLEAN;
            case "int":
            case "integer": return RuleType.INTEGER;
            case "double":
            case "float": return RuleType.DOUBLE;
            case "string": return RuleType.STRING;
            default: return null;
        }
    }

    public static class RuleManagerDump {
        public static List<String> dumpServer() {
            Map<String, String> m = getServerAllAsText();
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, String> e : m.entrySet()) {
                list.add(e.getKey() + " = " + e.getValue());
            }
            return list;
        }
        public static RuleType getServerType(String key) {
            return SERVER_TYPES.get(key);
        }

        private static final Map<String, String> SERVER_TEXT = new HashMap<>();
        private static final Map<String, RuleType> SERVER_TYPES = new HashMap<>();

        public static void acceptServerSnapshot(Map<String, StoredRule> src) {
            SERVER_TEXT.clear();
            SERVER_TYPES.clear();
            for (StoredRule r : src.values()) {
                SERVER_TEXT.put(r.key, r.value);
                SERVER_TYPES.put(r.key, r.type);
            }
        }

        private static Map<String, String> getServerAllAsText() {
            return new LinkedHashMap<>(SERVER_TEXT);
        }
    }
}
