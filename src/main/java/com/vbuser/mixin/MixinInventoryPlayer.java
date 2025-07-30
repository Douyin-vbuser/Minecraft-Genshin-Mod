package com.vbuser.mixin;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {

    @Shadow
    public int currentItem;

    /**
     * @author vbuser
     * @reason for fov control
     * 阻止快捷栏循环滚动以支持视距控制
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public void changeCurrentItem(int direction) {
        if (direction > 0) {
            direction = 1;
        }

        if (direction < 0) {
            direction = -1;
        }

        this.currentItem -= direction;

        if (this.currentItem < 0) {
            this.currentItem = 0;
        }
        if (this.currentItem > 8) {
            this.currentItem = 8;
        }
    }
}
