package com.vbuser.inventory.key;

import com.vbuser.inventory.proxy.ClientProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class ModKeyBinding extends KeyBinding {
    public ModKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        super(String.format("key.%s.%s", "inventory", description), keyConflictContext, keyModifier, keyCode, category);
        ClientProxy.KEY_BINDINGS.add(this);
    }
}
