package ren.liushuang;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * 主函数
 *
 * @author liushuang
 * @create 2017-03-16 AM10:59
 */
public class Main {

    public static final int WIDTH_LENGTH = 400;
    public static final int HEIGHT_LENGTH = 300;
    public static final int WIDTH_COUNT = 20;
    public static final int HEIGHT_COUNT = 15;
    public static JLabel[][] labels = null;
    public static List<Point> currentSnaker = new ArrayList<>();
    public static ImageIcon RED_ICON = new ImageIcon("/Users/liushuang/Pictures/RetroSnaker/red.png");
    public static ImageIcon BLACK_ICON = new ImageIcon("/Users/liushuang/Pictures/RetroSnaker/black.png");
    public static ImageIcon WHITE_ICON = new ImageIcon("/Users/liushuang/Pictures/RetroSnaker/white.png");
    public static ImageIcon GREEN_ICON = new ImageIcon("/Users/liushuang/Pictures/RetroSnaker/green.png");

    public static String direction;

    public static final String DIRECTION_LEFT = "left";
    public static final String DIRECTION_RIGHT = "right";
    public static final String DIRECTION_UP = "up";
    public static final String DIRECTION_DOWN = "down";

    public static boolean keepMoving = true;
    public static Point bean;

    public static void main(String[] args) throws InterruptedException {
        Frame f = new Frame("my awt");
        f.setSize(WIDTH_LENGTH, HEIGHT_LENGTH + 20);
        f.setLocation(100, 100);
        f.setLayout(null);
        f.addWindowListener(new MyWindowListener());
        f.setVisible(true);
        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    direction = DIRECTION_LEFT;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    direction = DIRECTION_RIGHT;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    direction = DIRECTION_UP;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    direction = DIRECTION_DOWN;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        init(f);
        Thread.sleep(2000);
        while (keepMoving) {
            Thread.sleep(200);
            move(f);
        }
    }

    private static void move(Frame f) {
        Point targetPoint = null;
        Point head = currentSnaker.get(currentSnaker.size() - 1);
        if (Objects.equals(DIRECTION_RIGHT, direction)) {
            targetPoint = new Point(head.getX() + 1, head.getY());
        } else if (Objects.equals(DIRECTION_LEFT, direction)) {
            targetPoint = new Point(head.getX() - 1, head.getY());
        } else if (Objects.equals(DIRECTION_UP, direction)) {
            targetPoint = new Point(head.getX(), head.getY() - 1);
        } else if (Objects.equals(DIRECTION_DOWN, direction)) {
            targetPoint = new Point(head.getX(), head.getY() + 1);
        }

        if (!canMove(targetPoint)) {
            alert(f);
        }

        labels[targetPoint.getX()][targetPoint.getY()].setIcon(GREEN_ICON);
        //labels[targetPoint.getX()][targetPoint.getY()].repaint();
        currentSnaker.add(targetPoint);
        if (hasBean(targetPoint)) {
            randomBean();
        } else {
            Point tail = currentSnaker.get(0);
            labels[tail.getX()][tail.getY()].setIcon(WHITE_ICON);
            currentSnaker.remove(0);
        }
    }

    private static boolean hasBean(Point targetPoint) {
        if (targetPoint.getX() == bean.getX() && targetPoint.getY() == bean.getY()) {
            return true;
        }
        return false;
    }

    private static boolean canMove(Point targetPoint) {
        if (targetPoint.getX() >= WIDTH_COUNT || targetPoint.getX() < 0) {
            return false;
        }
        if (targetPoint.getY() >= HEIGHT_COUNT || targetPoint.getY() < 0) {
            return false;
        }

        for (Point point : currentSnaker) {
            if (targetPoint.getX() == point.getX() && targetPoint.getY() == point.getY()) {
                return false;
            }
        }
        return true;
    }

    private static void alert(Frame f) {
        keepMoving = false;
        //弹出的对话框
        Dialog d = new Dialog(f, "GG", true);
        //设置弹出对话框的位置和大小
        d.setBounds(200, 200, 200, 200);
        //设置弹出对话框的布局为流式布局
        d.setLayout(new FlowLayout());
        Button button = new Button("GG");
        button.setLocation(100, 100);
        button.addActionListener((event) -> {
            d.dispose();
        });
        d.add(button);
        d.setVisible(true);
    }

    private static void init(Frame f) {
        labels = new JLabel[WIDTH_COUNT][HEIGHT_COUNT];
        currentSnaker.add(new Point(0, 0));
        direction = DIRECTION_RIGHT;
        for (int i = 0; i < WIDTH_COUNT; i++) {
            for (int j = 0; j < HEIGHT_COUNT; j++) {
                JLabel jLabel = new JLabel(WHITE_ICON);
                jLabel.setBounds(WIDTH_LENGTH / WIDTH_COUNT * i, HEIGHT_LENGTH / HEIGHT_COUNT * j + 20,
                    WIDTH_LENGTH / WIDTH_COUNT, HEIGHT_LENGTH / HEIGHT_COUNT);
                jLabel.setOpaque(true);
                jLabel.setSize(WIDTH_LENGTH / WIDTH_COUNT, HEIGHT_LENGTH / HEIGHT_COUNT);
                jLabel.repaint();
                labels[i][j] = jLabel;
                f.add(jLabel);
            }
        }
        f.repaint();
        labels[0][0].setIcon(GREEN_ICON);
        labels[0][0].setVisible(true);
        randomBean();
    }

    private static Random random = new Random();

    private static void randomBean() {
        int x = random.nextInt(WIDTH_COUNT);
        int y = random.nextInt(HEIGHT_COUNT);
        Point tryBean = new Point(x, y);
        if (canMove(tryBean)) {
            bean = tryBean;
            labels[bean.getX()][bean.getY()].setIcon(RED_ICON);
        } else {
            randomBean();
        }
    }
}
