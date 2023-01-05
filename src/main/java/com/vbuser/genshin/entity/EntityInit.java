package com.vbuser.genshin.entity;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {

    public static void registerEntities(){
        registerEntity("jing_die", JingDie.class, 1, 20, 9496552, 3532776);
    }

    public static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, int color1, int color2) {

        EntityRegistry.registerModEntity(new ResourceLocation(Reference.Mod_ID + ":" + name), entity, name, id, Main.instance, range, 1, true, color1, color2);

    }
}
