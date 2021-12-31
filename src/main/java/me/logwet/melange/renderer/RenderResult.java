package me.logwet.melange.renderer;

import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import lombok.Getter;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.parallelization.SharedKernels;
import me.logwet.melange.parallelization.kernel.PrepareBufferKernel;
import me.logwet.melange.parallelization.kernel.PrepareImageKernel;
import me.logwet.melange.parallelization.kernel.RenderDivineKernel;
import me.logwet.melange.util.ArrayHelper;
import me.logwet.melange.util.StrongholdData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderResult {
    private final int strongholdCount;
    @NotNull private final ImmutableList<DivineProvider> divineProviders;

    @Nullable @Getter private StrongholdData strongholdData;

    @Nullable @Getter private double[] buffer;

    @NotNull
    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    public RenderResult(
            int strongholdCount, @NotNull ImmutableList<DivineProvider> divineProviders) {
        this.strongholdCount = strongholdCount;
        this.divineProviders = divineProviders;
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
            prepareKernel.setup(strongholdData);
            buffer = prepareKernel.render();
        }

        assert buffer != null;
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        @SuppressWarnings("SuspiciousNameCombination")
        BufferedImage image =
                new BufferedImage(
                        MelangeConstants.WIDTH,
                        MelangeConstants.WIDTH,
                        BufferedImage.TYPE_USHORT_GRAY);

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
