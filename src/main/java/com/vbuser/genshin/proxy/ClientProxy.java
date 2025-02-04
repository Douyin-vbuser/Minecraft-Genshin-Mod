package com.vbuser.genshin.proxy;

import com.vbuser.genshin.init.ModKeyBinding;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientProxy extends CommonProxy {
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), id));
    }

    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<>();

    public static final KeyBinding GLIDER = new ModKeyBinding("glider", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_SPACE, "key.category.genshin");

    public static final KeyBinding PG = new ModKeyBinding("pg", KeyConflictContext.IN_GAME, KeyModifier.NONE, -100, "key.category.genshin");

    public static final KeyBinding E = new ModKeyBinding("e", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_E, "key.category.genshin");

    public static final KeyBinding Q = new ModKeyBinding("q", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_Q, "key.category.genshin");

    public static final KeyBinding AA = new ModKeyBinding("aa", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_1, "key.category.genshin");

    public static final KeyBinding BB = new ModKeyBinding("bb", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_2, "key.category.genshin");

    public static final KeyBinding CC = new ModKeyBinding("cc", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_3, "key.category.genshin");

    public static final KeyBinding DD = new ModKeyBinding("dd", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_4, "key.category.genshin");

    public static final KeyBinding RUN = new ModKeyBinding("run",KeyConflictContext.IN_GAME,KeyModifier.NONE,Keyboard.KEY_LCONTROL,"key.category.genshin");
}
