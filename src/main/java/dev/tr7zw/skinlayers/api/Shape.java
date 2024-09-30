package dev.tr7zw.skinlayers.api;

import dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper.Dimensions;

public record Shape(float yOffsetMagicValue, Dimensions dimensions) {
    public static final Shape HEAD = new Shape(0.0f, new Dimensions(8, 8, 8));
    public static final Shape BODY = new Shape(-0.2f, new Dimensions(8, 12, 4));
    public static final Shape LEGS = new Shape(-0.2f, new Dimensions(4, 14, 4));
    public static final Shape ARMS = new Shape(-0.1f, new Dimensions(4, 14, 4));
    public static final Shape ARMS_SLIM = new Shape(-0.1f, new Dimensions(3, 14, 4));
}
