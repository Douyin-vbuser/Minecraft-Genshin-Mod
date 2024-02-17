package com.vbuser.genshin.entity.element.wuli;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class WuLi extends Element {
    public WuLi(World worldIn) {
        super(worldIn);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.WU_LI;
    }
}
