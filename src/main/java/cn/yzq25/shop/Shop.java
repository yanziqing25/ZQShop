package cn.yzq25.shop;

import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

public abstract class Shop {
    private int id;
    private Position position;
    private int price;
    private Item item;

    public Shop(int id, Position position, int itemId, int meta, int price, int count) {
        this.id = id;
        this.position = position;
        this.price = price;
        this.item = Item.get(itemId, meta, count);
    }

    public int getId() {
        return id;
    }

    public Position getPosition(){
        return this.position;
    }

    public int getPrice(){
        return this.price;
    }

    public Item getItem() {
        return this.item;
    }
}
