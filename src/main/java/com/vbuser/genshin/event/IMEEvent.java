package com.vbuser.genshin.event;

import com.vbuser.ime.IMEController;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class IMEEvent {

    boolean pre = false;

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        boolean post = Minecraft.getMinecraft().player != null;
        if (post != pre) {
            pre = post;
            IMEController.toggleIME(post);
            System.out.println("IME turned " + (post ? "on" : "off"));
        }
    }
}
