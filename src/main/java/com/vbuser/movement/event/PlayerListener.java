package com.vbuser.movement.event;

import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;

public class PlayerListener {
    @SubscribeEvent
    public void playerEnterWorld(PlayerEvent.PlayerLoggedInEvent event){
        if(Storage_s.normal==null)Storage_s.normal = new HashMap<>();
        Storage_s.normal.put(event.player, true);
        if(Storage_s.renderer==null)Storage_s.renderer=new HashMap<>();
        FakePlayer fakePlayer = new FakePlayer(event.player.getEntityWorld(),event.player.getUniqueID());
        event.player.world.spawnEntity(fakePlayer);
        Storage_s.renderer.put(event.player,fakePlayer);
        event.player.setInvisible(true);
    }

    @SubscribeEvent
    public void playerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event){
        Storage_s.normal.remove(event.player);
        Storage_s.renderer.remove(event.player);
        event.player.world.removeEntity(Storage_s.renderer.get(event.player));
    }
}
