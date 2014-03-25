package com.aisidi.kuaiyue.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapTools {

	public static Bitmap scaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight)
	{
//		int srcWidth = bitmap.getWidth();
//		
//		float scaleWidth = ((float)dstWidth)/srcWidth;
//		float scaleHeight = ((float)dstHeight)/srcHeight;
//		
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		
//		Bitmap dstBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
		
		Bitmap dstBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
		if (dstBitmap!=bitmap) {
			bitmap.recycle();
			bitmap = dstBitmap;
		}
		return bitmap;
	}
}
