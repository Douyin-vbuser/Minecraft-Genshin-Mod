package com.vbuser.genshin.data.save;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public final class RuleManager {
    private RuleManager() {}

    public static final String MODID = "genshin";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID + ":rules");

    private static final Map<String, StoredRule> SERVER = new LinkedHashMap<>();
    private static final Map<String, StoredRule> CLIENT = new LinkedHashMap<>();
    private static final Map<String, StoredRule> DEFAULTS = new LinkedHashMap<>();
    private static boolean serverReady = false;

    public static void preInit(FMLPreInitializationEvent e) {
        NETWORK.registerMessage(MsgSyncAllRules.Handler.class, MsgSyncAllRules.class, 600, Side.CLIENT);
        NETWORK.registerMessage(MsgUpdateRule.Handler.class, MsgUpdateRule.class, 601, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    public static void registerDefault(String key, RuleType type, String defaultValue) {
        DEFAULTS.put(key, new StoredRule(key, type, defaultValue));
    }

    public static boolean getBoolean(String key, boolean def) {
        StoredRule r = getRuleEffective(key);
        return r != null ? r.asBoolean(def) : def;
    }
    public static int getInt(String key, int def) {
        StoredRule r = getRuleEffective(key);
        return r != null ? r.asInt(def) : def;
    }
    public static double getDouble(String key, double def) {
        StoredRule r = getRuleEffective(key);
        return r != null ? r.asDouble(def) : def;
    }
    public static String getString(String key, String def) {
        StoredRule r = getRuleEffective(key);
        return r != null ? r.asString(def) : def;
    }

    public static void setOnServer(String key, RuleType type, String value) {
        ensureServerThread();
        StoredRule rule = new StoredRule(key, type, value);
        SERVER.put(key, rule);
        DBRuleStore.upsert(rule);
        broadcastUpdate(rule);
        CommandRule.RuleManagerDump.acceptServerSnapshot(SERVER);
    }

    private static StoredRule getRuleEffective(String key) {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT) {
            return CLIENT.get(key);
        } else {
            return SERVER.get(key);
        }
    }

    public static void registerDefaultRule(){
        //Test rule:
        RuleManager.registerDefault("test_boolean", RuleType.BOOLEAN, "true");
        RuleManager.registerDefault("test_double", RuleType.DOUBLE, "114.514");
        RuleManager.registerDefault("test_string", RuleType.STRING, "MiHomo");

        //Real rule:
        RuleManager.registerDefault("glider_enabled",RuleType.BOOLEAN,"false");
    }

    private static void broadcastFull(EntityPlayerMP to) {
        NETWORK.sendTo(new MsgSyncAllRules(SERVER), to);
    }

    private static void broadcastUpdate(StoredRule r) {
        NETWORK.sendToAll(new MsgUpdateRule(r));
    }

    private static void ensureServerThread() {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
            throw new IllegalStateException("RuleManager.setOnServer must be called on server side.");
        }
    }

    public static void clientAcceptFullSync(Map<String, StoredRule> data) {
        CLIENT.clear();
        CLIENT.putAll(data);
    }

    public static void clientAcceptUpdate(StoredRule rule) {
        CLIENT.put(rule.key, rule);
    }

    public static class Events {

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load e) throws IOException {
            if (e.getWorld().isRemote) return;
            if (e.getWorld().provider.getDimension() != 0) return;
            DBRuleStore.initForWorld(e.getWorld());

            SERVER.clear();
            SERVER.putAll(DBRuleStore.loadAll());

            for (StoredRule def : DEFAULTS.values()) {
                if (!SERVER.containsKey(def.key)) {
                    SERVER.put(def.key, def);
                    if (!DBRuleStore.existsKey(def.key)) {
                        DBRuleStore.insert(def);
                    }
                }
            }

            serverReady = true;
            CommandRule.RuleManagerDump.acceptServerSnapshot(SERVER);
        }

        @SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload e) {
            if (e.getWorld().isRemote) return;
            if (e.getWorld().provider.getDimension() != 0) return;
            SERVER.clear();
            DBRuleStore.clear();
            serverReady = false;
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
            if (e.player.world.isRemote) return;
            if (!(e.player instanceof EntityPlayerMP)) return;
            if (!serverReady) return;
            broadcastFull((EntityPlayerMP) e.player);
        }
    }
}
