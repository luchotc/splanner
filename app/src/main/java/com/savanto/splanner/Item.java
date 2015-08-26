package com.savanto.splanner;


public class Item {
    public long id;
    public String text;

    public Item(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
