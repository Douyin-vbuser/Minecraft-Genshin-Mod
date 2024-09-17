package com.vbuser.movement.event;

import com.vbuser.genshin.proxy.ClientProxy;
import com.vbuser.movement.Storage_s;
import com.vbuser.movement.entity.FakePlayer;
import com.vbuser.movement.util.IntArray;
import com.vbuser.movement.util.PrecisePos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class PlayerMovement {

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
            IntArray intArray = detectFacing(player,!climbMap.get(player.getUniqueID()));
            BlockPos pos = new BlockPos(player.posX+intArray.getX(), player.posY, player.posZ+ intArray.getZ());
            if(climbOK(player,pos)){
                jumpCountdown.put(player.getUniqueID(),20);
                jumpOver(player,intArray,0.15f);
            }else{
                player.setPosition(player.posX, y+0.15, player.posZ);
            }
        }else{
            player.setPosition(player.posX, y+0.15, player.posZ);
        }
    }

    //Climbing logic:

    public static Map<UUID,Boolean> climbMap = new HashMap<>();
    public static Map<UUID, PrecisePos> posMap = new HashMap<>();
    public static Map<UUID,IntArray> stateMap = new HashMap<>();
    public static Map<UUID,Integer> jumpCountdown = new HashMap<>();

    private boolean climbOK(EntityPlayer player,BlockPos pos){
        Block block = player.world.getBlockState(pos).getBlock();
        return block.getDefaultState().isFullBlock() && block.getDefaultState().getCollisionBoundingBox(player.world, pos) == FULL_BLOCK_AABB && block != Blocks.BARRIER;
    }

    private boolean inJump(EntityPlayer player){
        return player.world.getBlockState(new BlockPos(player.posX,player.posY-0.2,player.posZ)).getBlock().equals(Blocks.AIR);
    }

    public static IntArray detectFacing(EntityPlayer player,boolean includeMotion){
        double yaw = includeMotion? getYaw(player) : getClimbYaw(player);
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

    public static double getClimbYaw(EntityPlayer player){
        IntArray intArray = stateMap.get(player.getUniqueID());
        double result;
        if(intArray.getZ()==0){
            result = intArray.getX()<=0?90:-90;
        }else{
            result = intArray.getZ()>=0?0:180;
        }
        return result;
    }

    public static double getYaw(EntityPlayer player){
        Vec2f input = new Vec2f(player.moveForward, player.moveStrafing);
        double delta = Math.atan2(input.y, input.x) * 180 / Math.PI;
        return (player.rotationYawHead - delta)%360;
    }

    public static boolean isMoving(EntityPlayer player){
        return player.moveStrafing != 0 || player.moveForward != 0;
    }

    @SubscribeEvent
    public void climbMainMethod(TickEvent.PlayerTickEvent event){
        EntityPlayer player = event.player;
        if(!climbMap.containsKey(player.getUniqueID())){
            climbMap.put(player.getUniqueID(),false);
        }
        if(!stateMap.containsKey(player.getUniqueID())){
            stateMap.put(player.getUniqueID(),new IntArray(114514));
        }
        player.setNoGravity(climbMap.get(player.getUniqueID())&&!isMoving(player));
        if(player.moveForward != 0 || player.moveStrafing != 0){
            IntArray intArray = detectFacing(player,!climbMap.get(player.getUniqueID()));
            BlockPos pos = new BlockPos(player.posX+intArray.getX(), player.posY, player.posZ+ intArray.getZ());
            performClimb(player,pos,intArray);
        }
        if(player.moveForward==0 && player.moveStrafing==0){
            if(climbMap.get(player.getUniqueID())) {
                if(posMap.containsKey(player.getUniqueID()))player.setPosition(posMap.get(player.getUniqueID()).getX(), posMap.get(player.getUniqueID()).getY(), posMap.get(player.getUniqueID()).getZ());
            }
        } else{
            posMap.put(player.getUniqueID(),new PrecisePos(player));
        }
    }

    public void performClimb(EntityPlayer player,BlockPos pos,IntArray intArray){
        if(!climbMap.get(player.getUniqueID())){
            if((climbOK(player, pos)) && climbOK(player, pos.up())){
                if(climbOK(player,pos.up(2))&&inJump(player)){
                    climbMap.put(player.getUniqueID(),true);
                    stateMap.put(player.getUniqueID(),intArray);
                }else if(inJump(player)){
                    jumpCountdown.put(player.getUniqueID(),20);
                    jumpOver(player,intArray,0.25f);
                }
            }
        }else{
            if(climbOK(player,pos)&&climbOK(player,pos.up())){
                if(climbOK(player,pos.up(2))){
                    if(!inJump(player)){
                        climbMap.put(player.getUniqueID(),false);
                        stateMap.put(player.getUniqueID(),new IntArray(114514));
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
                    jumpCountdown.put(player.getUniqueID(),20);
                    jumpOver(player,intArray,0.25f);
                }
            }else{
                climbMap.put(player.getUniqueID(),false);
                stateMap.put(player.getUniqueID(),new IntArray(114514));
            }
        }
    }

    public void jumpOver(EntityPlayer player,IntArray intArray,float param){
        if(jumpCountdown.get(player.getUniqueID())>0) {
            player.motionY = wv * param *jumpCountdown.get(player.getUniqueID());
            player.motionZ = intArray.getZ() * 0.0030162615090425808;
            player.motionX = intArray.getX() * 0.0030162615090425808;

            jumpCountdown.put(player.getUniqueID(),jumpCountdown.get(player.getUniqueID())-1);
        }
    }

    //Glide logic:
    public static boolean equipped = true;
    private static boolean using = false;
    static float y_speed = 1.3f/20;
    static float side_speed = 0.86f/20;

    public static boolean isPlayerFalling(EntityPlayer player){
        return !player.onGround && player.motionY < 0 && !player.isInWater() && player.getEntityWorld().isAirBlock(player.getPosition().down(2));
    }

    @SubscribeEvent
    public void glide(TickEvent.PlayerTickEvent event){
        EntityPlayer player =event.player;
        if (equipped && using) {
            if (isPlayerFalling(player)) {
                player.motionY = -y_speed;
                player.moveRelative(0, side_speed, 0, 0);
            } else {
                using = false;
            }
        }
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && ClientProxy.GLIDER.isPressed()){
            if(equipped && isPlayerFalling(player)){
                using = !using;
                player.fallDistance=0;
            }
        }
    }
}