package com.vbuser.genshin.client.model.tile;

import com.vbuser.genshin.blocks.tileEntity.TileEntityChuan;
import com.vbuser.genshin.util.Reference;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChuanModel extends AnimatedGeoModel<TileEntityChuan> {
    public ChuanModel(){

    }

    @Override
    public ResourceLocation getAnimationFileLocation(TileEntityChuan animatable) {
        return new ResourceLocation(Reference.Mod_ID, "animations/chuan_song_mao_dian.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(TileEntityChuan animatable) {
        return animatable.getBlockMetadata()==1 ? new ResourceLocation(Reference.Mod_ID, "geo/chuan_song_mao_dian.geo.json") : new ResourceLocation(Reference.Mod_ID, "geo/chuan_song_mao_dian_0.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TileEntityChuan entity) {
        return entity.getBlockMetadata()==1 ? new ResourceLocation(Reference.Mod_ID, "textures/blocks/chuan_song_mao_dian.png") : new ResourceLocation(Reference.Mod_ID, "textures/blocks/chuan_song_mao_dian_0.png");
    }
}
