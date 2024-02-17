package com.vbuser.genshin.entity.element.shui;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Shui extends Element {
    public Shui(World worldIn) {
        super(worldIn);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.SHUI;
    }
}
