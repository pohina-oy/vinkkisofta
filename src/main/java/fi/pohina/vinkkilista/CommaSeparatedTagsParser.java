package fi.pohina.vinkkilista;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class CommaSeparatedTagsParser {

    private static final String DELIMITER = ",";

    /**
     * Parses comma-delimited tags to a set of tags.
     *
     * @param commaSeparatedTags the comma-separated tags.
     * @return the parsed tags, or an empty set if the input was <c>null</c> or
     * empty.
     */
    public Set<String> parse(String commaSeparatedTags) {
        if (Strings.isNullOrEmpty(commaSeparatedTags)) {
            return Sets.newHashSet();
        }

        List<String> tags = splitAndTrimTags(commaSeparatedTags);
        removeNullOrEmptyTags(tags);

        return Sets.newHashSet(tags);
    }

    private List<String> splitAndTrimTags(String commaSeparatedTags) {
        // splitToList returns an immutable list - we need to remove null/empty
        // tags so we need a mutable list
        return Lists.newArrayList(
            Splitter.on(DELIMITER)
                .trimResults()
                .split(commaSeparatedTags)
        );
    }

    private void removeNullOrEmptyTags(List<String> tags) {
        tags.removeIf(Strings::isNullOrEmpty);
    }
}
