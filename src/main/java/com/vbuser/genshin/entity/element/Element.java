package com.vbuser.genshin.entity.element;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Element extends EntityLiving {

    public Element(World worldIn) {
        super(worldIn);
    }

    public abstract int setLastingTick();

    public abstract int damage();

    public abstract DamageSource source();

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted >= setLastingTick()) {
            setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        this.hurtResistantTime = 0;
        this.hurtTime = 0;
        return super.attackEntityFrom(this.source(), damage());
    }
}
