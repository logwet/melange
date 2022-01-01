package me.logwet.melange.commands.source;

import me.logwet.melange.commands.provider.CommandProvider.Type;

public class MelangeCommandSource implements CommandSource {
    @Override
    public String getUser() {
        return "root";
    }

    @Override
    public Role getRole() {
        return Role.NONE;
    }

    @Override
    public Type getType() {
        return Type.MELANGE;
    }

    @Override
    public void sendSuccess(String message) {}

    @Override
    public void sendError(String message) {}
}
