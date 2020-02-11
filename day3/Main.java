import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Main {

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);

    private static class Position {

        public int x;
        public int y;

        public Position(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return String.format("[%d, %d]", this.x, this.y);
        }

        public boolean equals(final Position other) {
            return this.x == other.x && this.y == other.y;
        }
    }

    private static enum MovementDirection {
        Left(-1, 0), Right(1, 0), Up(0, 1), Down(0, -1);

        private MovementDirection(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        private final int x;
        private final int y;

        public Position next(final Position current) {
            return new Position(current.x + this.x, current.y + this.y);
        }

        public static MovementDirection fromString(final String input) {
            return switch (input) {
            case "L" -> MovementDirection.Left;
            case "U" -> MovementDirection.Up;
            case "D" -> MovementDirection.Down;
            case "R" -> MovementDirection.Right;
            default -> null;
            };
        }

    }

    private static class Move {
        private final MovementDirection movementDirection;
        private final int steps;

        public Move(final MovementDirection d, final int steps) {
            this.movementDirection = d;
            this.steps = steps;
        }
    }

    public static List<Position> yieldSteps(Position currentPosition, final Move move) {
        final List<Position> stepsToYield = new ArrayList<Position>();
        for (int i = 0; i < move.steps; ++i) {
            currentPosition = move.movementDirection.next(currentPosition);
            stepsToYield.add(currentPosition);
        }
        return stepsToYield;
    }

    public static List<Position> getPathFromWire(final List<Move> moves) {
        final List<Position> path = new LinkedList<Position>();
        Position currentPosition = new Position(0, 0);

        for (final Move move : moves) {
            path.addAll(yieldSteps(currentPosition, move));
            // set last position as new currentPosition
            currentPosition = path.get(path.size() - 1);
        }

        return path;
    }

    private static List<Move> mapToMoves(final String wire) {
        return Arrays.asList(wire.split(",")).stream()
                .map(s -> new Move(MovementDirection.fromString(s.substring(0, 1)), Integer.parseInt(s.substring(1))))
                .collect(Collectors.toList());
    }

    private static class IntersectionResult {
        public Position intersection;
        public int distance;

        public IntersectionResult(final Position i, final int d) {
            this.intersection = i;
            this.distance = d;
        }
    }

    private static List<IntersectionResult> findIntersections(final List<Position> wire1Path,
            final List<Position> wire2Path) {

        final List<IntersectionResult> intersections = new LinkedList<IntersectionResult>();
        for (int w1 = 0; w1 < wire1Path.size(); ++w1) {
            final Position w1pos = wire1Path.get(w1);
            final int ww1 = w1;
            // parallelize
            final Runnable task = () -> {
                for (int w2 = 0; w2 < wire2Path.size(); ++w2) {
                    final Position w2pos = wire2Path.get(w2);

                    if (w1pos.equals(w2pos)) {
                        // add 2 for both 0,0 positions
                        intersections.add(new IntersectionResult(w1pos, 2 + ww1 + w2));
                        System.out.println("Found intersection " + w1pos);
                    }
                }
            };
            task.run();
            executor.execute(task);

        }
        return intersections;
    }

    public static void main(final String[] args) {

        final List<String> wires = Helper.readListFromFile("input.txt");
        final List<Move> wire1 = mapToMoves(wires.get(0));
        final List<Move> wire2 = mapToMoves(wires.get(1));
        final List<Position> wire1Path = getPathFromWire(wire1);
        final List<Position> wire2Path = getPathFromWire(wire2);

        System.out.println("Finding intersections");
        final List<IntersectionResult> intersections = findIntersections(wire1Path, wire2Path);

        System.out.println("Minimum Manhattan Distance " + findMinimumDistance(intersections));
    }

    private static int findMinimumDistance(final List<IntersectionResult> intersections) {
        int minDistance = Integer.MAX_VALUE;

        for (final IntersectionResult ir : intersections) {
            if (ir.distance < minDistance) {
                minDistance = ir.distance;
            }
        }
        return minDistance;
    }
}