package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.view.GameView;

public class Zombie extends BaseModel {
	private int locationX;
	private int locationY;
	private boolean isAlife;
	// 僵尸位于的跑道，因为此僵尸只跟其所在的跑道内的植物、子弹等进行碰撞检测
	private int raceWay;
	// 因为僵尸是移动中的 所以他要有动画帧的下标
	private int frameIndex = 0;
	// 移动速度，每一帧移动3像素
	private int peedX = 3;

	public Zombie(int locationX, int locationY, int raceWay) {
		this.locationX = locationX;
		this.locationY = locationY;
		isAlife = true;
		this.raceWay = raceWay;
	}

	// 在某跑道随机产生僵尸，同时间隔一段时间出现一只僵尸
	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		if (locationX < 0) {
			Config.game = false;
		}
		if (isAlife) {
			canvas.drawBitmap(Config.zombieFrames[frameIndex], locationX,
					locationY, paint);
			frameIndex = (++frameIndex) % 7;
			locationX -= peedX;
			// 碰撞检测，僵尸发起的此碰撞检测
			GameView.getInstance().checkCollision(this, raceWay);
		}

	}

	@Override
	public int getModelWidth() {
		// TODO Auto-generated method stub
		return Config.zombieFrames[0].getWidth();
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
