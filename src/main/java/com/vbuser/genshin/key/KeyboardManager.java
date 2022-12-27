package com.vbuser.genshin.key;

import com.vbuser.genshin.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class KeyboardManager {
    public static void init(){
        for(KeyBinding keyBinding: ClientProxy.KEY_BINDINGS){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if(ClientProxy.TEST.isPressed()){
            if(player == null) return;
            if(mc.isGamePaused()) return;
            if(!mc.inGameHasFocus) return;
            if(mc.currentScreen != null) return;

            player.sendChatMessage("test");
        }
    }
}
