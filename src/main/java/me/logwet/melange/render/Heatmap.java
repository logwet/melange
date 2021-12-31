package me.logwet.melange.render;

import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import lombok.Getter;
import me.logwet.melange.Melange;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.config.Config;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.render.kernel.PrepareBufferKernel;
import me.logwet.melange.render.kernel.PrepareImageKernel;
import me.logwet.melange.render.kernel.RenderDivineKernel;
import me.logwet.melange.render.kernel.ScaleKernel;
import me.logwet.melange.util.ArrayHelper;
import me.logwet.melange.util.StrongholdData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Heatmap {
    private final int strongholdCount;
    private final int range;

    @NotNull private final ImmutableList<DivineProvider> divineProviders;

    @Nullable @Getter private StrongholdData strongholdData;

    @Nullable @Getter private double[] buffer;

    @NotNull
    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    public Heatmap(
            int strongholdCount,
            int range,
            @NotNull ImmutableList<DivineProvider> divineProviders) {
        this.strongholdCount = strongholdCount;
        this.range = range;
        this.divineProviders = divineProviders;
    }

    public Heatmap(@NotNull ImmutableList<DivineProvider> divineProviders) {
        this(
                (Integer) Config.getProperty("stronghold_count"),
                (Integer) Config.getProperty("range"),
                divineProviders);
    }

    public Heatmap() {
        this(ImmutableList.copyOf(Melange.providerList));
    }

    public static BufferedImage newRawImage() {
        //noinspection SuspiciousNameCombination
        return new BufferedImage(
                MelangeConstants.WIDTH, MelangeConstants.WIDTH, BufferedImage.TYPE_USHORT_GRAY);
    }

    private void genBuffer() {
        RenderDivineKernel renderKernel = SharedKernels.RENDER.get();
        synchronized (SharedKernels.RENDER) {
            renderKernel.setup(divineProviders, strongholdCount);
            strongholdData = renderKernel.render();
        }

        assert strongholdData != null;

        PrepareBufferKernel prepareKernel = SharedKernels.PREPARE_BUFFER.get();
        synchronized (SharedKernels.PREPARE_BUFFER) {
            prepareKernel.setup(strongholdData, range);
            buffer = prepareKernel.render();
        }

        ScaleKernel scaleKernel = SharedKernels.SCALE.get();
        synchronized (SharedKernels.SCALE) {
            scaleKernel.setup(buffer, ArrayHelper.normaliseSumFactor(buffer));
            scaleKernel.render();
        }

        assert buffer != null;
    }

    private BufferedImage genRender() {
        //        try {
        //            TimeUnit.SECONDS.sleep(5);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }

        long startTime = System.currentTimeMillis();

        BufferedImage image = newRawImage();

        genBuffer();

        PrepareImageKernel kernel = SharedKernels.PREPARE_IMAGE.get();
        synchronized (SharedKernels.PREPARE_IMAGE) {
            assert buffer != null;
            kernel.setup(
                    buffer,
                    ((DataBufferUShort) image.getRaster().getDataBuffer()).getData(),
                    ArrayHelper.maxArray(buffer));

            kernel.render();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }
}
