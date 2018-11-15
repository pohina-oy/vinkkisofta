package fi.pohina.vinkkilista.domain;

import java.util.*;

public class Blog implements Bookmark {

    private String title;
    private String author;
    private String url;
    private BookmarkType type;
    private ArrayList<Course> relatedCourses;

    /**
     * Constructor
     * @param title
     * @param url
     * @param type 
     */
    public Blog(String title, String url, BookmarkType type) {
        this.title = title;
        this.url = url;
        this.type = type;

        relatedCourses = new ArrayList<Course>();
    }
    public Blog(String title, String url, BookmarkType type, String author) {
        this.title = title;
        this.url = url;
        this.type = type;
        this.author = author;
        
        relatedCourses = new ArrayList<Course>();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        if (author == null) {
            return "No author specified.";
        }
        return author;
    }

    public String getUrl() {
        return url;
    }

    public BookmarkType getBookmarkType() {
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
