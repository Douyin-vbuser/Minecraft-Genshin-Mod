package com.vbuser.genshin.init.key;

import com.vbuser.genshin.proxy.ClientProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "genshin")
public class KeyboardManager {
    public static void init() {
        for (KeyBinding keyBinding : ClientProxy.KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
