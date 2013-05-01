package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;

public class Bullet extends BaseModel {

	// 位置
	private int locationX;
	private int locationY;
	// 生命
	private boolean isAlife;
	// 子弹产生时间
	private long birthTime = 0l;

	// X方向上的速度分量
	// 根据帧数，确定移动时间，然后来确定移动方式
	private float SpeedX = 10;

	public Bullet(int locationX, int locationY) {
		this.locationX = locationX + 40;
		this.locationY = locationY + 20;
		this.isAlife = true;
		// 获取系统时间
		birthTime = System.currentTimeMillis();
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		if (isAlife) {
			// 移动
			locationX += SpeedX;

			// 如果图片的Y轴坐标移动到超出屏幕或者说移动到与屏幕齐平，那么生命周期结束
			if (locationX > Config.deviceWidth) {
				// 去除子弹
				isAlife = false;
			}
		}

		canvas.drawBitmap(Config.bullet, locationX, locationY, paint);

	}

	@Override
	public int getModelWidth() {
		// TODO Auto-generated method stub
		return Config.bullet.getWidth();
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public boolean isAlife() {
		return isAlife;
	}

	public void setAlife(boolean isAlife) {
		this.isAlife = isAlife;
	}

}
