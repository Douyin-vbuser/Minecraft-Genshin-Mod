package com.vbuser.genshin.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class BiomeDisplayHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private Biome currentStableBiome = null;
    private Biome pendingBiome = null;
    private int stableTimer = 0;
    private int displayTimer = 0;
    private boolean firstEntry = true;
    private final Map<String, String> biomeNameCache = new HashMap<>();
    private static final int STABLE_TIME = 100;
    private static final int DISPLAY_TIME = 40;
    private static final int FADE_TIME = 40;

    @SubscribeEvent
    public void onPlayerMove(PlayerEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();

        if (player.world.isRemote && player.equals(mc.player)) {
            Biome currentBiome = player.world.getBiome(player.getPosition());

            if (hasBiomeChanged(currentBiome)) {
                pendingBiome = currentBiome;
                stableTimer = 0;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null) return;

        if (pendingBiome != null) {
            if (stableTimer++ >= STABLE_TIME) {
                if (currentStableBiome != null) {
                    String currentKey = getBiomeKey(currentStableBiome);
                    String pendingKey = getBiomeKey(pendingBiome);
                    if (!currentKey.equals(pendingKey)) {
                        if (!firstEntry) {
                            displayTimer = DISPLAY_TIME + FADE_TIME;
                        } else {
                            firstEntry = false;
                        }
                    }
                } else {
                    firstEntry = false;
                }

                currentStableBiome = pendingBiome;
                pendingBiome = null;
            }
        }

        if (displayTimer > 0) {
            displayTimer--;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (displayTimer <= 0 || currentStableBiome == null) return;

        String biomeKey = getBiomeKey(currentStableBiome);
        String localized = getLocalizedBiomeName(biomeKey);

        ScaledResolution res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        float baseWidth = 250f;
        float baseHeight = 120f;
        float scaleX = width / baseWidth;
        float scaleY = height / baseHeight;
        float scale = Math.min(scaleX, scaleY);

        scale = (int)(10*Math.max(0.3f, Math.min(scale, 8.0f))) / 10f;

        int yPos = height / 6;
        int xPos = (int) ((width - mc.fontRenderer.getStringWidth(localized) * scale) / 2f);

        float alpha = 1.0f;
        if (displayTimer <= FADE_TIME) {
            alpha = (float) displayTimer / FADE_TIME;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        GlStateManager.scale(scale, scale, 1.0f);

        float scaledX = xPos / scale;
        float scaledY = yPos / scale;

        int color = (int) (255 * alpha) << 24 | 0xFFFFFF;

        mc.fontRenderer.drawStringWithShadow(localized, scaledX, scaledY, color);

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    private boolean hasBiomeChanged(Biome newBiome) {
        if (currentStableBiome == null && pendingBiome == null) {
            return true;
        }

        Biome reference = (pendingBiome != null) ? pendingBiome : currentStableBiome;

        String referenceKey = getBiomeKey(reference);
        String newKey = getBiomeKey(newBiome);
        return !referenceKey.equals(newKey);
    }

    private String getBiomeKey(Biome biome) {
        ResourceLocation loc = Biome.REGISTRY.getNameForObject(biome);
        return loc != null ? loc.toString() : biome.getBiomeName();
    }

    private String getLocalizedBiomeName(String biomeKey) {
        return biomeNameCache.computeIfAbsent(biomeKey, k ->
                I18n.format("biome." + k.replace(':', '.').toLowerCase())
        );
    }
}