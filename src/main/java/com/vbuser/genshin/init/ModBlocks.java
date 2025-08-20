package com.vbuser.genshin.init;

import com.vbuser.genshin.block.BlockCloud;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();

    public static final Block CLOUD_BLOCK = new BlockCloud("cloud_block", Material.SNOW);
}
