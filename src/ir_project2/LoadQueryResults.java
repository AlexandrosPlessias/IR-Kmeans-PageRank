/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package ir_project2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Loading query.ser file from project1. SimCosWithNames contain all
 * members(String) with similarity cosine(Float) for query of constructor.
 */
public class LoadQueryResults {

    TreeMap<String, Float> simCosWithNames;
    
    // Constructor.
    public LoadQueryResults(String loadingPath, String query) {

        // Load SimCosWithNames.ser file from folder "loadingPath".
        try (FileInputStream fis = new FileInputStream(loadingPath); ObjectInputStream ois = new ObjectInputStream(fis)) {
            simCosWithNames = (TreeMap) ois.readObject();
        } catch (IOException ioe) {
            System.out.println("File don't exist");
            System.err.println(ioe.getMessage());
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            System.err.println(c.getMessage());
            return;
        }

        System.out.println("Deserialized TreeMap with depMember,simCos pairs was successful.");
        System.out.println("Query: \"" + query + "\" results loaded from project1!!!");

    }

    // Getter.
    public TreeMap<String, Float> getSimCosWithNames() {
        return simCosWithNames;
    }

    //Print all Members with similarity cosines.
    void printQueryResults() {

        for (Map.Entry<String, Float> entrySet : simCosWithNames.entrySet()) {
            String DEPmember = entrySet.getKey();
            Float simCos = entrySet.getValue();
            System.out.println("(" + DEPmember + "," + simCos + ")");
        }

    }
}
