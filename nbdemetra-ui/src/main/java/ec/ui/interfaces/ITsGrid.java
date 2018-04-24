package ec.ui.interfaces;

/**
 *
 * @author Jeremy Demortier
 * @version 2010-12-02
 */
public interface ITsGrid extends ITsCollectionView, IColorSchemeAble {

    public static final String ORIENTATION_PROPERTY = "orientation";
    public static final String CHRONOLOGY_PROPERTY = "chronology";
    public static final String MODE_PROPERTY = "mode";
    public static final String SINGLE_TS_INDEX_PROPERTY = "singleTsIndex";
    public static final String ZOOM_PROPERTY = "zoom";

    Orientation getOrientation();

    void setOrientation(Orientation orientation);

    Chronology getChronology();

    void setChronology(Chronology chronology);

    Mode getMode();

    void setMode(Mode mode);

    void setSingleTsIndex(int singleTsIndex);

    int getSingleTsIndex();

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);
    
    /**
     * Defines the order the data are displayed
     *
     * @author Jeremy Demortier
     * @version 2010-08-12
     */
    public enum Chronology {

        /**
         * Oldest data first
         */
        ASCENDING,
        /**
         * Newest data first
         */
        DESCENDING;

        public Chronology reverse() {
            return this == ASCENDING ? DESCENDING : ASCENDING;
        }
    }

    /**
     *
     * @author Demortier Jeremy
     */
    public enum Mode {

        /**
         * Display only a single timeseries in the grid
         */
        SINGLETS,
        /**
         * Display 1 to n timeseries in the grid
         */
        MULTIPLETS;

        public Mode toggle() {
            return this == SINGLETS ? MULTIPLETS : SINGLETS;
        }
    }

    /**
     * Defines the orientation of a JTSGrid
     *
     * @author Jeremy Demortier
     * @version 2010-08-12
     */
    public enum Orientation {

        /**
         * Timeline is along vertical axis (in most cases, table height > table
         * width)
         */
        NORMAL,
        /**
         * Timeline is along horizontal axis (in most cases, table height <
         * table width)
         */
        REVERSED;

        public Orientation transpose() {
            return this == NORMAL ? REVERSED : NORMAL;
        }
    }
}
