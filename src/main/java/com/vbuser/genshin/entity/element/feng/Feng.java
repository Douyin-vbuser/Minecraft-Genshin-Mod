package com.vbuser.genshin.entity.element.feng;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Feng extends Element {
    public Feng(World worldIn){super(worldIn);}

    @Override
    public DamageSource source() {
        return ModDamageSource.FENG;
    }
}
