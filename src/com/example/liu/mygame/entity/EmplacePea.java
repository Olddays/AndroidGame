package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.model.TouchAble;
import com.example.liu.mygame.view.GameView;

public class EmplacePea extends BaseModel implements TouchAble {

	private int locationX;
	private int locationY;
	// 生命
	private boolean isAlife;
	// 触摸区域（矩形）
	private Rect touchArea;

	public EmplacePea(int locationX, int locationY) {
		this.locationX = locationX;
		this.locationY = locationY;
		this.isAlife = true;
		// 初始化触摸响应矩形区域
		touchArea = new Rect(0, 0, Config.deviceWidth, Config.deviceHeight);
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		if (isAlife) {
			canvas.drawBitmap(Config.peaFrames[0], locationX, locationY, paint);
		}
	}

	@Override
	public boolean onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		int x = (int) event.getX();
		int y = (int) event.getY();

		// 如果点击的地方是在矩形区域内，那么开始设置跟随
		if (touchArea.contains(x, y)) {
			// 图标跟随
			// switch中需要相应三个事件：按下、抬起、拖动
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				// drawSelf方法已定，那么我们需要改变表示位置的两个变量，同时也要改变响应点击的区域touchArea
				locationX = x - Config.peaFrames[0].getWidth() / 2;
				locationY = y - Config.peaFrames[0].getHeight() / 2;
				break;
			case MotionEvent.ACTION_UP:
				// 放手以后此移动中的实例的生命周期结束并在特定点产生新的固定的实例
				isAlife = false;
				// 交由GameView处理
				GameView.getInstance().applay4Plant(locationX, locationY, this);
				break;
			}
		}
		return false;
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
