package me.logwet.melange.commands.commands.divine;

import ch.qos.logback.classic.Logger;
import com.mojang.brigadier.context.CommandContext;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import me.logwet.melange.Melange;
import me.logwet.melange.commands.commands.alias.AbstractAliasCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.DivineProvider;
import org.slf4j.LoggerFactory;

public abstract class AbstractDivineCommand<T extends DivineProvider> extends AbstractAliasCommand
        implements DivineCommand {
    protected final Class<T> clazz;
    protected final Logger LOGGER;

    public AbstractDivineCommand(Class<T> clazz, String root, String... aliases) {
        super(root, aliases);
        this.clazz = clazz;
        LOGGER = (Logger) LoggerFactory.getLogger(clazz);
    }

    protected Future<?> add(T provider) {
        LOGGER.info("Adding provider " + clazz.getSimpleName());
        return Melange.addProviderAndUpdateRender(provider);
    }

    protected boolean addAndWait(T provider) {
        try {
            add(provider).get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to add " + clazz.getSimpleName());
        }

        return false;
    }

    @Override
    public int remove(CommandContext<CommandSource> context) {
        boolean removed = false;

        try {
            removed = Melange.removeAllProvidersOfType(clazz).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to get provider list for removal", e);
        }

        String name = clazz.getSimpleName();

        if (removed) {
            context.getSource().sendSuccess("Removed all instances of " + name);
        } else {
            context.getSource().sendError("No instances of " + name + " to remove!");
        }

        Melange.resetHeatmapAndRender();

        return 1;
    }

    @Override
    public Type getType() {
        return Type.DIVINE;
    }
}
