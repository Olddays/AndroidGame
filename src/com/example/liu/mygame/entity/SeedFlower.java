package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.model.TouchAble;
import com.example.liu.mygame.view.GameView;

public class SeedFlower extends BaseModel implements TouchAble {

	private int locationX;
	private int locationY;
	// 生命
	private boolean isAlife;
	// 触摸区域（矩形）
	private Rect touchArea;

	public SeedFlower(int locationX, int locationY) {
		this.locationX = locationX;
		this.locationY = locationY;
		this.isAlife = true;
		// 初始化触摸响应矩形区域
		touchArea = new Rect(locationX, locationY, locationX
				+ Config.seedFlower.getWidth(), locationY
				+ Config.seedFlower.getHeight());
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		if (isAlife) {
			canvas.drawBitmap(Config.seedFlower, locationX, locationY, paint);
		}
	}

	@Override
	public boolean onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		// 获取并传入触摸的X，Y坐标，getX() getY()获取到的数据都是float型
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (touchArea.contains(x, y)) {
			// 当触摸点落在区域内则响应
			// 生成安置状态的花（优先级最高）
			if (Config.sunlight >= 50) {
				applay4EmplaceFlower();
				return true;
			}
		}
		return false;
	}

	// 通过GameView来请求生成一个安置状态的花（优先级最高）
	private void applay4EmplaceFlower() {
		// TODO Auto-generated method stub
		GameView.getInstance().applay4EmplacePlant(locationX, locationY, this);
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
