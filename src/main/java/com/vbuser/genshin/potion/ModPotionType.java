package com.vbuser.genshin.potion;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

import javax.annotation.Nullable;

public class ModPotionType extends PotionType {
    public ModPotionType(@Nullable String name, PotionEffect... effects) {
        super(name,effects);
        setRegistryName("genshin", name);
        ModPotions.TYPE_INSTANCES.add(this);
    }
}
