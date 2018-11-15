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
public interface Bookmark {
    public BookmarkType getBookmarkType();
    public String getTitle();
}
