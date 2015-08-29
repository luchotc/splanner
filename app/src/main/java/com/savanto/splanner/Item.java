package com.savanto.splanner;


public final class Item {
    public final long id;
    public final long time;
    public final String text;

    public Item(long id, String text) {
        this(id, 0, text);
    }

    public Item(long id, long time, String text) {
        this.id = id;
        this.time = time;
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
