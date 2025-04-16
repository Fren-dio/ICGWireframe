package ICG.laboratory_3;

import ICG.laboratory_3.Editor.FigureEditPanel;
import ICG.laboratory_3.Editor.FigureEditWindow;

import javax.swing.*;
import java.awt.*;

public class ToolBarMenu extends JToolBar {


    private FrameWork frameWork;

    private final ImagePanel imagePanel;
    private Dimension windowDimension;

    public ToolBarMenu(FrameWork frameWork, ImagePanel imagePanel, Dimension windowDimension) {
        this.imagePanel = imagePanel;
        this.frameWork = frameWork;
        this.windowDimension = windowDimension;

        Dimension fixedSize = new Dimension((int) this.windowDimension.getWidth(), 50);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        this.setFloatable(false);

        addPointsEditor();


    }


    void addPointsEditor() {
        JButton btn = new JButton("Figure editor");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> openEditorForm());

        this.add(btn);
    }


    void openEditorForm() {
        new FigureEditWindow();
    }


}
