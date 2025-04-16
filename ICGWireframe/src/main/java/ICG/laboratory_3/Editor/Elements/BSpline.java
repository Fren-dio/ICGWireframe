package ICG.laboratory_3.Editor.Elements;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BSpline {

    private static final double[][] Ms = {
            {-1,  3, -3, 1},
            { 3, -6,  3, 0},
            {-3,  0,  3, 0},
            { 1,  4,  1, 0}
    };

    public static List<Point> generateBSpline(List<Point> controlPoints, int segments) {
        List<Point> splinePoints = new ArrayList<>();
        int n = controlPoints.size();

        if (n < 4) return splinePoints; // Нужно минимум 4 точки

        // Для каждого участка сплайна
        for (int i = 0; i < n - 3; i++) {
            Point[] points = {
                    controlPoints.get(i),
                    controlPoints.get(i+1),
                    controlPoints.get(i+2),
                    controlPoints.get(i+3)
            };

            // Генерируем точки на участке
            for (int j = 0; j <= segments; j++) {
                double t = (double)j / segments;
                Point p = calculateBSplinePoint(t, points);
                splinePoints.add(p);
            }
        }

        return splinePoints;
    }

    private static Point calculateBSplinePoint(double t, Point[] points) {
        double[] T = {t*t*t, t*t, t, 1};

        // Вычисляем коэффициенты
        double[] coeff = new double[4];
        for (int i = 0; i < 4; i++) {
            coeff[i] = 0;
            for (int j = 0; j < 4; j++) {
                coeff[i] += T[j] * Ms[j][i];
            }
            coeff[i] /= 6.0;
        }

        // Вычисляем координаты точки
        double x = 0, y = 0;
        for (int i = 0; i < 4; i++) {
            x += coeff[i] * points[i].x;
            y += coeff[i] * points[i].y;
        }

        return new Point((int)x, (int)y);
    }

}
