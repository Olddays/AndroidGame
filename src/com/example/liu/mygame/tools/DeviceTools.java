package com.example.liu.mygame.tools;

import com.example.liu.mygame.global.Config;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

public class DeviceTools {
	private static int[] deviceWidthHeight = new int[2];

	// 重新设置Bitmap的大小
	public static Bitmap resizeBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Log.i("info", width + "," + height);
			Matrix matrix = new Matrix();
			matrix.postScale(Config.scaleWidth, Config.scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	// 重载
	// 原因是找到的素材需要进行处理来适应手机屏幕，等比操作,但是如果合成的两张材料图不成比例 那么就不得不用这种重载来适应
	// 首先传入一个bitmap和期望的宽高
	public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			// 获取传入的图片宽高
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			// 传入期望的宽高
			int newWidth = w;
			int newHeight = h;
			// 缩放比
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// 图片矩阵对象，3X3矩阵
			Matrix matrix = new Matrix();
			// 把缩放比传入期望矩阵
			matrix.postScale(scaleWidth, scaleHeight);
			// 生成期望的图片
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizeBitmap;
		} else {
			return null;
		}
	}

	// 获取屏幕的宽高
	// 在DisplayMetrics类中可以获取屏幕的亮度，宽高，刷新率等相关信息
	public static int[] getDeviceInfo(Context context) {
		if ((deviceWidthHeight[0] == 0) && (deviceWidthHeight[1] == 0)) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			deviceWidthHeight[0] = metrics.widthPixels;
			deviceWidthHeight[1] = metrics.heightPixels;
		}
		return deviceWidthHeight;
	}

}
