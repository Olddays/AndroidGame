package com.example.liu.mygame.global;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Point;

//常量
public class Config {
	public static float scaleWidth;
	public static float scaleHeight;

	public static int deviceWidth;
	public static int deviceHeight;

	public static Bitmap gameBK;
	public static Bitmap seedBank;
	
	public static Bitmap gameOver;
	
	// seedBank的位置X坐标
	public static int seedBankLocationX;

	public static Bitmap seedFlower;
	public static Bitmap seedPea;

	// 阳光
	public static Bitmap sun;
	// 阳光的生存时间5000毫秒
	public static long lifeTime = 5000;
	// 现在的阳光值
	public static int sunlight = 200;
	// 僵尸和植物图片的高度差
	public static int heightYDistance;

	// 子弹
	public static Bitmap bullet;

	// 将图片帧放入数组
	public static Bitmap[] flowerFrames = new Bitmap[8];
	public static Bitmap[] peaFrames = new Bitmap[8];
	public static Bitmap[] zombieFrames = new Bitmap[7];

	// 放置植物的点
	public static HashMap<Integer, Point> plantPoints = new HashMap<Integer, Point>();
	// 跑道
	public static int[] raceWayYpoints = new int[5];

	// 输赢判断标志
	public static boolean game = true;

}
