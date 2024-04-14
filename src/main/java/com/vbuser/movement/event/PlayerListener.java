package com.vbuser.movement.event;

import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

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
        climbMap.put(event.player.getUniqueID().toString(),0);
    }

    @SubscribeEvent
    public void playerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event){
        Storage_s.normal.remove(event.player);
        event.player.world.removeEntity(Storage_s.renderer.get(event.player));
        Storage_s.renderer.remove(event.player);
        climbMap.remove(event.player.getUniqueID().toString());
    }

    @SubscribeEvent
    public void invisible(TickEvent.PlayerTickEvent event){
        event.player.setInvisible(true);
    }

    //Swimming logic:

    int y;
    final double wv = 0.11785904894762855,sv = 0.15321675646360336;
    final double jv = sv;
    final double ja = 0.1;

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

    //Climbing logic:

    private static final Map<String,Integer> climbMap = new HashMap<>();
    private static final Map<String,Integer> timerMap = new HashMap<>();
    private static final Map<String,Integer> facingMap = new HashMap<>();

    @SubscribeEvent
    private void climb(TickEvent.PlayerTickEvent event){
        EntityPlayer player =  event.player;
        if(player.moveForward!=0 || player.moveStrafing!=0){
            int state = climbMap.get(player.getUniqueID().toString());
            if(state==0){
                detect(event);
            }
            if(state==1){
                if(timerMap.get(player.getUniqueID().toString())>0){
                    jump(event);
                    timerMap.put(player.getUniqueID().toString(),timerMap.get(player.getUniqueID().toString())-1);
                }
                else{
                    climbMap.put(player.getUniqueID().toString(),2);
                }
            }
            if(state==2){
                listen(event);
            }
            if(state==3){
                if(timerMap.get(player.getUniqueID().toString())>0){
                    endClimb(event);
                    timerMap.put(player.getUniqueID().toString(),timerMap.get(player.getUniqueID().toString())-1);
                }
                else{
                    climbMap.put(player.getUniqueID().toString(),0);
                }
            }
        }
    }

    public void detect(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        double moveYaw = ((player.rotationYawHead+Math.atan2(player.moveStrafing,player.moveForward)%360)*Math.PI/360);
        BlockPos pos = new BlockPos(player.posX,player.posY,player.posZ);
        BlockPos pos1 = new BlockPos(pos.getX(), pos.getY(), pos.getZ()+(Math.cos(moveYaw)>0?0.4:-0.4));
        BlockPos pos2 = new BlockPos(pos.getX()+Math.sin(moveYaw)>0?0.4:-0.4,pos.getY(),pos.getZ());
        if(Math.abs(Math.tan(moveYaw))>1){
            if(judgeBlock(player,pos1) && judgeBlock(player,pos1.up())){
                climbMap.put(player.getUniqueID().toString(),1);
                facingMap.put(player.getUniqueID().toString(),(Math.sin(moveYaw)>0) ?1:3);
                timerMap.put(player.getUniqueID().toString(),15);
                player.rotationYawHead = facingMap.get(player.getUniqueID().toString())*90;
                player.motionX = Math.cos(player.rotationYawHead /180 *Math.PI)*wv;
                player.motionZ = Math.sin(player.rotationYawHead /180 *Math.PI)*wv;
            }
        }
        else{
            if(judgeBlock(player,pos2) && judgeBlock(player,pos2.up())){
                climbMap.put(player.getUniqueID().toString(),1);
                facingMap.put(player.getUniqueID().toString(),(Math.cos(moveYaw)>0 ?0:2));
                timerMap.put(player.getUniqueID().toString(),15);
                player.rotationYawHead = facingMap.get(player.getUniqueID().toString())*90;
                player.motionX = Math.cos(player.rotationYawHead /180 *Math.PI)*wv;
                player.motionZ = Math.sin(player.rotationYawHead /180 *Math.PI)*wv;
            }
        }
    }

    public boolean judgeBlock(EntityPlayer player,BlockPos pos){
        World world = player.world;
        Block block = world.getBlockState(pos).getBlock();
        return block.getDefaultState().isFullBlock() && block.getDefaultState().getCollisionBoundingBox(world, pos) == FULL_BLOCK_AABB && block != Blocks.BARRIER;
    }

    public void jump(TickEvent.PlayerTickEvent event){
        EntityPlayer player =  event.player;
        player.motionY = jv - timerMap.get(player.getUniqueID().toString())*ja;
        Storage_s.renderer.get(player).rotationYaw=facingMap.get(player.getUniqueID().toString());
        player.motionX=0;
        player.motionZ=0;
    }

    public void endClimb(TickEvent.PlayerTickEvent event){
        EntityPlayer player =  event.player;
        if(timerMap.get(player.getUniqueID().toString())>0) {
            player.motionY = timerMap.get(player.getUniqueID().toString()) * 0.1;
            int a = facingMap.get(player.getUniqueID().toString());
            if(a==0)player.motionX = 0.1;
            if(a==1)player.motionZ = 0.1;
            if(a==2)player.motionX = -0.1;
            if(a==3)player.motionZ = -0.1;
        }
    }

    public void listen(TickEvent.PlayerTickEvent event){
        EntityPlayer player =  event.player;
        if(facingMap.get(player.getUniqueID().toString())==0){
            player.motionY = 0.8*wv*player.moveForward/0.98;
            player.motionZ = 0.8*wv*player.moveStrafing/0.98;
            player.motionX = 0;
            BlockPos pos = new BlockPos(player);
            if(!judgeBlock(player,(new BlockPos(pos.getX()+1,pos.getY()+1,pos.getZ())))){
                climbMap.put(player.getUniqueID().toString(),3);
                timerMap.put(player.getUniqueID().toString(),20);
            }
        }
        if(facingMap.get(player.getUniqueID().toString())==2){
            player.motionY = 0.8*wv*player.moveForward/0.98;
            player.motionZ = -0.8*wv*player.moveStrafing/0.98;
            player.motionX = 0;
            BlockPos pos = new BlockPos(player);
            if(!judgeBlock(player,(new BlockPos(pos.getX()-1,pos.getY()+1,pos.getZ())))){
                climbMap.put(player.getUniqueID().toString(),3);
                timerMap.put(player.getUniqueID().toString(),20);
            }
        }
        if(facingMap.get(player.getUniqueID().toString())==1){
            player.motionY = 0.8*wv*player.moveForward/0.98;
            player.motionX = 0.8*wv*player.moveStrafing/0.98;
            player.motionZ = 0;
            BlockPos pos = new BlockPos(player);
            if(!judgeBlock(player,(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()+1)))){
                climbMap.put(player.getUniqueID().toString(),3);
                timerMap.put(player.getUniqueID().toString(),20);
            }
        }
        if(facingMap.get(player.getUniqueID().toString())==3){
            player.motionY = 0.8*wv*player.moveForward/0.98;
            player.motionX = -0.8*wv*player.moveStrafing/0.98;
            player.motionZ = 0;
            BlockPos pos = new BlockPos(player);
            if(!judgeBlock(player,(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()-1)))){
                climbMap.put(player.getUniqueID().toString(),3);
                timerMap.put(player.getUniqueID().toString(),20);
            }
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
