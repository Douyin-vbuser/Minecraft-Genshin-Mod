package com.vbuser.genshin.entity.element.cao;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Cao extends Element {
    public Cao(World worldIn) {
        super(worldIn);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.CAO;
    }
}
