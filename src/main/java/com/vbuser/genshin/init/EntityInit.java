package com.vbuser.genshin.init;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.entity.organism.JingDie;
import com.vbuser.genshin.entity.organism.XianLing;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {

    public static void registerEntities(){
        registerEntity("jing_die", JingDie.class,1, 20, 9496552, 3532776);
        registerEntity("xian_ling", XianLing.class,3,10,3532776,9496552);
    }

    public static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, int color1, int color2){
        EntityRegistry.registerModEntity(new ResourceLocation("genshin:" + name), entity, name, id, Main.instance, range, 1, true, color1, color2);
    }
}
