package ICG.laboratory_3;

import ICG.laboratory_3.Editor.Elements.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private JScrollPane scrollPane;
    private FrameWork frameWork;


    private static final Color NEAR_COLOR = Color.BLUE;
    private static final Color FAR_COLOR = Color.RED;
    private static final double MAX_DISTANCE = 750.0;

    // Параметры осей
    private final int axisLength = 50;
    private final Point coordinateCenter = new Point(80, 80);

    // Углы поворота (в радианах)
    private double rotationX = 0;
    private double rotationY = 0;
    private int prevMouseX, prevMouseY;

    // Параметры фигуры
    private List<Point> bSplinePoints = new ArrayList<>();
    private int M = 10; // Количество образующих
    private int M1 = 1; // Число отрезков между образующими
    private int smooth = 1; // сглаживание
    private double zoom = 1.0;


    public ImagePanel(JScrollPane scrollPane, FrameWork frameWork) {
        this.scrollPane = scrollPane;
        this.frameWork = frameWork;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        //getInfoAboutBSplinePoints();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("paintComponent called");
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        this.M = frameWork.getM();
        this.M1 = frameWork.getM1();

        draw_OX_OY_OZ(g2d);
        draw3DFigure(g2d);
    }

    public void resetRotate() {
        this.rotationX = 0;
        this.rotationY = 0;
        repaint();
    }


    public void rotateOXY() {
        this.rotationX = 0;
        this.rotationY = 0;
        repaint();
    }

    public void rotateOXZ() {
        this.rotationX = Math.PI/2;
        this.rotationY = 0;
        repaint();
    }

    public void rotateOYZ() {
        this.rotationX = 0;
        this.rotationY = Math.PI/2;
        repaint();
    }

    public void rotateOXYZ() {
        this.rotationX = Math.PI/4;
        this.rotationY = -Math.PI/4;
        repaint();
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




    void setbSplinePoints(List<Point> bSplinePoints) {
        this.bSplinePoints = bSplinePoints;
        repaint();
    }


    private void draw3DFigure(Graphics2D g2d) {
        bSplinePoints = new ArrayList<>();
        bSplinePoints = frameWork.getInfoAboutBSplinePoints();
        this.M = frameWork.getM();
        this.M1 = frameWork.getM1();
        this.smooth = frameWork.getSmooth();

        if (bSplinePoints == null || bSplinePoints.size() < 4) return;

        List<Point2D.Double> generatrixPoints = generateBSplinePoints();
        List<List<Point3D>> surfacePoints = createSurfacePoints(generatrixPoints);

        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minDistance = Double.MAX_VALUE;
        double maxDistance = -Double.MAX_VALUE;

        for (List<Point3D> generatrix : surfacePoints) {
            for (Point3D p : generatrix) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);

                double dist = calculateDistanceToCamera(p);
                minDistance = Math.min(minDistance, dist);
                maxDistance = Math.max(maxDistance, dist);
            }
        }

        double width = maxX - minX;
        double height = maxY - minY;
        double scale = Math.min(
                (getWidth() - 200) / width,
                (getHeight() - 200) / height
        ) * 0.8 * zoom;

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        for (List<Point3D> generatrix : surfacePoints) {
            Point3D prevPoint = generatrix.get(0);
            int prevX = (int)((prevPoint.x - centerX) * scale + getWidth()/2);
            int prevY = (int)(getHeight()/2 - (prevPoint.y - centerY) * scale);

            for (int i = 1; i < generatrix.size(); i++) {
                Point3D currentPoint = generatrix.get(i);
                int currX = (int)((currentPoint.x - centerX) * scale + getWidth()/2);
                int currY = (int)(getHeight()/2 - (currentPoint.y - centerY) * scale);

                g2d.setColor(getCalibratedDistanceColor(prevPoint, currentPoint, minDistance, maxDistance));
                g2d.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;
                prevPoint = currentPoint;
            }
        }

        int pointsPerGeneratrix = surfacePoints.get(0).size();
        if (M1 != 1) {
            for (int pointIdx = 0; pointIdx < pointsPerGeneratrix; pointIdx += this.M1 * this.M1) {
                drawSmoothedCalibratedCircle(g2d, surfacePoints, pointIdx, centerX, centerY, scale, minDistance, maxDistance);
            }
            drawSmoothedCalibratedCircle(g2d, surfacePoints, pointsPerGeneratrix - 1, centerX, centerY, scale, minDistance, maxDistance);
        } else {
            for (int pointIdx = 0; pointIdx < pointsPerGeneratrix; pointIdx += 1) {
                drawSmoothedCalibratedCircle(g2d, surfacePoints, pointIdx, centerX, centerY, scale, minDistance, maxDistance);
            }
        }
    }

    private Color getCalibratedDistanceColor(Point3D p1, Point3D p2, double minDist, double maxDist) {
        double distance = (calculateDistanceToCamera(p1) + calculateDistanceToCamera(p2)) / 2.0;
        double range = maxDist - minDist;

        double ratio = range > 0 ? (distance - minDist) / range : 0.5;

        ratio = Math.pow(ratio, 0.7);

        int red = (int)(NEAR_COLOR.getRed() * (1 - ratio) + FAR_COLOR.getRed() * ratio);
        int green = (int)(NEAR_COLOR.getGreen() * (1 - ratio) + FAR_COLOR.getGreen() * ratio);
        int blue = (int)(NEAR_COLOR.getBlue() * (1 - ratio) + FAR_COLOR.getBlue() * ratio);

        return new Color(
                Math.max(0, Math.min(255, red)),
                Math.max(0, Math.min(255, green)),
                Math.max(0, Math.min(255, blue))
        );
    }

    private void drawSmoothedCalibratedCircle(Graphics2D g2d, List<List<Point3D>> surfacePoints,
                                              int pointIdx, double centerX, double centerY,
                                              double scale, double minDist, double maxDist) {
        List<Point3D> circlePoints = new ArrayList<>();
        for (List<Point3D> generatrix : surfacePoints) {
            circlePoints.add(generatrix.get(pointIdx));
        }

        if (!circlePoints.isEmpty()) {
            circlePoints.add(circlePoints.get(0));
        }

        List<Point3D> smoothedPoints = new ArrayList<>();
        for (int i = 0; i < circlePoints.size() - 1; i++) {
            Point3D p1 = circlePoints.get(i);
            Point3D p2 = circlePoints.get(i + 1);

            smoothedPoints.add(p1);

            for (int j = 1; j < smooth; j++) {
                double t = (double)j / smooth;
                double x = p1.x + t * (p2.x - p1.x);
                double y = p1.y + t * (p2.y - p1.y);
                double z = p1.z + t * (p2.z - p1.z);
                smoothedPoints.add(new Point3D(x, y, z));
            }
        }
        if (!circlePoints.isEmpty()) {
            smoothedPoints.add(circlePoints.get(circlePoints.size() - 1));
        }

        if (!smoothedPoints.isEmpty()) {
            Point3D prevPoint = smoothedPoints.get(0);
            int prevX = (int) ((prevPoint.x - centerX) * scale + getWidth() / 2);
            int prevY = (int) (getHeight() / 2 - (prevPoint.y - centerY) * scale);

            for (int i = 1; i < smoothedPoints.size(); i++) {
                Point3D currentPoint = smoothedPoints.get(i);
                int currX = (int) ((currentPoint.x - centerX) * scale + getWidth() / 2);
                int currY = (int) (getHeight() / 2 - (currentPoint.y - centerY) * scale);

                g2d.setColor(getCalibratedDistanceColor(prevPoint, currentPoint, minDist, maxDist));
                g2d.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;
                prevPoint = currentPoint;
            }
        }
    }


    public void zoomIn() {
        zoom *= 1.1;
        zoom = Math.min(zoom, 3.0);
        repaint();
    }

    public void zoomOut() {
        zoom /= 1.1;
        zoom = Math.max(zoom, 0.1);
        repaint();
    }

    public void resetZoom() {
        zoom = 1.0;
        repaint();
    }

    public void clear() {
        rotationX = rotationY = 0;
        zoom = 1.0;
        repaint();
    }



    private void drawSmoothedCircle(Graphics2D g2d, List<List<Point3D>> surfacePoints, int pointIdx,
                                    double centerX, double centerY, double scale) {
        List<Point3D> circlePoints = new ArrayList<>();
        for (List<Point3D> generatrix : surfacePoints) {
            circlePoints.add(generatrix.get(pointIdx));
        }

        if (!circlePoints.isEmpty()) {
            circlePoints.add(circlePoints.get(0));
        }

        List<Point3D> smoothedPoints = new ArrayList<>();
        for (int i = 0; i < circlePoints.size() - 1; i++) {
            Point3D p1 = circlePoints.get(i);
            Point3D p2 = circlePoints.get(i + 1);

            smoothedPoints.add(p1);

            for (int j = 1; j < smooth; j++) {
                double t = (double)j / smooth;
                double x = p1.x + t * (p2.x - p1.x);
                double y = p1.y + t * (p2.y - p1.y);
                double z = p1.z + t * (p2.z - p1.z);
                smoothedPoints.add(new Point3D(x, y, z));
            }
        }
        if (!circlePoints.isEmpty()) {
            smoothedPoints.add(circlePoints.get(circlePoints.size() - 1));
        }

        if (!smoothedPoints.isEmpty()) {
            Point3D prevPoint = smoothedPoints.get(0);
            int prevX = (int) ((prevPoint.x - centerX) * scale + getWidth() / 2);
            int prevY = (int) (getHeight() / 2 - (prevPoint.y - centerY) * scale);

            for (int i = 1; i < smoothedPoints.size(); i++) {
                Point3D currentPoint = smoothedPoints.get(i);
                int currX = (int) ((currentPoint.x - centerX) * scale + getWidth() / 2);
                int currY = (int) (getHeight() / 2 - (currentPoint.y - centerY) * scale);

                // Устанавливаем цвет на основе расстояния
                g2d.setColor(getDistanceColor(prevPoint, currentPoint));
                g2d.drawLine(prevX, prevY, currX, currY);

                prevX = currX;
                prevY = currY;
                prevPoint = currentPoint;
            }
        }
    }

    private List<Point2D.Double> generateBSplinePoints() {


        List<Point2D.Double> points = new ArrayList<>();
        int n = bSplinePoints.size() - 1;
        int steps = n * (M1) + 1;

        for (int i = 0; i < steps; i++) {
            double t = (double)i / (steps - 1) * n;
            int k = (int)Math.floor(t);
            if (k > n - 3) k = n - 3;
            double u = t - k;

            double x = 0, y = 0;
            for (int j = 0; j <= 3; j++) {
                double basis = bSplineBasis(j, u);
                x += bSplinePoints.get(k + j).x * basis;
                y += bSplinePoints.get(k + j).y * basis;
            }

            points.add(new Point2D.Double(x, y));
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
                double x = p.x * Math.cos(angle);
                double y = p.x * Math.sin(angle);
                double z = p.y;

                // вращение вокруг OY
                double tempX = x * Math.cos(rotationY) + z * Math.sin(rotationY);
                double tempZ = -x * Math.sin(rotationY) + z * Math.cos(rotationY);
                x = tempX;
                z = tempZ;

                // вращение вокруг OX
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



    private Color getDistanceColor(Point3D p1, Point3D p2) {
        double distance = (calculateDistanceToCamera(p1) + calculateDistanceToCamera(p2)) / 2.0;
        double ratio = Math.min(distance / MAX_DISTANCE, 1.0);

        ratio = Math.pow(ratio, 0.7);

        int red = (int)(NEAR_COLOR.getRed() * (1 - ratio) + FAR_COLOR.getRed() * ratio);
        int green = (int)(NEAR_COLOR.getGreen() * (1 - ratio) + FAR_COLOR.getGreen() * ratio);
        int blue = (int)(NEAR_COLOR.getBlue() * (1 - ratio) + FAR_COLOR.getBlue() * ratio);

        return new Color(
                Math.max(0, Math.min(255, red)),
                Math.max(0, Math.min(255, green)),
                Math.max(0, Math.min(255, blue))
        );
    }

    private double calculateDistanceToCamera(Point3D point) {
        Point3D cameraPosition = new Point3D(0, 0, -10);

        double dx = point.x - cameraPosition.x;
        double dy = point.y - cameraPosition.y;
        double dz = point.z - cameraPosition.z;

        Point3D viewDirection = new Point3D(0, 0, 20); // От (-10,0,0) к (10,0,0)

        double dotProduct = dx * viewDirection.x + dy * viewDirection.y + dz * viewDirection.z;
        double viewLength = Math.sqrt(viewDirection.x*viewDirection.x +
                viewDirection.y*viewDirection.y +
                viewDirection.z*viewDirection.z);

        double projectedDistance = dotProduct / viewLength;

        double totalDistance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        double perpendicularDistance = Math.sqrt(totalDistance*totalDistance - projectedDistance*projectedDistance);

        return projectedDistance * 0.7 + perpendicularDistance * 0.3;
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

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}


    public void updateScrollBars() {}

    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            // Прокрутка вверх - увеличение
            zoom *= 1.1;
        } else {
            // Прокрутка вниз - уменьшение
            zoom /= 1.1;
        }

        zoom = Math.max(0.1, Math.min(zoom, 10.0));

        repaint();
    }

    private static class Point3D {
        double x, y, z;
        Point3D(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
    }
}