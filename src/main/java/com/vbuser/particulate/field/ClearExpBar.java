package com.vbuser.particulate.field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClearExpBar {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.ingameGUI == null || mc.ingameGUI instanceof HookedGuiIngame) return;

            HookedGuiIngame newGui = new HookedGuiIngame(mc);
            setIngameGui(mc, newGui);

            System.out.println("[FuckExpBar] Successfully hook method renderExpBar");
        } catch (Exception ex) {
            System.err.println("[FuckExpBar] Hook failed:" + ex.getMessage());
        }
    }

    public static class HookedGuiIngame extends GuiIngame {
        public HookedGuiIngame(Minecraft mc) {
            super(mc);

            try {
                copyCriticalState(mc.ingameGUI, this);
            } catch (Exception e) {
                System.err.println("[FuckExpBar] Warning on state copying:" + e.getMessage());
            }
        }

        @Override
        public void renderExpBar(ScaledResolution scaledRes, int x) {

        }

        private void copyCriticalState(GuiIngame source, GuiIngame target) throws Exception {
            Map<String, String> criticalFields = new HashMap<>();
            criticalFields.put("persistantChatGUI", "persistantChatGUI");
            criticalFields.put("overlayBoss", "overlayBoss");
            criticalFields.put("recordPlayingUpFor", "field_73846_l");
            criticalFields.put("recordIsPlaying", "field_73847_m");

            for (Map.Entry<String, String> entry : criticalFields.entrySet()) {
                try {
                    copyField(source, target, entry.getKey());
                } catch (NoSuchFieldException ex) {
                    copyField(source, target, entry.getValue());
                }
            }
        }

        private void copyField(Object source, Object target, String fieldName)
                throws NoSuchFieldException, IllegalAccessException {
            Field field = GuiIngame.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(source);
            field.set(target, value);
        }
    }

    private void setIngameGui(Minecraft mc, GuiIngame newGui) throws Exception {
        try {
            Field ingameGuiField = Minecraft.class.getDeclaredField("field_71456_v");
            ingameGuiField.setAccessible(true);
            ingameGuiField.set(mc, newGui);
        } catch (NoSuchFieldException e) {
            Field backupField = Minecraft.class.getDeclaredField("ingameGUI");
            backupField.setAccessible(true);
            backupField.set(mc, newGui);
        }
    }
}
