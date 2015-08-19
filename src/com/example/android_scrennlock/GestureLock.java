package com.example.android_scrennlock;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GestureLock extends View {
	/**
	 * 这个是自定义view，可以实现绘制图案，和解锁
	 */

	// 实现一个3*3的格子
	private Point[][] points = new Point[3][3];
	// 判断是否已经初始化
	private boolean inited = false;
	// 定义三种图片的背景
	private Bitmap bitmapnormal;
	private Bitmap bitmappress;
	private Bitmap bitmaperror;
	// 定义一个图片的半径
	private float bitmapR;
	// 定义当前鼠标所在的坐标
	float mouseX, mouseY;
	// 定义一个用于判断当前是否在绘制状态，默认为false
	private boolean isDraw = false;
	// 定义一个list,里面记录的是被绘制过的点的坐标
	private ArrayList<Point> pointlist = new ArrayList<Point>();
	// 定义一个list,里面记录的是当前点的编号 如0,1,2.。
	private ArrayList<Integer> passList = new ArrayList<Integer>();
	// 定义一个外部接口，可以在外部类中访问
	private onDrawFinishendLister lister;

	// 默认的构造方法
	public GestureLock(Context context) {
		super(context);
	}

	/**
	 * 默认的构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public GestureLock(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 默认的构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public GestureLock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 定义一个画笔，用于画九宫格
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	// 定义一个画笔，用于画图案正确的连线
	Paint pressPaint = new Paint();
	// 定义一个画笔，用于画，图案错误的连线
	Paint errorPaint = new Paint();

	// onDraw方法，但view被创建时调用
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 当没有初始化时，调用init();
		if (!inited) {
			init();
		}
		// 开始绘制点
		drawPoint(canvas);
		// 如果pointlist不为空，将其中包含的点之间用线连接起来
		if (pointlist.size() > 0) {
			// a作为第一个点
			Point a = pointlist.get(0);
			for (int i = 1; i < pointlist.size(); i++) {
				// b作为第二个点
				Point b = pointlist.get(i);
				// 调用drawline方法将a,b连接起来
				drawline(canvas, a, b);
				// 然后将后一个点赋值给前一个点，依次循环
				a = b;
			}
			// 当还是绘制状态时，将最后一个点，与当前鼠标所在的点之间用线连接起来
			if (isDraw) {
				drawline(canvas, a, new Point(mouseX, mouseY));

			}

		}
	}

	/**
	 * 
	 * @param canvas
	 *            画布
	 * @param a
	 *            当前点
	 * @param b
	 *            下一个点
	 */
	private void drawline(Canvas canvas, Point a, Point b) {
		// if当前点的状态为按下状态，否则绘制为错误状态
		if (a.state == Point.STATE_PRESS) {
			canvas.drawLine(a.x, a.y, b.x, b.y, pressPaint);
		} else if (a.state == Point.STATE_ERROR) {
			canvas.drawLine(a.x, a.y, b.x, b.y, errorPaint);
		}
	}

	// 判断手势的方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 得到当前触摸位置的X坐标
		mouseX = event.getX();
		// 得到当前触摸位置的Y坐标
		mouseY = event.getY();
		// 定义一个一维数组用来保存x,y坐标的值
		int[] ij;
		int i, j;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 当手指重新开始触摸屏幕时，清除原先数据
			restPoint();
			// 得到当前选中点的坐标
			ij = getselectPoint();
			// if不为空，就得到其坐标，将isDraw设置成true，表示当前处于绘制状态，将当前点的状态设置为按下状态
			if (ij != null) {
				isDraw = true;
				i = ij[0];
				j = ij[1];
				points[i][j].state = Point.STATE_PRESS;
				// 将该点加入到pointlist中，passlist中
				pointlist.add(points[i][j]);
				passList.add(i * 3 + j);

			}
			break;
		case MotionEvent.ACTION_MOVE:
			// 当手指在屏幕中Move时，如果处于绘制状态，得到Move时经过点的坐标
			if (isDraw) {
				ij = getselectPoint();
				if (ij != null) {
					i = ij[0];
					j = ij[1];
					// 如果pointlist中已经存在这个点了，就不用加进去了
					if (!pointlist.contains(points[i][j])) {
						points[i][j].state = Point.STATE_PRESS;
						pointlist.add(points[i][j]);
						passList.add(i * 3 + j);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 设置一个判断变量，默认为false
			boolean valid = false;
			// if 处于绘制状态和，lister不为空
			if (lister != null && isDraw) {
				// 将这个方法返回的值赋值给valid
				valid = lister.onDrawfinished(passList);
			}
			// if valid为false说明密码验证失败，将点的状态改为错误状态
			if (!valid) {
				// 用for循环将pointlist中的点的状态改为错误状态态
				for (Point p : pointlist) {
					p.state = Point.STATE_ERROR;
				}
			}
			// 将绘制状态改为false
			isDraw = false;
			break;

		}
		// 重新绘制
		this.postInvalidate();
		return true;
	}

	// 这个方法是得到点的坐标
	private int[] getselectPoint() {
		Point point = new Point(mouseX, mouseY);
		// 循环判断当前触摸的点位于九宫格的哪一个点
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				// if 两点间的距离小于半径，说明当前点位于point[i][j]中
				if (points[i][j].distance(point) < bitmapR) {
					int[] result = new int[2];
					result[0] = i;
					result[1] = j;
					return result;
				}
			}
		}

		return null;
	}

	// 这个方法是绘制点
	private void drawPoint(Canvas canvas) {
		// 循环九个点，根据其所处的状态绘制
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if (points[i][j].state == Point.STATE_NORMAL) {
					// NORMAL
					canvas.drawBitmap(bitmapnormal, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				} else if (points[i][j].state == Point.STATE_PRESS) {
					// PRESS
					canvas.drawBitmap(bitmappress, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				} else {
					// ERROR
					canvas.drawBitmap(bitmaperror, points[i][j].x - bitmapR,
							points[i][j].y - bitmapR, paint);
				}
			}
		}
	}

	// 初始化操作
	private void init() {
		// 设置画笔的颜色
		pressPaint.setColor(Color.YELLOW);
		// 设置画笔的宽度
		pressPaint.setStrokeWidth(5);
		errorPaint.setColor(Color.RED);
		errorPaint.setStrokeWidth(5);
		// 为三种状态的背景图片赋值
		bitmapnormal = BitmapFactory.decodeResource(getResources(),
				R.drawable.normal);
		bitmappress = BitmapFactory.decodeResource(getResources(),
				R.drawable.press);
		bitmaperror = BitmapFactory.decodeResource(getResources(),
				R.drawable.error);
		// 得到图片的半径
		bitmapR = bitmapnormal.getHeight() / 2;
		// 得到屏幕的宽和高
		int width = getWidth();
		int height = getHeight();
		// 得到偏移量
		int offset = Math.abs(width - height) / 2;
		int offsetX, offsetY;
		// 定义边长
		int space;
		// if实在横屏状态
		if (width > height) {
			offsetX = offset;
			offsetY = 0;
			space = height / 4;
		} else {
			offsetX = 0;
			offsetY = offset;
			space = width / 4;
		}
		// 得到每一个点的坐标
		points[0][0] = new Point(offsetX + space, offsetY + space);
		points[0][1] = new Point(offsetX + space * 2, offsetY + space);
		points[0][2] = new Point(offsetX + space * 3, offsetY + space);

		points[1][0] = new Point(offsetX + space, offsetY + space * 2);
		points[1][1] = new Point(offsetX + space * 2, offsetY + space * 2);
		points[1][2] = new Point(offsetX + space * 3, offsetY + space * 2);

		points[2][0] = new Point(offsetX + space, offsetY + space * 3);
		points[2][1] = new Point(offsetX + space * 2, offsetY + space * 3);
		points[2][2] = new Point(offsetX + space * 3, offsetY + space * 3);
		// 设置初始化的状态为true
		inited = true;
	}

	// 该方法用于重置图案，清除所有数据，也用于外部接口
	public void restPoint() {
		// 清除list中的值
		passList.clear();
		pointlist.clear();
		// 循坏遍历九个点，将其状态设置为正常
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				points[i][j].state = Point.STATE_NORMAL;
			}
		}
		// 重新绘制
		this.postInvalidate();
	}

	// 定义一个接口
	public interface onDrawFinishendLister {
		boolean onDrawfinished(ArrayList<Integer> passList);
	}

	// 定义一个供外部类调用的方法
	public void setonDrawFinishendLister(onDrawFinishendLister lister) {
		this.lister = lister;
	}

}
