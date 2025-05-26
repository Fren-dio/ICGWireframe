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
    private int M1 = 1;
    private int smooth = 1;

    private List<Point> bSplinePoints = new ArrayList<>();
    private int splineSegments = 1;
    private boolean showBSpline = true;

    private Circle draggedCircle = null;
    private Point dragOffset = null;

    // Добавленные поля для масштабирования
    private double scale = 1.0;
    private static final double ZOOM_FACTOR = 0.1; // 10% увеличение/уменьшение
    private ArrayList<Circle> originalCircles = new ArrayList<>(); // Храним оригинальные координаты



    private Point translation = new Point(0, 0); // Для перемещения изображения
    private Point lastDragPoint; // Для отслеживания перемещения мыши
    private static final int PAN_SPEED = 10; // Скорость перемещения стрелками
    private Point gridOffset = new Point(0, 0);


    public FigureEditPanel(JScrollPane scrollPane, FigureEditWindow figureEditWindow) {
        this.scrollPane = scrollPane;
        this.figureEditWindow = figureEditWindow;
        this.windowDimension = getSize();
        this.elementsMode = true;

        setDefainPoints();

        setPreferredSize(new Dimension((int) this.windowDimension.getWidth(), (int) this.windowDimension.getHeight()));

        addMouseListener(this);
        addMouseMotionListener(this);

        repaint();

        saveOriginalCoordinates();
    }


    public void normalizeCoordinates() {
        if (circles.isEmpty()) return;

        // Находим минимальные и максимальные координаты
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Circle circle : circles) {
            Point p = circle.center;
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        // Вычисляем центр фигуры
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;

        // Вычисляем смещение к центру панели
        int panelCenterX = getWidth() / 2;
        int panelCenterY = getHeight() / 2;
        int dx = panelCenterX - centerX;
        int dy = panelCenterY - centerY;

        // Применяем смещение ко всем точкам
        for (Circle circle : circles) {
            circle.center.translate(dx, dy);
        }

        // Обновляем оригинальные координаты
        saveOriginalCoordinates();
        updateBSpline();
        repaint();
    }


    private void saveOriginalCoordinates() {
        originalCircles.clear();
        for (Circle circle : circles) {
            originalCircles.add(new Circle(new Point(circle.center), circle.radius, circle.number));
        }
    }

    // Методы для масштабирования
    public void zoomIn() {
        translation.setLocation(0, 0);
        scale += ZOOM_FACTOR;
        applyZoom();
    }

    public void zoomOut() {
        translation.setLocation(0, 0);
        scale = Math.max(0.1, scale - ZOOM_FACTOR); // Минимальный масштаб 10%
        applyZoom();
    }

    public void resetZoom() {
        scale = 1.0;
        applyZoom();
    }

    private void applyZoom() {
        // Восстанавливаем оригинальные координаты
        translation.setLocation(0, 0);
        for (int i = 0; i < originalCircles.size(); i++) {
            circles.get(i).center.setLocation(originalCircles.get(i).center);
        }

        // Обновляем B-сплайн
        updateBSpline();

        // Перерисовываем панель
        revalidate();
        repaint();
    }

    private Point getOriginalPoint(Point mousePoint) {
        return new Point(
                (int)((mousePoint.x - translation.x) / scale),
                (int)((mousePoint.y - translation.y) / scale)
        );
    }

    @Override
    protected void paintComponent(Graphics g) {

        backgroundColor = new Color(26, 21, 63);
        gridsLineColor = new Color(97, 97, 97);
        mainLineColor = new Color(199, 208, 204);

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setBackground(backgroundColor);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        g2d.translate(translation.x, translation.y);
        g2d.scale(scale, scale);

        int gridTranslateX = (int)(translation.x / scale);
        int gridTranslateY = (int)(translation.y / scale);

        drawBackgroundAndGrid(g2d, gridTranslateX, gridTranslateY);

        drawCircles(g2d);
        drawLines(g2d);
        drawNumbers(g2d);
        drawBSpline(g2d);

        g2d.dispose();
    }


    private void drawBackgroundAndGrid(Graphics2D g2d, int gridTranslateX, int gridTranslateY) {
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int imageWidth = (int)(getWidth() / scale);
        int imageHeight = (int)(getHeight() / scale);
        int horizontalSteps = 20;
        int horizontalStepSize = (int)(imageWidth/horizontalSteps);
        int verticalStepSize = horizontalStepSize;

        int centerX = imageWidth/2 - gridTranslateX;
        int centerY = imageHeight/2 - gridTranslateY;

        drawGridLines(g2d, imageWidth, imageHeight, horizontalStepSize, verticalStepSize,
                centerX, centerY, gridTranslateX, gridTranslateY);

        drawAxes(g2d, imageWidth, imageHeight, centerX, centerY);
    }


    private void drawGridLines(Graphics2D g2d, int imageWidth, int imageHeight,
                               int horizontalStepSize, int verticalStepSize,
                               int centerX, int centerY, int offsetX, int offsetY) {
        g2d.setColor(gridsLineColor);

        // Получаем видимую область с учетом масштаба и перемещения
        Rectangle visibleRect = getVisibleRect();
        int visibleWidth = (int)(visibleRect.width / scale);
        int visibleHeight = (int)(visibleRect.height / scale);
        int visibleX = (int)(-translation.x / scale);
        int visibleY = (int)(-translation.y / scale);

        // Рассчитываем начальные позиции для линий сетки
        int startX = (visibleX / horizontalStepSize) * horizontalStepSize;
        int startY = (visibleY / verticalStepSize) * verticalStepSize;

        // Рисуем горизонтальные линии (ось Y) по всей видимой области
        //for (int y = startY; y < startY + visibleHeight + verticalStepSize; y += verticalStepSize) {
        //    int screenY = y - offsetY;
        //    g2d.drawLine(visibleX, screenY, visibleX + visibleWidth, screenY);
        //}
        int tickSize = 10;
        int step = 50; // Шаг делений
        int startYTick = (visibleY / step) * step;
        for (int y = -10*(visibleY + visibleHeight)/step; y < 10*(visibleY + visibleHeight)/step; y += 1) {
            g2d.drawLine(imageWidth/2 - 10*imageWidth, imageHeight/2+y*step, imageWidth/2 + 10*imageWidth, imageHeight/2+y*step);
        }

        // Рисуем вертикальные линии (ось X) по всей видимой области
        int startXTick = (visibleX / step) * step;
        for (int x = -10*(visibleX + visibleWidth)/step; x < 10*(visibleX + visibleWidth)/step; x += 1) {
            g2d.drawLine(imageWidth/2+x*step, imageHeight/2 - 10*imageHeight, imageWidth/2+x*step, imageHeight/2 + 10*imageHeight);
        }
    }


    private void drawAxes(Graphics2D g2d, int imageWidth, int imageHeight, int centerX, int centerY) {
        g2d.setColor(mainLineColor);
        g2d.setStroke(new BasicStroke(3));

        // Получаем видимую область
        Rectangle visibleRect = getVisibleRect();
        int visibleX = (int)(-translation.x / scale);
        int visibleY = (int)(-translation.y / scale);
        int visibleWidth = (int)(visibleRect.width / scale);
        int visibleHeight = (int)(visibleRect.height / scale);

        // Ось Y (вертикальная) - рисуем на всю видимую высоту
        int axisY = centerX;
        g2d.drawLine(imageWidth/2, visibleY, imageWidth/2, visibleY + visibleHeight);

        // Ось X (горизонтальная) - рисуем на всю видимую ширину
        int axisX = centerY;
        g2d.drawLine(visibleX, imageHeight/2, visibleX + visibleWidth, imageHeight/2);

        // Деления на осях (только в видимой области)
        drawAxisTicks(g2d, visibleX, visibleY, imageWidth, imageHeight, visibleWidth, visibleHeight, centerX, centerY);
    }

    // Обновленный метод для делений на осях
    private void drawAxisTicks(Graphics2D g2d, int visibleX, int visibleY,
                               int imageWidth, int imageHeight,
                               int visibleWidth, int visibleHeight,
                               int centerX, int centerY) {


        int tickSize = 10;
        int step = 50; // Шаг делений
        int startYTick = (visibleY / step) * step;
        for (int y = -10*(visibleY + visibleHeight)/step; y < 10*(visibleY + visibleHeight)/step; y += 1) {
            g2d.drawLine(imageWidth/2 - tickSize/2, imageHeight/2+y*step, imageWidth/2 + tickSize/2, imageHeight/2+y*step);
        }

        // Рисуем вертикальные линии (ось X) по всей видимой области
        int startXTick = (visibleX / step) * step;
        for (int x = -10*(visibleX + visibleWidth)/step; x < 10*(visibleX + visibleWidth)/step; x += 1) {
            g2d.drawLine(imageWidth/2+x*step, imageHeight/2 - tickSize/2, imageWidth/2+x*step, imageHeight/2 + tickSize/2);
        }
    }


    public int getM() {
        return this.M;
    }

    public int getM1() {
        return this.M1;
    }
    public int getN() {
        return this.splineSegments;
    }
    public int getK() {
        return this.currentPointCount;
    }


    public int getSmooth() {
        return this.smooth;
    }
    public int getMainRed() {
        return this.mainRed;
    }
    public int getMainGreen() {
        return this.mainGreen;
    }
    public int getMainBlue() {
        return this.mainBlue;
    }
    public int getBSplineRed() {
        return this.BSplineRed;
    }
    public int getBSplineGreen() {
        return this.BSplineGreen;
    }
    public int getBSplineBlue() {
        return this.BSplineBlue;
    }




    public List<Point> getInfoAboutBSplinePoints() {
        //System.out.println( "\ngetInfoAboutBSplinePoints:");
        //for (int i=0; i<bSplinePoints.size(); i++) {
        //    System.out.println(bSplinePoints.get(i).x + " " + bSplinePoints.get(i).y);
        //}
        List<Point> centeredPoints = new ArrayList<>();

        // Получаем центр панели (координата X центральной оси)
        int centerX = getWidth() / 2;

        // Преобразуем координаты относительно центральной оси
        for (Point p : bSplinePoints) {
            // Переводим в мировые координаты (учитывая масштаб)
            int worldX = (int)(p.x / scale);
            int worldY = (int)(p.y / scale);

            // Смещаем относительно центральной оси
            centeredPoints.add(new Point(worldX - centerX, worldY));
        }

        //System.out.println("\nCentered BSpline points (relative to coordinate axis):");
        //for (Point p : centeredPoints) {
        //    System.out.println(p.x + " " + p.y);
        //}

        return centeredPoints;
    }


    ArrayList<Circle> getCircles() {
        return this.circles;
    }

    void setCircles(ArrayList<Circle> circles, int radius, int number) {
        this.circles = circles;
        this.circleRadius = radius;
        this.nextCircleNumber = number;

        renumberCircles();
        updateBSpline();
        repaint();
    }


    public void setM(int value) {
        this.M = value;
    }

    public void setM1(int value) {
        this.M1 = value;
    }
    public void setK(int value) {
        this.currentPointCount = value;
    }

    public void setN(int value) {
        this.splineSegments = value;
    }

    public void setSmooth(int value) {
        this.smooth = value;
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
        circles.add(new Circle(new Point((int) (630), (int) (327)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (630), (int) (490)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (630), (int) (490)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (620), (int) (520)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (600), (int) (520)), circleRadius, nextCircleNumber++));
        circles.add(new Circle(new Point((int) (580), (int) (520)), circleRadius, nextCircleNumber++));

        updateBSpline();
        repaint();

    }

    public void setNForBSpline(int value) {
        this.splineSegments = value;
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
        if (SwingUtilities.isMiddleMouseButton(e)) {
            lastDragPoint = e.getPoint();
            return;
        }
        else if (e.getButton() == MouseEvent.BUTTON1) {
            if (this.elementsMode) {
                Point originalPoint = getOriginalPoint(e.getPoint());

                // Проверка нажатия на существующую окружность
                for (int i = circles.size() - 1; i >= 0; i--) {
                    Circle circle = circles.get(i);
                    if (circle.center.distance(originalPoint) <= circle.radius) {
                        draggedCircle = circle;
                        dragOffset = new Point(
                                originalPoint.x - circle.center.x,
                                originalPoint.y - circle.center.y
                        );
                        return;
                    }
                }

                // Создание новой окружности
                Circle newCircle = new Circle(originalPoint, circleRadius, nextCircleNumber++);
                circles.add(newCircle);
                originalCircles.add(new Circle(new Point(originalPoint), circleRadius, newCircle.number));
                updateBSpline();
                repaint();
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            Point originalPoint = getOriginalPoint(e.getPoint());
            for (int i = circles.size() - 1; i >= 0; i--) {
                Circle circle = circles.get(i);
                if (circle.center.distance(originalPoint) <= circle.radius) {
                    circles.remove(i);
                    originalCircles.remove(i);
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
        if (SwingUtilities.isMiddleMouseButton(e) && lastDragPoint != null) {
            Point currentPoint = e.getPoint();
            int dx = currentPoint.x - lastDragPoint.x;
            int dy = currentPoint.y - lastDragPoint.y;

            translation.translate(dx, dy);
            lastDragPoint = currentPoint;
            repaint();
            return;
        }
        else if (draggedCircle != null) {
            Point originalPoint = getOriginalPoint(e.getPoint());
            draggedCircle.center.x = originalPoint.x - dragOffset.x;
            draggedCircle.center.y = originalPoint.y - dragOffset.y;

            // Обновляем оригинальные координаты
            for (int i = 0; i < circles.size(); i++) {
                if (circles.get(i) == draggedCircle) {
                    originalCircles.get(i).center.setLocation(draggedCircle.center);
                    break;
                }
            }

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