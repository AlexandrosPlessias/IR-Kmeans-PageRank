/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * RelevanceAndImportance is the implementation of question 3. Actually combine
 * the Relevance(similarity cosine) of a DEP member with a query and the
 * Importance(Page Rank score) of this DEP member in this department.
 *
 * +++ Multiply: parerankPercent * simCos. 
 * + Harmonic mean: 2*(simCos * parerankPercent) / (simCos 
 * + parerankPercent) - Weighted sum: w1*parerankPercent + w2*simCos. 
 * --- Sum: parerankPercent + simCos.
 */
public class RelevanceAndImportance {

    // Members with similarity cosines.
    private TreeMap<String, Float> simCosWithNames;
    // Members with importance.
    private TreeMap<String, Float> wkPairs;

    // Create data buckets for store formula's scores.
    private TreeMap<String, Float> membersAndScores = new TreeMap<>();
    // Create data buckets for store formula's sorted scores.
    private ArrayList<Map.Entry<String, Float>> sortedScoresWithMembers;

    // Constructor.
    public RelevanceAndImportance(TreeMap<String, Float> simCosWithNames, TreeMap<String, Float> wkPairs) {

        // Initilizations.
        this.simCosWithNames = simCosWithNames;
        this.wkPairs = wkPairs;

        for (Map.Entry<String, Float> entrySet : simCosWithNames.entrySet()) {
            String depMemberOfSimCos = entrySet.getKey();
            Float simCos = entrySet.getValue();

            for (Map.Entry<String, Float> entrySet1 : wkPairs.entrySet()) {
                String depMemberOfPagerank = entrySet1.getKey();
                Float parerankPercent = entrySet1.getValue();

                // Only same members.
                if (depMemberOfSimCos.compareToIgnoreCase(depMemberOfPagerank) == 0) {
                    // Formula calculation(parerankPercent * simCos).
                    Float score;

                    // +++ Multiply.
                    score = parerankPercent * simCos;

                    //   + Harmonic mean.
                    // score = 2 * (simCos * parerankPercent) / (simCos + parerankPercent); 
                    
                    //   - Weighted sum.
                    // Float w1 = (float) 0.5; // Float w2 = (float) 0.5;
                    //score = w1 * parerankPercent + w2 * simCos; 
                    
                    // --- Sum
                    //score = parerankPercent + simCos; 
                    membersAndScores.put(depMemberOfSimCos, score);

                }
            }
        }

        // Store formula's sorted scores.
        sortedScoresWithMembers = new ArrayList<>(membersAndScores.entrySet());
        // Comparator by score.
        Collections.sort(sortedScoresWithMembers, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return -(o1.getValue().compareTo(o2.getValue())); // Invert for descending order(max to min).
            }
        });

    }

    // Write sorted formula's scores to file.
    public void writeScoresToFile(File resultsFile, String query) {

        // Create filename.
        query = query.replaceAll(" ", "-");
        String scores = resultsFile + File.separator + "resultsPR-" + query + ".txt";

        // Open file and write header.
        try (FileWriter fw = new FileWriter(scores)) {

            fw.append("DEPmember, scrore = SimilarityCosine * Parerank.");
            fw.append(System.getProperty("line.separator"));
            fw.append(System.getProperty("line.separator"));

            // Un-wrap/reverse score calculation of "sortedScoresWithMembers" for write to file.
            for (Map.Entry<String, Float> scoreWithMember : sortedScoresWithMembers) {

                String memberName = scoreWithMember.getKey();
                Float score = scoreWithMember.getValue();
                Float simCosScore = simCosWithNames.get(scoreWithMember.getKey());
                Float pageRankScore = wkPairs.get(scoreWithMember.getKey());

                String formatedDEPmember = String.format("%13s", memberName);
                String formatedScrore = String.format("%-12s", score);
                String formatedSimCos = String.format("%-11s", simCosScore);
                String formatedParerankPercent = String.format("%-13s", pageRankScore);

                // Write to file.
                fw.append(formatedDEPmember + ", " + formatedScrore + " = " + formatedSimCos + " * " + formatedParerankPercent);
                fw.append(System.getProperty("line.separator"));
            }

            fw.close();
        } catch (IOException ex) {
            System.err.println("Could Open to: " + scores);
            System.err.println(ex.getMessage());
        }

        System.out.println("File \"" + scores + "\" was created successfully.");
    }

}
