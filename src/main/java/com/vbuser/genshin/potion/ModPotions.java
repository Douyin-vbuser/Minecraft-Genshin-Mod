package com.vbuser.genshin.potion;

import com.vbuser.genshin.init.InitPotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
@Mod.EventBusSubscriber(modid = "genshin")
public class ModPotions {

    public static final List<Potion> INSTANCES = new ArrayList<Potion>();
    public static final List<PotionType> TYPE_INSTANCES = new ArrayList<>();

    public static final PotionTest TEST = new PotionTest(false,0xcccc00,"test",0);
    public static final PotionFeng FENG = new PotionFeng(false,0x1fff9e,"feng",1);
    public static final PotionShui SHUI = new PotionShui(false,0x0095e5,"shui",2);
    public static final PotionHuo HUO = new PotionHuo(false,0xe50000,"huo",3);
    public static final PotionBing BING = new PotionBing(false,0x79f4ff,"bing",4);
    public static final PotionYan YAN = new PotionYan(false,0xd6a63d,"yan",5);
    public static final PotionCao CAO = new PotionCao(false,0x06ff24,"cao",6);
    public static final PotionLei LEI = new PotionLei(false,0xbf84e6,"lei",7);

    public static final PotionQ Q = new PotionQ(false,0xffffff,"q",8);

    @Nullable
    private static Potion getRegisteredMobEffect(String id)
    {
        Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(id));

        if (potion == null)
        {
            throw new IllegalStateException("Invalid MobEffect requested: " + id);
        }
        else
        {
            return potion;
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().registerAll(INSTANCES.toArray(new Potion[0]));
    }


    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> evt) {
        InitPotionTypes.init();
        evt.getRegistry().registerAll(TYPE_INSTANCES.toArray(new PotionType[0]));
    }

}
