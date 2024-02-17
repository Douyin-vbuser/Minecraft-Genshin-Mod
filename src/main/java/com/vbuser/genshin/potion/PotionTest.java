package com.vbuser.genshin.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class PotionTest extends PotionBase{
    public PotionTest(boolean isBadEffectIn, int liquidColorIn, String name, int icon){
        super(isBadEffectIn,liquidColorIn,name,icon);
    }

    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent event) {
        World world = event.getEntity().getEntityWorld();
        EntityLivingBase hurtOne = event.getEntityLiving();

        if (event.isCanceled() || !event.getSource().isMagicDamage())
        {
            return;
        }

        //source:https://blog.csdn.net/Jay_fearless/article/details/124049197
        Collection<PotionEffect> activePotionEffects = hurtOne.getActivePotionEffects();
        for (int i = 0; i < activePotionEffects.size(); i++) {
            PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
            if (buff.getPotion() instanceof PotionTest)
            {
                if (!world.isRemote)
                {
                    float reduceRatio = buff.getAmplifier();
                    event.setAmount(Math.max(1 - reduceRatio, 0f) * event.getAmount());
                }
            }
        }
    }
}
