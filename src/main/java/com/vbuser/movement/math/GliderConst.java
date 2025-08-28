// 数据来源声明：本部分原始数据引用自第三方公开论坛 NGA 玩家社区
// 原文链接：https://ngabbs.com/read.php?tid=25695397&rand=930。
// 免责声明：上述数据系通过合法公开渠道获取，其知识产权归属于原作者或相关权利方。本人未参与、亦不支持任何对游戏客户端的非授权解包行为。

package com.vbuser.movement.math;

public final class GliderConst {
    public static final double HORIZONTAL = 5.99 / 20.0;
    public static final double VERTICAL = -1.91 / 20.0;
    public static final float ALPHA = 0.8f;
    public static double lerp(double c, double t, float a) { return c + (t - c) * a; }
    private GliderConst() {}
}
