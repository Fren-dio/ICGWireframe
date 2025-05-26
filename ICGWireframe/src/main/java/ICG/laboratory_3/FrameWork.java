package ICG.laboratory_3;

import ICG.laboratory_3.Editor.Elements.Circle;
import ICG.laboratory_3.Utils.BoxLayoutUtils;
import ICG.laboratory_3.Utils.WindowSettings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;


public class FrameWork extends JFrame {

    private Dimension windowDimension;
    private Dimension screenDimension;
    private Dimension screenShiftDimension;
    private ImagePanel imagePanel;
    private ToolBarMenu toolBarMenu;
    private JScrollPane scrollPane;
    private ArrayList<Circle> circles = new ArrayList<>();

    public static void main(String[] args) {
        new FrameWork();
    }



    FrameWork() {
        super("ICGWireframe");

        setWindowDimension();
        setWindowLocation();

        createMenuBar();
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

    void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu("Help");

        String message = new String("О программе".getBytes(), StandardCharsets.UTF_8);
        JMenuItem aboutItem = new JMenuItem(message);
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);





        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Сохранить параметры");
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".json")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
                }
                saveParametersToFile(fileToSave.getAbsolutePath());
            }
        });
        fileMenu.add(saveItem);

        // Load Item
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Загрузить параметры");
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                loadParametersFromFile(fileToLoad.getAbsolutePath());
            }
        });
        fileMenu.add(loadItem);

        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);
    }

    private void saveParametersToFile(String filePath) {
        try {
            JSONObject jsonData = new JSONObject();

            jsonData.put("M", getM());
            jsonData.put("M1", getM1());
            jsonData.put("N", getN());
            jsonData.put("K", getK());
            jsonData.put("smooth", getSmooth());

            jsonData.put("mainRed", getMainRed());
            jsonData.put("mainGreen", getMainGreen());
            jsonData.put("mainBlue", getMainBlue());
            jsonData.put("BSplineRed", getBSplineRed());
            jsonData.put("BSplineGreen", getBSplineGreen());
            jsonData.put("BSplineBlue", getBSplineBlue());

            JSONArray circlesArray = new JSONArray();
            this.circles = getCircles();
            for (Circle circle : this.circles) {
                JSONObject circleObj = new JSONObject();
                circleObj.put("number", circle.number);
                circleObj.put("x", circle.center.x);
                circleObj.put("y", circle.center.y);
                circleObj.put("radius", circle.radius);
                circlesArray.put(circleObj);
            }
            jsonData.put("circles", circlesArray);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonData.toString(4));
                JOptionPane.showMessageDialog(this, "Success saved", "Save", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Saved error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadParametersFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonData = new JSONObject(new JSONTokener(reader));

            int M = jsonData.getInt("M");
            setM(M);
            int M1 = jsonData.getInt("M1");
            setM1(M1);
            int splineSegments = jsonData.getInt("N");
            setN(splineSegments);
            int currentPointCount = jsonData.getInt("K");
            setK(currentPointCount);
            int smooth = jsonData.getInt("smooth");
            setSmooth(smooth);

            int mainRed = jsonData.getInt("mainRed");
            setMainRed(mainRed);
            int mainGreen = jsonData.getInt("mainGreen");
            setMainGreen(mainGreen);
            int mainBlue = jsonData.getInt("mainBlue");
            setMainBlue(mainBlue);
            int BSplineRed = jsonData.getInt("BSplineRed");
            setBSplineRed(BSplineRed);
            int BSplineGreen = jsonData.getInt("BSplineGreen");
            setBSplineGreen(BSplineGreen);
            int BSplineBlue = jsonData.getInt("BSplineBlue");
            setBSplineBlue(BSplineBlue);

            this.circles.clear();
            JSONArray circlesArray = jsonData.getJSONArray("circles");
            int nextCircleNumber = 1;

            int radius = 20;
            int number = 1;
            for (int i = 0; i < circlesArray.length(); i++) {
                JSONObject circleObj = circlesArray.getJSONObject(i);
                Point center = new Point(circleObj.getInt("x"), circleObj.getInt("y"));
                radius = circleObj.getInt("radius");
                number = circleObj.getInt("number");

                this.circles.add(new Circle(center, radius, number));
                nextCircleNumber = Math.max(nextCircleNumber, number + 1);
            }
            setCircles(this.circles, radius, number);

            imagePanel.rotateOXYZ();
            imagePanel.resetZoom();
            imagePanel.repaint();

            JOptionPane.showMessageDialog(this, "Success loaded", "Load", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load error:  you can load .json file in correct format (file must be saved in this app or you can look file's structure and write same)", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        String aboutMessage = new String(("<html> " +
                "<h2>ICGWireframe</h2>" +
                "<p><b>Версия:</b> 1.0</p>" +
                "<p><b>Автор:</b> Кулишова Анастасия</p>" +
                "<p><b>Описание:</b> Программа для построения проволочной модели тела вращения</p>" +
                "<p>Образующая строится по заданным опорным точкам с помощью B-сплайна</p>" +
                "<p>Трехмерная модель формируется вращением образующей вокруг оси Z</p>" +
                "<hr>" +
                "<p><b>Функционал:</b></p>" +
                "<ul>" +
                "<li>Редактор образующей с добавлением/удалением/перемещением опорных точек</li>" +
                "<li>Построение B-сплайна по опорным точкам</li>" +
                "<li>Визуализация 3D модели вращения по заданным параметрам</li>" +
                "<li>Возможность вращения фигуры</li>" +
                "<li>Настройка параметров визуализации</li>" +
                "<li>Сохранение построенной фигуры в файле, загрузка фигуры из файла</li>" +
                "</html>").getBytes(), StandardCharsets.UTF_8);

        JOptionPane.showMessageDialog(
                this,
                aboutMessage,
                "О программе ICGWireframe",
                JOptionPane.INFORMATION_MESSAGE
        );
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
    ArrayList<Circle> getCircles() {
        return this.toolBarMenu.getCircles();
    }

    void setCircles(ArrayList<Circle> circles, int radius, int number) {
        this.toolBarMenu.setCircles(circles, radius, number);
    }

    public int getM() {
        return this.toolBarMenu.getM();
    }

    public int getM1() {
        return this.toolBarMenu.getM1();
    }
    public int getN() {
        return this.toolBarMenu.getN();
    }
    public int getK() {
        return this.toolBarMenu.getK();
    }

    public int getSmooth() {
        return this.toolBarMenu.getSmooth();
    }
    public int getMainRed() {
        return this.toolBarMenu.getMainRed();
    }
    public int getMainGreen() {
        return this.toolBarMenu.getMainGreen();
    }
    public int getMainBlue() {
        return this.toolBarMenu.getMainBlue();
    }
    public int getBSplineRed() {
        return this.toolBarMenu.getBSplineRed();
    }
    public int getBSplineGreen() {
        return this.toolBarMenu.getBSplineGreen();
    }
    public int getBSplineBlue() {
        return this.toolBarMenu.getBSplineBlue();
    }

    public void setM(int value) {
        this.toolBarMenu.setM(value);
    }
    public void setM1(int value) {
        this.toolBarMenu.setM1(value);
    }
    public void setK(int value) {
        this.toolBarMenu.setK(value);
    }
    public void setN(int value) {
        this.toolBarMenu.setN(value);
    }
    public void setSmooth(int value) {
        this.toolBarMenu.setSmooth(value);
    }
    public void setMainRed(int value) {
        this.toolBarMenu.setMainRed(value);
    }
    public void setMainGreen(int value) {
        this.toolBarMenu.setMainGreen(value);
    }
    public void setMainBlue(int value) {
        this.toolBarMenu.setMainBlue(value);
    }
    public void setBSplineRed(int value) {
        this.toolBarMenu.setBSplineRed(value);
    }
    public void setBSplineGreen(int value) {
        this.toolBarMenu.setBSplineGreen(value);
    }
    public void setBSplineBlue(int value) {
        this.toolBarMenu.setBSplineBlue(value);
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
        setMinimumSize(new WindowSettings(new Dimension(640,480)).getDimension());
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
