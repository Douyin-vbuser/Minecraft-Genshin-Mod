package com.vbuser.genshin.entity.organism.model;

import com.vbuser.genshin.entity.organism.JingDie;
import com.vbuser.genshin.util.Reference;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JingDieModel extends AnimatedGeoModel<JingDie> {

	@Override
	public ResourceLocation getModelLocation(JingDie object) {
		return new ResourceLocation(Reference.Mod_ID, "geo/jing_die.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(JingDie object) {
		return new ResourceLocation(Reference.Mod_ID, "textures/entity/jing_die.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(JingDie object) {
		return new ResourceLocation(Reference.Mod_ID, "animations/jing_die.animation.json");
	}
}