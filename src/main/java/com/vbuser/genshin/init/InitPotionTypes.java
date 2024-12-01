package com.vbuser.genshin.init;

import com.vbuser.genshin.potion.ModPotionType;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;

public class InitPotionTypes {
    public static final PotionType BLINDNESS;
    public static final PotionType LUCKY;
    public static final PotionType UNLUCKY;
    public static final PotionType HASTE;
    public static final PotionType RESISTANCE;
    public static final PotionType WITHER;
    public static final PotionType LEVITATION;
    public static final PotionType GLOWING;

    public static final ModPotionType TEST = new ModPotionType("test", new PotionEffect(MobEffects.REGENERATION, 450, 2));

    static {
        if (!Bootstrap.isRegistered()) {
            throw new RuntimeException("Accessed Potions before Bootstrap!");
        } else {
            BLINDNESS = createType(MobEffects.BLINDNESS);
            LUCKY = createType(MobEffects.LUCK);
            UNLUCKY = createType(MobEffects.UNLUCK);
            HASTE = createType(MobEffects.HASTE);
            RESISTANCE = createType(MobEffects.RESISTANCE);
            WITHER = createType(MobEffects.WITHER);
            LEVITATION = createType(MobEffects.LEVITATION);
            GLOWING = createType(MobEffects.GLOWING);

        }
    }

    public static void init() {

    }

    static ModPotionType createType(Potion potion) {
        return new ModPotionType(potion.getName(), getEffect(potion));
    }

    public static PotionEffect getEffect(Potion potion) {
        return new PotionEffect(potion, potion.isBadEffect() ? 1800 : 3600);
    }
}
