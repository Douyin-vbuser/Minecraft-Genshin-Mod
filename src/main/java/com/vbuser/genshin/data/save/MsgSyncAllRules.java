package com.vbuser.genshin.data.save;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

import java.util.HashMap;
import java.util.Map;

public class MsgSyncAllRules implements IMessage {
    public NBTTagCompound tag;

    public MsgSyncAllRules() {}
    public MsgSyncAllRules(Map<String, StoredRule> rules) {
        tag = new NBTTagCompound();
        for (StoredRule r : rules.values()) {
            NBTTagCompound one = new NBTTagCompound();
            one.setString("type", r.type.id());
            one.setString("value", r.value == null ? "" : r.value);
            tag.setTag(r.key, one);
        }
    }

    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeTag(buf, tag); }
    @Override public void fromBytes(ByteBuf buf) { tag = ByteBufUtils.readTag(buf); }

    public static class Handler implements IMessageHandler<MsgSyncAllRules, IMessage> {
        @Override public IMessage onMessage(MsgSyncAllRules msg, MessageContext ctx) {
            net.minecraftforge.fml.common.FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                Map<String, StoredRule> map = new HashMap<>();
                for (String key : msg.tag.getKeySet()) {
                    NBTTagCompound one = msg.tag.getCompoundTag(key);
                    RuleType type = RuleType.from(one.getString("type"));
                    String value = one.getString("value");
                    map.put(key, new StoredRule(key, type, value));
                }
                RuleManager.clientAcceptFullSync(map);
            });
            return null;
        }
    }
}
