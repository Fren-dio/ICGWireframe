package ICG.laboratory_3;

import ICG.laboratory_3.Editor.Elements.Circle;
import ICG.laboratory_3.Editor.FigureEditPanel;
import ICG.laboratory_3.Editor.FigureEditWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
        addReset();
        toOXY();
        toOXZ();
        toOYZ();
        toOXYZ();

        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> imagePanel.zoomIn());
        this.add(zoomInButton);
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> imagePanel.zoomOut());
        this.add(zoomOutButton);
        JButton resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(e -> imagePanel.resetZoom());
        this.add(resetZoomButton);

        zoomInButton.setToolTipText("Zoom in");
        zoomOutButton.setToolTipText("Zoom out");
        resetZoomButton.setToolTipText("Reset zoom to default");



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

    void addReset() {
        JButton btn = new JButton("Reset rotate");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> imagePanel.resetRotate());

        this.add(btn);
    }

    void toOXY() {
        JButton btn = new JButton("OXY");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> imagePanel.rotateOXY());

        this.add(btn);
    }

    void toOXZ() {
        JButton btn = new JButton("OXZ");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> imagePanel.rotateOXZ());

        this.add(btn);
    }

    void toOYZ() {
        JButton btn = new JButton("OYZ");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> imagePanel.rotateOYZ());

        this.add(btn);
    }

    void toOXYZ() {
        JButton btn = new JButton("3D");

        Dimension figureEditorBtnDimension = new Dimension(140, 40);

        btn.setPreferredSize(figureEditorBtnDimension);
        btn.setMinimumSize(figureEditorBtnDimension);
        btn.setMaximumSize(figureEditorBtnDimension);

        btn.addActionListener(e -> imagePanel.rotateOXYZ());

        this.add(btn);
    }


    void openEditorForm() {
        this.figureEditWindow.setVisible(true);
    }

    List<Point> getInfoAboutBSplinePoints() {
        return this.figureEditWindow.getInfoAboutBSplinePoints();
    }
    ArrayList<Circle> getCircles() {
        return this.figureEditWindow.getCircles();
    }
    void setCircles(ArrayList<Circle> circles, int radius, int number) {
        this.figureEditWindow.setCircles(circles, radius, number);
    }

    public int getM() {
        return figureEditWindow.getM();
    }

    public int getM1() {
        return figureEditWindow.getM1();
    }
    public int getN() {
        return this.figureEditWindow.getN();
    }
    public int getK() {
        return this.figureEditWindow.getK();
    }

    public int getSmooth() {
        return figureEditWindow.getSmooth();
    }
    public int getMainRed() {
        return this.figureEditWindow.getMainRed();
    }
    public int getMainGreen() {
        return this.figureEditWindow.getMainGreen();
    }
    public int getMainBlue() {
        return this.figureEditWindow.getMainBlue();
    }
    public int getBSplineRed() {
        return this.figureEditWindow.getBSplineRed();
    }
    public int getBSplineGreen() {
        return this.figureEditWindow.getBSplineGreen();
    }
    public int getBSplineBlue() {
        return this.figureEditWindow.getBSplineBlue();
    }


    public void setM(int value) {
        this.figureEditWindow.setM(value);
    }
    public void setM1(int value) {
        this.figureEditWindow.setM1(value);
    }
    public void setK(int value) {
        this.figureEditWindow.setK(value);
    }
    public void setN(int value) {
        this.figureEditWindow.setN(value);
    }
    public void setSmooth(int value) {
        this.figureEditWindow.setSmooth(value);
    }
    public void setMainRed(int value) {
        this.figureEditWindow.setMainRed(value);
    }
    public void setMainGreen(int value) {
        this.figureEditWindow.setMainGreen(value);
    }
    public void setMainBlue(int value) {
        this.figureEditWindow.setMainBlue(value);
    }
    public void setBSplineRed(int value) {
        this.figureEditWindow.setBSplineRed(value);
    }
    public void setBSplineGreen(int value) {
        this.figureEditWindow.setBSplineGreen(value);
    }
    public void setBSplineBlue(int value) {
        this.figureEditWindow.setBSplineBlue(value);
    }

    public void setbSplinePoints(List<Point> bSplinePoints) {
        frameWork.setbSplinePoints(bSplinePoints);
    }


}
