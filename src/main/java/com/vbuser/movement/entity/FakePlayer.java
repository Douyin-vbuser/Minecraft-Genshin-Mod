package com.vbuser.movement.entity;

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
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;
import java.util.UUID;

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

    //Correction of yaw and position:
    @Override
    public void onLivingUpdate() {
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
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
