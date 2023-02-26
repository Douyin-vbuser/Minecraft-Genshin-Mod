package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.Objects;

public abstract class BlockContainerBase extends BlockContainer implements IHasModel {
    public BlockContainerBase(String name, Material material){    //用于注册带容器方块
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
    }

    @Override
    public void registerModels(){
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this),0,"inventory");
    }

}
