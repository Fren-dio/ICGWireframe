package ICG.laboratory_3;

import ICG.laboratory_3.Utils.BoxLayoutUtils;
import ICG.laboratory_3.Utils.WindowSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class FrameWork extends JFrame {

    private Dimension windowDimension;
    private Dimension screenDimension;
    private Dimension screenShiftDimension;
    private ImagePanel imagePanel;
    private ToolBarMenu toolBarMenu;
    private JScrollPane scrollPane;

    public static void main(String[] args) {
        new FrameWork();
    }



    FrameWork() {
        super("ICGWireframe");

        setWindowDimension();
        setWindowLocation();

        addToolBarMenu();
        addImagePanel();
        this.toolBarMenu.addImagePanel(this.imagePanel);

        pack();
        setVisible(true);
    }

    void addImagePanel() {

        installScrollPane();

        this.imagePanel = new ImagePanel(scrollPane, this);
        scrollPane.setViewportView(imagePanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                imagePanel.updateScrollBars();
            }
        });
    }

    void installScrollPane() {
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(imagePanel);

        add(scrollPane, BorderLayout.CENTER);
    }

    List<Point> getInfoAboutBSplinePoints() {
        return this.toolBarMenu.getInfoAboutBSplinePoints();
    }

    void setbSplinePoints(List<Point> bSplinePoints) {
        imagePanel.setbSplinePoints(bSplinePoints);
    }

    void addToolBarMenu() {
        BoxLayoutUtils blUtils = new BoxLayoutUtils();
        JPanel utilsPanel = blUtils.createHorizontalPanel();
        this.toolBarMenu = new ToolBarMenu(this, this.windowDimension);
        utilsPanel.add(toolBarMenu);
        getContentPane().add(utilsPanel, "North");
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
