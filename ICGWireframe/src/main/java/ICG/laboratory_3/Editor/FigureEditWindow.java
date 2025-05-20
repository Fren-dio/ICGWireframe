package ICG.laboratory_3.Editor;

import ICG.laboratory_3.Editor.Elements.Circle;
import ICG.laboratory_3.FrameWork;
import ICG.laboratory_3.ImagePanel;
import ICG.laboratory_3.ToolBarMenu;
import ICG.laboratory_3.Utils.BoxLayoutUtils;
import ICG.laboratory_3.Utils.WindowSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class FigureEditWindow extends JFrame {

    private Dimension windowDimension;
    private Dimension screenDimension;
    private Dimension screenShiftDimension;
    private FigureEditPanel figureEditPanel;
    private FigureEditToolBar figureEditToolBar;
    private JScrollPane scrollPane;

    private ToolBarMenu toolBarMenu;


    public FigureEditWindow(ToolBarMenu toolBarMenu) {
        super("Figure editor");

        setWindowDimension();
        setWindowLocation();

        this.toolBarMenu = toolBarMenu;

        addFigureEditPanel();
        addToolBarMenu();
        pack();
        setVisible(false);
    }





    void addFigureEditPanel() {

        installScrollPane();

        this.figureEditPanel = new FigureEditPanel(scrollPane, this);
        scrollPane.setViewportView(figureEditPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                figureEditPanel.updateScrollBars();
            }
        });
    }

    public List<Point> getInfoAboutBSplinePoints() {
        return figureEditPanel.getInfoAboutBSplinePoints();
    }
    public ArrayList<Circle> getCircles() {
        return this.figureEditPanel.getCircles();
    }
    public void setCircles(ArrayList<Circle> circles, int radius, int number) {
        this.figureEditPanel.setCircles(circles, radius, number);
    }

    public int getM() {
        return figureEditPanel.getM();
    }

    public int getM1() {
        return figureEditPanel.getM1();
    }
    public int getN() {
        return this.figureEditPanel.getN();
    }
    public int getK() {
        return this.figureEditPanel.getK();
    }

    public int getSmooth() {
        return figureEditPanel.getSmooth();
    }
    public int getMainRed() {
        return this.figureEditPanel.getMainRed();
    }
    public int getMainGreen() {
        return this.figureEditPanel.getMainGreen();
    }
    public int getMainBlue() {
        return this.figureEditPanel.getMainBlue();
    }
    public int getBSplineRed() {
        return this.figureEditPanel.getBSplineRed();
    }
    public int getBSplineGreen() {
        return this.figureEditPanel.getBSplineGreen();
    }
    public int getBSplineBlue() {
        return this.figureEditPanel.getBSplineBlue();
    }



    public void setM(int value) {
        this.figureEditPanel.setM(value);
    }
    public void setM1(int value) {
        this.figureEditPanel.setM1(value);
    }
    public void setK(int value) {
        this.figureEditPanel.setK(value);
    }
    public void setN(int value) {
        this.figureEditPanel.setN(value);
    }
    public void setSmooth(int value) {
        this.figureEditPanel.setSmooth(value);
    }
    public void setMainRed(int value) {
        this.figureEditPanel.setMainRed(value);
    }
    public void setMainGreen(int value) {
        this.figureEditPanel.setMainGreen(value);
    }
    public void setMainBlue(int value) {
        this.figureEditPanel.setMainBlue(value);
    }
    public void setBSplineRed(int value) {
        this.figureEditPanel.setBSplineRed(value);
    }
    public void setBSplineGreen(int value) {
        this.figureEditPanel.setBSplineGreen(value);
    }
    public void setBSplineBlue(int value) {
        this.figureEditPanel.setBSplineBlue(value);
    }


    public void setbSplinePoints(List<Point> bSplinePoints) {
        toolBarMenu.setbSplinePoints(bSplinePoints);
    }


    void installScrollPane() {
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(figureEditPanel);

        add(scrollPane, BorderLayout.CENTER);
    }

    void addToolBarMenu() {
        BoxLayoutUtils blUtils = new BoxLayoutUtils();
        JPanel utilsPanel = blUtils.createHorizontalPanel();
        this.figureEditToolBar = new FigureEditToolBar(this, this.figureEditPanel, this.windowDimension);
        utilsPanel.add(figureEditToolBar);
        getContentPane().add(utilsPanel, BorderLayout.SOUTH);
    }

    void setWindowDimension() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension shift = new Dimension(20, 20);
        WindowSettings windowSettings = new WindowSettings(new Dimension(
                (int) (screenSize.getWidth() - shift.getWidth()),
                (int) (screenSize.getHeight() - shift.getHeight())
        ));
        Dimension setWindowDimension = windowSettings.getDimension();
        setPreferredSize(setWindowDimension);
        setSize(setWindowDimension);
        setMinimumSize(setWindowDimension);
        setResizable(true);

        this.windowDimension = setWindowDimension;
        this.screenDimension = screenSize;
        this.screenShiftDimension = shift;
    }

    void setWindowLocation() {
        setLocation(
                (int)((screenDimension.getWidth() - windowDimension.getWidth()) / 2),
                (int)((screenDimension.getHeight() - windowDimension.getHeight()) / 2)
        );
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

}
