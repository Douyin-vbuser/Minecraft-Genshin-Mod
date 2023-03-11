package com.vbuser.genshin.blocks.tileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileEntityChuan extends TileEntity implements IAnimatable {

    public boolean isActived;

    //向NBT写入读取布尔值
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("actived", this.isActived);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.isActived = compound.getBoolean("actived");
    }

    //方块动画
    private final AnimationFactory manager = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){
        if(((TileEntity)event.getAnimatable()).getBlockMetadata()==1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("loop", true));
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate1(AnimationEvent<E> event){
        if(((TileEntity)event.getAnimatable()).getTileData().getBoolean("actived")){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("actived",false));
        }
        return PlayState.CONTINUE;//注：激活动画并未正常实现
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController< >(this, "controller", 10, this::predicate));
        data.addAnimationController(new AnimationController< >(this,"controller1",10,this::predicate1));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.manager;
    }
}
