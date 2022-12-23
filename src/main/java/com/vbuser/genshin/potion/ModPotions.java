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

@Mod.EventBusSubscriber(modid = Reference.Mod_ID)
public class ModPotions {
    public static final List<Potion> INSTANCES = new ArrayList<Potion>();
    public static final List<PotionType> TYPE_INSTANCES = new ArrayList<>();

    public static final PotionTest TEST = new PotionTest(false,0xcccc00,"test",0);

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
