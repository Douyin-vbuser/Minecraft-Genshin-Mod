package com.vbuser.movement.entity.model;

import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FakePlayerModel extends AnimatedGeoModel<FakePlayer> {
    @Override
    public ResourceLocation getModelLocation(FakePlayer fakePlayerModel) {
        return new ResourceLocation("movement", "geo/fake_player.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(FakePlayer fakePlayerModel) {
        return new ResourceLocation("movement", "textures/fake_player.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(FakePlayer fakePlayerModel) {
        //return new ResourceLocation("movement","animations/fake_player.animation.json");
        return null;
    }
}
