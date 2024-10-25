package fr.maxlego08.cookie;

public enum CookieUpgrade {
    MANUAL_CLICK,
    GRANDMA,
    FARM,
    FACTORY,
    MINE,
    BANK,
    TEMPLE,
    TOWER,
    TIME_MACHINE,
    ANTIMATTER_CONDENSER,
    PRISM,
    CHANCELLERY,
    DIMENSIONAL_CORRIDOR,
    CELESTIAL_OVENS;

    public CookieUpgrade getPreviousUpgrade() {
        int ordinal = this.ordinal();
        return ordinal > 0 ? CookieUpgrade.values()[ordinal - 1] : null;
    }
}
