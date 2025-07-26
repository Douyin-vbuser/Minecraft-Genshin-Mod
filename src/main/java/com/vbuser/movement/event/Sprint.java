package com.vbuser.movement.event;

import com.vbuser.genshin.proxy.ClientProxy;
import com.vbuser.movement.Movement;
import com.vbuser.movement.network.SprintPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Sprint {

    public static ConcurrentMap<EntityPlayer, Boolean> sprintingPlayers = new ConcurrentHashMap<>();
    private boolean last = true;

    @SubscribeEvent
    public void server(TickEvent.ServerTickEvent event) {
        for (ConcurrentMap.Entry<EntityPlayer, Boolean> entry : sprintingPlayers.entrySet()) {
            EntityPlayer player = entry.getKey();
            player.setSprinting(entry.getValue());
        }
    }

    @SubscribeEvent
    public void sneaking(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.setSneaking(false);
        }
    }

    @SubscribeEvent
    public void enter(PlayerEvent.PlayerLoggedInEvent event) {
        sprintingPlayers.put(event.player, true);
        event.player.setSprinting(true);
        event.player.setSneaking(false);
    }

    @SubscribeEvent
    public void leave(PlayerEvent.PlayerLoggedOutEvent event) {
        sprintingPlayers.remove(event.player);
    }

    @SubscribeEvent
    public void client(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null) {
                player.setSprinting(last);
                player.movementInput.sneak = false;
            }
        }
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        if (ClientProxy.RUN.isPressed()) {
            last = !last;
            Movement.network.sendToServer(new SprintPacket(last));
            System.out.println("Toggle Sprint " + player.getName() + ": " + last);
        }
    }
}