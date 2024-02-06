package com.vbuser.genshin.init;

import com.vbuser.genshin.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<Block>();
    public static final Block TAI_XIAN = new BlockBase("tai_xian", Material.GRASS);
    public static final Block ZHUAN_KUAI = new ZhuanKuai("zhuan_kuai",Material.IRON);
    public static final Block TIAN_TIAN_HUA = new TianTianHua("tiantianhua",Material.GRASS);
    public static final Block SHU_MEI = new ShuMei("shumei",Material.GLASS);
    public static final Block HUANG_SE_SHU_YE = new FlowerBase("leaves_yellow",Material.LEAVES);
    public static final Block BO_HE = new BoHe("bo_he",Material.GLASS);
    public static final Block LUO_LUO_MEI = new LuoLuoMei("luo_luo_mei",Material.GRASS);
    public static final Block XIAO_DENG_CAO = new XiaoDengCao("xiao_deng_cao",Material.GRASS);
    public static final Block FENG_CHE_JU = new FengCheJu("feng_che_ju",Material.GRASS);
    public static final Block SAI_XI_LI_YA_HUA = new SaiXiLiYaHua("sai_xi_li_ya_hua",Material.GRASS);
    public static final Block GOU_GOU_GUO = new GouGouGuo("gou_gou_guo",Material.GRASS);
    public static final Block DU_DU_LIAN = new GouGouGuo("du_du_lian",Material.GRASS);
    public static final Block MU_FENG_MO_GU = new MuFengMoGu("mu_feng_mo_gu",Material.GRASS);
    public static final Block PU_GONG_YING = new PuGongYing("pu_gong_ying",Material.GRASS);
    public static final Block MO_GU = new MoGu("mo_gu",Material.GRASS);
    public static final Block SONG_RONG = new SongRong("song_rong",Material.GRASS);
    public static final Block HE_CHENG_TAI = new HeChengTai("he_cheng_tai",Material.IRON);
    public static final Block JIA_ZAI_QI = new JiaZaiQi("jia_zai_qi",Material.IRON);
    public static final Block ZHA_DAN = new ZhaDan("zha_dan",Material.TNT);
    public static final Block HUO_JU = new HuoJu("huo_ju",Material.WOOD);
    public static final Block BAN = new BlockBase("ban",Material.BARRIER);
    public static final Block PRE_STONE = new BlockBase("pre_stone",Material.REDSTONE_LIGHT);
    public static final Block XIAN_LING_ZHI_TING = new XianLingZhiTing("xian_ling_zhi_ting",Material.IRON);
    public static final Block DENG = new Deng("deng",Material.IRON);
    //public static final Block FENG_FANG_BEI = new FangBeiBase("feng_fang_bei",Material.IRON,Feng.class);
    //public static final Block SHUI_FANG_BEI = new FangBeiBase("shui_fang_bei",Material.IRON, Shui.class);
    //public static final Block HUO_FANG_BEI = new FangBeiBase("huo_fang_bei",Material.IRON, Huo.class);
    //public static final Block LEI_FANG_BEI =  new FangBeiBase("lei_fang_bei",Material.IRON, Lei.class);
    //public static final Block BING_FANG_BEI = new FangBeiBase("bing_fang_bei",Material.IRON, Bing.class);
    //public static final Block YAN_FANG_BEI = new FangBeiBase("yan_fang_bei",Material.IRON, Yan.class);
    //public static final Block CAO_FANG_BEI = new FangBeiBase("cao_fang_bei",Material.IRON, Cao.class);
}
