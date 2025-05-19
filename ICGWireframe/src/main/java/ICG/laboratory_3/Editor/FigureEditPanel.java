package ICG.laboratory_3.Editor;

import ICG.laboratory_3.Editor.Elements.BSpline;
import ICG.laboratory_3.Editor.Elements.Circle;

import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.*;

public class FigureEditPanel extends JPanel implements MouseListener, MouseMotionListener {
    private JScrollPane scrollPane;
    private FigureEditWindow figureEditWindow;
    private Dimension windowDimension;

    private Color backgroundColor;
    private Color gridsLineColor;
    private Color mainLineColor;
    private boolean elementsMode;
    private ArrayList<Circle> circles = new ArrayList<>();
    private int nextCircleNumber = 1;
    private int circleRadius = 20;
    private Color circleColor;
    private int mainRed = 128;
    private int mainGreen = 0;
    private int mainBlue = 255;
    private int BSplineRed = 255;
    private int BSplineGreen = 255;
    private int BSplineBlue = 0;
    private int currentPointCount = 4;
    private int M = 10;
    private int M1 = 4;


    private List<Point> bSplinePoints = new ArrayList<>();
    private int splineSegments = 1; // Количество отрезков на участок сплайна
    private boolean showBSpline = true;

    private Circle draggedCircle = null;
    private Point dragOffset = null;


    public FigureEditPanel(JScrollPane scrollPane, FigureEditWindow figureEditWindow) {
        this.scrollPane = scrollPane;
        this.figureEditWindow = figureEditWindow;
        this.windowDimension = getSize();
        this.elementsMode = true;

        setDefainPoints();

        // Устанавливаем предпочтительный размер панели
        setPreferredSize(new Dimension((int) this.windowDimension.getWidth(), (int) this.windowDimension.getHeight()));

        // Добавляем слушатели мыши
        addMouseListener(this);
        addMouseMotionListener(this);

        repaint();
    }


    public List<Point> getInfoAboutBSplinePoints() {
        System.out.println( "\ngetInfoAboutBSplinePoints:");
        for (int i=0; i<bSplinePoints.size(); i++) {
            System.out.println(bSplinePoints.get(i).x + " " + bSplinePoints.get(i).y);
        }
        System.out.println( "\n");
        System.out.println( "\npoints:");
        for (int i=0; i<circles.size(); i++) {
            System.out.println(circles.get(i).center.x+ " " + circles.get(i).center.y);
        }
        System.out.println( "\n\n\n");
        return this.bSplinePoints;
    }


    public void setM(int value) {
        this.M = value;
    }

    public void setM1(int value) {
        this.M1 = value;
    }

    public void setPointsCount(int value) {
        // Минимум 4 точки для B-сплайна
        currentPointCount = Math.max(value, 4);

        // Если нужно уменьшить количество точек
        if (currentPointCount < circles.size()) {
            while (circles.size() > currentPointCount) {
                circles.remove(circles.size() - 1);
            }
            renumberCircles();
            updateBSpline();
            repaint();
        }
        // Если нужно увеличить количество точек
        else if (currentPointCount > circles.size()) {
            // Получаем размеры панели
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Определяем начальную позицию для новых точек
            Point lastPoint = circles.isEmpty() ?
                    new Point(panelWidth/4, panelHeight/2) :
                    circles.get(circles.size() - 1).center;

            // Параметры для размещения новых точек
            int stepX = 50;
            int stepY = 50;
            int margin = circleRadius * 2; // Отступ от краев

            while (circles.size() < currentPointCount) {
                // Вычисляем новую позицию с проверкой границ
                int newX = lastPoint.x + stepX;
                int newY = lastPoint.y + stepY;

                // Проверяем правую границу
                if (newX + circleRadius > panelWidth - margin) {
                    newX = margin + circleRadius;
                    newY += stepY * 2;
                }

                // Проверяем нижнюю границу
                if (newY + circleRadius > panelHeight - margin) {
                    newY = margin + circleRadius;
                }

                // Проверяем верхнюю границу
                if (newY - circleRadius < margin) {
                    newY = margin + circleRadius;
                }

                // Создаем новую точку
                Circle newCircle = new Circle(
                        new Point(newX, newY),
                        circleRadius,
                        nextCircleNumber++
                );

                circles.add(newCircle);
                lastPoint = newCircle.center;
            }

            updateBSpline();
            repaint();
        }
    }

