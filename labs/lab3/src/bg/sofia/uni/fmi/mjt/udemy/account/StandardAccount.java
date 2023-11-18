package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class StandardAccount extends AccountBase {

    public StandardAccount(String username, double balance) {
        super(username, balance);
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (purchasedCourses[i].equals(course)) throw new CourseAlreadyPurchasedException();
        }

        if (purchasedCoursesSize >= COURSE_CAPACITY) throw new MaxCourseCapacityReachedException();

        if (course.getPrice() > balance) throw new InsufficientBalanceException();

        purchasedCourses[purchasedCoursesSize++] = course;
        balance -= course.getPrice();
    }
}
