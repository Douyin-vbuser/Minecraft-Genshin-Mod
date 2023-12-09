package com.vbuser.inventory.proxy;

import com.vbuser.inventory.key.ModKeyBinding;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.ArrayList;

public class ClientProxy {
    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<>();

    public static final KeyBinding B = new ModKeyBinding("b", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_B,"key.category.genshin");
}
