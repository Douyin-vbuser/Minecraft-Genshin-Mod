package com.vbuser.genshin.entity.organism;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class XianLing extends EntityFlying implements IAnimatable, IAnimationTickable {

    public int dx, dy, dz;

    public XianLing(World worldIn) {
        super(worldIn);
        setSize(0.6f, 0.6f);
    }

    public XianLing(World worldIn, int dx, int dy, int dz) {
        super(worldIn);
        setSize(0.6f, 0.6f);
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (dx * dy * dz != 0) {
            BlockPos targetPos = new BlockPos(dx, dy, dz);

            EntityAIMoveToLocation moveToTarget = new EntityAIMoveToLocation(this, targetPos, 0.1D, 5.0D);

            this.tasks.addTask(0, moveToTarget);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("loop", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {

    }

    @Override
    public int tickTimer() {
        return 0;
    }


    public static class EntityAIMoveToLocation extends EntityAIBase {

        XianLing entity;
        BlockPos targetPos;
        double speed, tolerance;

        public EntityAIMoveToLocation(XianLing xianLing, BlockPos targetPos, double speed, double tolerance) {
            this.entity = xianLing;
            this.targetPos = targetPos;
            this.tolerance = tolerance;
            this.speed = speed;
        }

        public int nonAirBlockCount(Entity entity, int radius) {
            World world = entity.world;
            BlockPos entityPos = entity.getPosition();
            int nonAirBlockCount = 0;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos blockPos = entityPos.add(x, y, z);
                        IBlockState blockState = world.getBlockState(blockPos);
                        if (!blockState.getBlock().isAir(blockState, world, blockPos)) {
                            nonAirBlockCount++;
                        }
                    }
                }
            }

            return nonAirBlockCount;
        }

        @Override
        public boolean shouldExecute() {
            int range = 5;
            int playerCount = entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((new BlockPos(entity)).add(-range, -range, -range), (new BlockPos(entity).add(range, range, range)))).size();
            return playerCount > 0 || !targetPos.equals(new BlockPos(0, 0, 0));
        }

        @Override
        public boolean shouldContinueExecuting() {
            int range = 10;
            int nonAirBlockCount = nonAirBlockCount(entity, range);
            int playerCount = entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((new BlockPos(entity)).add(-range, -range, -range), (new BlockPos(entity).add(range, range, range)))).size();
            return playerCount > 0 || nonAirBlockCount == 0;
        }

        @Override
        public void startExecuting() {
            Vec3d direction = new Vec3d(this.targetPos.getX() - this.entity.posX, this.targetPos.getY() - this.entity.posY, this.targetPos.getZ() - this.entity.posZ).normalize();
            this.entity.motionX = direction.x * speed;
            this.entity.motionY = direction.y * speed;
            this.entity.motionZ = direction.z * speed;
        }

        @Override
        public void updateTask() {
            if (!targetPos.equals(new BlockPos(0, 0, 0))) {
                int range = 10;
                int nonAirBlockCount = nonAirBlockCount(entity, range);
                int playerCount = entity.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((new BlockPos(entity)).add(-range, -range, -range), (new BlockPos(entity).add(range, range, range)))).size();

                if (playerCount == 0 && nonAirBlockCount != 0) {
                    this.entity.motionX = 0;
                    this.entity.motionY = 0;
                    this.entity.motionZ = 0;
                } else if (!this.entity.world.isAirBlock(new BlockPos(this.entity.posX, this.entity.posY, this.entity.posZ))) {
                    this.entity.motionX = 0;
                    this.entity.motionY = 0;
                    this.entity.motionZ = 0;
                    this.entity.setDead();
                } else {
                    Vec3d direction = new Vec3d(this.targetPos.getX() - this.entity.posX, this.targetPos.getY() + 0.6 - this.entity.posY, this.targetPos.getZ() - this.entity.posZ).normalize();
                    this.entity.motionX = direction.x * speed;
                    this.entity.motionY = direction.y * speed;
                    this.entity.motionZ = direction.z * speed;
                    double yaw = -Math.atan2(entity.motionX, entity.motionZ) * 180.0 / Math.PI;
                    double pitch = -Math.asin(entity.motionY / speed) * 180.0 / Math.PI;
                    this.entity.rotationYaw = (float) yaw;
                    this.entity.rotationPitch = (float) pitch;
                }
            }
        }
    }

}
