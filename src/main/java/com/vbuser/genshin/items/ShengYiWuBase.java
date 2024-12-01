package com.vbuser.genshin.items;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class ShengYiWuBase extends Item implements IHasModel {

    private final int type;

    private final int level;

    private final int index;

    public ShengYiWuBase(String name, int type, int level, int index) {
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(Main.SHENG_YI_WU);
        this.type = type;
        this.level = level;
        this.index = index;
        setMaxStackSize(1);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            assert tagCompound != null;
            int deng_ji = tagCompound.getInteger("deng_ji");
            int jing_yan = tagCompound.getInteger("jing_yan");
            int mainValue = tagCompound.getInteger("mainValue");
            int mainProperty = tagCompound.getInteger("mainProperty");
            tooltip.add(translateType(type) + "(" + level + ")");
            tooltip.add(I18n.format("genshin.dj") + deng_ji + "(" + jing_yan + ")");
            tooltip.add(I18n.format("genshin." + translateProperty(mainProperty)) + ":" + displayCorrection(mainProperty, mainValue));
            if (hasNBT(stack, "aProperty")) {
                tooltip.add(I18n.format("genshin." + translateProperty(tagCompound.getInteger("aProperty"))) + ":" + displayCorrection(tagCompound.getInteger("aProperty"), tagCompound.getInteger("aValue")));
            }
            if (hasNBT(stack, "bProperty")) {
                tooltip.add(I18n.format("genshin." + translateProperty(tagCompound.getInteger("bProperty"))) + ":" + displayCorrection(tagCompound.getInteger("bProperty"), tagCompound.getInteger("bValue")));
            }
            if (hasNBT(stack, "cProperty")) {
                tooltip.add(I18n.format("genshin." + translateProperty(tagCompound.getInteger("cProperty"))) + ":" + displayCorrection(tagCompound.getInteger("cProperty"), tagCompound.getInteger("cValue")));
            }
            if (hasNBT(stack, "dProperty")) {
                tooltip.add(I18n.format("genshin." + translateProperty(tagCompound.getInteger("dProperty"))) + ":" + displayCorrection(tagCompound.getInteger("dProperty"), tagCompound.getInteger("dValue")));
            }
        }
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(new NBTTagCompound());
        assert stack.getTagCompound() != null;
        stack.getTagCompound().setInteger("deng_ji", 0);
        stack.getTagCompound().setInteger("jing_yan", 0);
        stack.getTagCompound().setInteger("type", type);
        stack.getTagCompound().setInteger("level", level);
        randomDefaultProperty(type, level, stack);
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!stack.hasTagCompound()) {
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityIn;
                if (player.getHeldItemMainhand().getItem() != Items.STICK) {
                    stack.setTagCompound(new NBTTagCompound());
                    randomDefaultProperty(type, level, stack);
                }
            }
        }
    }

    public boolean hasNBT(ItemStack itemStack, String key) {
        NBTTagCompound tag = itemStack.getTagCompound();
        return tag != null && tag.hasKey(key);
    }

    public int baseXp(int level) {
        if (level == 1) {
            return 420;
        } else if (level == 2) {
            return 840;
        } else if (level == 3) {
            return 1260;
        } else if (level == 4) {
            return 2520;
        } else if (level == 5) {
            return 3780;
        } else {
            return 0;
        }
    }

    public String displayCorrection(int key, int value) {
        return (key == 1 || key == 3 || key == 5 || key == 8) ? String.valueOf(value) : (String.valueOf((float) value / 10) + "%");
    }

    public void addDengJi(ItemStack stack, int add) {
        assert stack.getTagCompound() != null;
        int pre_dj = stack.getTagCompound().getInteger("deng_ji");
        int pre_jy = stack.getTagCompound().getInteger("jing_yan");
        int mainProperty = stack.getTagCompound().getInteger("mainProperty");
        int jy = 0, dj = 0;
        jy = pre_jy + add;
        switch (level) {
            case 1:
                if (jy < 600) {
                    dj = 0;
                }
                if (jy >= 600 && jy < 1350) {
                    dj = 1;
                }
                if (jy >= 1350 && jy < 2250) {
                    dj = 2;
                }
                if (jy >= 2250 && jy < 3250) {
                    dj = 3;
                }
                if (jy >= 3250) {
                    dj = 4;
                    jy = 3250;
                    randomNewProperty(stack, false, level, mainProperty);
                }
                ;
            case 2:
                ;//TODO
            case 3:
                if (jy < 1800) {
                    dj = 0;
                }
                if (jy >= 1800 && jy < 4025) {
                    dj = 1;
                }
                if (jy >= 4025 && jy < 6675) {
                    dj = 2;
                }
                if (jy >= 6675 && jy < 9775) {
                    dj = 3;
                }
                if (jy >= 9775 && jy < 13325) {
                    dj = 4;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 13325 && jy < 17325) {
                    dj = 5;
                }
                if (jy >= 17325 && jy < 21825) {
                    dj = 6;
                }
                if (jy >= 21825 && jy < 26825) {
                    dj = 7;
                }
                if (jy >= 26825 && jy < 32075) {
                    dj = 8;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 32075 && jy < 38150) {
                    dj = 9;
                }
                if (jy >= 38150 && jy < 44775) {
                    dj = 10;
                }
                if (jy >= 44775 && jy < 52000) {
                    dj = 11;
                }
                if (jy >= 52000) {
                    dj = 12;
                    jy = 52000;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                ;
            case 4:
                if (jy < 2400) {
                    dj = 0;
                }
                if (jy >= 2400 && jy < 5375) {
                    dj = 1;
                }
                if (jy >= 5375 && jy < 8925) {
                    dj = 2;
                }
                if (jy >= 8925 && jy < 13050) {
                    dj = 3;
                }
                if (jy >= 13050 && jy < 17775) {
                    dj = 4;
                    randomNewProperty(stack, hasNBT(stack, "cProperty"), level, mainProperty);
                }
                if (jy >= 17775 && jy < 23125) {
                    dj = 5;
                }
                if (jy >= 23125 && jy < 29125) {
                    dj = 6;
                }
                if (jy >= 29125 && jy < 35800) {
                    dj = 7;
                }
                if (jy >= 35800 && jy < 43175) {
                    dj = 8;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 43175 && jy < 51275) {
                    dj = 9;
                }
                if (jy >= 51275 && jy < 60125) {
                    dj = 10;
                }
                if (jy >= 60125 && jy < 69750) {
                    dj = 11;
                }
                if (jy >= 69750 && jy < 80175) {
                    dj = 12;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 80175 && jy < 92300) {
                    dj = 13;
                }
                if (jy >= 92300 && jy < 106375) {
                    dj = 14;
                }
                if (jy >= 106375 && jy < 122675) {
                    dj = 15;
                }
                if (jy >= 122675) {
                    dj = 16;
                    jy = 122675;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                ;
            case 5:
                if (jy < 3000) {
                    dj = 0;
                }
                if (jy >= 3000 && jy < 6725) {
                    dj = 1;
                }
                if (jy >= 6725 && jy < 11150) {
                    dj = 2;
                }
                if (jy >= 11150 && jy < 16300) {
                    dj = 3;
                }
                if (jy >= 16300 && jy < 22200) {
                    dj = 4;
                }
                if (jy >= 22200 && jy < 28875) {
                    dj = 5;
                    randomNewProperty(stack, hasNBT(stack, "dProperty"), level, mainProperty);
                }
                if (jy >= 28875 && jy < 36375) {
                    dj = 6;
                }
                if (jy >= 36375 && jy < 44725) {
                    dj = 7;
                }
                if (jy >= 44725 && jy < 53950) {
                    dj = 8;
                }
                if (jy >= 53950 && jy < 64075) {
                    dj = 9;
                }
                if (jy >= 64075 && jy < 75125) {
                    dj = 10;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 75125 && jy < 87150) {
                    dj = 11;
                }
                if (jy >= 87150 && jy < 100175) {
                    dj = 12;
                }
                if (jy >= 100175 && jy < 115325) {
                    dj = 13;
                }
                if (jy >= 115325 && jy < 132925) {
                    dj = 14;
                }
                if (jy >= 132925 && jy < 153300) {
                    dj = 15;
                    randomNewProperty(stack, true, level, mainProperty);
                }
                if (jy >= 153300 && jy < 176800) {
                    dj = 16;
                }
                if (jy >= 176800 && jy < 203850) {
                    dj = 17;
                }
                if (jy >= 203850 && jy < 234900) {
                    dj = 18;
                }
                if (jy >= 234900 && jy < 270475) {
                    dj = 19;
                }
                if (jy >= 270475) {
                    dj = 20;
                    jy = 2704750;
                    randomNewProperty(stack, true, level, mainProperty);
                }
        }
        //TODO:correct mainValue
        stack.getTagCompound().setInteger("deng_ji", dj);
        stack.getTagCompound().setInteger("jing_yan", jy);
    }

    public String translateType(int type) {
        switch (type) {
            case 1:
                return I18n.format("genshin.syw.h");
            case 2:
                return I18n.format("genshin.syw.m");
            case 3:
                return I18n.format("genshin.syw.s");
            case 4:
                return I18n.format("genshin.syw.b");
            case 5:
                return I18n.format("genshin.syw.t");
            default:
                return null;
        }
    }

    public static String translateProperty(int index) {
        switch (index) {
            case 1:
                return "sm";//num
            case 2:
                return "sm";
            case 3:
                return "gj";//num
            case 4:
                return "gj";
            case 5:
                return "fy";//num
            case 6:
                return "fy";
            case 7:
                return "cn";
            case 8:
                return "jt";
            case 9:
                return "bj";
            case 10:
                return "bs";
            case 11:
                return "hs";
            case 12:
                return "ss";
            case 13:
                return "ws";
            case 14:
                return "fs";
            case 15:
                return "cs";
            case 16:
                return "bns";
            case 17:
                return "ys";
            case 18:
                return "ls";
            default:
                return null;
        }
    }

    public int randomValue(int type) {
        int[] a = new int[]{27, 31, 35, 39};
        int[] b = new int[]{54, 62, 70, 78};
        int[] c = new int[]{41, 47, 53, 58};
        int[] d = new int[]{51, 58, 66, 73};
        int[] e = new int[]{45, 52, 58, 65};
        int[] g = new int[]{14, 16, 18, 19};
        int[] h = new int[]{209, 239, 269, 299};
        int[] i = new int[]{16, 19, 21, 23};

        if (type == 1) {
            return h[(int) (Math.random() * 4)];
        } else if (type == 2) {
            return c[(int) (Math.random() * 4)];
        } else if (type == 3) {
            return g[(int) (Math.random() * 4)];
        } else if (type == 4) {
            return c[(int) (Math.random() * 4)];
        } else if (type == 5) {
            return i[(int) (Math.random() * 4)];
        } else if (type == 6) {
            return d[(int) (Math.random() * 4)];
        } else if (type == 7) {
            return e[(int) (Math.random() * 4)];
        } else if (type == 8) {
            return i[(int) (Math.random() * 4)];
        } else if (type == 9) {
            return a[(int) (Math.random() * 4)];
        } else if (type == 10) {
            return b[(int) (Math.random() * 4)];
        } else {
            return 0;
        }
    }

    public void randomNewProperty(ItemStack stack, boolean isFull, int level, int MainProperty) {
        int aProperty, bProperty, cProperty, dProperty, r;
        int[] f = Arrays.stream(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}).filter(i -> i != MainProperty).toArray();
        assert stack.getTagCompound() != null;
        if (level == 1) {
            aProperty = f[(int) (Math.random() * f.length)];
            stack.getTagCompound().setInteger("aProperty", aProperty);
            stack.getTagCompound().setInteger("aValue", randomValue(aProperty));
        } else if (level == 2) {
            stack.getTagCompound().setInteger("aValue", randomValue(stack.getTagCompound().getInteger("aProperty")) + randomValue(stack.getTagCompound().getInteger("aProperty")));
        } else if (level == 3) {
            r = (int) (Math.random() * 2);
            if (r == 0) {
                stack.getTagCompound().setInteger("aValue", stack.getTagCompound().getInteger("aValue") + randomValue(stack.getTagCompound().getInteger("aProperty")));
            }
            if (r == 1) {
                stack.getTagCompound().setInteger("bValue", stack.getTagCompound().getInteger("bValue") + randomValue(stack.getTagCompound().getInteger("bProperty")));
            }
        } else if (level == 4) {
            if (isFull) {
                r = (int) (Math.random() * 3);
                if (r == 0) {
                    stack.getTagCompound().setInteger("aValue", stack.getTagCompound().getInteger("aValue") + randomValue(stack.getTagCompound().getInteger("aProperty")));
                }
                if (r == 1) {
                    stack.getTagCompound().setInteger("bValue", stack.getTagCompound().getInteger("bValue") + randomValue(stack.getTagCompound().getInteger("bProperty")));
                }
                if (r == 2) {
                    stack.getTagCompound().setInteger("cValue", stack.getTagCompound().getInteger("cValue") + randomValue(stack.getTagCompound().getInteger("cProperty")));
                }
            } else {
                aProperty = stack.getTagCompound().getInteger("aProperty");
                bProperty = stack.getTagCompound().getInteger("bProperty");
                int[] temp_f = Arrays.stream(f).filter(i -> i != aProperty || i != bProperty).toArray();
                cProperty = temp_f[(int) (Math.random() * temp_f.length)];
                stack.getTagCompound().setInteger("cProperty", cProperty);
                stack.getTagCompound().setInteger("cValue", randomValue(cProperty));
            }
        } else if (level == 5) {
            if (isFull) {
                r = (int) (Math.random() * 4);
                if (r == 0) {
                    stack.getTagCompound().setInteger("aValue", stack.getTagCompound().getInteger("aValue") + randomValue(stack.getTagCompound().getInteger("aProperty")));
                }
                if (r == 1) {
                    stack.getTagCompound().setInteger("bValue", stack.getTagCompound().getInteger("bValue") + randomValue(stack.getTagCompound().getInteger("bProperty")));
                }
                if (r == 2) {
                    stack.getTagCompound().setInteger("cValue", stack.getTagCompound().getInteger("cValue") + randomValue(stack.getTagCompound().getInteger("cProperty")));
                }
                if (r == 3) {
                    stack.getTagCompound().setInteger("dValue", stack.getTagCompound().getInteger("dValue") + randomValue(stack.getTagCompound().getInteger("dProperty")));
                }
            } else {
                aProperty = stack.getTagCompound().getInteger("aProperty");
                bProperty = stack.getTagCompound().getInteger("bProperty");
                cProperty = stack.getTagCompound().getInteger("cProperty");
                int[] temp_f = Arrays.stream(f).filter(i -> i != aProperty || i != bProperty || i != cProperty).toArray();
                dProperty = temp_f[(int) (Math.random() * temp_f.length)];
                stack.getTagCompound().setInteger("dProperty", dProperty);
                stack.getTagCompound().setInteger("dValue", randomValue(dProperty));
            }
        }
    }

    public void randomDefaultProperty(int type, int level, ItemStack itemStack) {
        assert itemStack.getTagCompound() != null;
        itemStack.getTagCompound().setInteger("index", index);
        itemStack.getTagCompound().setInteger("type", type);
        itemStack.getTagCompound().setInteger("level", level);
        int mainProperty, aProperty, bProperty, cProperty, dProperty, mainValue;
        int[] s = new int[]{2, 4, 6, 7, 8};
        int[] b = new int[]{2, 4, 6, 8, 11, 12, 13, 14, 15, 16, 17, 18};
        int[] t = new int[]{2, 4, 6, 8, 9, 10};
        int[] f = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        switch (type) {
            case 1:
                mainProperty = 1;
                break;
            case 2:
                mainProperty = 3;
                break;
            case 3:
                mainProperty = s[(int) (Math.random() * 5)];
                break;
            case 4:
                mainProperty = b[(int) (Math.random() * 12)];
                break;
            case 5:
                mainProperty = t[(int) (Math.random() * 6)];
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        mainValue = 0;        //TODO:correct the value

        itemStack.getTagCompound().setInteger("mainProperty", mainProperty);
        itemStack.getTagCompound().setInteger("mainValue", mainValue);

        int[] temp;

        switch (level) {
            case 2:
                do {
                    aProperty = f[(int) (Math.random() * f.length)];
                } while (aProperty == mainProperty);
                itemStack.getTagCompound().setInteger("aProperty", aProperty);
                itemStack.getTagCompound().setInteger("aValue", randomValue(aProperty));
                break;
            case 3:
                temp = Arrays.stream(f).filter(i -> i != mainProperty).toArray();
                aProperty = temp[(int) (Math.random() * temp.length)];
                int finalAProperty = aProperty;
                int[] temp_1 = Arrays.stream(temp).filter(i -> i != finalAProperty).toArray();
                bProperty = temp_1[(int) (Math.random() * temp_1.length)];
                itemStack.getTagCompound().setInteger("aProperty", aProperty);
                itemStack.getTagCompound().setInteger("aValue", randomValue(aProperty));
                itemStack.getTagCompound().setInteger("bProperty", bProperty);
                itemStack.getTagCompound().setInteger("bValue", randomValue(bProperty));
                break;
            case 4:
                temp = Arrays.stream(f).filter(i -> i != mainProperty).toArray();
                aProperty = temp[(int) (Math.random() * temp.length)];
                int finalAProperty_4 = aProperty;
                int[] temp_1_4 = Arrays.stream(temp).filter(i -> i != finalAProperty_4).toArray();
                bProperty = temp_1_4[(int) (Math.random() * temp_1_4.length)];
                if (Math.random() > 2.0 / 3) {
                    int[] temp_2_4 = Arrays.stream(temp_1_4).filter(i -> i != bProperty).toArray();
                    cProperty = temp_2_4[(int) (Math.random() * temp_2_4.length)];
                    itemStack.getTagCompound().setInteger("cProperty", cProperty);
                    itemStack.getTagCompound().setInteger("cValue", randomValue(cProperty));
                }
                itemStack.getTagCompound().setInteger("aProperty", aProperty);
                itemStack.getTagCompound().setInteger("aValue", randomValue(aProperty));
                itemStack.getTagCompound().setInteger("bProperty", bProperty);
                itemStack.getTagCompound().setInteger("bValue", randomValue(bProperty));
                break;
            case 5:
                temp = Arrays.stream(f).filter(i -> i != mainProperty).toArray();
                aProperty = temp[(int) (Math.random() * temp.length)];
                int finalAProperty_5 = aProperty;
                int[] temp_1_5 = Arrays.stream(temp).filter(i -> i != finalAProperty_5).toArray();
                bProperty = temp_1_5[(int) (Math.random() * temp_1_5.length)];
                int finalBProperty_5 = bProperty;
                int[] temp_2_5 = Arrays.stream(temp_1_5).filter(i -> i != finalBProperty_5).toArray();
                cProperty = temp_2_5[(int) (Math.random() * temp_2_5.length)];
                if (Math.random() < 0.25) {
                    int[] temp_3_5 = Arrays.stream(temp_2_5).filter(i -> i != finalAProperty_5).toArray();
                    dProperty = temp_3_5[(int) (Math.random() * temp_3_5.length)];
                    itemStack.getTagCompound().setInteger("dProperty", dProperty);
                    itemStack.getTagCompound().setInteger("dValue", randomValue(dProperty));
                }
                itemStack.getTagCompound().setInteger("aProperty", aProperty);
                itemStack.getTagCompound().setInteger("aValue", randomValue(aProperty));
                itemStack.getTagCompound().setInteger("bProperty", bProperty);
                itemStack.getTagCompound().setInteger("bValue", randomValue(bProperty));
                itemStack.getTagCompound().setInteger("cProperty", cProperty);
                itemStack.getTagCompound().setInteger("cValue", randomValue(cProperty));
                break;
            default:
                break;
        }
    }
}