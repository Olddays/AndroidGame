package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.model.TouchAble;

public class Sun extends BaseModel implements TouchAble {

	// 位置
	private int locationX;
	private int locationY;
	// 生命
	private boolean isAlife;
	// 可触摸区域
	private Rect touchArea;
	// 阳光产生时间
	private long birthTime;
	// 标示阳光的状态
	private SunState state;

	// 移动距离
	private int DirectionDistanceX;
	private int DirectionDistanceY;

	// XY方向上的速度分量
	// 根据帧数，确定移动时间，然后来确定移动方式
	private float SpeedX;
	private float SpeedY;

	// 用此枚举来标示阳光的状态
	// 两个状态：静止、移动
	// 移动过程中生命周期对阳光无效，静止时生命周期有效
	public enum SunState {
		SHOW, MOVE
	}

	public Sun(int locationX, int locationY) {
		this.locationX = locationX;
		this.locationY = locationY;
		this.isAlife = true;
		// 初始化触摸响应矩形区域
		// 对于每个阳光来说能出没的地方只有他这张图片大小的区域
		touchArea = new Rect(locationX, locationY, locationX
				+ Config.sun.getWidth(), locationY + Config.sun.getHeight());
		// 获取系统时间
		birthTime = System.currentTimeMillis();
		// 初始实例化为SHOW状态
		state = SunState.SHOW;
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		if (isAlife) {

			if (state == SunState.SHOW) {
				// 判断当前系统时间如果比出生时间大5000毫秒那么阳光生命结束，消失
				if (System.currentTimeMillis() - birthTime > Config.lifeTime) {
					isAlife = false;
				}
			} else {// 对于move状态的阳光的处理
				// 移动
				locationX -= SpeedX;
				locationY -= SpeedY;
				// 如果图片的Y轴坐标移动到超出屏幕或者说移动到与屏幕齐平，那么生命周期结束
				if (locationY <= 0) {
					// 去除阳光
					isAlife = false;
					// 改变阳光值
					Config.sunlight += 25;
				}
			}

			canvas.drawBitmap(Config.sun, locationX, locationY, paint);

		}
	}

	// 触摸事件响应
	@Override
	public boolean onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		// 获取触摸点
		int x = (int) event.getX();
		int y = (int) event.getY();
		// 如果触摸点在可触摸区域内
		if (touchArea.contains(x, y)) {
			// 开始运动并且不可被点击，同时可能会与上边框产生碰撞事件
			// 移动过程中也需要时间，如果这个收集时间中用了超过阳光生命值5秒的时间
			// 那么我们需要在点击以后改变阳光的状态并删除原本的静态阳光
			state = SunState.MOVE;
			// 改变状态以后，那么就要开始移动，移动的起点不一定，但是终点是一定的
			// 移动的终点可以认为是条形框（seedBank）的左上角点
			// 起始点就是此阳光图片的左上角
			// XY方向上的移动距离
			DirectionDistanceX = locationX - Config.seedBankLocationX;
			DirectionDistanceY = locationY;
			// 移动速度分量的计算，具体帧数需要项目分析，这里设置为20帧
			SpeedX = DirectionDistanceX / 20f;
			SpeedY = DirectionDistanceY / 20f;
			return true;
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
