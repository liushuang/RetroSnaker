package ren.liushuang;

/**
 * 单个点
 *
 * @author liushuang
 * @create 2017-03-16 AM11:50
 */
public class Point {

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
