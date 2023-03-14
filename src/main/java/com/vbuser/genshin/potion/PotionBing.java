package com.vbuser.genshin.potion;

import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class PotionBing extends PotionBase{
    public PotionBing(boolean isBadEffectIn, int liquidColorIn, String name, int icon){
        super(isBadEffectIn,liquidColorIn,name,icon);
    }

    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent event){
        World world = event.getEntity().getEntityWorld();
        EntityLivingBase hurt = event.getEntityLiving();

        if (event.getSource()== ModDamageSource.HUO) {
            Collection<PotionEffect> activePotionEffects = hurt.getActivePotionEffects();
            for(int i = 0;i<activePotionEffects.size();i++){
                PotionEffect buff = (PotionEffect)activePotionEffects.toArray()[i];
                if (buff.getPotion() instanceof PotionBing && !world.isRemote){
                    event.setAmount(2*event.getAmount());
                }
            }
        }

    }
}
