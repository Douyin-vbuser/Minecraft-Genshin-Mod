package com.vbuser.genshin.client.renderer.tile;

import com.vbuser.genshin.blocks.tileEntity.TileEntityBao;
import com.vbuser.genshin.client.model.tile.BaoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class TileBaoRenderer extends GeoBlockRenderer<TileEntityBao> {
    public TileBaoRenderer(){super(new BaoModel());}
}
