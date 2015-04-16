package cn.Ragnarok;

import android.graphics.Bitmap;

public class ReliefFilter {

	static {
		System.loadLibrary("DeblurringAPP");
	}

	public static Bitmap changeToRelief(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int[] returnPixels = NativeFilterFunc.reliefFilter(pixels, width,
				height);
		Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height,
				Bitmap.Config.ARGB_8888);

		// set the saturation
		// Canvas c = new Canvas(returnBitmap);
		// Paint paint = new Paint();
		// ColorMatrix cm = new ColorMatrix();
		// cm.setSaturation(0);
		// ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		// paint.setColorFilter(f);
		// c.drawBitmap(returnBitmap, 0, 0, paint);

		return returnBitmap;
	}

}
