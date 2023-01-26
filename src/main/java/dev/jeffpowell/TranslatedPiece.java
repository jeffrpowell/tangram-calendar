package dev.jeffpowell;

import java.awt.geom.Point2D;
import java.util.Set;

public class TranslatedPiece {
    private final Piece originPiece;
    private final Set<Point2D> locations;
    
    public TranslatedPiece(Piece originPiece, Set<Point2D> locations) {
        this.originPiece = originPiece;
        this.locations = locations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locations == null) ? 0 : locations.hashCode());
        result = prime * result + ((originPiece == null) ? 0 : originPiece.hashCode());
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
        TranslatedPiece other = (TranslatedPiece) obj;
        if (locations == null) {
            if (other.locations != null)
                return false;
        } else if (!locations.equals(other.locations))
            return false;
        if (originPiece == null) {
            if (other.originPiece != null)
                return false;
        } else if (!originPiece.equals(other.originPiece))
            return false;
        return true;
    }

    public Piece getOriginPiece() {
        return originPiece;
    }

    public Set<Point2D> getLocations() {
        return locations;
    }
    
}
