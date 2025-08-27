package com.vbuser.genshin.data.save;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MsgUpdateRule implements IMessage {
    public String key;
    public String type;
    public String value;

    public MsgUpdateRule() {}
    public MsgUpdateRule(StoredRule rule) {
        this.key = rule.key;
        this.type = rule.type.id();
        this.value = rule.value == null ? "" : rule.value;
    }

    @Override public void toBytes(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("key", key);
        tag.setString("type", type);
        tag.setString("value", value);
        ByteBufUtils.writeTag(buf, tag);
    }
    @Override public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        assert tag != null;
        key = tag.getString("key");
        type = tag.getString("type");
        value = tag.getString("value");
    }

    public static class Handler implements IMessageHandler<MsgUpdateRule, IMessage> {
        @Override public IMessage onMessage(MsgUpdateRule msg, MessageContext ctx) {
            net.minecraftforge.fml.common.FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(()
                    -> RuleManager.clientAcceptUpdate(new StoredRule(msg.key, RuleType.from(msg.type), msg.value)));
            return null;
        }
    }
}