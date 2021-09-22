package com.isoterik.racken;

import com.badlogic.gdx.utils.GdxRuntimeException;

/** The current version of racken
 *
 * @author mzechner */
public class Version {
    /** the current version of racken as a String in the major.minor.revision format **/
    public static final String VERSION = "1.0.0";

    /** the current major version of racken **/
    public static final int MAJOR;

    /** the current minor version of racken **/
    public static final int MINOR;

    /** the current revision version of racken **/
    public static final int REVISION;

    static {
        try {
            String[] v = VERSION.split("\\.");
            MAJOR = v.length < 1 ? 0 : Integer.parseInt(v[0]);
            MINOR = v.length < 2 ? 0 : Integer.parseInt(v[1]);
            REVISION = v.length < 3 ? 0 : Integer.parseInt(v[2]);
        }
        catch (Throwable t) {
            // Should never happen
            throw new GdxRuntimeException("Invalid version "+VERSION, t);
        }
    }

    public static boolean isHigher (int major, int minor, int revision) {
        return isHigherEqual(major, minor, revision+1);
    }

    public static boolean isHigherEqual (int major, int minor, int revision) {
        if (MAJOR != major)
            return MAJOR > major;
        if (MINOR != minor)
            return MINOR > minor;
        return REVISION >= revision;
    }

    public static boolean isLower (int major, int minor, int revision) {
        return isLowerEqual(major, minor, revision-1);
    }

    public static boolean isLowerEqual (int major, int minor, int revision) {
        if (MAJOR != major)
            return MAJOR < major;
        if (MINOR != minor)
            return MINOR < minor;
        return REVISION <= revision;
    }

}
