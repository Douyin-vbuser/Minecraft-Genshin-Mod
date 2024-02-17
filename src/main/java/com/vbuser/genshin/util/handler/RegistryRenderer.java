package com.vbuser.genshin.util.handler;

import com.vbuser.genshin.entity.organism.JingDie;
import com.vbuser.genshin.entity.organism.XianLing;
import com.vbuser.genshin.entity.organism.render.JingDieRender;
import com.vbuser.genshin.entity.organism.render.XianLingRender;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RegistryRenderer {
    public static void registry(){
        RenderingRegistry.registerEntityRenderingHandler(JingDie.class, JingDieRender::new);
        RenderingRegistry.registerEntityRenderingHandler(XianLing.class, XianLingRender::new);
    }
}
