//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.opencv.xfeatures2d;

import org.opencv.features2d.Feature2D;

public class SURF extends Feature2D {
    protected SURF(long addr) {
        super(addr);
    }

    public static SURF __fromPtr__(long addr) {
        return new SURF(addr);
    }

    public static SURF create(double hessianThreshold, int nOctaves, int nOctaveLayers, boolean extended, boolean upright) {
        SURF retVal = __fromPtr__(create_0(hessianThreshold, nOctaves, nOctaveLayers, extended, upright));
        return retVal;
    }

    public static SURF create() {
        SURF retVal = __fromPtr__(create_1());
        return retVal;
    }

    public boolean getExtended() {
        boolean retVal = getExtended_0(this.nativeObj);
        return retVal;
    }

    public boolean getUpright() {
        boolean retVal = getUpright_0(this.nativeObj);
        return retVal;
    }

    public double getHessianThreshold() {
        double retVal = getHessianThreshold_0(this.nativeObj);
        return retVal;
    }

    public int getNOctaveLayers() {
        int retVal = getNOctaveLayers_0(this.nativeObj);
        return retVal;
    }

    public int getNOctaves() {
        int retVal = getNOctaves_0(this.nativeObj);
        return retVal;
    }

    public void setExtended(boolean extended) {
        setExtended_0(this.nativeObj, extended);
    }

    public void setHessianThreshold(double hessianThreshold) {
        setHessianThreshold_0(this.nativeObj, hessianThreshold);
    }

    public void setNOctaveLayers(int nOctaveLayers) {
        setNOctaveLayers_0(this.nativeObj, nOctaveLayers);
    }

    public void setNOctaves(int nOctaves) {
        setNOctaves_0(this.nativeObj, nOctaves);
    }

    public void setUpright(boolean upright) {
        setUpright_0(this.nativeObj, upright);
    }

    protected void finalize() throws Throwable {
        delete(this.nativeObj);
    }

    private static native long create_0(double var0, int var2, int var3, boolean var4, boolean var5);

    private static native long create_1();

    private static native boolean getExtended_0(long var0);

    private static native boolean getUpright_0(long var0);

    private static native double getHessianThreshold_0(long var0);

    private static native int getNOctaveLayers_0(long var0);

    private static native int getNOctaves_0(long var0);

    private static native void setExtended_0(long var0, boolean var2);

    private static native void setHessianThreshold_0(long var0, double var2);

    private static native void setNOctaveLayers_0(long var0, int var2);

    private static native void setNOctaves_0(long var0, int var2);

    private static native void setUpright_0(long var0, boolean var2);

    private static native void delete(long var0);
}
