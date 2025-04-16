package ICG.laboratory_3.Editor;

import ICG.laboratory_3.FrameWork;
import ICG.laboratory_3.ImagePanel;
import ICG.laboratory_3.ToolBarMenu;
import ICG.laboratory_3.Utils.BoxLayoutUtils;
import ICG.laboratory_3.Utils.WindowSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class FigureEditWindow extends JFrame {

    private Dimension windowDimension;
    private Dimension screenDimension;
    private Dimension screenShiftDimension;
    private FigureEditPanel figureEditPanel;
    private FigureEditToolBar figureEditToolBar;
    private JScrollPane scrollPane;



    public FigureEditWindow() {
        super("Figure editor");

        setWindowDimension();
        setWindowLocation();

        addFigureEditPanel();
        addToolBarMenu();

        pack();
        setVisible(true);
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
