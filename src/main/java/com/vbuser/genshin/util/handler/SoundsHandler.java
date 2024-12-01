package com.vbuser.genshin.util.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundsHandler {
    public static SoundEvent TEST;
    public static SoundEvent PICK;

    public static SoundEvent XDC;

    public static void registerSounds() {
        TEST = registerSound("test");
        PICK = registerSound("pick");

        XDC = registerSound("xiao_deng_cao");
    }

    private static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation("genshin", name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }

}
