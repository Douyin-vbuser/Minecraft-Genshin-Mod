package com.vbuser.genshin.entity.element.yan;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Yan extends Element {
    public Yan(World worldIn) {
        super(worldIn);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.YAN;
    }
}
