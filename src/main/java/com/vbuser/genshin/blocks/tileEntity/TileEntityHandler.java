package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.util.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityHandler {
    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityChuan.class,new ResourceLocation(Reference.Mod_ID+":chuan_song_mao_dian"));
        GameRegistry.registerTileEntity(TileEntityBao.class,new ResourceLocation(Reference.Mod_ID+"bao_xiang"));
    }
}
