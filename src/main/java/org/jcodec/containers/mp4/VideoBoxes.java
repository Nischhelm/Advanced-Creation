package org.jcodec.containers.mp4;

import org.jcodec.codecs.h264.mp4.AvcCBox;
import org.jcodec.containers.mp4.boxes.*;

public class VideoBoxes extends Boxes {
    public VideoBoxes() {
        mappings.put(PixelAspectExt.fourcc(), PixelAspectExt.class);
        mappings.put(AvcCBox.fourcc(), AvcCBox.class);
        mappings.put(ColorExtension.fourcc(), ColorExtension.class);
        mappings.put(GamaExtension.fourcc(), GamaExtension.class);
        mappings.put(CleanApertureExtension.fourcc(), CleanApertureExtension.class);
        mappings.put(FielExtension.fourcc(), FielExtension.class);
    }
}