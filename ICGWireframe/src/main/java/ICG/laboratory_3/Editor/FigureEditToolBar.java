package ICG.laboratory_3.Editor;

import javax.swing.*;
import java.awt.*;

public class FigureEditToolBar extends JToolBar {

    FigureEditWindow editWindow;
    FigureEditPanel editPanel;
    Dimension windowDimension;

    public FigureEditToolBar(FigureEditWindow window, FigureEditPanel panel, Dimension dim) {
        this.editWindow = window;
        this.editPanel = panel;
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
        row.add(new JSpinner(new SpinnerNumberModel(4, 4, 100, 1)));
        // число опорных точек (не менее 4).
        row.add(new JLabel("K:"));
        row.add(new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)));
        row.add(new JLabel("Main color red:"));
        row.add(new JSpinner(new SpinnerNumberModel(128, 0, 255, 1)));
        row.add(new JLabel("B-spline red:"));
        row.add(new JSpinner(new SpinnerNumberModel(0, 0, 255, 1)));
    }

    void addRow2(JPanel row) {
        // число отрезков по окружностям между соседними образующими (≥ 1).
        row.add(new JLabel("M1:"));
        row.add(new JSpinner(new SpinnerNumberModel(4, 4, 100, 1)));
        // число образующих
        row.add(new JLabel("M:"));
        row.add(new JSpinner(new SpinnerNumberModel(10, 1, 100, 1)));
        row.add(new JLabel("Main color green:"));
        row.add(new JSpinner(new SpinnerNumberModel(128, 0, 255, 1)));
        row.add(new JLabel("B-spline green:"));
        row.add(new JSpinner(new SpinnerNumberModel(0, 0, 255, 1)));
    }

    void addRow3(JPanel row) {
        row.add(new JLabel("X of 7 point:"));
        row.add(new JSpinner(new SpinnerNumberModel(4, 4, 100, 1)));
        row.add(new JLabel("Y of 7 point:"));
        row.add(new JSpinner(new SpinnerNumberModel(10, 1, 100, 1)));
        row.add(new JLabel("Main color blue:"));
        row.add(new JSpinner(new SpinnerNumberModel(0, 0, 255, 1)));
        row.add(new JLabel("B-spline blue:"));
        row.add(new JSpinner(new SpinnerNumberModel(128, 0, 255, 1)));

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

        JCheckBox addingBox = new JCheckBox("Add points mode", true);
        addingBox.addActionListener(e -> {
            this.editPanel.setElementsMode(!(this.editPanel.getElementsMode()));
        });
        row.add(addingBox);
    }

}
