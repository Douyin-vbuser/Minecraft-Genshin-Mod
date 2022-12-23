package com.vbuser.genshin.init;

import com.vbuser.genshin.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block TAI_XIAN = new BlockBase("tai_xian", Material.GRASS);
    //苔藓块   Moss Block(copy from Minecraft 1.19)

    public static final Block ZHUAN_KUAI = new ZhuanKuai("zhuan_kuai",Material.IRON);
    //砖块(烫脚)  Bricks(attacked by "Caelestinum Final Termini")

    public static final Block TIAN_TIAN_HUA = new TianTianHua("tiantianhua",Material.GRASS);
    //甜甜花  Sweet Flower

    public static final Block SHU_MEI = new ShuMei("shumei",Material.GLASS);
    //树莓 Berry

    public static final Block HUANG_SE_SHU_YE = new FlowerBase("leaves_yellow",Material.LEAVES);
    //黄色树叶 Yellow Leaves

    public static final Block BO_HE = new BoHe("bo_he",Material.GLASS);
    //薄荷 Mint(maybe?)

    public static final Block LUO_LUO_MEI = new LuoLuoMei("luo_luo_mei",Material.GRASS);
    //落落莓 Valberry

    public static final Block CHUAN_SONG_MAO_DIAN = new ChuanSongMaoDian("chuan_song_mao_dian",Material.IRON);
    //传送锚点 Teleport Waypoint

    public static final Block XIAO_DENG_CAO = new XiaoDengCao("xiao_deng_cao",Material.GRASS);
    //小灯草 Small Lamp Grass

    public static final Block FENG_CHE_JU = new FengCheJu("feng_che_ju",Material.GRASS);
    //风车菊 Windwheel Aster

    public static final Block SAI_XI_LI_YA_HUA = new SaiXiLiYaHua("sai_xi_li_ya_hua",Material.GRASS);
    //塞西莉亚花 Cecilia

    public static final Block GOU_GOU_GUO = new GouGouGuo("gou_gou_guo",Material.GRASS);
    //钩钩果 Wolfhook

    public static final Block DU_DU_LIAN = new GouGouGuo("du_du_lian",Material.GRASS);
    //嘟嘟莲 Calla Lily

    public static final Block MU_FENG_MO_GU = new MuFengMoGu("mu_feng_mo_gu",Material.GRASS);
    //慕风蘑菇 Phillanemo Mushroom
}
