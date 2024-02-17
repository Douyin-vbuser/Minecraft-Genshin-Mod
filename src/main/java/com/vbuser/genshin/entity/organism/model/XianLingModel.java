package com.vbuser.genshin.entity.organism.model;

import com.vbuser.genshin.entity.organism.XianLing;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class XianLingModel extends AnimatedGeoModel<XianLing> {
    @Override
    public ResourceLocation getModelLocation(XianLing xianLing) {
        return new ResourceLocation("genshin", "geo/xian_ling.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(XianLing xianLing) {
        return new ResourceLocation("genshin", "textures/entity/xian_ling.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(XianLing xianLing) {
        return new ResourceLocation("genshin", "animations/xian_ling.animation.json");
    }
}
