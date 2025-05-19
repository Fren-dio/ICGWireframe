package ICG.laboratory_3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {
    private JScrollPane scrollPane;
    private FrameWork frameWork;

    // Параметры осей
    private final int axisLength = 50;
    private final Point coordinateCenter = new Point(80, 80);

    // Углы поворота (в радианах)
    private double rotationX = 0;
    private double rotationY = 0;
    private int prevMouseX, prevMouseY;

    // Параметры фигуры
    private List<Point> bSplinePoints = new ArrayList<>();
    private int M = 12; // Количество образующих
    private int M1 = 1; // Число отрезков между образующими
    private double zoom = 1.0;
    private final double cameraDistance = 5.0;



    private final Point3D cameraPos = new Point3D(-10, 0, 0);
    private final Point3D viewPoint = new Point3D(10, 0, 0);
    private final Point3D upVector = new Point3D(0, 1, 0);
    private double zn = 5.0; // расстояние до плоскости проекции
    private double zf = 20.0; // дальняя плоскость отсечения


    public ImagePanel(JScrollPane scrollPane, FrameWork frameWork) {
        this.scrollPane = scrollPane;
        this.frameWork = frameWork;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        //getInfoAboutBSplinePoints();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    void getInfoAboutBSplinePoints() {
        bSplinePoints = frameWork.getInfoAboutBSplinePoints();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("paintComponent called");
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        draw_OX_OY_OZ(g2d);
        draw3DFigure(g2d);
    }

    void draw_OX_OY_OZ(Graphics2D g) {
        int centerX = coordinateCenter.x;
        int centerY = coordinateCenter.y;

        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();

        g.setStroke(new BasicStroke(2));

        Point2D.Double xAxis = calculateAxisDirection(1, 0, 0);
        Point2D.Double yAxis = calculateAxisDirection(0, 1, 0);
        Point2D.Double zAxis = calculateAxisDirection(0, 0, 1);

        g.setColor(Color.RED);
        drawAxis(g, centerX, centerY, xAxis, "X");

        g.setColor(Color.GREEN);
        drawAxis(g, centerX, centerY, yAxis, "Y");

        g.setColor(Color.BLUE);
        drawAxis(g, centerX, centerY, zAxis, "Z");

        g.setColor(originalColor);
        g.setStroke(originalStroke);
    }






    private void draw3DFigure(Graphics2D g2d) {
        System.out.println("draw3DFigure called");
        bSplinePoints = new ArrayList<>();
        bSplinePoints.add(new Point(0, 0));
        bSplinePoints.add(new Point(3, 5));
        bSplinePoints.add(new Point(6, 3));
        bSplinePoints.add(new Point(9, 0));

        if (bSplinePoints == null || bSplinePoints.size() < 4) return;

        // Generate points along the B-spline curve
        List<Point2D.Double> generatrixPoints = generateBSplinePoints();
        // Create the 3D surface points with rotation
        List<List<Point3D>> surfacePoints = createSurfacePoints(generatrixPoints);

        // Calculate bounds for normalization
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (List<Point3D> generatrix : surfacePoints) {
            for (Point3D p : generatrix) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
        }

        // Calculate scale factor to fit the figure in the panel
        double width = maxX - minX;
        double height = maxY - minY;
        double scale = Math.min(
                (getWidth() - 200) / width,  // 100 margin on each side
                (getHeight() - 200) / height // 100 margin on each side
        ) * 0.8; // Additional scaling to fit better

        // Calculate center offset
        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        // Draw only generatrix lines (meridians)
        g2d.setColor(Color.BLUE);
        for (List<Point3D> generatrix : surfacePoints) {
            Point3D firstPoint = generatrix.get(0);
            int prevX = (int)((firstPoint.x - centerX) * scale + getWidth()/2);
            int prevY = (int)(getHeight()/2 - (firstPoint.y - centerY) * scale);

            for (int i = 1; i < generatrix.size(); i++) {
                Point3D currentPoint = generatrix.get(i);
                int currX = (int)((currentPoint.x - centerX) * scale + getWidth()/2);
                int currY = (int)(getHeight()/2 - (currentPoint.y - centerY) * scale);

                g2d.drawLine(prevX, prevY, currX, currY);
                prevX = currX;
                prevY = currY;
            }
        }
    }

    private List<Point2D.Double> generateBSplinePoints() {


        List<Point2D.Double> points = new ArrayList<>();
        int n = bSplinePoints.size() - 1; // n = K-1 where K is number of control points
        int steps = n * (M1) + 1; // Number of points along the curve

        for (int i = 0; i < steps; i++) {
            double t = (double)i / (steps - 1) * n;
            int k = (int)Math.floor(t);
            if (k > n - 3) k = n - 3;
            double u = t - k;

            // Calculate point using cubic B-spline formula
            double x = 0, y = 0;
            for (int j = 0; j <= 3; j++) {
                double basis = bSplineBasis(j, u);
                x += bSplinePoints.get(k + j).x * basis;
                y += bSplinePoints.get(k + j).y * basis;
            }

            points.add(new Point2D.Double(x, y));
        }

        System.out.println("Generated B-spline points:");
        for (Point2D.Double p : points) {
            System.out.printf("(%.2f, %.2f)%n", p.x, p.y);
        }

        return points;
    }

    private double bSplineBasis(int j, double u) {
        switch (j) {
            case 0: return (-u*u*u + 3*u*u - 3*u + 1) / 6;
            case 1: return (3*u*u*u - 6*u*u + 4) / 6;
            case 2: return (-3*u*u*u + 3*u*u + 3*u + 1) / 6;
            case 3: return u*u*u / 6;
            default: return 0;
        }
    }

    private List<List<Point3D>> createSurfacePoints(List<Point2D.Double> generatrixPoints) {
        List<List<Point3D>> surfacePoints = new ArrayList<>();

        for (int j = 0; j <= M; j++) {
            double angle = Math.toRadians(j * 360.0 / M);
            List<Point3D> generatrix3D = new ArrayList<>();

            for (Point2D.Double p : generatrixPoints) {
                // Исходные координаты (вращение вокруг OZ)
                double x = p.x * Math.cos(angle);
                double y = p.x * Math.sin(angle);
                double z = p.y;

                // Применяем вращение вокруг оси OY
                double tempX = x * Math.cos(rotationY) + z * Math.sin(rotationY);
                double tempZ = -x * Math.sin(rotationY) + z * Math.cos(rotationY);
                x = tempX;
                z = tempZ;

                // Применяем вращение вокруг оси OX
                double tempY = y * Math.cos(rotationX) - z * Math.sin(rotationX);
                tempZ = y * Math.sin(rotationX) + z * Math.cos(rotationX);
                y = tempY;
                z = tempZ;

                generatrix3D.add(new Point3D(x, y, z));
            }
            surfacePoints.add(generatrix3D);
        }
        return surfacePoints;
    }

    private void drawGeneratrixLines(Graphics2D g2d, List<List<Point3D>> surfacePoints) {
        g2d.setColor(Color.BLUE);

        for (List<Point3D> generatrix : surfacePoints) {
            Point2D prevPoint = projectPoint(generatrix.get(0));

            for (int i = 1; i < generatrix.size(); i++) {
                Point2D currentPoint = projectPoint(generatrix.get(i));
                g2d.drawLine((int)prevPoint.getX(), (int)prevPoint.getY(),
                        (int)currentPoint.getX(), (int)currentPoint.getY());
                prevPoint = currentPoint;
            }
        }
    }

    private void drawParallelCircles(Graphics2D g2d, List<List<Point3D>> surfacePoints) {
        g2d.setColor(Color.RED);
        int numGeneratrices = surfacePoints.size();
        int pointsPerGeneratrix = surfacePoints.get(0).size();

        // We draw circles only for control points (K-2 circles)
        int circleCount = bSplinePoints.size() - 2;
        int step = (pointsPerGeneratrix - 1) / (bSplinePoints.size() - 3);

        for (int circleIdx = 0; circleIdx < circleCount; circleIdx++) {
            int pointIdx = circleIdx * step;
            if (pointIdx >= pointsPerGeneratrix) pointIdx = pointsPerGeneratrix - 1;

            for (int j = 0; j < numGeneratrices; j++) {
                int nextJ = (j + 1) % numGeneratrices;
                Point3D p1 = surfacePoints.get(j).get(pointIdx);
                Point3D p2 = surfacePoints.get(nextJ).get(pointIdx);
                Point2D p1Proj = projectPoint(p1);

                if (M1 == 1) {
                    // Simple line between adjacent generatrices
                    Point2D p2Proj = projectPoint(p2);
                    g2d.drawLine((int)p1Proj.getX(), (int)p1Proj.getY(),
                            (int)p2Proj.getX(), (int)p2Proj.getY());
                } else {
                    // Draw intermediate points for smoother circle
                    double angle1 = Math.toRadians(j * 360.0 / M);
                    double angle2 = Math.toRadians((j + 1) * 360.0 / M);
                    Point2D prevProj = p1Proj; // Initialize with first point

                    for (int k = 1; k <= M1; k++) {
                        double t = (double)k / M1;
                        double angle = angle1 + t * (angle2 - angle1);

                        double x = p1.x * Math.cos(angle);
                        double y = p1.x * Math.sin(angle);
                        double z = p1.z;

                        Point2D currentProj = projectPoint(new Point3D(x, y, z));
                        g2d.drawLine((int)prevProj.getX(), (int)prevProj.getY(),
                                (int)currentProj.getX(), (int)currentProj.getY());
                        prevProj = currentProj;
                    }
                }
            }
        }
    }

    // Simple orthographic projection onto OXY plane (ignoring Z coordinate)
    private Point2D projectPoint(Point3D p3d) {
        int screenX = coordinateCenter.x + (int)(p3d.x * 20);
        int screenY = coordinateCenter.y - (int)(p3d.y * 20);
        return new Point2D.Double(screenX, screenY);
    }









    private Point2D.Double calculateAxisDirection(double x, double y, double z) {
        double newX = x * Math.cos(rotationY) + z * Math.sin(rotationY);
        double newZ = -x * Math.sin(rotationY) + z * Math.cos(rotationY);
        double newY = y * Math.cos(rotationX) - newZ * Math.sin(rotationX);
        newZ = y * Math.sin(rotationX) + newZ * Math.cos(rotationX);
        return new Point2D.Double(newX, newY);
    }

    private void drawAxis(Graphics2D g, int startX, int startY,
                          Point2D.Double direction, String label) {
        int endX = startX + (int)(axisLength * direction.x);
        int endY = startY - (int)(axisLength * direction.y);
        g.drawLine(startX, startY, endX, endY);
        g.drawString(label, endX + 5, endY + 5);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - prevMouseX;
        int dy = e.getY() - prevMouseY;


        rotationY -= dx * 0.01;
        rotationX -= dy * 0.01;

        prevMouseX = e.getX();
        prevMouseY = e.getY();
        repaint();
    }

    // Остальные методы интерфейса
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    public void clear() {
        rotationX = rotationY = 0;
        zoom = 1.0;
        repaint();
    }

    public void updateScrollBars() {}

    // Вспомогательный класс для 3D точек
    private static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
    }
}