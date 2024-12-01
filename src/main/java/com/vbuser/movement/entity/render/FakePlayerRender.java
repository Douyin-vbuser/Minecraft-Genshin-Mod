package com.vbuser.movement.entity.render;

import com.vbuser.movement.entity.FakePlayer;
import com.vbuser.movement.entity.model.FakePlayerModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class FakePlayerRender extends GeoEntityRenderer<FakePlayer> {
    public FakePlayerRender(RenderManager renderManager) {
        super(renderManager, new FakePlayerModel());
    }
}
