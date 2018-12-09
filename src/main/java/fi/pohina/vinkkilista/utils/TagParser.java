package fi.pohina.vinkkilista.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class TagParser {
    
    private static final String DELIMITER = ",";
    
    public String tagFromUrl(String url) {
        String[] videoUrls = {"youtube.com", "vimeo.com", "youtu.be"};
        String[] blogUrls = {
            "blogger.com", "blogs.helsinki.fi",
            "wordpress.org", "blogspot.com"};
        String[] bookUrls = {".suomalainen.com"};
        String[] scienceUrls = {"dl.acm.org", "ieeexplore.ieee.org"};
        
        for (String videoUrl : videoUrls) {
            if (url.contains(videoUrl)) {
                return "video";
            }
        }
        
        for (String blogUrl : blogUrls) {
            if (url.contains(blogUrl)) {
                return "blog";
            }
        }
        
        for (String bookUrl : bookUrls) {
            if (url.contains(bookUrl)) {
                return "book";
            }
        }
        
        for (String scienceUrl : scienceUrls) {
            if (url.contains(scienceUrl)) {
                return "scientific publication";
            }
        }
        
        return "";
    }

    /**
     * Parses comma-delimited tags to a set of tags.
     *
     * @param commaSeparatedTags the comma-separated tags.
     * @return the parsed tags, or an empty set if the input was <c>null</c> or
     * empty.
     */
    public Set<String> csvToSet(String commaSeparatedTags) {
        if (Strings.isNullOrEmpty(commaSeparatedTags)) {
            return Sets.newHashSet();
        }
        
        List<String> tags = splitAndTrimTags(commaSeparatedTags);
        validateTags(tags);
        
        return Sets.newHashSet(tags);
    }
    
    private List<String> splitAndTrimTags(String commaSeparatedTags) {
        // Create a new ArrayList from split's iterable instead of using
        // splitToList because it returns an immutable list. We need to remove
        // null/empty tags so the list needs to be mutable.
        return Lists.newArrayList(
                Splitter.on(DELIMITER)
                        .trimResults()
                        .split(commaSeparatedTags)
        );
    }
    
    private void validateTags(List<String> tags) {
        tags.removeIf(Strings::isNullOrEmpty);
        tags.replaceAll(tag -> {
            return tag.replaceAll(" +", " ").toLowerCase().trim();
        });
    }
    
}
