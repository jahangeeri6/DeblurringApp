package cn.Ragnarok;

import android.graphics.Bitmap;

public class PixelateFilter {
	static {
		System.loadLibrary("DeblurringAPP");
	}

	public static final Bitmap changeToPixelate(Bitmap bitmap, int pixelSize) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int[] returnPixels = NativeFilterFunc.pxelateFilter(pixels, width,
				height, pixelSize);
		Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height,
				Bitmap.Config.ARGB_8888);

		return returnBitmap;

	}
}
