/*
 * Created by JFormDesigner on Sun Nov 12 18:27:38 CST 2023
 */

package me.h3xadecimal.gui;

import me.h3xadecimal.Main;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.swing.*;

/**
 * @author ht_ge
 */
public class UiMain extends JFrame {

    public UiMain() throws Exception {
        initComponents();

        lbAbout.setText("""
                <html>
                <body>
                
                <p>这框真帅吧</p>
                <p>菜就多练，玩不起就别玩，蚁钳是蚁钳，显载是显载</p>
                <p>By H3xadecimal</p>
                <p>https://space.bilibili.com/434219171</p>
                
                </body>
                </html>
                """.trim());

        // 配置
        sliderRefreshInterval.setValue(Main.prop.getRefreshInterval());
        sliderMaxCount.setValue(Main.prop.getMaxCount());
        sliderMinCount.setValue(Main.prop.getMinCount());
    }

    private void thisWindowClosed(WindowEvent e) {
    }

    private void thisWindowClosing(WindowEvent e) {
        Main.exit(0);
    }

    private void confirmSettings(MouseEvent e) {
        Main.prop.setRefreshInterval(sliderRefreshInterval.getValue());
        Main.prop.setMaxCount(sliderMaxCount.getValue());
        Main.prop.setMinCount(sliderMinCount.getValue());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        tpMain = new JTabbedPane();
        pnSettings = new JPanel();
        pnRefreshInterval = new JPanel();
        lbRefreshInterval = new JLabel();
        sliderRefreshInterval = new JSlider();
        pnMaxCount = new JPanel();
        lbMaxCount = new JLabel();
        sliderMaxCount = new JSlider();
        pnMinCount = new JPanel();
        lbMinCount = new JLabel();
        sliderMinCount = new JSlider();
        btnConfirm = new JButton();
        pnAbout = new JPanel();
        lbAbout = new JLabel();

        //======== this ========
        setTitle("\u8fd9\u6846\u771f\u5e05\u5427");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== tpMain ========
        {

            //======== pnSettings ========
            {
                pnSettings.setLayout(new GridLayout(4, 1));

                //======== pnRefreshInterval ========
                {
                    pnRefreshInterval.setLayout(new GridLayout(1, 2));

                    //---- lbRefreshInterval ----
                    lbRefreshInterval.setText("\u5237\u65b0\u95f4\u9694\uff08\u5e27\uff09");
                    pnRefreshInterval.add(lbRefreshInterval);

                    //---- sliderRefreshInterval ----
                    sliderRefreshInterval.setMinimum(30);
                    sliderRefreshInterval.setMaximum(600);
                    sliderRefreshInterval.setValue(150);
                    pnRefreshInterval.add(sliderRefreshInterval);
                }
                pnSettings.add(pnRefreshInterval);

                //======== pnMaxCount ========
                {
                    pnMaxCount.setLayout(new GridLayout(1, 2));

                    //---- lbMaxCount ----
                    lbMaxCount.setText("\u6700\u5927\u6570\u91cf");
                    pnMaxCount.add(lbMaxCount);

                    //---- sliderMaxCount ----
                    sliderMaxCount.setMajorTickSpacing(1);
                    sliderMaxCount.setMaximum(20);
                    sliderMaxCount.setValue(16);
                    sliderMaxCount.setMinimum(2);
                    pnMaxCount.add(sliderMaxCount);
                }
                pnSettings.add(pnMaxCount);

                //======== pnMinCount ========
                {
                    pnMinCount.setLayout(new GridLayout(1, 2));

                    //---- lbMinCount ----
                    lbMinCount.setText("\u6700\u5c0f\u6570\u91cf");
                    pnMinCount.add(lbMinCount);

                    //---- sliderMinCount ----
                    sliderMinCount.setMaximum(19);
                    sliderMinCount.setMinimum(1);
                    sliderMinCount.setValue(4);
                    pnMinCount.add(sliderMinCount);
                }
                pnSettings.add(pnMinCount);

                //---- btnConfirm ----
                btnConfirm.setText("\u5e94\u7528");
                btnConfirm.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        confirmSettings(e);
                    }
                });
                pnSettings.add(btnConfirm);
            }
            tpMain.addTab("\u8bbe\u7f6e", pnSettings);

            //======== pnAbout ========
            {
                pnAbout.setLayout(new BorderLayout());

                //---- lbAbout ----
                lbAbout.setVerticalAlignment(SwingConstants.TOP);
                lbAbout.setHorizontalAlignment(SwingConstants.LEFT);
                pnAbout.add(lbAbout, BorderLayout.CENTER);
            }
            tpMain.addTab("\u5173\u4e8e", pnAbout);
        }
        contentPane.add(tpMain, BorderLayout.CENTER);
        setSize(635, 425);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane tpMain;
    private JPanel pnSettings;
    private JPanel pnRefreshInterval;
    private JLabel lbRefreshInterval;
    private JSlider sliderRefreshInterval;
    private JPanel pnMaxCount;
    private JLabel lbMaxCount;
    private JSlider sliderMaxCount;
    private JPanel pnMinCount;
    private JLabel lbMinCount;
    private JSlider sliderMinCount;
    private JButton btnConfirm;
    private JPanel pnAbout;
    private JLabel lbAbout;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
