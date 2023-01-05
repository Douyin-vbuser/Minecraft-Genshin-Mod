package com.vbuser.genshin.util.handlers;

import com.vbuser.genshin.entity.JingDie;
import com.vbuser.genshin.entity.render.JingDieRender;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler {
    public static void registerEntityRenders(){

        RenderingRegistry.registerEntityRenderingHandler(JingDie.class, new IRenderFactory<JingDie>()
        {
            @Override
            public Render<? super JingDie> createRenderFor(RenderManager manager)
            {
                return new JingDieRender(manager) ;
            }
        });
    }
}
