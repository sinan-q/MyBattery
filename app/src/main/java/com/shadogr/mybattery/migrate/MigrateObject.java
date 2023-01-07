package com.shadogr.mybattery.migrate;

public class MigrateObject {

    boolean isChecked;
    String rom;

    public MigrateObject() {
    }

    public MigrateObject(String rom, boolean isChecked) {
        this.rom = rom;
        this.isChecked = isChecked;

    }
}