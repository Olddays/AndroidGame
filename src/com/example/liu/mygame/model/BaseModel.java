package com.example.liu.mygame.model;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BaseModel {
	// 基础类，对于所有能展示在屏幕上的动态对象都要继承自此类

	// 位置
	private int locationX;
	private int locationY;
	// 生命
	private boolean isAlife;

	// 绘制自己，即移动
	public void drawSelf(Canvas canvas, Paint paint) {

	}

	public int getModelWidth() {
		return 0;
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
