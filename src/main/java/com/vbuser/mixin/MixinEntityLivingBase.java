package com.vbuser.mixin;

import com.vbuser.genshin.math.WaterHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Unique private static final double SPRINT_BLOCKS_PER_TICK = 5.612D / 20.0D;
    @Unique private static final double DEEP_WATER_SPEED = SPRINT_BLOCKS_PER_TICK * 0.5D;

    @Unique private boolean genshin$wasInAnyWater = false;
    @Unique private boolean genshin$hasSurfaceY = false;
    @Unique private double genshin$surfaceY = 0.0D;

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void genshin$deepWaterSurf(float strafe, float vertical, float forward, CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer)) return;

        Entity e = (Entity) (Object) this;
        World world = e.world;
        BlockPos feet = new BlockPos(e.posX, e.posY, e.posZ);

        boolean inAnyWaterNow = WaterHelper.isWaterRaw(feet, world);
        if (!genshin$wasInAnyWater && inAnyWaterNow) {
            genshin$surfaceY = WaterHelper.getTopWaterBottomY(feet, world);
            genshin$hasSurfaceY = true;
        }
        if (genshin$wasInAnyWater && !inAnyWaterNow) {
            genshin$hasSurfaceY = false;
        }
        genshin$wasInAnyWater = inAnyWaterNow;

        if (!genshin$hasSurfaceY || !WaterHelper.isInDeepWaterRaw((EntityPlayer) self)) return;

        double s = strafe;
        double f = forward;
        double len = Math.sqrt(s * s + f * f);

        double dx = 0.0D, dz = 0.0D;
        if (len > 1.0E-4) {
            s /= len;
            f /= len;
            double rad = Math.toRadians(self.rotationYaw);
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);

            dx = s * cos - f * sin;
            dz = f * cos + s * sin;

            dx *= DEEP_WATER_SPEED;
            dz *= DEEP_WATER_SPEED;
        }

        e.motionX = dx;
        e.motionY = 0.0D;
        e.motionZ = dz;

        e.move(MoverType.SELF, e.motionX, 0.0D, e.motionZ);
        double lockY = genshin$surfaceY + 0.001D;
        e.setPosition(e.posX, lockY, e.posZ);
        e.fallDistance = 0.0F;


        ci.cancel();
    }
}