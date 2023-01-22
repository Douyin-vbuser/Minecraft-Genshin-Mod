package com.vbuser.genshin.entity.render;

import com.vbuser.genshin.entity.JingDie;
import com.vbuser.genshin.entity.model.JingDieModel;
import com.vbuser.genshin.util.Reference;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class JingDieRender extends GeoEntityRenderer<JingDie> {

    public JingDieRender(RenderManager renderManager) {
        super(renderManager, new JingDieModel());
    }

}
