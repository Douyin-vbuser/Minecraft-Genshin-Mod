package com.vbuser.genshin.inventory.key;

import com.vbuser.genshin.inventory.proxy.ClientProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KeyboardManager {
    public static void init(){
        for(KeyBinding keyBinding: ClientProxy.KEY_BINDINGS){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
