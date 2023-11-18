package bg.sofia.uni.fmi.mjt.udemy.course;

import bg.sofia.uni.fmi.mjt.udemy.course.duration.CourseDuration;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.Objects;

public class Course implements Completable, Purchasable {

    final String name, description;
    final double price;
    Category category;
    Resource[] content;
    private boolean isPurchased = false;
    private double grade;

    public Course(String name, String description, double price, Resource[] content, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.content = content;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Double.compare(price, course.price) == 0 && isPurchased == course.isPurchased && Objects.equals(name, course.name) && Objects.equals(description, course.description) && category == course.category && Arrays.equals(content, course.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, description, price, category, isPurchased);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    /**
     * Returns the name of the course.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the course.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the price of the course.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the category of the course.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the content of the course.
     */
    public Resource[] getContent() {
        return content;
    }

    /**
     * Returns the total duration of the course.
     */
    public CourseDuration getTotalTime() {
        return CourseDuration.of(content);
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    /**
     * Completes a resource from the course.
     *
     * @param resourceToComplete the resource which will be completed.
     * @throws IllegalArgumentException if resourceToComplete is null.
     * @throws ResourceNotFoundException if the resource could not be found in the course.
     */
    public void completeResource(Resource resourceToComplete) throws ResourceNotFoundException {
        if (resourceToComplete == null) throw new IllegalArgumentException("Cannot pass null to completeResource().");

        boolean isFound = false;
        for (int i = 0; i < content.length; ++i) {
            if (resourceToComplete.equals(content[i])) {
                content[i].complete();
                isFound = true;
            }
        }

        if (!isFound) throw new ResourceNotFoundException();
    }

    @Override
    public void purchase() {
        isPurchased = true;
    }

    @Override
    public boolean isPurchased() {
        return isPurchased;
    }

    public Course complete(double grade) throws CourseNotCompletedException {
        if (getCompletionPercentage() != 100) throw new CourseNotCompletedException();

        setGrade(grade);

        return this;
    }

    @Override
    public boolean isCompleted() {
        return getCompletionPercentage() == 100;
    }

    @Override
    public int getCompletionPercentage() {
        int totalCompleted = 0;
        for (Resource curr: content) {
            if (curr.isCompleted) ++totalCompleted;
        }

        if (totalCompleted == 0) {
            return 0;
        } else {
            return Math.round(totalCompleted * 100.0f / content.length);
        }
    }

    public void completeAll(Resource[] resourcesToComplete) throws ResourceNotFoundException {
        for (Resource resourceToComplete: resourcesToComplete) {
            completeResource(resourceToComplete);
        }
    }
}
