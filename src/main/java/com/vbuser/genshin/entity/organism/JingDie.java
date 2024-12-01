package com.vbuser.genshin.entity.organism;

import com.vbuser.genshin.util.handler.SoundsHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

@SuppressWarnings("all")
public class JingDie extends EntityFlying implements IAnimatable, IAnimationTickable {

    private Entity target;
    private int flyingTicks;

    public JingDie(World worldIn) {
        super(worldIn);
        setSize(0.9F, 0.6F);
        setHealth(1F);
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("zzz", true));
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

    @Override
    public void tick() {

    }

    @Override
    public int tickTimer() {
        return 0;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        JingDie entity = this;
        if (!entity.isDead) {
            if (entity.target == null) {
                List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(1));
                if (!players.isEmpty()) {
                    entity.target = players.get(0);
                }
            } else {
                double dx = entity.target.posX - entity.posX;
                double dy = entity.target.posY - entity.posY;
                double dz = entity.target.posZ - entity.posZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                double speed = 1.2;
                double d = speed / distance;
                double vx = dx * d;
                double vy = dy * d + 1.4;
                double vz = dz * d;
                entity.motionX = -vx / 20;
                entity.motionY = vy / 20;
                entity.motionZ = -vz / 20;
                entity.setAIMoveSpeed((float) speed);
                entity.getNavigator().clearPath();
                double yaw = -Math.atan2(motionX, motionZ) * 180.0 / Math.PI;
                double pitch = -Math.asin(motionY / speed) * 180.0 / Math.PI;
                this.rotationYaw = (float) yaw;
                this.rotationPitch = (float) pitch;
                entity.flyingTicks++;
                if (entity.flyingTicks >= 100 || entity.getDistanceSq(entity.target) <= 1) {
                    entity.setDead();
                }
            }
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            world.playSound(player, player.posX, player.posY, player.posZ, SoundsHandler.PICK, SoundCategory.NEUTRAL, 3f, 1f);
            //dropItem(ModItems.JING_HE, 1);
            setDead();
        }
        return true;
    }
}
