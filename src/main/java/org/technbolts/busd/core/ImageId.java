package org.technbolts.busd.core;

public class ImageId {

    public static ImageId imageIdOrNull(Integer raw) {
        if (raw == null)
            return null;
        return imageId(raw);
    }

    public static ImageId imageId(int raw) {
        if (raw < 0)
            throw new IllegalArgumentException("Cannot be negative");
        return new ImageId(raw);
    }

    private final int raw;

    private ImageId(int raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "ImageId{" + raw + '}';
    }

    public int raw() {
        return raw;
    }

    public String asString() {
        return String.valueOf(raw);
    }
}
