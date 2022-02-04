package dev.jeffrpowell;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Piece {
    private final Set<Point2D> originVectors;
    
    /**
     * Give an array of points representing the shape
     * There should be one point set at (0,0) to mark the top-left
     * All points should have dimensions >= 0
     * @param pts
     */
    public Piece(Point2D... pts) {
        this.originVectors = new HashSet<>();
        originVectors.addAll(Arrays.asList(pts));
    }

    public Piece(Set<Point2D> pts) {
        this.originVectors = pts;
    }

    public Set<Point2D> getOriginVectors() {
        return originVectors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((originVectors == null) ? 0 : originVectors.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Piece other = (Piece) obj;
        if (originVectors == null) {
            if (other.originVectors != null)
                return false;
        } else if (!originVectors.equals(other.originVectors))
            return false;
        return true;
    }
}
