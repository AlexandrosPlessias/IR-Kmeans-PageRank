/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package ir_project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MemberCoAuthors get member file with full text do the necessary processing
 * and store all CoAuthors in form:
 * TreeMap<coauthor, the number of the cooperations>. First get the parts of
 * file i want and after dismiss special characters and trivial words like "and" &
 * author.
 */
public class MemberCoAuthors {

    private final String DEPmemberName;
    private final TreeMap<String, Float> DEPmemberCoAuthors;
    
    // Constructor.
    public MemberCoAuthors(String DEPmemder, String DEPsMemberFile, ArrayList<String> allMembersSurnames) {

        // Get real surname without .bib in end.
        this.DEPmemberName = DEPmemder.replaceAll(".bib", "");
        this.DEPmemberCoAuthors = new TreeMap<>();
        ArrayList<String> sentencesOfText = new ArrayList<>();

        /* update comment
         Use REGEXP for find all author of DEP's member.
         Pattern analysis: whitespaces "author" whitespaces (anything) "},"
         Anything means all characters until \n.
         */
        Pattern authorsPattern = Pattern.compile("(?s)\\bauthor\\b.*?\\},");
        Matcher titlePatternMatcher = authorsPattern.matcher(DEPsMemberFile);
        while (titlePatternMatcher.find()) {
            sentencesOfText.add(titlePatternMatcher.group(0));

        }

        /*
         Remove all unnecessary spaces.
         Remove symbols like =,{,},"," and words: author and "and".
         This is a EARLY STOPWORD REMOVAL.
         */
        for (int i = 0; i < sentencesOfText.size(); i++) {
            sentencesOfText.set(i, sentencesOfText.get(i).replaceAll("(\\s+)", " "));
            sentencesOfText.set(i, sentencesOfText.get(i).replaceAll("(=)|(\\{)|(\\})|(,)|(\\bauthor\\b)|(\\band\\b)", ""));
        }

        // Saperate every sentence(by " ") to words and add them to NEW arrayList bagOfAuthors.
        ArrayList<String> bagOfAuthors = new ArrayList<>();
        for (String sentenceOfText : sentencesOfText) {
            String tempStrings[] = sentenceOfText.split(" ");
            bagOfAuthors.addAll(Arrays.asList(tempStrings));
        }

        // Free(?) of arraylist.
        sentencesOfText.clear();

        // Remove all empty records(all the space words). 
        bagOfAuthors.removeAll(Arrays.asList(null, ""));

        // Initialization and fill DEPmemberCoAuthors withs all allMembersSurnames and zeros.
        for (String membersSurname : allMembersSurnames) {
            DEPmemberCoAuthors.put(membersSurname, (float) 0);
        }
        
        // Create of one member Coaythors.
        for (String author : bagOfAuthors) {
            // Update only the others profs score.
            if (author.compareTo(DEPmemberName) != 0) {
                // Only member of our list can updated.
                if ((DEPmemberCoAuthors.containsKey(author))) {
                    Float currentCount = DEPmemberCoAuthors.get(author);
                    currentCount++;
                    DEPmemberCoAuthors.put(author, currentCount);
                }
            }
        }

        // Free(?) of arraylist.
        bagOfAuthors.clear();

    }

    // Getters.
    public String getMemberName() {
        return DEPmemberName;
    }

    public TreeMap<String, Float> getMemberDictionary() {
        return DEPmemberCoAuthors;
    }

}
