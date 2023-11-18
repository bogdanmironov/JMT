package bg.sofia.uni.fmi.mjt.udemy.course.duration;

import bg.sofia.uni.fmi.mjt.udemy.course.Resource;

public record CourseDuration(int hours, int minutes) {
    public CourseDuration {
        if (hours < 0 || hours > 24) throw new IllegalArgumentException("CourseDuration hours must be within [0, 24] ");

        if (minutes < 0 || minutes > 60) throw new IllegalArgumentException("CourseDuration minutes must be within [0, 60]");
    }

    public static CourseDuration of(Resource[] content) {
        int totalMinutes = 0;

        for (Resource res: content) {
            totalMinutes += res.getDuration().minutes();
        }

        return new CourseDuration(totalMinutes/60, totalMinutes%60);
    }

    public int compareTo(CourseDuration other) {
        if (hours == other.hours && minutes == other.minutes) {
            return 0;
        } else if (hours > other.hours) {
            return 1;
        } else if (hours == other.hours && minutes > other.minutes) {
            return 1;
        } else {
            return -1;
        }
    }
}