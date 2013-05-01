package com.example.liu.mygame.model;

import android.view.MotionEvent;

public interface TouchAble {
	// 对于能接受触摸事件的对象的一个公用接口

	// 传入MotionEvent事件
	public boolean onTouch(MotionEvent event);

}
