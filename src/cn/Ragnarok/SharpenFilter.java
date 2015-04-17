package cn.Ragnarok;

import android.graphics.Bitmap;

public class SharpenFilter {

	static {
		System.loadLibrary("DeblurringAPP");
	}

	public static Bitmap changeToSharpen(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int[] returnPixels = NativeFilterFunc.sharpenFilter(pixels, width,
				height);
		Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height,
				Bitmap.Config.ARGB_8888);
		return returnBitmap;
	}
}
