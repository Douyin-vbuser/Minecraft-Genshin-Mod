package com.vbuser.genshin.client.renderer.tile;

import com.vbuser.genshin.blocks.tileEntity.TileEntityChuan;
import com.vbuser.genshin.client.model.tile.ChuanModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class TileChuanRenderer extends GeoBlockRenderer<TileEntityChuan> {
    public TileChuanRenderer(){super(new ChuanModel());}
}
