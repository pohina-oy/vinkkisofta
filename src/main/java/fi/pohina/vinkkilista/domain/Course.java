/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.pohina.vinkkilista.domain;

/**
 *
 * @author porrasm
 */
public class Course {
    private String name;
    private String description;
    
    public Course(String name) {
        this.name = name;
        this.description = "";
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
