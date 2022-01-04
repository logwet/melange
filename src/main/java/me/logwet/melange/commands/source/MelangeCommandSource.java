package me.logwet.melange.commands.source;

import lombok.EqualsAndHashCode;
import me.logwet.melange.commands.provider.CommandProvider.Type;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MelangeCommandSource implements CommandSource {
    @EqualsAndHashCode.Include private final String username = "root";
    @EqualsAndHashCode.Include private final Role role = Role.OWNER;

    @Override
    public String getUser() {
        return username;
    }

    @Override
    public Role getRole() {
        return role;
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
