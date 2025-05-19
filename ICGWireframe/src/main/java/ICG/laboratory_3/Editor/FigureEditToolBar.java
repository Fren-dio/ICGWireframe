package ICG.laboratory_3.Editor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class FigureEditToolBar extends JToolBar {

    FigureEditWindow editWindow;
    FigureEditPanel figureEditPanel;
    Dimension windowDimension;

    public FigureEditToolBar(FigureEditWindow window, FigureEditPanel panel, Dimension dim) {
        this.editWindow = window;
        this.figureEditPanel = panel;
        this.windowDimension = dim;

        Dimension fixedSize = new Dimension((int) this.windowDimension.getWidth(), 100);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);
        this.setFloatable(false);

        addSettingselements();
    }


    void addSettingselements() {
        JPanel setsGrid = new JPanel();
        setsGrid.setLayout(new BoxLayout(setsGrid, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new GridLayout(1, 4, 5, 5));
        row1.setMaximumSize(new Dimension((int) this.windowDimension.getWidth()-50, 30));
        //row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        addRow1(row1);
        setsGrid.add(row1);

        JPanel row2 = new JPanel(new GridLayout(1, 4, 5, 5));
        row2.setMaximumSize(new Dimension((int) this.windowDimension.getWidth()-50, 30));
        //row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        addRow2(row2);
        setsGrid.add(row2);

        JPanel row3 = new JPanel(new GridLayout(1, 4, 5, 5));
        row3.setMaximumSize(new Dimension((int) this.windowDimension.getWidth()-50, 30));
        //row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
        addRow3(row3);
        setsGrid.add(row3);

        JPanel row4 = new JPanel(new GridLayout(1, 4, 5, 5));
        row4.setMaximumSize(new Dimension((int) this.windowDimension.getWidth()-50, 30));
        //row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
        addRow4(row4);
        setsGrid.add(row4);

        add(setsGrid);
    }

    void addRow1(JPanel row) {
        // число отрезков для каждого участка B-сплайна (≥ 1).
        row.add(new JLabel("N:"));
        JSpinner N = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        N.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setNForBSpline((Integer)N.getValue());
                figureEditPanel.updateImage();
            }
        });
        row.add(N);

        // число опорных точек (не менее 4).
        row.add(new JLabel("K:"));
        JSpinner K = new JSpinner(new SpinnerNumberModel(5, 4, 100, 1));
        K.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setPointsCount((Integer) K.getValue());
            }
        });
        row.add(K);

        row.add(new JLabel("Main color red:"));
        JSpinner colorRed = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));
        figureEditPanel.setMainRed((Integer) colorRed.getValue());
        figureEditPanel.repaint();
        colorRed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setMainRed((Integer) colorRed.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorRed);

        row.add(new JLabel("B-spline red:"));
        JSpinner colorRedBSpline = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));
        figureEditPanel.setBSplineRed((Integer) colorRedBSpline.getValue());
        figureEditPanel.repaint();
        colorRedBSpline.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setBSplineRed((Integer) colorRedBSpline.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorRedBSpline);
    }

    void addRow2(JPanel row) {
        // число отрезков по окружностям между соседними образующими (≥ 1).
        row.add(new JLabel("M1:"));
        JSpinner M1 = new JSpinner(new SpinnerNumberModel(4, 4, 100, 1));
        M1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setM1((Integer) M1.getValue());
            }
        });
        row.add(M1);
        // число образующих
        row.add(new JLabel("M:"));
        JSpinner M = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        M.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setM((Integer) M.getValue());
            }
        });
        row.add(M);

        row.add(new JLabel("Main color green:"));
        JSpinner colorGreen = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        figureEditPanel.setMainGreen((Integer) colorGreen.getValue());
        figureEditPanel.repaint();
        colorGreen.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setMainGreen((Integer) colorGreen.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorGreen);

        row.add(new JLabel("B-spline green:"));
        JSpinner colorGreenBSpline = new JSpinner(new SpinnerNumberModel(128, 0, 255, 1));
        figureEditPanel.setBSplineGreen((Integer) colorGreenBSpline.getValue());
        figureEditPanel.repaint();
        colorGreenBSpline.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setBSplineGreen((Integer) colorGreenBSpline.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorGreenBSpline);
    }

    void addRow3(JPanel row) {

        row.add(new JLabel(""));
        row.add(new JLabel(""));
        row.add(new JLabel(""));
        row.add(new JLabel(""));

        row.add(new JLabel("Main color blue:"));
        JSpinner colorBlue = new JSpinner(new SpinnerNumberModel(255, 0, 255, 1));
        figureEditPanel.setMainBlue((Integer) colorBlue.getValue());
        figureEditPanel.repaint();
        colorBlue.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setMainBlue((Integer) colorBlue.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorBlue);

        row.add(new JLabel("B-spline blue:"));
        JSpinner colorBlueBSpline = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        figureEditPanel.setBSplineBlue((Integer) colorBlueBSpline.getValue());
        figureEditPanel.repaint();
        colorBlueBSpline.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                figureEditPanel.setBSplineBlue((Integer) colorBlueBSpline.getValue());
                figureEditPanel.repaint();
            }
        });
        row.add(colorBlueBSpline);

    }

    void addRow4(JPanel row) {
        Dimension btnDimension = new Dimension(60, 10);

        JButton OKBtn = new JButton("OK");
        OKBtn.setPreferredSize(btnDimension);
        OKBtn.setMinimumSize(btnDimension);
        OKBtn.setMaximumSize(btnDimension);
        row.add(OKBtn);

        JButton ApplyBtn = new JButton("Apply");
        ApplyBtn.setPreferredSize(btnDimension);
        ApplyBtn.setMinimumSize(btnDimension);
        ApplyBtn.setMaximumSize(btnDimension);
        row.add(ApplyBtn);

        JCheckBox checkBox = new JCheckBox("Auto change");
        row.add(checkBox);

        JButton NormalizeBtn = new JButton("Normalize");
        NormalizeBtn.setPreferredSize(btnDimension);
        NormalizeBtn.setMinimumSize(btnDimension);
        NormalizeBtn.setMaximumSize(btnDimension);
        row.add(NormalizeBtn);

        JButton ZoomPlusBtn = new JButton("Zoom +");
        ZoomPlusBtn.setPreferredSize(btnDimension);
        ZoomPlusBtn.setMinimumSize(btnDimension);
        ZoomPlusBtn.setMaximumSize(btnDimension);
        row.add(ZoomPlusBtn);

        JButton ZoomMinusBtn = new JButton("Zoom -");
        ZoomMinusBtn.setPreferredSize(btnDimension);
        ZoomMinusBtn.setMinimumSize(btnDimension);
        ZoomMinusBtn.setMaximumSize(btnDimension);
        row.add(ZoomMinusBtn);

    }

}