    private void setDefainPoints() {
        int halfHeight = (int)((this.windowDimension.getHeight())/2);
        int halfWight = (int)((this.windowDimension.getWidth())/2);

        // Параметры окна
        int windowWidth = 800;
        int windowHeight = 600;
        double scale = 1.3; // Масштаб для вписывания в окно

        // Автоматическое вычисление смещения
        double max_X = 120 * scale; // Максимальный радиус (120 из ваших координат)
        double max_Y = 420 * scale; // Общая высота (420 из ваших координат)

        double offsetX = (windowWidth / 2) - (max_X / 2);
        double offsetY = (windowHeight / 2) - (max_Y / 2);

        // Основание вазы (дно → начало расширения)
        circles.add(new Circle(new Point((int) (342), (int) (28)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (202), (int) (54)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (246), (int) (131)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (474), (int) (134)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (320), (int) (278)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (456), (int) (308)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (620), (int) (327)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (545), (int) (461)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (667), (int) (463)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (758), (int) (487)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (768), (int) (523)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (852), (int) (560)), circleRadius, nextCircleNumber++));


        updateBSpline();
        repaint();

    }

    public void setNForBSpline(int value) {
        this.splineSegments = value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int imageWidth = getWidth();
        int imageHeight = getHeight();
        int horizontalSteps = 20;
        int horizontalStepSize = (int)(imageWidth/horizontalSteps);
        int verticalStepSize = horizontalStepSize;
        int verticalSteps = (int)(imageHeight/horizontalSteps);

        backgroundColor = new Color(26, 21, 63);
        gridsLineColor = new Color(97, 97, 97);
        mainLineColor = new Color(199, 208, 204);

        drawLinesOnGrid(g2d, imageWidth, imageHeight, horizontalStepSize, verticalStepSize);
        drawLinesForSteps(g2d, imageWidth, imageHeight, horizontalStepSize, verticalStepSize);

        drawCircles(g2d);
        drawLines(g2d);
        drawNumbers(g2d);
        drawBSpline(g2d);

        g2d.dispose();
    }


    private void updateBSpline() {
        List<Point> controlPoints = new ArrayList<>();
        for (Circle circle : circles) {
            controlPoints.add(circle.center);
        }
        bSplinePoints = BSpline.generateBSpline(controlPoints, splineSegments);
    }

    private void drawBSpline(Graphics2D g2d) {
        if (bSplinePoints.size() < 2 || !showBSpline) return;

        g2d.setColor(new Color(BSplineRed, BSplineGreen, BSplineBlue));
        g2d.setStroke(new BasicStroke(2));

        Point prev = bSplinePoints.get(0);
        for (int i = 1; i < bSplinePoints.size(); i++) {
            Point current = bSplinePoints.get(i);
            g2d.drawLine(prev.x, prev.y, current.x, current.y);
            prev = current;
        }
    }


    void drawLines(Graphics2D g2d) {
        if (circles.size() < 2) return;

        g2d.setColor(this.circleColor);
        for (int i = 0; i < circles.size() - 1; i++) {
            Point p1 = circles.get(i).center;
            Point p2 = circles.get(i + 1).center;
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    void drawCircles(Graphics2D g2d) {
        this.circleColor = new Color(mainRed, mainGreen, mainBlue);
        for (Circle circle : circles) {
            g2d.setColor(this.circleColor);
            g2d.drawOval(circle.center.x - circle.radius,
                    circle.center.y - circle.radius,
                    circle.radius * 2,
                    circle.radius * 2);

            drawCenteredNumber(g2d, circle);
        }
    }

    void drawNumbers(Graphics2D g2d) {
        for (Circle circle : circles) {
            drawCenteredNumber(g2d, circle);
        }
    }

    void drawCenteredNumber(Graphics2D g2d, Circle circle) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, circle.radius)); // Размер шрифта зависит от радиуса

        String numberText = Integer.toString(circle.number);

        // Вычисляем позицию текста для центрирования
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(numberText);
        int textHeight = fm.getAscent();

        int x = circle.center.x - textWidth/2;
        int y = circle.center.y + textHeight/4; // Корректировка по вертикали

