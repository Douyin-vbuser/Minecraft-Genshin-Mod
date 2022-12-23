package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ZhuanKuai extends BlockBase{

    public ZhuanKuai(String name, Material material){
        super(name,material);
        setCreativeTab(Main.JIANCAI_TAB);
        setHardness(4f);
        setResistance(20f);
        setHarvestLevel("pickaxe",3);
        setSoundType(SoundType.STONE);
    }
}
