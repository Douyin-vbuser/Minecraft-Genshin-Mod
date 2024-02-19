package com.vbuser.movement.event;

import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class PlayerListener {

    //Fake player event:

    @SubscribeEvent
    public void playerEnterWorld(PlayerEvent.PlayerLoggedInEvent event){
        if(Storage_s.normal==null)Storage_s.normal = new HashMap<>();
        Storage_s.normal.put(event.player, true);
        if(Storage_s.renderer==null)Storage_s.renderer=new HashMap<>();
        FakePlayer fakePlayer = new FakePlayer(event.player.getEntityWorld(),event.player.getUniqueID());
        event.player.world.spawnEntity(fakePlayer);
        Storage_s.renderer.put(event.player,fakePlayer);
    }

    @SubscribeEvent
    public void playerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event){
        Storage_s.normal.remove(event.player);
        event.player.world.removeEntity(Storage_s.renderer.get(event.player));
        Storage_s.renderer.remove(event.player);
    }

    @SubscribeEvent
    public void invisible(TickEvent.PlayerTickEvent event){
        event.player.setInvisible(true);
    }

    //Swimming logic:

    int y;
    final double wv = 0.11785904894762855,sv = 0.15321675646360336;

    @SubscribeEvent
    public void inWater(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = player.world;
        BlockPos pos = new BlockPos(player);

        if (player.isInWater()) {
            int waterDepth = getWaterDepth(world, pos);
            if (waterDepth == 1) {
                y = pos.getY();
                moveInWater(player,false);
            } else if (waterDepth >= 2) {
                if (!world.isAirBlock(pos.down())) {
                    player.setPosition(player.posX, y+0.15, player.posZ);
                }
                moveInWater(player,true);
            }
        }
    }

    private int getWaterDepth(World world, BlockPos pos) {
        int depth = 0;
        BlockPos pos1 = pos.up();
        int maxHeight = pos.getY();
        while (world.getBlockState(pos).getMaterial() == Material.WATER) {
            pos = pos.down();
            depth++;
        }
        while(world.getBlockState(pos1).getMaterial() == Material.WATER){
            maxHeight = pos1.getY();
            pos1 = pos1.up();
            depth++;
        }
        y = maxHeight;
        return depth;
    }

    private void moveInWater(EntityPlayer player,boolean deep){
        if(player.moveStrafing!=0 || player.moveForward!=0) {
            double movementInput = Math.atan2(player.moveForward, player.moveStrafing) +(player.rotationYawHead /180 * Math.PI);
            double v = (player.isSprinting() ? sv : wv) * (deep ? 0.8 : 1);
            player.motionX = Math.cos(movementInput) * v;
            player.motionZ = Math.sin(movementInput) * v;
        }
    }

    //Remember to remove the method below in release version:

    @SubscribeEvent
    public void tickCheck(TickEvent.ClientTickEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player!=null) {
            String message = "v=" + Math.sqrt(Math.pow(player.motionX, 2) + Math.pow(player.motionZ, 2)) + "m/s";
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage(message, false);
        }
    }
}
