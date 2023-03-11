package com.vbuser.genshin.client.model.tile;

import com.vbuser.genshin.blocks.tileEntity.TileEntityBao;
import com.vbuser.genshin.util.Reference;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BaoModel extends AnimatedGeoModel<TileEntityBao> {
    public BaoModel(){

    }

    @Override
    public ResourceLocation getAnimationFileLocation(TileEntityBao animatable) {
        return new ResourceLocation(Reference.Mod_ID, "animations/bao_xiang_1.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(TileEntityBao animatable) {
        return new ResourceLocation(Reference.Mod_ID, "geo/bao_xiang_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TileEntityBao entity) {
        return new ResourceLocation(Reference.Mod_ID, "textures/blocks/bao_xiang_1.png");
    }
}
