package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public abstract class AccountBase implements Account {
    final String username;
    double balance;
    public static final int COURSE_CAPACITY = 100;
    protected int purchasedCoursesSize = 0;
    Course[] purchasedCourses;
    protected int completedCoursesSize = 0;
    Course[] completedCourses;

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;

        purchasedCourses = new Course[COURSE_CAPACITY];
        completedCourses = new Course[COURSE_CAPACITY];
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void addToBalance(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot add negative amount to balance");

        balance += amount;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    public Course[] getPurchasedCourses() {
        return purchasedCourses;
    }

    @Override
    public void completeResourcesFromCourse(Course course, Resource[] resourcesToComplete) throws CourseNotPurchasedException, ResourceNotFoundException {
        if(course == null || resourcesToComplete == null) throw new IllegalArgumentException("Arguments of completeResourcesFromCourse are non nullable");

        if(!isCoursePurchased(course)) throw new CourseNotPurchasedException();

        course.completeAll(resourcesToComplete);
    }

    @Override
    public void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException {
        boolean isPurchased = false;
        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (purchasedCourses[i].equals(course)) {
                isPurchased = true;
            }
        }
        if(!isPurchased) throw new CourseNotPurchasedException();

        Course completedCourse = course.complete(grade);
        completedCourses[completedCoursesSize++] = completedCourse;
    }

    @Override
    public Course getLeastCompletedCourse() {
        Course leastCompletedCourse = null;

        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (leastCompletedCourse == null) {
                leastCompletedCourse = purchasedCourses[i];
            } else if (leastCompletedCourse.getCompletionPercentage() > purchasedCourses[i].getCompletionPercentage()) {
                leastCompletedCourse = purchasedCourses[i];
            }
        }

        return leastCompletedCourse;
    }

    private boolean isCoursePurchased(Course course) {
        boolean isPurchased = false;

        for (int i = 0; i < purchasedCoursesSize; ++i) {
            if (purchasedCourses[i].equals(course)) {
                isPurchased = true;
                break;
            }
        }

        return isPurchased;
    }
}
