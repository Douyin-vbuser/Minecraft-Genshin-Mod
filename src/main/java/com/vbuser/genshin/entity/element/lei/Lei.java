package com.vbuser.genshin.entity.element.lei;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Lei extends Element {
    public Lei(World worldIn) {
        super(worldIn);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.LEI;
    }
}
