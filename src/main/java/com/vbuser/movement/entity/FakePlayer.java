package com.vbuser.movement.entity;

import com.vbuser.movement.Storage_s;
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
    private boolean pre_moving;
    private double x,y,z;

    public FakePlayer(World worldIn){
        super(worldIn);
        if(Minecraft.getMinecraft().player==null) {
            this.setDead();
            return;
        }
        player_uuid = Minecraft.getMinecraft().player.getUniqueID();
        world = worldIn;
        Storage_s.renderer.put(worldIn.getPlayerEntityByUUID(player_uuid),this);
        setSize(0.6f,1.8f);
        BlockPos pos  = new BlockPos(Objects.requireNonNull(worldIn.getPlayerEntityByUUID(player_uuid)));
        setPosition(pos.getX(),pos.getY(),pos.getZ());
    }

    public FakePlayer(World worldIn, UUID uuid){
        super(worldIn);
        player_uuid = uuid;
        world = worldIn;
        Storage_s.renderer.put(worldIn.getPlayerEntityByUUID(uuid),this);
        setSize(0.6f,1.8f);
        BlockPos pos  = new BlockPos(Objects.requireNonNull(worldIn.getPlayerEntityByUUID(player_uuid)));
        setPosition(pos.getX(),pos.getY(),pos.getZ());
    }

    //Correction of yaw and position:

    @Override
    public void onLivingUpdate(){
        EntityPlayer player = world.getPlayerEntityByUUID(player_uuid);
        if(player != null) {
            if (Storage_s.normal.get(player)) {
                Vec2f input = new Vec2f(player.moveForward, player.moveStrafing);
                if (input.equals(Vec2f.ZERO)) {
                    if (!pre_moving) {
                        yaw_temp = player.rotationYawHead;
                    }
                    rotationYaw = yaw_temp;
                    pre_moving = false;
                } else {
                    double delta = Math.atan2(input.y, input.x) * 180 / Math.PI;
                    rotationYaw = (float) (player.rotationYawHead - delta)%360;
                }
                x =player.posX;
                y =player.posY;
                z =player.posZ;
            }
            this.setPosition(x,y,z);
        }
    }

    //GeckoLib methods:

    @Override
    public void tick() {}

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
