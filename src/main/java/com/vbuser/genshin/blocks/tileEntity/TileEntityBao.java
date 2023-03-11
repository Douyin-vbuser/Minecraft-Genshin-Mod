package com.vbuser.genshin.blocks.tileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.NonNullList;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TileEntityBao extends TileEntityLockableLoot implements IAnimatable,IInventory {
    public TileEntityBao()
    {
    }

    //方块动画
    private final AnimationFactory manager = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){
        if(((TileEntity)event.getAnimatable()).getBlockMetadata()%2==1) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("new", false));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController< >(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.manager;
    }

    //箱子实现
    private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);

    public int getSizeInventory()
    {
        return 27;
    }

    public boolean isEmpty()
    {
        for (ItemStack itemstack : contents)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public String getName(){return "genshin:bao_xiang";}

    public void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
        contents=NonNullList.withSize(this.getSizeInventory(),ItemStack.EMPTY);

        if(!this.checkLootAndRead(compound)){
            ItemStackHelper.loadAllItems(compound,contents);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        if(!this.checkLootAndWrite(compound)){
            ItemStackHelper.saveAllItems(compound,contents);
        }
        return compound;
    }

    public int getInventoryStackLimit(){return 64;}

    @Override
    protected NonNullList<ItemStack> getItems() {
        return contents;
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player){
        this.fillWithLoot(player);
        return new ContainerChest(playerInventory,this,player);
    }

    public String getGuiID(){return "minecraft:chest";}
}
