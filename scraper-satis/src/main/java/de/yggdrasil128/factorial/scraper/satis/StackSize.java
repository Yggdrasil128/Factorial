package de.yggdrasil128.factorial.scraper.satis;

public enum StackSize {

    SS_ONE(1), SS_SMALL(50), SS_MEDIUM(100), SS_BIG(200), SS_HUGE(500), SS_FLUID(50);

    private final int amount;

    private StackSize(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

}