        g2d.drawString(numberText, x, y);
    }

    public void setMainRed(int color) {
        this.mainRed = color;
    }
    public void setMainGreen(int color) {
        this.mainGreen = color;
    }
    public void setMainBlue(int color) {
        this.mainBlue = color;
    }
    public void setBSplineRed(int color) {
        this.BSplineRed = color;
    }
    public void setBSplineGreen(int color) {
        this.BSplineGreen = color;
    }
    public void setBSplineBlue(int color) {
        this.BSplineBlue = color;
    }

    void drawLinesForSteps(Graphics2D g2d, int imageWidth, int imageHeight, int horizontalStepSize, int verticalStepSize) {
        g2d.setColor(mainLineColor);
        // по оси х
        int shiftForSteps = 10;
        for (int i=(int)(imageWidth/2)+horizontalStepSize; i<imageWidth; i+=horizontalStepSize) {
            g2d.drawLine(i, (int)(imageHeight/2)-shiftForSteps, i, (int)(imageHeight/2)+shiftForSteps);
        }
        for (int i=(int)(imageWidth/2)-horizontalStepSize; i>0; i-=horizontalStepSize) {
            g2d.drawLine(i, (int)(imageHeight/2)-shiftForSteps, i, (int)(imageHeight/2)+shiftForSteps);
        }

        // линии по оси y
        for (int i=(int)(imageHeight/2)+verticalStepSize; i<imageHeight; i+=verticalStepSize) {
            g2d.drawLine((int)(imageWidth/2)-shiftForSteps, i, (int)(imageWidth/2)+shiftForSteps, i);
        }
        for (int i=(int)(imageHeight/2)-verticalStepSize; i>0; i-=verticalStepSize) {
            g2d.drawLine((int)(imageWidth/2)-shiftForSteps, i, (int)(imageWidth/2)+shiftForSteps, i);
        }
    }


    void drawLinesOnGrid(Graphics2D g2d, int imageWidth, int imageHeight, int horizontalStepSize, int verticalStepSize) {


        // Заливаем фон
        g2d.setBackground(backgroundColor);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        // линии по оси х
        g2d.setColor(gridsLineColor);
        for (int i=(int)(imageWidth/2)+horizontalStepSize; i<imageWidth; i+=horizontalStepSize) {
            g2d.drawLine(i, 0, i, imageHeight);
        }
        for (int i=(int)(imageWidth/2)-horizontalStepSize; i>0; i-=horizontalStepSize) {
            g2d.drawLine(i, 0, i, imageHeight);
        }

        // линии по оси y
        for (int i=(int)(imageHeight/2)+verticalStepSize; i<imageHeight; i+=verticalStepSize) {
            g2d.drawLine(0, i, imageWidth, i);
        }
        for (int i=(int)(imageHeight/2)-verticalStepSize; i>0; i-=verticalStepSize) {
            g2d.drawLine(0, i, imageWidth, i);
        }

        g2d.setColor(mainLineColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine((int)(imageWidth/2), 0, (int)(imageWidth/2), imageHeight);
        g2d.drawLine(0, (int)(imageHeight/2), imageWidth, (int)(imageHeight/2));
    }

    public void updateScrollBars() {
        if (scrollPane != null) {
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }


    public void setElementsMode(boolean newMode) {
        this.elementsMode = newMode;
    }

    public boolean getElementsMode() {
        return this.elementsMode;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    public void updateImage() {
        updateBSpline();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // левая кнопка мыши
            if (this.elementsMode) {
                // Проверяем, не нажали ли мы на существующую окружность
                for (int i = circles.size() - 1; i >= 0; i--) {
                    Circle circle = circles.get(i);
                    if (circle.center.distance(e.getPoint()) <= circle.radius) {
                        draggedCircle = circle;
                        dragOffset = new Point(e.getX() - circle.center.x, e.getY() - circle.center.y);
                        return;
                    }
                }

                // Если не нажали на существующую окружность, создаем новую
                circles.add(new Circle(e.getPoint(), circleRadius, nextCircleNumber++));
                updateBSpline();
                repaint();
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON3) { // Правая кнопка мыши
            for (int i = circles.size() - 1; i >= 0; i--) {
                Circle circle = circles.get(i);
                if (circle.center.distance(e.getPoint()) <= circle.radius) {
                    circles.remove(i);
                    // Перенумеровываем
                    renumberCircles();
                    updateBSpline();
                    repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedCircle != null) {
            // Перемещаем окружность с учетом смещения (чтобы не прыгала к курсору)
            draggedCircle.center.x = e.getX() - dragOffset.x;
            draggedCircle.center.y = e.getY() - dragOffset.y;
            updateBSpline();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Сбрасываем перетаскиваемую окружность при отпускании кнопки
        draggedCircle = null;
        dragOffset = null;
    }

    private void renumberCircles() {
        nextCircleNumber = 1;
        for (Circle circle : circles) {
            circle.number = nextCircleNumber++;
        }
        nextCircleNumber = circles.size() + 1;
    }


    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


    @Override
    public void mouseMoved(MouseEvent e) {}
}