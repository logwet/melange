package me.logwet.melange;

import com.formdev.flatlaf.FlatDarculaLaf;
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
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.config.Config;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.gui.MelangeFrame;
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
    private static final AtomicBoolean HAS_SHUTDOWN = new AtomicBoolean(false);
    @Getter private static final List<DivineProvider> providerList = new ArrayList<>();

    @Getter @Setter @Nullable private static Heatmap heatmap;

    @Getter @Nullable private static MelangeFrame mainFrame;

    static {
        String version = Melange.class.getPackage().getImplementationVersion();

        VERSION = Objects.nonNull(version) ? version : "DEVELOPMENT";
    }

    public static void launch() {
        Melange.onStarting();

        FlatDarculaLaf.setup();
        mainFrame = new MelangeFrame();
        mainFrame.setVisible(true);
    }

    public static void onStarting() {
        SharedKernels.forceLoad();
        execute(Config::load);
        resetCommandManager();
    }

    public static void onClosing() {
        try {
            synchronized (HAS_SHUTDOWN) {
                if (!HAS_SHUTDOWN.get()) {
                    updateConfig();
                    EXECUTOR.shutdown();
                    HAS_SHUTDOWN.set(true);
                }
            }
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

    public static Future<?> resetCommandManager() {
        return submit(CommandManager::initialize);
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

        LOGGER.error("MelangeFrame object is null");
        return CompletableFuture.completedFuture(null);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Future<?> resetHeatmapWithoutRender() {
        return resetHeatmapAsync(Runnables.doNothing());
    }

    public static void addProvider(DivineProvider provider) {
        execute(() -> providerList.add(provider));
    }

    public static Future<?> addProviderAndUpdateRender(DivineProvider provider) {
        addProvider(provider);
        return Melange.resetHeatmapAndRender();
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
