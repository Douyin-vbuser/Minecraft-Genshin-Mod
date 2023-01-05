package com.vbuser.genshin.util.handlers;

import com.vbuser.genshin.commands.CommandTpdim;
import com.vbuser.genshin.entity.EntityInit;
import com.vbuser.genshin.entity.JingDie;
import com.vbuser.genshin.entity.render.JingDieRender;
import com.vbuser.genshin.init.InitBiome;
import com.vbuser.genshin.init.InitDimension;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegistryHandler {

    public static void initRegistries(){
        SoundsHandler.registerSounds();
    }


    public static void preInitRegistries(FMLPreInitializationEvent event){
        InitBiome.registerBiomes();
        InitDimension.registerDimensions();
        EntityInit.registerEntities();
        RenderHandler.registerEntityRenders();
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event){
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event){
        event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event){
        for(Item item:ModItems.ITEMS){
            if(item instanceof IHasModel){
                ((IHasModel)item).registerModels();
            }
        }

        for(Block block:ModBlocks.BLOCKS){
            if(block instanceof IHasModel){
                ((IHasModel) block).registerModels();
            }
        }
    }

    public static void serverRegistries(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandTpdim());
    }
}
