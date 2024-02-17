package com.vbuser.genshin.entity.element.bing;

import com.vbuser.genshin.entity.element.Element;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class Bing extends Element {

    public Bing(World world){
        super(world);
    }

    @Override
    public DamageSource source() {
        return ModDamageSource.BING;
    }
}
