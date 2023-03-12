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
        int meta =entity.getBlockMetadata();
        switch ((meta-meta%2)/2+1) {
            case(1):return new ResourceLocation(Reference.Mod_ID, "textures/blocks/bao_xiang_1.png");
            case(2):return new ResourceLocation(Reference.Mod_ID,"textures/blocks/bao_xiang_2.png");
            case(3):return new ResourceLocation(Reference.Mod_ID,"textures/blocks/bao_xiang_3.png");
            case(4):return new ResourceLocation(Reference.Mod_ID,"textures/blocks/bao_xiang_4.png");
            case(5):return new ResourceLocation(Reference.Mod_ID,"textures/blocks/bao_xiang_5.png");
            default:return null;
        }
    }
}
