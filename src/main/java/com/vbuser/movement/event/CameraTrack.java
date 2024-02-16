package com.vbuser.movement.event;

import com.vbuser.movement.Movement;
import com.vbuser.movement.Storage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Objects;

public class CameraTrack {
    private int start_x,start_y,start_z,end_x,end_y,end_z,start_yaw,end_yaw,last_time;
    private int current_time;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null){
            if(Storage.pack_received){
                Movement.network.sendToServer(new TS_TN(Minecraft.getMinecraft().player.getUniqueID(),false));
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                start_x = Storage.start_x;
                start_y = Storage.start_y;
                start_z = Storage.start_z;
                end_x = Storage.end_x;
                end_y = Storage.end_y;
                end_z = Storage.end_z;
                last_time = Storage.last_time;
                start_yaw = Storage.start_yaw;
                end_yaw = Storage.end_yaw;
                current_time = 0;
                Storage.pack_received = false;
                Storage.is_performing = true;
            }
            if(current_time >= last_time){
                Storage.is_performing = false;
                current_time=0;last_time=0;
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
                Movement.network.sendToServer(new TS_TN(Minecraft.getMinecraft().player.getUniqueID(),true));
            }
        }
    }

    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup event){
        if(Minecraft.getMinecraft().player != null && current_time < last_time){
            float progress = (float)current_time / last_time;

            float current_x = start_x + (end_x - start_x) * progress;
            float current_y = start_y + (end_y - start_y) * progress;
            float current_z = start_z + (end_z - start_z) * progress;
            float current_yaw = start_yaw + (end_yaw - start_yaw) * progress;

            Objects.requireNonNull(Minecraft.getMinecraft().getRenderViewEntity()).setPositionAndUpdate(current_x, current_y, current_z);
            Minecraft.getMinecraft().getRenderViewEntity().rotationYaw = current_yaw;
            Minecraft.getMinecraft().getRenderViewEntity().rotationPitch = 0;

            current_time++;
        }
    }
}
