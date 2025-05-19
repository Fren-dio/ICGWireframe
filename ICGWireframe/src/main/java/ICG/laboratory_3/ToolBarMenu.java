package ICG.laboratory_3;

import ICG.laboratory_3.Editor.FigureEditPanel;
import ICG.laboratory_3.Editor.FigureEditWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ToolBarMenu extends JToolBar {


    private FrameWork frameWork;

    private ImagePanel imagePanel;
    private Dimension windowDimension;
    private FigureEditWindow figureEditWindow;

    public ToolBarMenu(FrameWork frameWork, Dimension windowDimension) {
        this.frameWork = frameWork;
        this.windowDimension = windowDimension;

        Dimension fixedSize = new Dimension((int) this.windowDimension.getWidth(), 50);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        this.figureEditWindow = new FigureEditWindow(this);

        this.setFloatable(false);

        addPointsEditor();
    }

    void addImagePanel(ImagePanel imagePanel) {
        this.imagePanel = imagePanel;
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
        this.figureEditWindow.setVisible(true);
    }

    List<Point> getInfoAboutBSplinePoints() {
        return this.figureEditWindow.getInfoAboutBSplinePoints();
    }

    public void setbSplinePoints(List<Point> bSplinePoints) {
        frameWork.setbSplinePoints(bSplinePoints);
    }


}
