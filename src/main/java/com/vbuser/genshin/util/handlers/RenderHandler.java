package com.vbuser.genshin.util.handlers;

import com.vbuser.genshin.entity.organism.JingDie;
import com.vbuser.genshin.entity.organism.render.JingDieRender;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler {
    public static void registerEntityRenders(){
        RenderingRegistry.registerEntityRenderingHandler(JingDie.class, JingDieRender::new);
    }
}
