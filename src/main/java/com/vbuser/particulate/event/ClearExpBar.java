package com.vbuser.particulate.event;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * ClearExpBar 类用于处理游戏界面渲染事件，专门用于隐藏经验条<br>
 * 当客户端渲染游戏界面时，此类会拦截经验条的渲染并取消其显示
 */
public class ClearExpBar {

    /**
     * 游戏界面渲染前的事件处理方法<br>
     * 当检测到要渲染经验条元素时，取消该渲染操作
     * @param event 游戏界面渲染事件对象
     */
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        // 检查当前渲染的元素是否为经验条
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            // 取消经验条的渲染
            event.setCanceled(true);
        }
    }
}