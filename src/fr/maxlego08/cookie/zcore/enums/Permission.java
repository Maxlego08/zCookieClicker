package fr.maxlego08.cookie.zcore.enums;

public enum Permission {

    ZCOOKIECLICKER_USE,
    ZCOOKIECLICKER_RELOAD,
    ZCOOKIECLICKER_SET,
    ZCOOKIECLICKER_REMOVE,
    ZCOOKIECLICKER_ADD,

    ;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    public String getPermission() {
        return permission;
    }

}
