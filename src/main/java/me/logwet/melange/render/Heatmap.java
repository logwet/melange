package me.logwet.melange.render;

import ch.qos.logback.classic.Logger;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import me.logwet.melange.util.BufferHolder;
import me.logwet.melange.util.StrongholdData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class Heatmap {
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Heatmap.class);

    private final int strongholdCount;
    private final int range;

    @NotNull private final ImmutableList<DivineProvider> divineProviders;

    @Nullable @Getter private StrongholdData strongholdData;

    @Nullable @Getter private BufferHolder bufferHolder;

    @NotNull
    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    public Heatmap(@NotNull ImmutableList<DivineProvider> divineProviders) {
        this(
                (Integer) Config.getProperty("stronghold_count"),
                (Integer) Config.getProperty("range"),
                divineProviders);
    }

    public Heatmap() {
        this(ImmutableList.copyOf(Melange.getProviderList()));
    }

    public static BufferedImage newRawImage() {
        //noinspection SuspiciousNameCombination
        return new BufferedImage(
                MelangeConstants.WIDTH, MelangeConstants.WIDTH, BufferedImage.TYPE_USHORT_GRAY);
    }

    private void genBuffer() {
        synchronized (SharedKernels.RENDER) {
            RenderDivineKernel renderKernel = SharedKernels.RENDER.get();
            renderKernel.setup(divineProviders, strongholdCount);
            strongholdData = renderKernel.render();
        }

        assert strongholdData != null;

        synchronized (SharedKernels.PREPARE_BUFFER) {
            PrepareBufferKernel prepareKernel = SharedKernels.PREPARE_BUFFER.get();
            prepareKernel.setup(strongholdData, range);
            bufferHolder = new BufferHolder(prepareKernel.render(), false, true);
        }

        synchronized (SharedKernels.SCALE) {
            ScaleKernel scaleKernel = SharedKernels.SCALE.get();
            scaleKernel.setup(
                    bufferHolder.getBuffer(),
                    ArrayHelper.normaliseSumFactor(bufferHolder.getSum()));
            scaleKernel.render();
        }

        bufferHolder = bufferHolder.update(true, false);
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

        assert bufferHolder != null;

        synchronized (SharedKernels.PREPARE_IMAGE) {
            PrepareImageKernel kernel = SharedKernels.PREPARE_IMAGE.get();
            kernel.setup(
                    bufferHolder, ((DataBufferUShort) image.getRaster().getDataBuffer()).getData());
            kernel.render();
        }

        long endTime = System.currentTimeMillis();

        LOGGER.info("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Heatmap heatmap = (Heatmap) o;
        return strongholdCount == heatmap.strongholdCount
                && range == heatmap.range
                && Objects.equal(divineProviders, heatmap.divineProviders);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(strongholdCount, range, divineProviders);
    }
}
