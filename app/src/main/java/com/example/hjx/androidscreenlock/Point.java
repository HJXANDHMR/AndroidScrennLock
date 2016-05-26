package com.example.hjx.androidscreenlock;

public class Point {
    // 定义点的三种状态
    public static int STATE_NORMAL = 0;
    public static int STATE_PRESS = 1;
    public static int STATE_ERROR = 2;
    //定义，x,y的坐标
    public float x;
    public float y;
    // 设置当前点的默认状态为STATE_NORMAL
    public int state = STATE_NORMAL;

    // 构造函数
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // 计算两个点的距离
    public float distance(Point point) {
        float distance = (float) Math.sqrt((x - point.x) * (x - point.x)
                + (y - point.y) * (y - point.y));

        return distance;
    }
}
