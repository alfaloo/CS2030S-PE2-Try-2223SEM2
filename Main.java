import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

class Main {

  public static Stream<Point> pointStream(Point point, Function<Point, Point> f) {
    return Stream.iterate(point, f::apply);
  }

  public static Stream<Point> generateGrid(Point point, int n) {
    return pointStream(point, x -> new Point(x.getX() + 1, x.getY())).limit(n)
             .flatMap(row -> pointStream(row, y -> new Point(y.getX(), y.getY() + 1)).limit(n));
  }

  public static Stream<Circle> concentricCircles(Circle circle, Function<Double, Double> f) {
    return Stream.iterate(circle, c -> new Circle(c.getCenter(), f.apply(c.getRadius())));
  }

  public static Stream<Point> pointStreamFromCircle(Stream<Circle> circles) {
    return circles.map(c -> c.getCenter());
  }
}