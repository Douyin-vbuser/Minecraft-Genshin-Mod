package com.vbuser.genshin.entity.render;

import com.vbuser.genshin.entity.JingDie;
import com.vbuser.genshin.entity.model.JingDieModel;
import com.vbuser.genshin.util.Reference;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class JingDieRender extends RenderLiving<JingDie> {

    public JingDieRender(RenderManager rendermanagerIn){
        super(rendermanagerIn,new JingDieModel(), 0.1f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(JingDie entity) {
        return new ResourceLocation(Reference.Mod_ID + ":textures/entity/jing_die.png");
    }
}
