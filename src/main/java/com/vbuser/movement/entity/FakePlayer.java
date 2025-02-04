package com.vbuser.movement.entity;

import com.vbuser.genshin.event.AttackState;
import com.vbuser.movement.Storage_s;
import com.vbuser.movement.event.PlayerMovement;
import com.vbuser.movement.util.IntArray;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("all")
public class FakePlayer extends EntityLiving implements IAnimatable, IAnimationTickable {

    private UUID player_uuid;
    private World world;
    private float yaw_temp;
    private double x, y, z;

    @SuppressWarnings("all")
    public FakePlayer(World worldIn) {
        super(worldIn);
        if (Minecraft.getMinecraft().player == null) {
            this.setDead();
            return;
        }
        player_uuid = Minecraft.getMinecraft().player.getUniqueID();
        world = worldIn;
        Storage_s.renderer.put(worldIn.getPlayerEntityByUUID(player_uuid), this);
        setSize(0.6f, 1.8f);
        BlockPos pos = new BlockPos(Objects.requireNonNull(worldIn.getPlayerEntityByUUID(player_uuid)));
        setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public FakePlayer(World worldIn, UUID uuid) {
        super(worldIn);
        player_uuid = uuid;
        world = worldIn;
        Storage_s.renderer.put(worldIn.getPlayerEntityByUUID(uuid), this);
        setSize(0.6f, 1.8f);
        BlockPos pos = new BlockPos(Objects.requireNonNull(worldIn.getPlayerEntityByUUID(player_uuid)));
        setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void onLivingUpdate() {
        setRenderer();
        setAni();
        if(!Storage_s.renderer.containsValue(this) && Storage_s.renderer.containsKey(world.getPlayerEntityByUUID(player_uuid))){
            this.setDead();
        }
    }

    //Correction of yaw and position:
    public void setRenderer(){
        EntityPlayer player = world.getPlayerEntityByUUID(player_uuid);
        if (player != null) {
            if (Storage_s.normal.get(player)) {
                Vec2f input = new Vec2f(player.moveForward, player.moveStrafing);
                float targetYaw;

                if (PlayerMovement.climbMap.get(player.getUniqueID())) {
                    IntArray intArray = PlayerMovement.stateMap.get(player.getUniqueID());
                    if (intArray.getZ() == 0) {
                        targetYaw = intArray.getX() <= 0 ? 90 : -90;
                    } else {
                        targetYaw = intArray.getZ() >= 0 ? 0 : 180;
                    }
                } else {
                    if (input.x == 0 && input.y == 0) {
                        targetYaw = yaw_temp;
                    } else {
                        double delta = Math.atan2(input.y, input.x) * 180 / Math.PI;
                        targetYaw = (float) (player.rotationYawHead - delta) % 360;
                        yaw_temp = targetYaw;
                    }
                }

                float yawDiff = (targetYaw - this.rotationYaw) % 360;
                if (yawDiff > 180) yawDiff -= 360;
                if (yawDiff < -180) yawDiff += 360;

                float fact = 0.4f;

                this.rotationYaw += yawDiff * fact;

                this.rotationYawHead = this.rotationYaw;
                this.renderYawOffset = this.rotationYaw;

                this.prevRotationYaw += (this.rotationYaw - this.prevRotationYaw) * fact;
                this.prevRotationYawHead += (this.rotationYawHead - this.prevRotationYawHead) * fact;
                this.prevRenderYawOffset += (this.renderYawOffset - this.prevRenderYawOffset) * fact;

                x = player.posX;
                y = player.posY;
                z = player.posZ;
            }
            this.setPosition(x, y, z);
        }
    }

    AnimationState currentAnimationState = AnimationState.STOPPED;
    public enum AnimationState {
        STOPPED, RUN_START, RUN_LOOP, RUN_END
    }

    public void setAni(){
        EntityPlayer player = world.getPlayerEntityByUUID(player_uuid);
        if (player != null) {
            if(AttackState.getState(player_uuid)==0) {
                if (isRunning(player)) {
                    if (currentAnimationState == AnimationState.STOPPED || currentAnimationState == AnimationState.RUN_END) {
                        currentAnimationState = AnimationState.RUN_START;
                        ddl = System.currentTimeMillis() + 250;
                    } else if (currentAnimationState == AnimationState.RUN_START && isAnimationPlaying()) {
                        currentAnimationState = AnimationState.RUN_LOOP;
                        ddl = System.currentTimeMillis() + 960;
                    }
                } else {
                    if (currentAnimationState == AnimationState.RUN_LOOP) {
                        currentAnimationState = AnimationState.RUN_END;
                        ddl = System.currentTimeMillis() + 170;
                    } else if (currentAnimationState == AnimationState.RUN_END && isAnimationPlaying()) {
                        currentAnimationState = AnimationState.STOPPED;
                    }
                }
            }
        }
    }

    public static boolean isInDeepWater(EntityPlayer player) {
        return player.isInWater() && PlayerMovement.waterDepth(player.world, player.getPosition()) >= 2;
    }

    private boolean isRunning(EntityPlayer player){
        return player.isSprinting() && !PlayerMovement.climbMap.get(player.getUniqueID()) && PlayerMovement.isMoving(player) && !PlayerMovement.isPlayerFalling(player) && !isInDeepWater(player);
    }

    long ddl = System.currentTimeMillis();
    public boolean isAnimationPlaying(){
        return ddl >= System.currentTimeMillis();
    }

    //GeckoLib methods:

    @Override
    public void tick() {
    }

    @Override
    public int tickTimer() {
        return 0;
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<FakePlayer> controller = event.getController();

        switch (currentAnimationState) {
            case RUN_START:
                controller.setAnimation(new AnimationBuilder().addAnimation("run_start", false));
                break;
            case RUN_LOOP:
                controller.setAnimation(new AnimationBuilder().addAnimation("run_loop", true));
                break;
            case RUN_END:
                controller.setAnimation(new AnimationBuilder().addAnimation("run_end", false));
                break;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        AnimationController<FakePlayer> controller = new AnimationController<>(
                this,
                "controller",
                0,
                this::predicate
        );
        animationData.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
