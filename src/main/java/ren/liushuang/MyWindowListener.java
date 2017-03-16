package ren.liushuang;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * WindowListener的子类
 *
 * @author liushuang
 * @create 2017-03-16 AM11:02
 */
public class MyWindowListener extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
    }
}
