package ru.vit4liy.frontend.util;

import ru.vit4liy.perm.Permissible;

public class ClientUser implements Permissible{
    private static Permissible clientUser;

    public static Permissible get(){
        return (clientUser == null ? clientUser = new ClientUser(): clientUser);
    }

    @Override
    public String getGroupKey() {
        return "default";
    }
}
