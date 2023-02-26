package com.vbuser.genshin.entity.organism.render;

import com.vbuser.genshin.entity.organism.JingDie;
import com.vbuser.genshin.entity.organism.model.JingDieModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class JingDieRender extends GeoEntityRenderer<JingDie> {

    public JingDieRender(RenderManager renderManager) {
        super(renderManager, new JingDieModel());
    }

}
