package fi.pohina.vinkkilista.domain;

import java.util.*;

public class Blog {

    private String title;
    private String writer;
    private String url;
    private BookmarkType type;
    private ArrayList<Course> relatedCourses;

    public Blog(String title, String url, BookmarkType type) {
        this.title = title;
        this.url = url;
        this.type = type;

        relatedCourses = new ArrayList<Course>();
    }

    public String getTitle() {
        return title;
    }

    public String writer() {
        return writer;
    }

    public String getUrl() {
        return url;
    }

    public BookmarkType getType() {
        return type;
    }

    public ArrayList<Course> getRelatedCourses() {
        return relatedCourses;
    }

    public void addCourses(List<Course> courses) {
        for (Course course : courses) {
            addCourse(course);
        }
    }

    public void addCourse(Course course) {
        relatedCourses.add(course);
    }
}
