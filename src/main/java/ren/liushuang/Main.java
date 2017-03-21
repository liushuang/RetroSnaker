package ren.liushuang;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
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

    public static final int WIDTH_LENGTH = 300;
    public static final int HEIGHT_LENGTH = 200;
    public static final int WIDTH_COUNT = 15;
    public static final int HEIGHT_COUNT = 10;
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

    /**
     * 当前地图,0代表可以走,1代表不能走
     */
    public static int[][] type;

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
            Thread.sleep(50);
            findNextMove();
            move(f);
        }
    }

    private static void findNextMove() {
        Point head = currentSnaker.get(currentSnaker.size() - 1);
        Point tail = currentSnaker.get(0);
        boolean canReachGoal = canReachGoal(head, bean);
        if (canReachGoal) {
            List<Point> path = findNearestPath(head, bean);
            Point nextMove;
            if (path == null || path.size() == 0) {
                nextMove = tail;
            } else {
                nextMove = path.get(path.size() - 1);
            }
            boolean canReachTail = canReachGoal(nextMove, tail) && canReachGoal(bean, tail, path);

            if (canReachTail) {
                if(nextMove.getX() == bean.getX() && nextMove.getY() == bean.getY()){
                    List<Point> reachTairPath = findNearestPath(nextMove,tail);
                    if(reachTairPath.size() == 1 && currentSnaker.size() > 1){
                        pickFromFarthestToGoal(head, bean);
                        return;
                    }
                }
                findDirection(head, nextMove);
            } else {
                pickFromFarthestToGoal(head, bean);
            }
        } else {
            pickFromFarthestToGoal(head, bean);
        }
    }

    private static void pickFromFarthestToGoal(Point head, Point bean) {
        int maxDistance = -1;
        Point goal = null;
        Point tail = currentSnaker.get(0);
        List<Point> canMovePoint = new ArrayList<>();
        if (head.getX() - 1 >= 0 && type[head.getX() - 1][head.getY()] == 0) {
            Point temp = new Point(head.getX() - 1, head.getY());
            if (canReachGoal(temp, tail)) {
                canMovePoint.add(temp);
            }
        }
        if (head.getX() + 1 < HEIGHT_COUNT && type[head.getX() + 1][head.getY()] == 0) {
            Point temp = new Point(head.getX() + 1, head.getY());
            if (canReachGoal(temp, tail)) {
                canMovePoint.add(temp);
            }
        }
        if (head.getY() - 1 >= 0 && type[head.getX()][head.getY() - 1] == 0) {
            Point temp = new Point(head.getX(), head.getY() - 1);
            if (canReachGoal(temp, tail)) {
                canMovePoint.add(temp);
            }
        }
        if (head.getY() + 1 < WIDTH_COUNT && type[head.getX()][head.getY() + 1] == 0) {
            Point temp = new Point(head.getX(), head.getY() + 1);
            if (canReachGoal(temp, tail)) {
                canMovePoint.add(temp);
            }
        }
        for (Point p : canMovePoint) {
            int distance = Math.abs(bean.getX() - p.getX()) + Math.abs(bean.getY() - p.getY());
            if (distance > maxDistance) {
                maxDistance = distance;
                goal = p;
            }
        }
        if (goal == null) {
            goal = tail;
        }
        findDirection(head, goal);
    }

    private static void findDirection(Point head, Point firstMove) {
        if (firstMove.getX() == head.getX() + 1) {
            direction = DIRECTION_DOWN;
        } else if (firstMove.getX() == head.getX() - 1) {
            direction = DIRECTION_UP;
        } else if (firstMove.getY() == head.getY() + 1) {
            direction = DIRECTION_RIGHT;
        } else if (firstMove.getY() == head.getY() - 1) {
            direction = DIRECTION_LEFT;
        }
    }

    private static List<Point> findNearestPath(Point head, Point bean) {
        List<Point> result = new ArrayList<>();
        if(head.getX() == bean.getX() && head.getY() == bean.getY()){
            return null;
        }
        int[][] search = new int[HEIGHT_COUNT][WIDTH_COUNT];
        for (int i = 0; i < HEIGHT_COUNT; i++) {
            for (int j = 0; j < WIDTH_COUNT; j++) {
                if (type[i][j] == 1) {
                    search[i][j] = Integer.MAX_VALUE;
                } else {
                    search[i][j] = -1;
                }
            }
        }
        search[bean.getX()][bean.getY()] = -1;
        search[head.getX()][head.getY()] = 0;
        dfsWithWeight(search, head.getX(), head.getY());
        if (search[bean.getX()][bean.getY()] > -1) {
            result.add(new Point(bean.getX(), bean.getY()));
            for (int k = search[bean.getX()][bean.getY()]; k > 1; k--) {
                buildPath(result, search);
            }
        } else {
            System.out.println("error, can not find path");
        }
        return result;
    }

    private static void buildPath(List<Point> result, int[][] search) {
        Point currentPoint = result.get(result.size() - 1);
        int x = currentPoint.getX();
        int y = currentPoint.getY();
        int currentValue = search[currentPoint.getX()][currentPoint.getY()];
        if (x - 1 >= 0 && search[x - 1][y] == currentValue - 1) {
            result.add(new Point(x - 1, y));
        } else if (x + 1 < HEIGHT_COUNT && search[x + 1][y] == currentValue - 1) {
            result.add(new Point(x + 1, y));
        } else if (y - 1 >= 0 && search[x][y - 1] == currentValue - 1) {
            result.add(new Point(x, y - 1));
        } else if (y + 1 < WIDTH_COUNT && search[x][y + 1] == currentValue - 1) {
            result.add(new Point(x, y + 1));
        }
    }

    public static Queue<Point> dfsQueue = new LinkedList<>();

    private static void dfsWithWeight(int[][] search, int x, int y) {
        int currentValue = search[x][y];
        if (x - 1 >= 0 && search[x - 1][y] == -1) {
            search[x - 1][y] = currentValue + 1;
            dfsQueue.offer(new Point(x - 1, y));
        }
        if (x + 1 < HEIGHT_COUNT && search[x + 1][y] == -1) {
            search[x + 1][y] = currentValue + 1;
            dfsQueue.offer(new Point(x + 1, y));
        }
        if (y - 1 >= 0 && search[x][y - 1] == -1) {
            search[x][y - 1] = currentValue + 1;
            dfsQueue.offer(new Point(x, y - 1));
        }
        if (y + 1 < WIDTH_COUNT && search[x][y + 1] == -1) {
            search[x][y + 1] = currentValue + 1;
            dfsQueue.offer(new Point(x, y + 1));
        }

        if (!dfsQueue.isEmpty()) {
            Point p = dfsQueue.poll();
            dfsWithWeight(search, p.getX(), p.getY());
        }
    }

    private static boolean canReachGoal(Point head, Point bean) {
        return canReachGoal(head, bean, new ArrayList<>());
    }

    private static boolean canReachGoal(Point from, Point to, List<Point> extraPoint) {
        int[][] search = new int[HEIGHT_COUNT][WIDTH_COUNT];
        for (int i = 0; i < HEIGHT_COUNT; i++) {
            for (int j = 0; j < WIDTH_COUNT; j++) {
                if(type[i][j] == 1) {
                    search[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        for(Point p : extraPoint){
            search[p.getX()][p.getY()] = Integer.MAX_VALUE;
        }
        search[to.getX()][to.getY()] = 0;
        dfs(search, from.getX(), from.getY());
        if (search[to.getX()][to.getY()] == 1) {
            return true;
        }
        return false;
    }

    private static void dfs(int[][] search, int x, int y) {
        search[x][y] = 1;
        if (x - 1 >= 0 && search[x - 1][y] == 0) {
            dfs(search, x - 1, y);
        }
        if (x + 1 < HEIGHT_COUNT && search[x + 1][y] == 0) {
            dfs(search, x + 1, y);
        }
        if (y - 1 >= 0 && search[x][y - 1] == 0) {
            dfs(search, x, y - 1);
        }
        if (y + 1 < WIDTH_COUNT && search[x][y + 1] == 0) {
            dfs(search, x, y + 1);
        }
    }

    private static void move(Frame f) {
        Point targetPoint = null;
        Point head = currentSnaker.get(currentSnaker.size() - 1);
        if (Objects.equals(DIRECTION_RIGHT, direction)) {
            targetPoint = new Point(head.getX(), head.getY() +1);
        } else if (Objects.equals(DIRECTION_LEFT, direction)) {
            targetPoint = new Point(head.getX(), head.getY() -1);
        } else if (Objects.equals(DIRECTION_UP, direction)) {
            targetPoint = new Point(head.getX() -1, head.getY());
        } else if (Objects.equals(DIRECTION_DOWN, direction)) {
            targetPoint = new Point(head.getX() +1, head.getY());
        }

        if (!canMove(targetPoint)) {
            alert(f);
        }

        labels[targetPoint.getX()][targetPoint.getY()].setIcon(GREEN_ICON);
        //labels[targetPoint.getX()][targetPoint.getY()].repaint();
        currentSnaker.add(targetPoint);
        type[targetPoint.getX()][targetPoint.getY()] = 1;
        if (hasBean(targetPoint)) {
            randomBean();
        } else {
            Point tail = currentSnaker.get(0);
            labels[tail.getX()][tail.getY()].setIcon(WHITE_ICON);
            type[tail.getX()][tail.getY()] = 0;
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
        if (targetPoint.getX() >= HEIGHT_COUNT || targetPoint.getX() < 0) {
            return false;
        }
        if (targetPoint.getY() >= WIDTH_COUNT || targetPoint.getY() < 0) {
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
        labels = new JLabel[HEIGHT_COUNT][WIDTH_COUNT];
        type = new int[HEIGHT_COUNT][WIDTH_COUNT];
        currentSnaker.add(new Point(0, 0));
        direction = DIRECTION_RIGHT;
        for (int i = 0; i < HEIGHT_COUNT; i++) {
            for (int j = 0; j < WIDTH_COUNT; j++) {
                JLabel jLabel = new JLabel(WHITE_ICON);
                jLabel.setBounds(WIDTH_LENGTH / WIDTH_COUNT * j, HEIGHT_LENGTH / HEIGHT_COUNT * i + 20,
                    WIDTH_LENGTH / WIDTH_COUNT, HEIGHT_LENGTH / HEIGHT_COUNT);
                jLabel.setOpaque(true);
                jLabel.setSize(WIDTH_LENGTH / WIDTH_COUNT, HEIGHT_LENGTH / HEIGHT_COUNT);
                jLabel.repaint();
                labels[i][j] = jLabel;
                type[i][j] = 0;
                f.add(jLabel);
            }
        }
        f.repaint();
        labels[0][0].setIcon(GREEN_ICON);
        type[0][0] = 1;
        labels[0][0].setVisible(true);
        randomBean();
    }

    private static Random random = new Random(1);

    private static void randomBean() {
        int x = random.nextInt(HEIGHT_COUNT);
        int y = random.nextInt(WIDTH_COUNT);
        Point tryBean = new Point(x, y);
        if (canMove(tryBean)) {
            bean = tryBean;
            labels[bean.getX()][bean.getY()].setIcon(RED_ICON);
        } else {
            randomBean();
        }
    }
}
