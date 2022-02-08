package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GridConstants {
    private GridConstants() {}

    private static final EnumMap<Month, Point2D> MONTH_LOCATIONS;
    private static final Map<Integer, Point2D> DAY_LOCATIONS;
    private static final EnumMap<DayOfWeek, Point2D> DAY_OF_WEEK_LOCATIONS;

    static {
        //row 0
        MONTH_LOCATIONS = new EnumMap<>(Month.class);
        MONTH_LOCATIONS.put(Month.JANUARY, new Point2D.Double(0, 0));
        MONTH_LOCATIONS.put(Month.FEBRUARY, new Point2D.Double(1, 0));
        MONTH_LOCATIONS.put(Month.MARCH, new Point2D.Double(2, 0));
        MONTH_LOCATIONS.put(Month.APRIL, new Point2D.Double(3, 0));
        MONTH_LOCATIONS.put(Month.MAY, new Point2D.Double(4, 0));
        MONTH_LOCATIONS.put(Month.JUNE, new Point2D.Double(5, 0));
        //row 1
        MONTH_LOCATIONS.put(Month.JULY, new Point2D.Double(0, 1));
        MONTH_LOCATIONS.put(Month.AUGUST, new Point2D.Double(1, 1));
        MONTH_LOCATIONS.put(Month.SEPTEMBER, new Point2D.Double(2, 1));
        MONTH_LOCATIONS.put(Month.OCTOBER, new Point2D.Double(3, 1));
        MONTH_LOCATIONS.put(Month.NOVEMBER, new Point2D.Double(4, 1));
        MONTH_LOCATIONS.put(Month.DECEMBER, new Point2D.Double(5, 1));
        //row 2
        DAY_LOCATIONS = new HashMap<>();
        DAY_LOCATIONS.put(1, new Point2D.Double(0, 2));
        DAY_LOCATIONS.put(2, new Point2D.Double(1, 2));
        DAY_LOCATIONS.put(3, new Point2D.Double(2, 2));
        DAY_LOCATIONS.put(4, new Point2D.Double(3, 2));
        DAY_LOCATIONS.put(5, new Point2D.Double(4, 2));
        DAY_LOCATIONS.put(6, new Point2D.Double(5, 2));
        DAY_LOCATIONS.put(7, new Point2D.Double(6, 2));
        //row 3
        DAY_LOCATIONS.put(8, new Point2D.Double(0, 3));
        DAY_LOCATIONS.put(9, new Point2D.Double(1, 3));
        DAY_LOCATIONS.put(10, new Point2D.Double(2, 3));
        DAY_LOCATIONS.put(11, new Point2D.Double(3, 3));
        DAY_LOCATIONS.put(12, new Point2D.Double(4, 3));
        DAY_LOCATIONS.put(13, new Point2D.Double(5, 3));
        DAY_LOCATIONS.put(14, new Point2D.Double(6, 3));
        //row 4
        DAY_LOCATIONS.put(15, new Point2D.Double(0, 4));
        DAY_LOCATIONS.put(16, new Point2D.Double(1, 4));
        DAY_LOCATIONS.put(17, new Point2D.Double(2, 4));
        DAY_LOCATIONS.put(18, new Point2D.Double(3, 4));
        DAY_LOCATIONS.put(19, new Point2D.Double(4, 4));
        DAY_LOCATIONS.put(20, new Point2D.Double(5, 4));
        DAY_LOCATIONS.put(21, new Point2D.Double(6, 4));
        //row 5
        DAY_LOCATIONS.put(22, new Point2D.Double(0, 5));
        DAY_LOCATIONS.put(23, new Point2D.Double(1, 5));
        DAY_LOCATIONS.put(24, new Point2D.Double(2, 5));
        DAY_LOCATIONS.put(25, new Point2D.Double(3, 5));
        DAY_LOCATIONS.put(26, new Point2D.Double(4, 5));
        DAY_LOCATIONS.put(27, new Point2D.Double(5, 5));
        DAY_LOCATIONS.put(28, new Point2D.Double(6, 5));
        //row 6
        DAY_LOCATIONS.put(29, new Point2D.Double(0, 6));
        DAY_LOCATIONS.put(30, new Point2D.Double(1, 6));
        DAY_LOCATIONS.put(31, new Point2D.Double(2, 6));
        DAY_OF_WEEK_LOCATIONS = new EnumMap<>(DayOfWeek.class);
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(3, 6));
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(4, 6));
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(5, 6));
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(6, 6));
        //row 7
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(4, 7));
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(5, 7));
        DAY_OF_WEEK_LOCATIONS.put(DayOfWeek.SUNDAY, new Point2D.Double(6, 7));
    }

    public static Map<Point2D, Boolean> generateGrid(LocalDate targetDate) {
        Map<Point2D, Boolean> grid = MONTH_LOCATIONS.entrySet().stream()
            .filter(entry -> entry.getKey() != targetDate.getMonth())
            .map(Map.Entry::getValue)
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ));

        grid.putAll(DAY_LOCATIONS.entrySet().stream()
            .filter(entry -> entry.getKey() != targetDate.getDayOfMonth())
            .map(Map.Entry::getValue)
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ))
        );

        grid.putAll(DAY_OF_WEEK_LOCATIONS.entrySet().stream()
            .filter(entry -> entry.getKey() != targetDate.getDayOfWeek())
            .map(Map.Entry::getValue)
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ))
        );

        return grid;
    }

    public static Map<Point2D, Boolean> generateGrid() {
        Map<Point2D, Boolean> grid = MONTH_LOCATIONS.values().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ));

        grid.putAll(DAY_LOCATIONS.values().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ))
        );

        grid.putAll(DAY_OF_WEEK_LOCATIONS.values().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                pt -> false
            ))
        );

        return grid;
    }

    public static boolean isValidSolution(Set<Point2D> uncoveredPts) {
        if (MONTH_LOCATIONS.values().stream().noneMatch(uncoveredPts::contains)) {
            return false;
        }
        if (DAY_LOCATIONS.values().stream().noneMatch(uncoveredPts::contains)) {
            return false;
        }
        return DAY_OF_WEEK_LOCATIONS.values().stream().anyMatch(uncoveredPts::contains);
    }
}
