package org.osgl.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ImgTest {

    private static InputStream img1() {
        URL url = ImgTest.class.getResource("/img/img1.png");
        return IO.is(url);
    }

    private static InputStream img2() {
        URL url = ImgTest.class.getResource("/img/img2.jpg");
        return IO.is(url);
    }

    private static InputStream img3() {
        return IO.is(ImgTest.class.getResource("/img/img3.png"));
    }

    static void testCrop() {
        Img.source(img1()).crop(N.xy(30, 30), N.xy(100, 100)).writeTo(new File("/tmp/img1_crop.gif"));
    }

    static void testResize() {
        Img.source(img1()).resize(N.xy(100, 200)).writeTo(new File("/tmp/img1_resize.png"));
    }

    static void testResizeKeepRatio() {
        Img.source(img1()).resize(100, 200).keepRatio().writeTo(new File("/tmp/img1_resize_keep_ratio.png"));
    }

    private static void testIllegalArguments() {
        try {
            Img.source(img2()).resize(0.0f).writeTo("/tmp/img2_resize_zero.png");
            E.unexpected("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }

    static void testWatermarkWithDefSetting() {
        Img.source(img1()).watermark("CONFIDENTIAL").writeTo("/tmp/img1_watermark_def.png");
    }

    static void testWatermark() {
        Img.source(img1()).watermark("CONFIDENTIAL").offsetY(-200).color(Color.DARK_GRAY).writeTo("/tmp/img1_watermark.png");
    }

    private static void testCompress() {
        Img.source(img1()).compress(0.01f).writeTo("/tmp/img1_compress.jpeg");
    }

    private static void testCopy() {
        Img.source(img1()).copy().writeTo("/tmp/img1_copy.jpeg");
    }

    private static void testPipeline() {
        Img.source(img1())
                .resize(300, 400)
                .pipeline()
                .crop(50, 50, 250, 350)
                .pipeline()
                .watermark("HELLO OSGL")
                .writeTo("/tmp/img1_pipeline.png");
    }


    private static void testResizeByScale() {
        Img.source(img2()).resize(0.5f).writeTo("/tmp/img2_resize_scale.png");
    }

    private static void testProcessJPEGfile() {
        Img.source(img2())
                .resize(640, 480)
                .pipeline()
                .crop(50, 50, -50, -50)
                .pipeline()
                .watermark("HELLO OSGL")
                .writeTo("/tmp/img2_pipeline.jpg");
    }

    private static void testGenerateTrackingPixel() {
        IO.write(Img.TRACKING_PIXEL_BYTES, new File("/tmp/tracking_pixel.gif"));
    }

    private static void testFlip() {
        Img.source(img1()).flip().writeTo("/tmp/img1_flip_h.png");
        Img.source(img1()).flipVertial().writeTo("/tmp/img1_flip_v.png");
    }

    private static class Sunglass extends Img.Processor {
        private float alpha = 0.3f;

        Sunglass() {}
        Sunglass(float alpha) {this.alpha = alpha;}

        @Override
        protected BufferedImage run() {
            int w = sourceWidth;
            int h = sourceHeight;
            Graphics2D g = g();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.drawImage(source, 0, 0, w, h, null);
            return target;
        }
    }
    private static void testCustomizedProcessor() {
        // style A
        new Sunglass(0.7f).process(img2()).pipeline().resize(0.5f).writeTo("/tmp/img2_sunglass_style_a.jpg");
        // style B
        Img.source(img2()).resize(0.3f).transform(new Sunglass()).writeTo("/tmp/img2_sunglass_style_b.png");
    }

    private static void randomPixels() {
        Img.source(Img.F.randomPixels(400, 200)).blur().writeTo("/tmp/img_random_pixels.png");
    }

    private static void testBlur() {
        Img.source(img1()).blur().writeTo("/tmp/img1_blur_default.png");
        Img.source(img2()).blur(10).writeTo("/tmp/img2_blur_10.jpg");
        Img.source(img2()).blur(2).writeTo("/tmp/img2_blur_2.jpg");
        Img.source(img3()).blur(5).writeTo("/tmp/img3_blur_5.jpg");
    }

    private static void testConcatenate() {
        Img.source(img2()).appendWith(Img.source(img3())).writeTo("/tmp/img_concat_2_3.png");
        Img.source(img2()).appendTo(Img.source(img3())).writeTo("/tmp/img_concat_3_2.png");

        Img.source(img2()).appendWith(Img.source(img1()))
                .noScaleFix()
                .vertically()
                .writeTo("/tmp/img_concat_2_1.png");

        Img.source(img3()).appendWith(Img.source(img1()))
                .shinkToSmall()
                .writeTo("/tmp/img_concat_3_1.png");
    }

    public static void main(String[] args) {
        testConcatenate();
        testResize();
        testResizeByScale();
        testResizeKeepRatio();
        testCrop();
        testWatermarkWithDefSetting();
        testWatermark();
        testCompress();
        testCopy();
        testPipeline();
        testProcessJPEGfile();
        testGenerateTrackingPixel();
        testCustomizedProcessor();
        testIllegalArguments();
        testBlur();
        testFlip();
        randomPixels();
    }

}