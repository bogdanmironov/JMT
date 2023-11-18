package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.account.BusinessAccount;
import bg.sofia.uni.fmi.mjt.udemy.account.EducationalAccount;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.course.Resource;
import bg.sofia.uni.fmi.mjt.udemy.course.duration.ResourceDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

public class Udemy implements LearningPlatform {
    Account[] accounts;
    Course[] courses;

    public Udemy(Account[] accounts, Course[] courses) {
        this.accounts = accounts;
        this.courses = courses;
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        for (Course curr: courses) {
            if (name.equals(curr.getName())){
                return curr;
            }
        }

        throw new CourseNotFoundException();
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        int coursesWithKeywordLength = 0;

        for (Course curr: courses) {
            if (curr.getName().contains(keyword) || curr.getDescription().contains(keyword)) {
                ++coursesWithKeywordLength;
            }
        }

        Course[] coursesWithKeyword = new Course[coursesWithKeywordLength];
        coursesWithKeywordLength = 0;

        for (Course curr: courses) {
            if (curr.getName().contains(keyword) || curr.getDescription().contains(keyword)) {
                coursesWithKeyword[coursesWithKeywordLength++] = curr;
            }
        }

        return coursesWithKeyword;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        int coursesInCategoryCount = 0;

        for (Course currCourse: courses) {
            if (category.equals(currCourse.getCategory())) {
                ++coursesInCategoryCount;
            }
        }

        Course[] coursesInCategory = new Course[coursesInCategoryCount];
        coursesInCategoryCount = 0;

        for (Course currCourse: courses) {
            if (category.equals(currCourse.getCategory())) {
                coursesInCategory[coursesInCategoryCount++] = currCourse;
            }
        }

        return coursesInCategory;
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        for (Account curr: accounts) {
            if (name.equals(curr.getUsername())){
                return curr;
            }
        }

        throw new AccountNotFoundException();
    }

    @Override
    public Course getLongestCourse() {
        Course longest = null;

        for (Course curr: courses) {
            if (longest == null) {
                longest = curr;
            } else if(longest.getTotalTime().compareTo(curr.getTotalTime()) < 0 ) {
                longest = curr;
            }
        }

        return longest;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        Course[] coursesInCategory = getAllCoursesByCategory(category);

        if (coursesInCategory.length == 0) throw new IllegalArgumentException("No courses in category");

        Course cheapest = coursesInCategory[0];

        for (int i = 1; i < coursesInCategory.length; ++i) {
            if (cheapest.getPrice() > coursesInCategory[i].getPrice()) {
                cheapest = coursesInCategory[i];
            }
        }

        return cheapest;
    }

    public static void main(String[] args) {
//        Account acc = new BusinessAccount("Bobi", 23.0, new Category[]{Category.BUSINESS, Category.DESIGN});
        Account acc = new EducationalAccount("Bobi", 100.0);
        Resource[] resources = new Resource[]{new Resource("Res1", new ResourceDuration(5)), new Resource("Res2", new ResourceDuration(10)), new Resource("Res3", new ResourceDuration(10))};
        Course course = new Course("Java", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course2 = new Course("Java2", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course3 = new Course("Java3", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course4 = new Course("Java4", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course5 = new Course("Java5", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course6 = new Course("Java6", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course7 = new Course("Java7", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course8 = new Course("Java8", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course9 = new Course("Java9", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course10 = new Course("Java10", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course11 = new Course("Java11", "Cool java course", 3.0, resources, Category.BUSINESS);
        Course course12 = new Course("Java12", "Cool java course", 3.0, resources, Category.BUSINESS);

        try {
            acc.buyCourse(course);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course, resources);
            acc.completeCourse(course, 4.5);
            acc.buyCourse(course2);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course2, resources);
            acc.completeCourse(course2, 4.5);
            acc.buyCourse(course3);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course3, resources);
            acc.completeCourse(course3, 4.5);
            acc.buyCourse(course4);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course4, resources);
            acc.completeCourse(course4, 4.5);
            acc.buyCourse(course5);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course5, resources);
            acc.completeCourse(course5, 4.5);
            acc.buyCourse(course6);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course6, resources);
            acc.completeCourse(course6, 4.5);
            acc.buyCourse(course7);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course7, resources);
            acc.completeCourse(course7, 4.5);
            acc.buyCourse(course8);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course8, resources);
            acc.completeCourse(course8, 4.5);
            acc.buyCourse(course9);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course9, resources);
            acc.completeCourse(course9, 4.5);
            acc.buyCourse(course10);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course10, resources);
            acc.completeCourse(course10, 4.5);
            acc.buyCourse(course11);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course11, resources);
            acc.completeCourse(course11, 4.5);
            acc.buyCourse(course12);
            System.out.println(acc.getBalance());
            acc.completeResourcesFromCourse(course12, resources);
            acc.completeCourse(course12, 4.5);

            System.out.println();
            System.out.println(acc.getLeastCompletedCourse().getCompletionPercentage());
//            acc.getLeastCompletedCourse().completeAll(new Resource[]{new Resource("Res1", new ResourceDuration(5)), new Resource("Res2", new ResourceDuration(10))});
            acc.getLeastCompletedCourse().completeAll(resources);
            System.out.println(acc.getLeastCompletedCourse().getCompletionPercentage());
            System.out.println(acc.getLeastCompletedCourse().isCompleted());
            acc.completeCourse(course, 4.5);
            System.out.println(acc.getLeastCompletedCourse().getGrade());

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
