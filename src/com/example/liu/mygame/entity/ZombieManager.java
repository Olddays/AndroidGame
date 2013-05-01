package com.example.liu.mygame.entity;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.view.GameView;

public class ZombieManager extends BaseModel {
	// 一般需要显示出现在屏幕上的实体才需要继承BaseModel
	// 所以此处的僵尸控制器其实不需要继承BaseModel
	// 但是为了与之前的flower和pea产生器相统一
	// 效仿以前的模式减少工作量
	// 在这里也进行继承

	private boolean isAlife;
	// 最后一只僵尸的产生时间
	private long lastBirthTime;

	public ZombieManager() {
		lastBirthTime = System.currentTimeMillis();
		isAlife = true;
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		// 此处不需要绘制出图片，所以不需要draw，但是可以进行逻辑上的处理
		if (System.currentTimeMillis() - lastBirthTime > 15000) {
			lastBirthTime = System.currentTimeMillis();
			giveBirth2Zombie();
		}
	}

	private void giveBirth2Zombie() {
		// 与GameView请求加入僵尸
		GameView.getInstance().apply4AddZombie();
	}
}
