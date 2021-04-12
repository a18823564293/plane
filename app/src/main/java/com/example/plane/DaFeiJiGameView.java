package com.example.plane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class DaFeiJiGameView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable, android.view.View.OnTouchListener {

	private Bitmap my;// 自己
	private Bitmap baozha;// 爆炸
	private Bitmap bg;// 背景
	private Bitmap diren;// 敌人
	private Bitmap zidan;// 子弹
	private Bitmap erjihuancun;// 二级缓存
	private int display_w;// 屏幕的宽高
	private int display_h;
	private ArrayList<GameImage> gameImages = new ArrayList();
	private ArrayList<Zidan> zidans = new ArrayList<Zidan>();

	public DaFeiJiGameView(Context context) {
		super(context);
		getHolder().addCallback(this);//注册回调方法
		this.setOnTouchListener(this);// 事件注册

	}

	private SoundPool pool = null;
	private int sound_bomb = 0;
	private int sound_gameover = 0;
	private int sound_shot = 0;

	private void init() {
		// 加载照片
		my = BitmapFactory.decodeResource(getResources(), R.drawable.my);
		baozha = BitmapFactory
				.decodeResource(getResources(), R.drawable.baozha);
		bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
		diren = BitmapFactory.decodeResource(getResources(), R.drawable.diren);
		zidan = BitmapFactory.decodeResource(getResources(), R.drawable.zidan);

		// 生产二级缓存照片
		erjihuancun = Bitmap.createBitmap(display_w, display_h,
				Config.ARGB_8888);

		gameImages.add(new BeijingImage(bg));// 先加入背景照片
		gameImages.add(new FeijiImage(my));
		gameImages.add(new DijiImage(diren, baozha));

		// //加载声音

		pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);

		sound_bomb = pool.load(getContext(), R.raw.bomb, 1);
		sound_gameover = pool.load(getContext(), R.raw.gameover, 1);
		sound_shot = pool.load(getContext(), R.raw.shot, 1);

	}

	private class SoundPlay extends Thread {
		int i = 0;

		public SoundPlay(int i) {
			this.i = i;

		}

		public void run() {
			pool.play(i, 1, 1, 1, 0, 1);
		}
	}

	private interface GameImage {
		public Bitmap getBitmap();

		public int getX();

		public int getY();

	}

	private class Zidan implements GameImage {

		private Bitmap zidan;
		private FeijiImage feiji;
		private int x;
		private int y;

		public Zidan(FeijiImage feiji, Bitmap zidan) {
			this.feiji = feiji;
			this.zidan = zidan;
			x = (feiji.getX() + feiji.getWidth() / 2) - 8;
			y = feiji.getY() - zidan.getHeight();

		}

		public Bitmap getBitmap() {
			y -= 50;
			if (y <= -10) {
				zidans.remove(this);
			}
			return zidan;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	private class DijiImage implements GameImage {

		private Bitmap diren = null;

		private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		private List<Bitmap> baozhas = new ArrayList<Bitmap>();
		private int x;
		private int y;
		private int width;
		private int heihgt;

		public DijiImage(Bitmap diren, Bitmap baozha) {
			this.diren = diren;
			bitmaps.add(Bitmap.createBitmap(diren, 0, 0, diren.getWidth() / 4,
					diren.getHeight()));
			bitmaps.add(Bitmap.createBitmap(diren, (diren.getWidth() / 4) * 1,
					0, diren.getWidth() / 4, diren.getHeight()));
			bitmaps.add(Bitmap.createBitmap(diren, (diren.getWidth() / 4) * 2,
					0, diren.getWidth() / 4, diren.getHeight()));
			bitmaps.add(Bitmap.createBitmap(diren, (diren.getWidth() / 4) * 3,
					0, diren.getWidth() / 4, diren.getHeight()));

			baozhas.add(Bitmap.createBitmap(baozha, 0, 0,
					baozha.getWidth() / 4, baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 1, 0, baozha.getWidth() / 4,
					baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 2, 0, baozha.getWidth() / 4,
					baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 3, 0, baozha.getWidth() / 4,
					baozha.getHeight() / 2));

			baozhas.add(Bitmap.createBitmap(baozha, 0, baozha.getHeight() / 2,
					baozha.getWidth() / 4, baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 1, baozha.getHeight() / 2,
					baozha.getWidth() / 4, baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 2, baozha.getHeight() / 2,
					baozha.getWidth() / 4, baozha.getHeight() / 2));
			baozhas.add(Bitmap.createBitmap(baozha,
					(baozha.getWidth() / 4) * 3, baozha.getHeight() / 2,
					baozha.getWidth() / 4, baozha.getHeight() / 2));

			width = diren.getWidth() / 4;
			heihgt = diren.getHeight();

			y = -diren.getHeight();
			Random ran = new Random();
			x = ran.nextInt(display_w - (diren.getWidth() / 4));
		}

		private int index = 0;
		private int num = 0;

		public Bitmap getBitmap() {

			Bitmap bitmap = bitmaps.get(index);
			if (num == 7) {
				index++;
				if (index == 8 && state) {
					gameImages.remove(this);
				}
				if (index == bitmaps.size()) {
					index = 0;
				}
				num = 0;
			}
			y += dijiyidong;
			num++;
			if (y > display_h) {
				gameImages.remove(this);
			}
			return bitmap;
		}

		private boolean state = false;

		// 受到攻击
		public void shoudaogongji(ArrayList<Zidan> zidans) {

			if (!state) {
				for (GameImage zidan : (List<GameImage>) zidans.clone()) {
					if (zidan.getX() > x && zidan.getY() > y
							&& zidan.getX() < x + width
							&& zidan.getY() < y + heihgt) {

						zidans.remove(zidan);
						state = true;
						bitmaps = baozhas;
						fenshu += 10;
						new SoundPlay(sound_bomb).start();
//						pool.play(sound_bomb, 1, 1, 1, 0, 1);
						break;
						// Log.i("APP.TAG", "击中");

					}

				}
			}

		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	private class FeijiImage implements GameImage {

		private Bitmap my;
		private int x;
		private int y;
		private int width;

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		private int height;

		private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		private FeijiImage(Bitmap my) {
			this.my = my;
			bitmaps.add(Bitmap.createBitmap(my, 0, 0, my.getWidth() / 4,
					my.getHeight()));
			bitmaps.add(Bitmap.createBitmap(my, (my.getWidth() / 4) * 1, 0,
					my.getWidth() / 4, my.getHeight()));
			bitmaps.add(Bitmap.createBitmap(my, (my.getWidth() / 4) * 2, 0,
					my.getWidth() / 4, my.getHeight()));
			bitmaps.add(Bitmap.createBitmap(my, (my.getWidth() / 4) * 3, 0,
					my.getWidth() / 4, my.getHeight()));

			// 得到战机的高和宽
			width = my.getWidth() / 4;
			height = my.getHeight();

			x = (display_w - my.getWidth() / 4) / 2;
			y = display_h - my.getHeight() - 30;

		}

		private int index = 0;
		private int num = 0;

		public Bitmap getBitmap() {

			Bitmap bitmap = bitmaps.get(index);
			if (num == 7) {
				index++;
				if (index == bitmaps.size()) {
					index = 0;
				}
				num = 0;
			}

			num++;
			return bitmap;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public void setX(int x) {
			this.x = x;
		}

	}

	// 负责背景照片处理
	private class BeijingImage implements GameImage {
		private Bitmap bg;

		private BeijingImage(Bitmap bg) {
			this.bg = bg;
			newBitmap = Bitmap.createBitmap(display_w, display_h,
					Config.ARGB_8888);
		}

		private Bitmap newBitmap = null;
		private int heigth = 0;

		public Bitmap getBitmap() {
			Paint p = new Paint();
			Canvas canvas = new Canvas(newBitmap);

			canvas.drawBitmap(bg,
					new Rect(0, 0, bg.getWidth(), bg.getHeight()), new Rect(0,
							heigth, display_w, display_h + heigth), p);

			canvas.drawBitmap(bg,
					new Rect(0, 0, bg.getWidth(), bg.getHeight()), new Rect(0,
							-display_h + heigth, display_w, heigth), p);
			heigth++;
			if (heigth == display_h) {
				heigth = 0;
			}
			return newBitmap;

		}

		public int getX() {
			return 0;
		}

		public int getY() {
			return 0;
		}
	}

	private boolean state = false;
	private SurfaceHolder holder;
	private long fenshu = 0;
	private int guanqia = 1;

	private int chudishu = 30; // 出敌机的数字  （越小出现的频率越高）
	private int dijiyidong = 5; // 敌机移动
	private int xiayiguan = 50; // 下一关分数

	private int[][] sj = { { 1, 50, 30, 5 }, { 2, 100, 30, 5 },
			{ 3, 200, 30, 6 }, { 4, 300, 25, 6 }, { 5, 400, 25, 7 },
			{ 6, 500, 25, 7 }, { 7, 600, 20, 8 }, { 8, 700, 20, 8 },
			{ 9, 800, 15, 9 }, { 10, 900, 10, 10 }, { 11, 1000, 10, 10 },
			{ 12, 1100, 10, 10 } };

	private boolean stopState = false;

	public void stop() {
		stopState = true;
	}

	public void start() {
		stopState = false;
		thread.interrupt();// 起来
	}

	// 绘画中心
	public void run() {
		Paint p1 = new Paint();
		int diren_num = 0;
		int didan_num = 0; //子弹数量控制
		Paint p2 = new Paint();
		p2.setColor(Color.RED);
		p2.setTextSize(30);
		p2.setDither(true);
		p2.setAntiAlias(true);
		try {
			while (state) {
				while (stopState) {
					try {
						Thread.sleep(1000000);
					} catch (Exception e) {
					}
				}

				if (selectFeiji != null) {
					if (didan_num == 5) {//子弹数量控制
						new SoundPlay(sound_shot).start();
//						pool.play(sound_shot, 1, 1, 1, 0, 1);
						zidans.add(new Zidan(selectFeiji, zidan));
						didan_num = 0;
					}
					didan_num++;
				}

				Canvas newCanvas = new Canvas(erjihuancun);

				for (GameImage image : (List<GameImage>) gameImages.clone()) {
					if (image instanceof DijiImage) {
						((DijiImage) image).shoudaogongji(zidans);// 把子弹告诉敌机
					}
					newCanvas.drawBitmap(image.getBitmap(), image.getX(),
							image.getY(), p1);
				}
				for (GameImage image : (List<GameImage>) zidans.clone()) {
					newCanvas.drawBitmap(image.getBitmap(), image.getX(),
							image.getY(), p1);
				}

				// 分数

				newCanvas.drawText("分:" + fenshu, 0, 30, p2);
				newCanvas.drawText("关:" + guanqia, 0, 60, p2);
				newCanvas.drawText("下:" + xiayiguan, 0, 90, p2);

				if (sj[guanqia - 1][1] <= fenshu) {

					chudishu = sj[guanqia][2];
					dijiyidong = sj[guanqia][3];
					fenshu = sj[guanqia - 1][1] - fenshu;
					xiayiguan = sj[guanqia][1];
					guanqia = sj[guanqia][0];

				}

				// Log.i("APP.TAG", chudishu+"-"+dijiyidong);

				if (diren_num == chudishu) {
					diren_num = 0;
					gameImages.add(new DijiImage(diren, baozha));
				}
				diren_num++;
				Canvas canvas = holder.lockCanvas();
				canvas.drawBitmap(erjihuancun, 0, 0, p1);
				holder.unlockCanvasAndPost(canvas);
				Thread.sleep(10);
			}
		} catch (Exception e) {
		}

	}

	//视图创建时通知你
	public void surfaceCreated(SurfaceHolder holder) {

	}

	//视图销毁时通知你
	public void surfaceDestroyed(SurfaceHolder holder) {

		state = false;
	}

	//视图改变时通知你
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		display_w = width;
		display_h = height;
		init();
		this.holder = holder;
		state = true;
		thread = new Thread(this);
		thread.start();

	}

	Thread thread = null;

	FeijiImage selectFeiji;

	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {// 手接近屏幕
			for (GameImage game : gameImages) {
				if (game instanceof FeijiImage) {

					FeijiImage feiji = (FeijiImage) game;

					if (feiji.getX() < event.getX()
							&& feiji.getY() < event.getY()
							&& feiji.getX() + feiji.getWidth() > event.getX()
							&& feiji.getY() + feiji.getHeight() > event.getY()) {
						selectFeiji = feiji;
					} else {
						selectFeiji = null;
					}

					break;
				}
			}

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

			if (selectFeiji != null) {
				selectFeiji.setX((int) event.getX() - selectFeiji.getWidth()
						/ 2);
				selectFeiji.setY((int) event.getY() - selectFeiji.getHeight()
						/ 2);
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {

			selectFeiji = null;
		}

		return true;
	}

}
