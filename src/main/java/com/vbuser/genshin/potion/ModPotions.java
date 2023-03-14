package com.vbuser.genshin.potion;

import com.vbuser.genshin.init.InitPotionTypes;
import com.vbuser.genshin.util.Reference;
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
@Mod.EventBusSubscriber(modid = Reference.Mod_ID)
public class ModPotions {

    //我们计划通过Minecraft的Buff系统实现原神中的元素(Element)系统

    public static final List<Potion> INSTANCES = new ArrayList<Potion>();
    public static final List<PotionType> TYPE_INSTANCES = new ArrayList<>();

    public static final PotionTest TEST = new PotionTest(false,0xcccc00,"test",0);
    public static final PotionFeng FENG = new PotionFeng(false,0x1fff9e,"feng",1);      //风元素附着 Anemo Attachment
    public static final PotionShui SHUI = new PotionShui(false,0x0095e5,"shui",2);      //水元素附着 Hydro Attachment
    public static final PotionHuo HUO = new PotionHuo(false,0xe50000,"huo",3);      //火元素附着 Pyro Attachment
    public static final PotionBing BING = new PotionBing(false,0x79f4ff,"bing",4);      //冰元素附着 Cryo Attachment
    public static final PotionYan YAN = new PotionYan(false,0xd6a63d,"yan",5);      //岩元素附着 Geo Attachment
    public static final PotionCao CAO = new PotionCao(false,0x06ff24,"cao",6);      //草元素附着 Dendro Attachment
    public static final PotionLei LEI = new PotionLei(false,0xbf84e6,"lei",7);      //雷元素附着 Electro Attachment

    public static final PotionQ Q = new PotionQ(false,0xffffff,"q",8);      //元素战技释放时的无敌效果

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
