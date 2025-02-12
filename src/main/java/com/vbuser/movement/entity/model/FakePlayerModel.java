package com.vbuser.movement.entity.model;

import com.vbuser.genshin.event.CharacterChoice;
import com.vbuser.movement.entity.FakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FakePlayerModel extends AnimatedGeoModel<FakePlayer> {

    public static String idToCharacter(){
        EntityPlayer player = Minecraft.getMinecraft().player;
        int id = CharacterChoice.getCharacter(player.getUniqueID());
        switch (id){
            case 0 :return "kong";
            case 1 :return "an_bo";
            default:return "fake_player";
        }
    }

    @Override
    public ResourceLocation getModelLocation(FakePlayer fakePlayerModel) {
        return new ResourceLocation("genshin", "geo/"+ idToCharacter() +".geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(FakePlayer fakePlayerModel) {
        return new ResourceLocation("genshin", "textures/entity/"+ idToCharacter() +".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(FakePlayer fakePlayerModel) {
        return new ResourceLocation("genshin","animations/"+idToCharacter()+".animation.json");
    }
}
