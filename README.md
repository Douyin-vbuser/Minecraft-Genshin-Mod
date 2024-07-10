# MCI重构项目

由于原有项目的架构设计不合理，导致项目难以维护，因此决定对项目进行重构。

## 结构

 **·com.vbuser.genshin**:主模组,包含需要的方块、物品、剧情演出实现、大世界探索设计实现。
 
 **·com.vbuser.database**:简易数据库实现，用于实现背包系统，提供增加、读取、删除物品，对不可堆叠物品追加备注（原版中的NBT标签）等功能。
 
 **·com.vbuser.browser**:浏览器实现和http服务器搭建，用于CG播放和后续版本活动小游戏、七圣召唤接口提供。
 
 **·com.vbuser.movement**:实现自定义玩家实体运动规则、镜头运动规则。
 
## 原项目资产处理

原项目中item材质会全部替换为官方版本，其余基本保留；代码基本全部舍弃，实现全部重写，保留主模组的理想境框架。

原项目中的用户协议、借物表、待办表全部舍弃，保留“不受欢迎的创作者”清单，启动器舍弃重写。

## 代办列表

### Minecraft模组代码、资源方向：

2024.02.16版:

- [1.1] 向com.vbuser.genshin中引入完整的Idealland Framework，支持geckolib实体和维度
- [1.2] 制作怪物等实体的geckolib模型贴图和动画、AI逻辑
- [2.1] 用com.vbuser.database替代com.vbuser.inventory
- [2.2] 完成生产环境的背包GUI、角色系统、Web查看
- [3.1] 向com.vbuser.movement中引入自定义玩家实体运动规则(最好不使用Mixin)
- [3.2] 制作用于com.vbuser.movement的模型、动画、贴图
- [4.1] 整理常用接口
- [4.2] 向com.vbuser.genshin中引入低代码剧情实现
- [5.1] 制作可交互方块、npc(锻造、合成、烹饪、非剧情对话)
- [6.1] 制作小地图

### Web前后端方向：

- [1.1] 制作1.0时期活动GUI(div+css实现,不计划使用GuiScreen)
- [1.2] 制作基于json的网页-Minecraft数据同步实现

### Electron开发方向：

- [重写] 制作客户端启动器，实现启动与MCI更新、下载、文件校验
- [1.2] 在地图制作后，开发地图的更新器

### Minecraft地图方向：

- [1.1] 截至1.0开放的蒙德地区地形还原
- [1.2] 兼容模组实现（方块、生物、方块数据、维度实装、剧情等命令）

### 更好的原神项目方向：

- [1.1] 世界任务的配音补齐
- [1.2] 可视化低代码剧情制作(启动器)，用于征集现存剧情修订稿

私货：
```
[15:50:24] [Client thread/INFO]: [CHAT] 回合：7/7
[15:50:36] [Client thread/INFO]: [CHAT] 初窥门径 §b[MVP§d+§b] slepboii§f: L builder
[15:50:42] [Client thread/INFO]: [CHAT] 建筑大师 §b[MVP§c+§b] ellieemiller§f: oh leave them alone
[15:50:42] [Client thread/INFO]: [CHAT] 炉火纯青 §b[MVP§4+§b] SpookyEvent§f: ye im an L builder
[15:50:45] [Client thread/INFO]: [CHAT] 初窥门径 §b[MVP§d+§b] slepboii§f: glasses
[15:50:45] [Client thread/INFO]: [CHAT] 初窥门径 §7ratking344§7: go back to genshin impact
[15:50:47] [Client thread/INFO]: [CHAT] 炉火纯青 §b[MVP§4+§b] SpookyEvent§f: glasses
[15:50:56] [Client thread/INFO]: [CHAT] 初窥门径 §7ratking344§7: pointing
[15:51:02] [Client thread/INFO]: [CHAT] 建筑大师 §b[MVP§c+§b] ellieemiller§f: why dont u go back to genshin impact
[15:51:09] [Client thread/INFO]: [CHAT] 初窥门径 §b[MVP§d+§b] slepboii§f: profile
```