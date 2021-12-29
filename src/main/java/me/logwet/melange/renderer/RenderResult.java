package me.logwet.melange.renderer;

import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import lombok.Getter;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.consumer.StrongholdRing;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.divine.provider.feature.PlaceholderFeature;
import me.logwet.melange.util.DoubleBuffer2D;

public class RenderResult {
    private static final int COLOR_BITS = 16;
    private static final int COLOR_DEPTH = 1 << COLOR_BITS;

    private final boolean hasData = false;

    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    private BufferedImage buildImage(DoubleBuffer2D buffer) {
        @SuppressWarnings("SuspiciousNameCombination")
        final BufferedImage image =
                new BufferedImage(
                        MelangeConstants.WIDTH,
                        MelangeConstants.WIDTH,
                        BufferedImage.TYPE_USHORT_GRAY);

        buffer.normalizeInPlace(COLOR_DEPTH);

        buffer.operateOnIndices(
                i ->
                        image.getRaster()
                                .setPixel(
                                        buffer.getX(i),
                                        buffer.getXMask() - buffer.getY(i),
                                        new int[] {(int) buffer.get(i)}));
        return image;
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        ImmutableList<DivineProvider> providers = ImmutableList.of(new PlaceholderFeature());

        DoubleBuffer2D buffer = StrongholdRing.filter(providers);

        BufferedImage image = buildImage(buffer);

        long endTime = System.currentTimeMillis();

        System.out.println("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }
}
