package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.BusinessNotSupportedCourseException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase {
    Category[] allowedCategories;

    public BusinessAccount(String username, double balance, Category[] allowedCategories) {
        super(username, balance);
        this.allowedCategories = allowedCategories;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (purchasedCourses[i].equals(course)) throw new CourseAlreadyPurchasedException();
        }

        if (purchasedCoursesSize >= COURSE_CAPACITY) throw new MaxCourseCapacityReachedException();

        if (!isAllowed(course.getCategory())) throw new BusinessNotSupportedCourseException();

        double courseNewPrice = 0.8 * course.getPrice();
        if (courseNewPrice > balance) throw new InsufficientBalanceException();

        purchasedCourses[purchasedCoursesSize++] = course;
        balance-=courseNewPrice;
    }

    private boolean isAllowed(Category category) {
        for (Category currCategory: allowedCategories) {
            if (currCategory.equals(category)) return true;
        }

        return false;
    }
}
