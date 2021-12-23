package me.logwet.melange.renderer;

import java.awt.image.BufferedImage;
import lombok.Getter;

public class RenderResult {
    private final boolean hasData = false;

    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    private BufferedImage genRender() {
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_USHORT_GRAY);

        if (hasData) {}

        return image;
    }
}
