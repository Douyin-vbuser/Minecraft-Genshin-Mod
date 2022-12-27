package com.vbuser.genshin.potion;

import com.vbuser.genshin.util.Reference;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

import javax.annotation.Nullable;

public class ModPotionType extends PotionType {
    public ModPotionType(@Nullable String p_i46740_1_, PotionEffect... p_i46740_2_) {
        super(p_i46740_1_, p_i46740_2_);
        setRegistryName(Reference.Mod_ID, p_i46740_1_);
        ModPotions.TYPE_INSTANCES.add(this);
    }
}
