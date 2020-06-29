/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package ir_project2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Loading allMembersVectors.ser file from project1 VSMs from all DEP members.
 * allMembersVectors contain for each member(String) a TreeMap with all the
 * dictionary terms(String) with the corresponding tf-idf weight(Float).
 */
public class LoadVSMs {
    
    HashMap<String, TreeMap<String, Float>> allMembersVectors = new HashMap<>();

    // Constructor.
    public LoadVSMs(String loadingPath) {

        // Load allMembersVectors.ser file from folder "loadingPath".
        try (FileInputStream fis = new FileInputStream(loadingPath); ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.allMembersVectors = (HashMap) ois.readObject();
        } catch (IOException ioe) {
            System.out.println("File don't exist");
            ioe.getMessage();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.getMessage();
            return;
        }

        System.out.println("Deserialized HashMap was successful.");
        System.out.println("All VSMs loaded from project1 !!!");

    }

    // Getter.
    public HashMap<String, TreeMap<String, Float>> getAllMembersVectors() {
        return allMembersVectors;
    }

    //Print all VSMs.
    void printAllVSMs() {

        for (Map.Entry<String, TreeMap<String, Float>> memberDictionery : this.allMembersVectors.entrySet()) {
            String DEPmember = memberDictionery.getKey();
            TreeMap<String, Float> documentToWordCount = memberDictionery.getValue();
            System.out.println();
            System.out.println(DEPmember + ": ");
            for (Map.Entry<String, Float> oneTermOfDictionary : documentToWordCount.entrySet()) {
                String term = oneTermOfDictionary.getKey();
                Float termFrequency = oneTermOfDictionary.getValue();
                System.out.print(" (" + term + "," + termFrequency + ")");
            }
        }

    }

}
