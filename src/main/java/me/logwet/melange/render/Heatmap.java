package me.logwet.melange.render;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.util.List;
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
import me.logwet.melange.render.kernel.RenderDivineKernel;
import me.logwet.melange.util.ArrayHelper;
import me.logwet.melange.util.StrongholdData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Heatmap {
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Heatmap.class);

    @EqualsAndHashCode.Include private final int strongholdCount;
    @EqualsAndHashCode.Include private final int range;
    @EqualsAndHashCode.Include @NotNull private final List<DivineProvider> divineProviders;

    @Getter private double[] dataBuffer;

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
        StrongholdData strongholdData;

        synchronized (SharedKernels.RENDER) {
            RenderDivineKernel renderKernel = SharedKernels.RENDER.get();
            renderKernel.setup(divineProviders, strongholdCount);
            strongholdData = renderKernel.render();
        }

        assert strongholdData != null;

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

                for (int j = 0; j < strongholdCount; j++) {
                    if (factors[j] > 0) {
                        sum += strongholdData.getData(j)[i] * factors[j];
                    }
                }

                strongholdData.getData(0)[i] = sum;
            }

            buffer = strongholdData.getData(0);
        }

        ConvolveHelper.convolve(buffer, MelangeConstants.BIOME_PUSH_KERNEL);

        if (range > 0) {
            ConvolveHelper.convolve(buffer, ConvolveHelper.genRangeKernel(range));
        }

        dataBuffer = buffer;
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        BufferedImage image = newRawImage();

        genBuffer();

        assert dataBuffer != null;

        {
            short[] imgBuffer = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
            double max = ArrayHelper.maxArray(dataBuffer);
            double factor = max > 0 ? MelangeConstants.COLOR_DEPTH / max : 0D;

            for (int i = 0; i < MelangeConstants.BUFFER_SIZE; i++) {
                imgBuffer[i] = (short) (((int) (dataBuffer[i] * factor)) & 0xffff);
            }
        }

        long endTime = System.currentTimeMillis();

        LOGGER.info("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }

    @Value
    public static class ProspectiveHeatmap {
        int strongholdCount;
        int range;
        List<DivineProvider> divineProviders;
    }
}
