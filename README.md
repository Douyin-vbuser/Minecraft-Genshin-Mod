[老项目](https://github.com/66hh/Minecraft-Genshin-Mod) 的链接在此，内容已经基本完全移植。

有空会看pr和issue(但估计不太会上线修)，可[邮件](mailto:douyin_vbuser@outlook.com)加急

诶！你怎么知道我万叶6+1了\[emoji_书呆子]\[emoji_食指]。

----

# MCI重构项目

由于原有项目的架构设计不合理，导致项目难以维护，因此决定对项目进行重构。

## 结构

 **·com.vbuser.genshin**:主模组,包含需要的方块、物品、剧情演出实现、大世界探索设计实现。
 
 **·com.vbuser.database**:简易数据库实现，用于实现背包系统，提供增加、读取、删除物品，对不可堆叠物品追加备注（原版中的NBT标签）；游戏进度；世界规则、数据存储等功能。
 
 **·com.vbuser.browser**:浏览器实现和http服务器搭建，用于CG播放和后续版本活动小游戏、七圣召唤接口提供。
 
 **·com.vbuser.movement**:实现自定义玩家实体运动规则、镜头运动规则。

 **·com.vbuser.particulate**:通用粒子、特效渲染控制器。部分需要自定义的渲染逻辑
 
 **·com.vbuser.maple**:简单pytorch模仿，用于简单AI实现。

 **·com.vbuser.ime**:简单[IME](https://github.com/Douyin-vbuser/IME)实现，排除输入法干扰。

想要或实在没实力写的：自定义光照和LOD（自定义着色器），浏览器局域网串流操控（云游戏）

## 原项目资产处理

原项目中item材质会全部替换为官方版本，其余基本保留；代码基本全部舍弃，实现全部重写，保留主模组的理想境框架。

原项目中的用户协议、借物表、待办表全部舍弃，保留“不受欢迎的创作者”清单，启动器舍弃重写。

## 代办列表

跨二级目录序号-优先级关系同，完成即删除<br>
2024.07.17版:

### Minecraft模组代码、资源方向：

- [1] **完成开放世界框架(Minecraft Impact)**
- [\>] 修屎山，最大程度兼容多人游戏
<br><br>
- [2] **完成原神部分(Open-resource Minecraft Genshin mod)**
- [\>] 1.0角色建模、动作、攻击模组
- [\>] 制作逻辑方块、可交互方块、npc(锻造、合成、烹饪、非剧情对话)
- [2.2] 针对特殊玩法（风龙废墟战斗、蒙德城上空战斗等）兼容性实现
### Web前后端方向：

- [2.2] 制作1.0时期活动GUI(div+css实现,不计划使用GuiScreen)

### C++Windows桌面应用方向：

- [1.1] 制作客户端启动器，实现启动与MCI更新、下载、文件校验

### Minecraft地图方向：

- [1.1] 截至1.0开放的蒙德地区地形还原（不排除使用激进手段获取，另起仓库防DMCA）
- [1.2] 兼容模组实现（方块、生物、方块数据、维度实装、剧情等命令）

### 更好的原神项目方向：

- [1.1] 可视化低代码剧情制作(网页前端)，用于征集现存剧情修订稿
- [2.1] 世界任务配音补齐

## 借物表

<table>
    <tr>
        <th>资产类型</th>
        <td>描述</td>
        <td>数量</td>
        <td>产权</td>
        <td>链接</td>
    </tr>
    <tr>
        <th>贴图</th>
        <td>原神游戏材质</td>
        <td>78</td>
        <td>Proprietary</td>
        <td>https://www.miyoushe.com/ys/</td>
    </tr>
    <tr>
        <th>贴图</th>
        <td>Minecraft材质</td>
        <td>160</td>
        <td>Proprietary</td>
        <td>https://www.curseforge.com/minecraft/texture-packs/stay-true</td>
    </tr>
    <tr>
        <th>源代码(Java)</th>
        <td>理想境框架</td>
        <td>600+loc</td>
        <td>Custom License</td>
        <td>https://github.com/IdeallandEarthDept/IdeallandFramework</td>
    </tr>
    <tr>
        <th>源代码(Gradle)</th>
        <td>Mixin引入</td>
        <td>100-loc</td>
        <td>Unlicense</td>
        <td>https://github.com/mouse0w0/forge-mixin-example/blob/1.12.2-14.23.5.2860/build.gradle</td>
    </tr>
    <tr>
        <th>源代码(Java)</th>
        <td>AI辅助编程造成的潜在侵权风险</td>
        <td>1000+loc</td>
        <td>(Claud;Qwen;Chat-GPT)</td>
        <td>https://lmarena.ai</td>
    </tr>
</table>