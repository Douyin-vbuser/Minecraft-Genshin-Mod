package com.vbuser.movement.event;

import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FakePlayerEvent {
    @SubscribeEvent
    public void playerEvent(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        FakePlayer fakePlayer;
        World world = player.world;

        if (Storage_s.renderer.containsKey(player)) {
            fakePlayer = Storage_s.renderer.get(player);
        } else {
            fakePlayer = new FakePlayer(world, player.getUniqueID());
            world.spawnEntity(fakePlayer);
            Storage_s.renderer.put(player, fakePlayer);
        }

        int distance = (int) player.getDistance(fakePlayer);
        if (distance > 16) {
            fakePlayer.setDead();
            Storage_s.renderer.remove(player);
            world.spawnEntity(new FakePlayer(world, player.getUniqueID()));
            Storage_s.renderer.put(player, fakePlayer);
        }

    }
}
