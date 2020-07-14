package cn.yzq25.shop;

import cn.nukkit.level.Position;

public class PublicShop extends Shop {

    private PublicShop(int id, Position position, int itemId, int meta, int price, int count) {
        super(id, position, itemId, meta, price, count);
    }

    public PublicShop(int id, int x, int y, int z, String world, int itemId, int meta, int price, int count) {
        this(id, new Position(x, y, z, ShopMain.getInstance().getServer().getLevelByName(world)), itemId, meta, price, count);
    }
}
