package com.vbuser.movement.event;

import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
                moveInWater(player,true);
            }
            player.setPosition(player.posX, y+0.15, player.posZ);
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

    public static Map<UUID,Boolean> climbMap = new HashMap<>();
    public static Map<UUID,Double> yMap = new HashMap<>();

    private boolean climbOK(EntityPlayer player,BlockPos pos){
        Block block = player.world.getBlockState(pos).getBlock();
        return block.getDefaultState().isFullBlock() && block.getDefaultState().getCollisionBoundingBox(player.world, pos) == FULL_BLOCK_AABB && block != Blocks.BARRIER;
    }

    private boolean inJump(EntityPlayer player){
        return player.world.getBlockState(new BlockPos(player.posX,player.posY-0.2,player.posZ)).getBlock().equals(Blocks.AIR);
    }

    public static IntArray detectFacing(EntityPlayer player,boolean includeMotion){
        double yaw = includeMotion? getYaw(player) : player.rotationYawHead;
        if(yaw >-45&& yaw <=45){
            return new IntArray(2);
        } else if(yaw >45&& yaw <=135) {
            return new IntArray(3);
        } else if(yaw >-135&& yaw <=-45){
            return new IntArray(1);
        } else{
            return new IntArray(4);
        }
    }

    public static double getYaw(EntityPlayer player){
        Vec2f input = new Vec2f(player.moveForward, player.moveStrafing);
        double delta = Math.atan2(input.y, input.x) * 180 / Math.PI;
        return (player.rotationYawHead - delta)%360;
    }

    @SubscribeEvent
    public void climbMainMethod(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(!climbMap.containsKey(player.getUniqueID())){
            climbMap.put(player.getUniqueID(),false);
        }
        if(player.moveForward != 0 || player.moveStrafing != 0){
            IntArray intArray = detectFacing(player,!climbMap.get(player.getUniqueID()));
            BlockPos pos = new BlockPos(player.posX+intArray.getX(), player.posY, player.posZ+ intArray.getZ());
            performClimb(player,pos,intArray);
        }
        if(player.moveForward==0 && player.moveStrafing==0){
            if(climbMap.get(player.getUniqueID())) {
                if(yMap.containsKey(player.getUniqueID()))player.setPosition(player.posX, yMap.get(player.getUniqueID()), player.posZ);
            }
        } else{
            yMap.put(player.getUniqueID(),player.posY);
        }
    }

    public void performClimb(EntityPlayer player,BlockPos pos,IntArray intArray){
        if(!climbMap.get(player.getUniqueID())){
            if((climbOK(player, pos)) && climbOK(player, pos.up())){
                if(climbOK(player,pos.up(2))&&inJump(player)){
                    climbMap.put(player.getUniqueID(),true);
                }else if(inJump(player)){
                    jumpOver(player,intArray);
                }
            }
        }else{
            if(climbOK(player,pos)&&climbOK(player,pos.up())){
                if(climbOK(player,pos.up(2))){
                    if(!inJump(player)){
                        climbMap.put(player.getUniqueID(),false);
                    }else{
                        if(intArray.getX()>0){
                            player.motionY = wv*player.moveForward;
                            player.motionZ = wv*player.moveStrafing*-0.6;
                        }else if(intArray.getX()<0){
                            player.motionY = wv*player.moveForward;
                            player.motionZ = wv*player.moveStrafing*0.6;
                        }else if(intArray.getZ()>0){
                            player.motionY = wv*player.moveForward;
                            player.motionX = wv*player.moveStrafing*0.6;
                        }else{
                            player.motionY = wv*player.moveForward;
                            player.motionX = wv*player.moveStrafing*-0.6;
                        }
                        player.motionY = Math.max(player.motionY,0);
                    }
                }else{
                    jumpOver(player,intArray);
                }
            }else{
                climbMap.put(player.getUniqueID(),false);
            }
        }
    }

    public void jumpOver(EntityPlayer player,IntArray intArray){

    }
}
