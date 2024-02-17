package com.vbuser.genshin.entity.organism.render;

import com.vbuser.genshin.entity.organism.XianLing;
import com.vbuser.genshin.entity.organism.model.XianLingModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class XianLingRender extends GeoEntityRenderer<XianLing> {
    public XianLingRender(RenderManager renderManager) {
        super(renderManager,new XianLingModel());
    }

}
