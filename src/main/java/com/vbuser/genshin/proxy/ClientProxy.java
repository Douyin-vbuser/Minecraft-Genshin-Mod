package com.vbuser.genshin.proxy;

import com.vbuser.genshin.key.ModKeyBinding;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;
import java.util.List;
import java.util.ArrayList;

public class ClientProxy extends CommonProxy{
    public void registerItemRenderer(Item item, int meta,String id){
        ModelLoader.setCustomModelResourceLocation(item,meta,new ModelResourceLocation(item.getRegistryName(),id));
    }

    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<KeyBinding>();

    public static final KeyBinding TEST = new ModKeyBinding("test", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_I, "key.category.genshin");

    public boolean isServer()
    {
        return false;
    }
}
