package com.example.liu.mygame.view;

import java.util.ArrayList;

import com.example.liu.mygame.R;
import com.example.liu.mygame.entity.*;
import com.example.liu.mygame.global.Config;
import com.example.liu.mygame.model.BaseModel;
import com.example.liu.mygame.model.Plant;
import com.example.liu.mygame.model.TouchAble;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//对于这种有赛道的游戏可以用以下方法
//对于那种可以上下移动的游戏可以把所有精灵放在一个集合里 不用分图层 每次绘制的时候要按照Y坐标进行排序
//对于先画出来的肯定是排在前面的 所以Y最小的排在前面即离屏幕上边缘最近的精灵
//那种游戏肯定会通过游戏引擎来进行开发
public class GameView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	private Canvas canvas;
	private Paint paint;
	private SurfaceHolder surfaceHolder;
	private boolean gameRunFlag;
	private Context context;// 用于存放图片地址

	// 把GameView当做总管理，所有的实体都向这里发送请求并处理
	private static GameView gameView;

	private ArrayList<BaseModel> deadList;// 存放已消亡的实体,在拖动放手后实体会不显示，但是还存在所以要进行清理
	private ArrayList<BaseModel> gameLayout3;// 存放第三图层中的实体；
	private ArrayList<BaseModel> gameLayout2;// 存放第二图层中的实体
	private ArrayList<BaseModel> gameLayout1;// 存放第一图层中的实体

	// 跑道从上至下
	// 这些可以做一个封装，放入一个for循环进行创建即可
	private ArrayList<BaseModel> gameLayout4plant0;
	private ArrayList<BaseModel> gameLayout4plant1;
	private ArrayList<BaseModel> gameLayout4plant2;
	private ArrayList<BaseModel> gameLayout4plant3;
	private ArrayList<BaseModel> gameLayout4plant4;

	// 定义僵尸跑道
	private ArrayList<BaseModel> gamelayout4zombie0;
	private ArrayList<BaseModel> gamelayout4zombie1;
	private ArrayList<BaseModel> gamelayout4zombie2;
	private ArrayList<BaseModel> gamelayout4zombie3;
	private ArrayList<BaseModel> gamelayout4zombie4;

	// 定义僵尸控制器，通过此控制器来使僵尸实现移动
	private ZombieManager zombieManager;

	public GameView(Context context) {
		super(context);
		// TODO GameView
		this.context = context;
		paint = new Paint();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		gameRunFlag = true;

		gameView = this;

		if (Config.game == false) {
			canvas.drawBitmap(Config.gameOver, 0, 0, paint);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO surfaceCreated
		// 加载bitmap（图片）
		createElement();
		new Thread(this).start();

	}

	private void createElement() {
		// TODO createElement
		// 给植物与僵尸的高度差赋值
		Config.heightYDistance = Config.zombieFrames[0].getHeight()
				- Config.flowerFrames[0].getHeight();
		// 给seedBank的X坐标赋初值
		Config.seedBankLocationX = (Config.deviceWidth - Config.seedBank
				.getWidth()) / 2;
		// 初始化第三图层
		gameLayout3 = new ArrayList<BaseModel>();

		// 当此方法被触发时便会创建卡片对象
		gameLayout2 = new ArrayList<BaseModel>();
		SeedFlower seedFlower = new SeedFlower(
				(Config.deviceWidth - Config.seedBank.getWidth()) / 2
						+ Config.seedFlower.getWidth() / 3
						+ Config.seedBank.getWidth() / 7,
				Config.seedBank.getHeight() / 10);
		SeedPea seedPea = new SeedPea(
				(Config.deviceWidth - Config.seedBank.getWidth()) / 2
						+ Config.seedFlower.getWidth() / 7
						+ Config.seedBank.getWidth() / 7 * 2,
				Config.seedBank.getHeight() / 10);

		gameLayout2.add(seedFlower);
		gameLayout2.add(seedPea);

		// 添加安置状态中的植物
		gameLayout1 = new ArrayList<BaseModel>();
		deadList = new ArrayList<BaseModel>();

		gameLayout4plant0 = new ArrayList<BaseModel>();
		gameLayout4plant1 = new ArrayList<BaseModel>();
		gameLayout4plant2 = new ArrayList<BaseModel>();
		gameLayout4plant3 = new ArrayList<BaseModel>();
		gameLayout4plant4 = new ArrayList<BaseModel>();

		// 僵尸跑道初始化
		gamelayout4zombie0 = new ArrayList<BaseModel>();
		gamelayout4zombie1 = new ArrayList<BaseModel>();
		gamelayout4zombie2 = new ArrayList<BaseModel>();
		gamelayout4zombie3 = new ArrayList<BaseModel>();
		gamelayout4zombie4 = new ArrayList<BaseModel>();

		// 初始化僵尸控制器
		zombieManager = new ZombieManager();

		// 放置植物的合适位置
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 9; j++) {
				Config.plantPoints.put(i * 10 + j, new Point(
						(j + 2) * Config.deviceWidth / 11 - Config.deviceWidth
								/ 11 / 2, (i + 1) * Config.deviceHeight / 6));
				if (j == 0) {
					Config.raceWayYpoints[i] = (i + 1) * Config.deviceHeight
							/ 6;
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO surfaceChanged

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO surfaceDestroyed

	}

	// 所有的动画帧都由这个run方法来控制
	// 控制动画帧的时候要注意首先进行数据更新 然后在更新图像
	@Override
	public void run() {
		// TODO run
		while (gameRunFlag) {
			synchronized (surfaceHolder) {
				try {
					// 为了形成动画效果首先需要清理屏幕
					// 加锁避免很多线程同时绘制
					canvas = surfaceHolder.lockCanvas();
					// 绘入背景，最底层图层
					canvas.drawBitmap(Config.gameBK, 0, 0, paint);
					// 绘入上方植物栏，倒数第二层图层，仅覆盖于背景之上
					canvas.drawBitmap(Config.seedBank,
							Config.seedBankLocationX, 0, paint);
					// 数据更改操作
					updateData();
					// 绘入植物卡片（第二层）
					ondraw(canvas);
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					// 解锁并提交
					surfaceHolder.unlockCanvasAndPost(canvas);
					// CanvasAndPost必须要进行解锁 不管程序有什么问题必须给用户直观完整的显示过程
					// 以防万一的话 加入try catch
				}
			}
			// 加入以下语句每次循环中休眠50毫秒减少一直循环的系统资源浪费
			// 使用50毫秒的原因是在42帧及以上肉眼就会认为是流畅的，即1秒42张图片，每次循环休眠50毫秒即20帧
			// 如果把sleep放在synchronized中的话会出现程序每次遍历完立刻睡眠然后再次遍历没有给其他进程事件运行会造成卡死
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void updateData() {
		// 在此方法中进行数据更新
		// 清除deadList
		deadList.clear();
		// 遍历第一图层
		for (BaseModel model : gameLayout1) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		// 遍历第二图层
		for (BaseModel model : gameLayout2) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		// 遍历第三图层
		for (BaseModel model : gameLayout3) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}

		// 遍历五条跑道上的僵尸
		for (BaseModel model : gamelayout4zombie0) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gamelayout4zombie1) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gamelayout4zombie2) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gamelayout4zombie3) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gamelayout4zombie4) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		// 遍历五条跑道上的植物
		for (BaseModel model : gameLayout4plant0) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gameLayout4plant1) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gameLayout4plant2) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gameLayout4plant3) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		for (BaseModel model : gameLayout4plant4) {
			if (!model.isAlife()) {
				deadList.add(model);
			}
		}
		// 遍历deadList集合
		for (BaseModel model : deadList) {
			// 在各个图层列表中把它们移除
			gameLayout1.remove(model);
			gameLayout2.remove(model);
			gameLayout3.remove(model);
			gamelayout4zombie0.remove(model);
			gamelayout4zombie1.remove(model);
			gamelayout4zombie2.remove(model);
			gamelayout4zombie3.remove(model);
			gamelayout4zombie4.remove(model);
		}
	}

	private void ondraw(Canvas canvas) {
		// TODO ondraw
		// 在此方法中进行绘图作业
		// 按照游戏的层次进行绘制，先画游戏层次最下方的精灵
		// 按照已经写好的分层顺序

		// 绘制出阳光值
		Paint paint2 = new Paint();
		paint2.setTypeface(Typeface.DEFAULT_BOLD);
		paint2.setTextSize(15);
		canvas.drawText(Config.sunlight + "", Config.deviceWidth * 2 / 7,
				Config.deviceHeight / 8, paint2);

		// 僵尸控制器中的drawSelf实现僵尸移动
		zombieManager.drawSelf(canvas, paint);

		// 跑道应该处于第四层故放在上方先绘制出来
		// 遍历五条跑道调用drawSelf方法进行绘制植物
		// 此处也可以进行方法的抽象 或者说应该把这些重复的代码抽象为一个方法调用不同的值进去
		for (BaseModel model : gameLayout4plant0) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gameLayout4plant1) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gameLayout4plant2) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gameLayout4plant3) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gameLayout4plant4) {
			model.drawSelf(canvas, paint);
		}

		// 第三层（阳光）
		for (BaseModel model : gameLayout3) {
			model.drawSelf(canvas, paint);
		}

		// 第二层
		for (BaseModel model : gameLayout2) {
			model.drawSelf(canvas, paint);
		}

		// 遍历五条跑道调用drawSelf方法进行绘制僵尸
		// 此处也可以进行方法的抽象 或者说应该把这些重复的代码抽象为一个方法调用不同的值进去
		// 第二层是植物卡片，僵尸在经过第一行的时候应该可以挡住植物卡片
		for (BaseModel model : gamelayout4zombie0) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gamelayout4zombie1) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gamelayout4zombie2) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gamelayout4zombie3) {
			model.drawSelf(canvas, paint);
		}
		for (BaseModel model : gamelayout4zombie4) {
			model.drawSelf(canvas, paint);
		}

		// 第一层
		// gameLayout1比gameLayout2的层次要高故放在后面
		for (BaseModel model : gameLayout1) {
			model.drawSelf(canvas, paint);
		}

		/*
		 * private m=200; Paint paint3 = new Paint(); paint3.setAlpha(100);
		 * canvas.drawRect(100, 100, 200, m, paint3); m-=5; 设置半透明效果
		 * m的作用是可以让这个半透明效果逐步消去， m的变化大小就可以理解为此植物的冷却时间
		 */
	}

	// 在这里重写触摸响应事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO onTouchEvent
		// 对于相应来说gameLayout1的优先级最高故放在gameLayout2上方
		for (BaseModel model : gameLayout1) {
			// 判定是否为touchAble的子类，只有是touchAble的子类才能响应
			if (model instanceof TouchAble) {
				// 然后进行onTouch事件，查看是否被点击，如果点击那么返回true
				if (((TouchAble) model).onTouch(event)) {
					return true;
				}
			}
		}
		// 遍历第二层中的全部实体
		for (BaseModel model : gameLayout2) {
			// 判定是否为touchAble的子类，只有是touchAble的子类才能响应
			if (model instanceof TouchAble) {
				// 然后进行onTouch事件，查看是否被点击，如果点击那么返回true
				if (((TouchAble) model).onTouch(event)) {
					return true;
				}
			}
		}
		// 遍历第三层中的全部实体
		for (BaseModel model : gameLayout3) {
			// 判定是否为touchAble的子类，只有是touchAble的子类才能响应
			if (model instanceof TouchAble) {
				// 然后进行onTouch事件，查看是否被点击，如果点击那么返回true
				if (((TouchAble) model).onTouch(event)) {
					return true;
				}
			}
		}
		return false;
	}

	// 获取GameView的方法，让GameView编程所有实体的总桥梁
	public static GameView getInstance() {
		return gameView;
	}

	// 添加EmplacePlant植物（优先级最高）
	public void applay4EmplacePlant(int locationX, int locationY,
			BaseModel model) {
		// TODO applay4EmplacePlant
		// 相当于在进行数据更新，在onDraw中会从这里取出一个个元素进行绘制，绘制过程中如果这里还会有更新那么会产生冲突，所以需要在这里加入一个同步锁
		// 所有对于集合的操作都要加入同步锁，锁对象用surfaceHolder当得到此surfaceHolder锁对象的时候才能够进行操作
		synchronized (surfaceHolder) {
			// gameLayout1放的是正在安放状态的植物，没有放下
			// new一个处于安置状态的实体
			// gameLayout1中只能有0~1个实例
			if (gameLayout1.size() < 1) {
				if (model instanceof SeedPea) {
					gameLayout1.add(new EmplacePea(locationX, locationY));
				} else {
					gameLayout1.add(new EmplaceFlower(locationX, locationY));
				}
			}
		}

	}

	public void applay4Plant(int locationX, int locationY, BaseModel baseModel) {
		// TODO applay4Plant
		// 安放静态植物
		// 以空间换时间，因为植物、子弹、僵尸、都在第四层所以对于这些来说把它们分为五个赛道从上至下五层
		// 每条赛道上有两个集合，一个是僵尸的集合，另一个是植物与子弹的集合，这样分是为了碰撞检测
		// 为了减少碰撞检测事件去除部分不必要的运算，故而分成很多层
		// 循环这个可安放植物的HashMap，目的是拿出每个元素与locationX和locationY进行比较
		// key的作用是让每个Paint的标示不同
		synchronized (surfaceHolder) {// 加锁
			Point point;
			for (Integer key : Config.plantPoints.keySet()) {
				// 找距离locationX与locationY最近而且处于目标地域上
				point = Config.plantPoints.get(key);
				if ((Math.abs(locationX - point.x) < Config.deviceWidth / 11 / 2)
						&& (Math.abs(locationY - point.y) < Config.deviceHeight / 6 / 2)) {
					// 跑道标示
					int raceWayIndex = 6;
					for (int i = 0; i < Config.raceWayYpoints.length; i++) {
						// 遍历跑道的Y值
						if (point.y == Config.raceWayYpoints[i]) {
							// 如果Y值相等那么跑道确定
							raceWayIndex = i;
						}
					}
					if (isPlantExist(key, raceWayIndex)) {
						// 跑道数出发事件
						switch (raceWayIndex) {
						case 0:
							if (baseModel instanceof EmplacePea) {
								gameLayout4plant0.add(new Pea(point.x, point.y,
										key));
								Config.sunlight -= 100;
							} else {
								gameLayout4plant0.add(new Flower(point.x,
										point.y, key));
								Config.sunlight -= 50;
							}
							break;
						case 1:
							if (baseModel instanceof EmplacePea) {
								gameLayout4plant1.add(new Pea(point.x, point.y,
										key));
								Config.sunlight -= 100;
							} else {
								gameLayout4plant1.add(new Flower(point.x,
										point.y, key));
								Config.sunlight -= 50;
							}
							break;
						case 2:
							if (baseModel instanceof EmplacePea) {
								gameLayout4plant2.add(new Pea(point.x, point.y,
										key));
								Config.sunlight -= 100;
							} else {
								gameLayout4plant2.add(new Flower(point.x,
										point.y, key));
								Config.sunlight -= 50;
							}
							break;
						case 3:
							if (baseModel instanceof EmplacePea) {
								gameLayout4plant3.add(new Pea(point.x, point.y,
										key));
								Config.sunlight -= 100;
							} else {
								gameLayout4plant3.add(new Flower(point.x,
										point.y, key));
								Config.sunlight -= 50;
							}
							break;
						case 4:
							if (baseModel instanceof EmplacePea) {
								gameLayout4plant4.add(new Pea(point.x, point.y,
										key));
								Config.sunlight -= 100;
							} else {
								gameLayout4plant4.add(new Flower(point.x,
										point.y, key));
								Config.sunlight -= 50;
							}
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}

	// 判断此处是否已经有植物的方法
	// key是标示，raceWayIndex用于确定应查哪一个跑道
	private boolean isPlantExist(int key, int raceWayIndex) {
		switch (raceWayIndex) {
		case 0:
			for (BaseModel model : gameLayout4plant0) {
				// 首先 如果这个区域是继承了Plant接口的子类(因为子弹和阳光不继承Plant做以区别)
				if (model instanceof Plant) {
					// 然后此处的key与传入的key相等
					if (key == ((Plant) model).getmapIndex()) {
						// 那么返回此处不能种植植物
						return false;
					}
				}
			}
			break;
		case 1:
			for (BaseModel model : gameLayout4plant1) {
				// 首先 如果这个区域是继承了Plant接口的子类(因为子弹和阳光不继承Plant做以区别)
				if (model instanceof Plant) {
					// 然后此处的key与传入的key相等
					if (key == ((Plant) model).getmapIndex()) {
						// 那么返回此处不能种植植物
						return false;
					}
				}
			}
			break;
		case 2:
			for (BaseModel model : gameLayout4plant2) {
				// 首先 如果这个区域是继承了Plant接口的子类(因为子弹和阳光不继承Plant做以区别)
				if (model instanceof Plant) {
					// 然后此处的key与传入的key相等
					if (key == ((Plant) model).getmapIndex()) {
						// 那么返回此处不能种植植物
						return false;
					}
				}
			}
			break;
		case 3:
			for (BaseModel model : gameLayout4plant3) {
				// 首先 如果这个区域是继承了Plant接口的子类(因为子弹和阳光不继承Plant做以区别)
				if (model instanceof Plant) {
					// 然后此处的key与传入的key相等
					if (key == ((Plant) model).getmapIndex()) {
						// 那么返回此处不能种植植物
						return false;
					}
				}
			}
			break;
		case 4:
			for (BaseModel model : gameLayout4plant4) {
				// 首先 如果这个区域是继承了Plant接口的子类(因为子弹和阳光不继承Plant做以区别)
				if (model instanceof Plant) {
					// 然后此处的key与传入的key相等
					if (key == ((Plant) model).getmapIndex()) {
						// 那么返回此处不能种植植物
						return false;
					}
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	// 被Flower请求，用于产生阳光
	public void giveBrith2Sun(int locationX, int locationY) {
		// TODO giveBrith2Sun
		// 先设置锁住
		synchronized (surfaceHolder) {
			gameLayout3.add(new Sun(locationX, locationY));
		}
	}

	// 被Pea请求，用于产生子弹
	public void giveBirth2Bullet(int locationX, int locationY) {
		// TODO Auto-generated method stub
		// 先设置锁住
		synchronized (surfaceHolder) {// 加锁
			Point point;
			for (Integer key : Config.plantPoints.keySet()) {
				// 找距离locationX与locationY最近而且处于目标地域上
				point = Config.plantPoints.get(key);
				if ((Math.abs(locationX - point.x) < Config.deviceWidth / 11 / 2)
						&& (Math.abs(locationY - point.y) < Config.deviceHeight / 6 / 2)) {
					// 跑道标示
					int raceWayIndex = 6;
					for (int i = 0; i < Config.raceWayYpoints.length; i++) {
						// 遍历跑道的Y值
						if (point.y == Config.raceWayYpoints[i]) {
							// 如果Y值相等那么跑道确定
							raceWayIndex = i;
						}
					}

					switch (raceWayIndex) {
					case 0:
						gameLayout4plant0.add(new Bullet(locationX, locationY));
						break;
					case 1:
						gameLayout4plant1.add(new Bullet(locationX, locationY));
						break;
					case 2:
						gameLayout4plant2.add(new Bullet(locationX, locationY));
						break;
					case 3:
						gameLayout4plant3.add(new Bullet(locationX, locationY));
						break;
					case 4:
						gameLayout4plant4.add(new Bullet(locationX, locationY));
						break;
					default:
						break;
					}
				}
			}
		}
	}

	// 僵尸控制器产生相应，加入僵尸
	public void apply4AddZombie() {
		// TODO apply4AddZombie
		// 先解锁
		synchronized (surfaceHolder) {
			int raceWay = 0;
			// 0~4的随机数来进行跑到初始化
			// Math.random()产生的是0~1的不包括1的随机double型数字
			raceWay = (int) (Math.random() * 5);
			// Config.deviceWidth + 20是为了让僵尸逐步走入屏幕
			// Config.raceWayYpoints[raceWay] - Config.heightYDistance
			// 是为了让僵尸与植物的底对齐
			switch (raceWay) {
			case 0:
				gamelayout4zombie0
						.add(new Zombie(Config.deviceWidth + 20,
								Config.raceWayYpoints[raceWay]
										- Config.heightYDistance, raceWay));
				break;
			case 1:
				gamelayout4zombie1
						.add(new Zombie(Config.deviceWidth + 20,
								Config.raceWayYpoints[raceWay]
										- Config.heightYDistance, raceWay));
				break;
			case 2:
				gamelayout4zombie2
						.add(new Zombie(Config.deviceWidth + 20,
								Config.raceWayYpoints[raceWay]
										- Config.heightYDistance, raceWay));
				break;
			case 3:
				gamelayout4zombie3
						.add(new Zombie(Config.deviceWidth + 20,
								Config.raceWayYpoints[raceWay]
										- Config.heightYDistance, raceWay));
				break;
			case 4:
				gamelayout4zombie4
						.add(new Zombie(Config.deviceWidth + 20,
								Config.raceWayYpoints[raceWay]
										- Config.heightYDistance, raceWay));
				break;
			default:
				break;
			}
		}
	}

	// 处理碰撞检测，碰撞检测的发起者是僵尸
	public void checkCollision(Zombie zombie, int raceWay) {
		// TODO Auto-generated method stub
		synchronized (surfaceHolder) {
			switch (raceWay) {
			case 0:
				for (BaseModel model : gameLayout4plant0) {
					if (Math.abs((model.getLocationX() + model.getModelWidth() / 2)
							- (zombie.getLocationX() + zombie.getModelWidth() / 2)) < Math
							.abs((model.getModelWidth() + zombie
									.getModelWidth()) / 2)) {
						if (model instanceof Plant) {
							// 植物死
							model.setAlife(false);
						} else if (model instanceof Bullet) {
							// 子弹死
							model.setAlife(false);
							// 僵尸死
							zombie.setAlife(false);
							model.setAlife(true);
						}
					}
				}
				break;
			case 1:
				for (BaseModel model : gameLayout4plant1) {
					if (Math.abs((model.getLocationX() + model.getModelWidth() / 2)
							- (zombie.getLocationX() + zombie.getModelWidth() / 2)) < Math
							.abs((model.getModelWidth() + zombie
									.getModelWidth()) / 2)) {
						if (model instanceof Plant) {
							// 植物死
							model.setAlife(false);
						} else if (model instanceof Bullet) {
							// 子弹死
							model.setAlife(false);
							// 僵尸死
							zombie.setAlife(false);
							model.setAlife(true);
						}
					}
				}
				break;
			case 2:
				for (BaseModel model : gameLayout4plant2) {
					if (Math.abs((model.getLocationX() + model.getModelWidth() / 2)
							- (zombie.getLocationX() + zombie.getModelWidth() / 2)) < Math
							.abs((model.getModelWidth() + zombie
									.getModelWidth()) / 2)) {
						if (model instanceof Plant) {
							// 植物死
							model.setAlife(false);
						} else if (model instanceof Bullet) {
							// 子弹死
							model.setAlife(false);
							// 僵尸死
							zombie.setAlife(false);
							model.setAlife(true);
						}
					}
				}
				break;
			case 3:
				for (BaseModel model : gameLayout4plant3) {
					if (Math.abs((model.getLocationX() + model.getModelWidth() / 2)
							- (zombie.getLocationX() + zombie.getModelWidth() / 2)) < Math
							.abs((model.getModelWidth() + zombie
									.getModelWidth()) / 2)) {
						if (model instanceof Plant) {
							// 植物死
							model.setAlife(false);
						} else if (model instanceof Bullet) {
							// 子弹死
							model.setAlife(false);
							// 僵尸死
							zombie.setAlife(false);
							model.setAlife(true);
						}
					}
				}
				break;
			case 4:
				for (BaseModel model : gameLayout4plant4) {
					if (Math.abs((model.getLocationX() + model.getModelWidth() / 2)
							- (zombie.getLocationX() + zombie.getModelWidth() / 2)) < Math
							.abs((model.getModelWidth() + zombie
									.getModelWidth()) / 2)) {
						if (model instanceof Plant) {
							// 植物死
							model.setAlife(false);
						} else if (model instanceof Bullet) {
							// 子弹死
							model.setAlife(false);
							// 僵尸死
							zombie.setAlife(false);
							model.setAlife(true);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

}
