package com.vbuser.genshin.blocks.tileEntity;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileEntityChuan extends TileEntity implements IAnimatable {

    //·½¿é¶¯»­
    private final AnimationFactory manager = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){
        if(((TileEntity)event.getAnimatable()).getBlockMetadata()==2) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("loop", true));
        }

        if (((TileEntity) event.getAnimatable()).getBlockMetadata()==1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("actived", false));
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController< >(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.manager;
    }
}
