package com.vbuser.genshin.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class PotionQ extends PotionBase{
    public PotionQ(boolean isBadEffectIn, int liquidColorIn, String name, int icon){
        super(isBadEffectIn,liquidColorIn,name,icon);
    }
    
    @SubscribeEvent
    public static void onCreatureHurt(LivingHurtEvent event){
        World world = event.getEntity().getEntityWorld();
        EntityLivingBase hurt = event.getEntityLiving();
        Collection<PotionEffect> activePotionEffect = hurt.getActivePotionEffects();

        for(int i=0;i<activePotionEffect.size();i++){
            PotionEffect buff = (PotionEffect)activePotionEffect.toArray()[i];
            if(buff.getPotion() instanceof PotionQ  && !world.isRemote){
                event.setAmount(0);
            }
        }
    }
}
