package fi.pohina.vinkkilista.domain;

import java.util.*;

public class Blog implements Bookmark {

    private int id;
    private String title;
    private String author;
    private String url;
    private ArrayList<Course> relatedCourses;

    /**
     * Constructor
     * @param title
     * @param url
     */
    public Blog(String title, String url) {
        this.title = title;
        this.url = url;
        this.id = -1;

        relatedCourses = new ArrayList<Course>();
    }
    
    /**
     * Constructor
     * @param title
     * @param url
     * @param author
     */
    public Blog(String title, String url, String author) {
        this(title, url);
        this.author = author;
        this.id = -1;
        
        relatedCourses = new ArrayList<Course>();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        if (author == null || author.equals("")) {
            return "No author specified.";
        }
        return author;
    }

    public String getUrl() {
        return url;
    }

    public BookmarkType getBookmarkType() {
        return BookmarkType.Blog;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public ArrayList<Course> getRelatedCourses() {
        return relatedCourses;
    }

    public void addRelatedCourses(List<Course> courses) {
        for (Course course : courses) {
            addRelatedCourse(course);
        }
    }

    public void addRelatedCourse(Course course) {
        relatedCourses.add(course);
    }
}
