package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class EducationalAccount extends AccountBase {
    private final int PASSED_COURSES_DISCOUNT_THRESHOLD = 5;
    private final double COURSES_GRADE_DISCOUNT_THRESHOLD = 4.5;

    public EducationalAccount(String username, double balance) {
        super(username, balance);
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (purchasedCourses[i].equals(course)) throw new CourseAlreadyPurchasedException();
        }

        if (purchasedCoursesSize >= COURSE_CAPACITY) throw new MaxCourseCapacityReachedException();

        double coursePrice = course.getPrice();
        if (isEligibleForDiscount()) {
            coursePrice*=0.85;
        }

        if (coursePrice > balance) throw new InsufficientBalanceException();


        purchasedCourses[purchasedCoursesSize++] = course;
        balance -= coursePrice;
    }

    private boolean isEligibleForDiscount() {
        if (completedCoursesSize % 5 != 0 || completedCourses[0] == null) {
            return false;
        }

        double sumThresholdCourses = 0;
        for (int i = 1; i <= PASSED_COURSES_DISCOUNT_THRESHOLD; ++i) {
            sumThresholdCourses += completedCourses[completedCoursesSize-i].getGrade();
        }


        if (sumThresholdCourses/PASSED_COURSES_DISCOUNT_THRESHOLD >= 4.5){
            return true;
        } else {
            return false;
        }
    }
}
