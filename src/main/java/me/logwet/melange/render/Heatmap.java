package me.logwet.melange.render;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.logwet.melange.Melange;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.config.Config;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.render.convolve.ConvolveHelper;
import me.logwet.melange.render.divine.RenderDivineKernel;
import me.logwet.melange.util.ArrayHelper;
import me.logwet.melange.util.ArrayHelper.SearchResult;
import me.logwet.melange.util.StrongholdData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Heatmap {
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Heatmap.class);

    @EqualsAndHashCode.Include private final int strongholdCount;
    @EqualsAndHashCode.Include private final int range;
    @EqualsAndHashCode.Include @NotNull private final List<DivineProvider> divineProviders;

    @Nullable @Getter private DataBuffer dataBuffer;

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
        long startTime = System.currentTimeMillis();

        StrongholdData strongholdData;

        synchronized (SharedKernels.RENDER) {
            RenderDivineKernel renderKernel = SharedKernels.RENDER.get();
            renderKernel.setup(divineProviders, strongholdCount);
            strongholdData = renderKernel.render();
        }

        double[] buffer;

        {
            double[] factors =
                    new double[] {
                        strongholdData.getSumFactor(0),
                        strongholdData.getSumFactor(1),
                        strongholdData.getSumFactor(2)
                    };

            for (int i = 0; i < MelangeConstants.BUFFER_SIZE; i++) {
                double sum = 0D;
                int divisor = 0;

                for (int j = 0; j < strongholdCount; j++) {
                    if (factors[j] > 0) {
                        sum += strongholdData.getData(j)[i] * factors[j];
                        divisor++;
                    }
                }

                if (divisor > 0) {
                    strongholdData.getData(0)[i] = sum / divisor;
                } else {
                    strongholdData.getData(0)[i] = 0D;
                }
            }

            buffer = strongholdData.getData(0);
        }

        long endTime = System.currentTimeMillis();
        LOGGER.info("Buffer generation took " + (endTime - startTime) + "ms");

        buffer = ConvolveHelper.convolve(buffer, MelangeConstants.BIOME_PUSH_KERNEL);

        if (range > 0) {
            buffer = ConvolveHelper.convolve(buffer, ConvolveHelper.genRangeKernel(range));
        }

        dataBuffer = new DataBuffer(buffer);
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        BufferedImage image = newRawImage();

        genBuffer();

        assert dataBuffer != null;

        {
            short[] imgBuffer = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();

            dataBuffer.setMaxResult(ArrayHelper.maxAndIndexArray(dataBuffer.getBuffer()));

            double max = dataBuffer.getMaxResult().getValue();
            double factor = max > 0 ? MelangeConstants.COLOR_DEPTH / max : 0D;
            double[] buffer = dataBuffer.getBuffer();

            for (int i = 0; i < MelangeConstants.BUFFER_SIZE; i++) {
                imgBuffer[i] = (short) (((int) (buffer[i] * factor)) & 0xffff);
            }
        }

        long endTime = System.currentTimeMillis();

        dataBuffer.setRenderTime(endTime - startTime);

        LOGGER.info("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }

    @Value
    public static class ProspectiveHeatmap {
        int strongholdCount;
        int range;
        List<DivineProvider> divineProviders;
    }

    @Data
    public static class DataBuffer {
        final double[] buffer;
        SearchResult maxResult;
        Long renderTime;
    }
}
