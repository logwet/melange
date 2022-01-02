package me.logwet.melange;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Runnables;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.config.Config;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.gui.MainFrame;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.render.Heatmap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Melange {
    public static final String VERSION;
    public static final Dimension MIN_WINDOW_DIMENSION = new Dimension(600, 400);
    public static final Dimension WINDOW_DIMENSION = new Dimension(800, 600);
    public static final Logger LOGGER = LoggerFactory.getLogger("melange");

    public static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "melange"));

    @Getter private static final List<DivineProvider> providerList = new ArrayList<>();

    @Getter @Setter @Nullable private static Heatmap heatmap;

    @Getter @Nullable private static MainFrame mainFrame;

    static {
        String version = Melange.class.getPackage().getImplementationVersion();

        VERSION = Objects.nonNull(version) ? version : "DEVELOPMENT";
    }

    public static void launch() {
        Melange.onStarting();

        FlatIntelliJLaf.setup();
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void onStarting() {
        SharedKernels.forceLoad();
        execute(Config::load);
        execute(CommandManager::initialize);
    }

    public static void onClosing() {
        try {
            updateConfig();
            EXECUTOR.shutdown();
        } catch (Exception e) {
            LOGGER.error("Unable to shut Melange down properly.", e);
        }
    }

    public static void updateConfig() {
        execute(Config::save);
    }

    public static void execute(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    public static <V> Future<V> submit(Callable<V> callable) {
        return EXECUTOR.submit(callable);
    }

    public static Future<?> submit(Runnable runnable) {
        return EXECUTOR.submit(runnable);
    }

    public static <V> List<Future<V>> invokeAll(Collection<Callable<V>> callables)
            throws InterruptedException {
        return EXECUTOR.invokeAll(callables);
    }

    public static void resetHeatmap(Runnable runnable) {
        setHeatmap(new Heatmap());
        runnable.run();
        LOGGER.info("Reset heatmap");
    }

    public static Future<?> resetHeatmapAsync(Runnable runnable) {
        return submit(() -> resetHeatmap(runnable));
    }

    public static Future<?> resetHeatmapAndRender() {
        if (Objects.nonNull(mainFrame)) {
            return resetHeatmapAsync(mainFrame::updateRender);
        }

        LOGGER.error("MainFrame object is null");
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Future<?> resetHeatmapWithoutRender() {
        return resetHeatmapAsync(Runnables.doNothing());
    }

    public static void addProvider(DivineProvider provider) {
        execute(() -> providerList.add(provider));
    }

    public static Future<?> removeProvider(DivineProvider provider) {
        return submit(() -> Melange.providerList.remove(provider));
    }

    public static Future<Boolean> removeAllProvidersOfType(Class<? extends DivineProvider> clazz) {
        return submit(
                () -> {
                    boolean r = false;
                    for (DivineProvider provider : ImmutableList.copyOf(Melange.providerList)) {
                        if (clazz.isInstance(provider)) {
                            Melange.providerList.remove(provider);
                            r = true;
                        }
                    }
                    return r;
                });
    }

    public static Future<?> removeAllProviders() {
        return submit(Melange.providerList::clear);
    }
}
