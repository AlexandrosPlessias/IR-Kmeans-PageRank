/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Clustering_Kmeans is the implementation of question 1. K-means have 4 run
 * Modes: Mode 0 -> Cheat#1 : Calculate desirable centroid vector as
 * initial.(method cheatCenters()) Mode 1 -> Cheat#1+ : Calculate desirable
 * centroid vector as initial plus groupA and groupB initial with desirable
 * profs. With method cheatCenters(). Mode 2 -> Default DEP members, Vassilakis
 * and Boucouvlas as initial centroid vectors. Mode 3 -> Random initial centroid
 * vectors.
 *
 * The k-means separates all members depending on the Euclidian distance they
 * are have from each centroid, after calculated the changes if changes==0 the
 * k-means stop else calculate new centroid vector(calcGroupAnewCenter() and
 * calcGroupBnewCenter()) and calculate new Euclidian distances again.
 *
 * The K-means create 2 groups Group A and Group B, when the method
 * renameFiles() read the last meaningful line of each file (aGroup.txt and
 * bGroup.txt) and see if contain DEP member Vassilakis or Boucouvlas, those 2
 * members are landmarks for each department(Vassilakis for tety and Boucouvlas
 * for tett). If one file contain Vassilakis has renamed to tety.txt and if
 * contain Boucouvlas has renamed to tett.txt if both are in same file then the
 * files don't change filenames.
 *
 */
public class Clustering_Kmeans {

    // All Dep members with terms names and tf-ids.
    private final HashMap<String, TreeMap<String, Float>> allMembersVectors;

    // Only the first L terms.
    private int L = 0;

    // Mode 0 -> Cheat#1  : Calculate desirable centroid vector as initial.
    // Mode 1 -> Cheat#1+ : Calculate desirable centroid vector as initial plus groupA and groupB initial with desirable profs.
    // Mode 2 -> Default DEP memeders, Vassilakis and Boucouvlas as initial centroid vectors.
    // Mode 3 -> Random initial centroid vectors. 
    private final int mode;

    // Used for rename files.
    private final File resultsFile;
    private final File groupAFile;
    private final File groupBFile;
    private final String tetyLandmark = "Vassilakis";
    private final String tettLandmark = "Boucouvalas";

    // The center of each group.
    private TreeMap<String, Float> groupACenterVector = new TreeMap<>();
    private TreeMap<String, Float> groupBCenterVector = new TreeMap<>();

    // The memebers of each group.
    private HashMap<String, TreeMap<String, Float>> aGroup = new HashMap<>();
    private HashMap<String, TreeMap<String, Float>> bGroup = new HashMap<>();

    // Constructor.
    public Clustering_Kmeans(HashMap<String, TreeMap<String, Float>> allMembersVectors, int mode, File resultsFile) {

        // Initilization of allMembersVectors.
        this.allMembersVectors = allMembersVectors;

        // Initilization of L.
        // Get size of dictionary. I count the first memeber's terms becaouse all vectors are equal.
        for (Map.Entry<String, TreeMap<String, Float>> memberVSM : allMembersVectors.entrySet()) {
            TreeMap<String, Float> memberAllTermWithWithTfIdf = memberVSM.getValue();
            this.L = memberAllTermWithWithTfIdf.size();
            break;

        }

        this.mode = mode;
        this.resultsFile = resultsFile;

        // First clean file from old files.
        for (File file : resultsFile.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        // Create filenames.
        groupAFile = new File(resultsFile + File.separator + "aGroup.txt");
        groupBFile = new File(resultsFile + File.separator + "bGroup.txt");
    }

    // Implementation of K-means algorithm.
    public void runKmeans() {
        
        //Initialaize the center of each group and overwrite after first loop, according to given Mode.  
        if (mode == 0) {
            //System.out.println("Mode 0 -> Cheat#1  : Calculate desirable centroid vector as initial.");
            cheatCenters();
            aGroup.clear();
            bGroup.clear();
        } else if (mode == 1) {
            //System.out.println("Mode 1 -> Cheat#1+ : Calculate desirable centroid vector as initial plus groupA and groupB initial with desirable profs.");
            cheatCenters();
        } else if (mode == 2) {
           // System.out.println("Mode 2 -> Default DEP memeders, Vassilakis and Boucouvlas as initial centroid vectors.");
            groupACenterVector = allMembersVectors.get("Vassilakis");
            groupBCenterVector = allMembersVectors.get("Boucouvalas");
        } else if (mode == 3) {
            //System.out.println("Mode 3 -> Random initial centroid vectors.");
            Random random = new Random();
            List<String> keys = new ArrayList<>(allMembersVectors.keySet());
            String randomKey1 = keys.get(random.nextInt(keys.size()));
            String randomKey2 = keys.get(random.nextInt(keys.size()));

            // If random keys are egual use the default members Vassilakis & Boucouvalas of Mode 2.
            if (randomKey1.compareTo(randomKey2) == 0) {
                System.out.println("Keys are equal about that use the default centers: Vassilakis and Boucouvalas.");
                groupACenterVector = allMembersVectors.get("Vassilakis");
                groupBCenterVector = allMembersVectors.get("Boucouvalas");
            } else {
                System.out.println("Choose random for center the DEP memeders: " + randomKey1 + " & " + randomKey2 + ".");
                groupACenterVector = allMembersVectors.get(randomKey1);
                groupBCenterVector = allMembersVectors.get(randomKey2);
            }
        } else {
            System.out.println("Not valid mode. Use the default Mode 0.");
            System.out.println("Mode 0 -> Cheat#1  : Calculate desirable centroid vector as initial.");
            cheatCenters();
            aGroup.clear();
            bGroup.clear();
        }
        System.out.println("The K-means run for the first " + this.L + " terms.");
        System.out.println();

        // Create and fill aGroup and bGroup files.
        try (FileWriter fwAgroup = new FileWriter(groupAFile); FileWriter fwBgroup = new FileWriter(groupBFile)) {

            fwAgroup.append("K-means in Mode " + mode + " for first L(" + L + ") terms.");
            fwAgroup.append(System.getProperty("line.separator"));
            fwAgroup.append("Loop number: ( DEPname , Euclidean Distance )...");
            fwAgroup.append(System.getProperty("line.separator"));
            fwAgroup.append(System.getProperty("line.separator"));

            fwBgroup.append("K-means in Mode " + mode + " for first L(" + L + ") terms.");
            fwBgroup.append(System.getProperty("line.separator"));
            fwBgroup.append("Loop number: ( DEPname , Euclidean Distance )...");
            fwBgroup.append(System.getProperty("line.separator"));
            fwBgroup.append(System.getProperty("line.separator"));

            int changes;
            int countL;
            int loopCounter = 0;
            do {
                changes = 0;
                loopCounter++;
                fwAgroup.append("Loop #" + loopCounter + ": ");
                fwBgroup.append("Loop #" + loopCounter + ": ");

                // Calc K-means with euclidian distance.
                for (Map.Entry<String, TreeMap<String, Float>> memberVSM : allMembersVectors.entrySet()) {
                    TreeMap<String, Float> memberAllTermWithWithTfIdf = memberVSM.getValue();

                    countL = 0;
                    float groupAdiafora;
                    float euclidianA;
                    float teragonoA = 0;
                    for (Map.Entry<String, Float> memberTermWithTfIdf : memberAllTermWithWithTfIdf.entrySet()) {

                        // Only the first L terms.
                        if (countL == L) {
                            break;
                        }
                        countL++;

                        for (Map.Entry<String, Float> groupACenterTermWithTfIdf : groupACenterVector.entrySet()) {

                            // I want common terms ONLY.
                            if (memberTermWithTfIdf.getKey().compareTo(groupACenterTermWithTfIdf.getKey()) == 0) {
                                // Calc Euclidean Distance (current prof,center1).
                                if (memberTermWithTfIdf.getValue() == 0 && groupACenterTermWithTfIdf.getValue() == 0) {
                                    teragonoA += 0;
                                } else if (memberTermWithTfIdf.getValue() == 0) {
                                    teragonoA += Math.pow(groupACenterTermWithTfIdf.getValue(), 2);
                                } else if (groupACenterTermWithTfIdf.getValue() == 0) {
                                    teragonoA += Math.pow(memberTermWithTfIdf.getValue(), 2);
                                } else {
                                    groupAdiafora = memberTermWithTfIdf.getValue() - groupACenterTermWithTfIdf.getValue();
                                    teragonoA += Math.pow(groupAdiafora, 2);
                                }
                            }

                        }

                    }
                    euclidianA = (float) Math.sqrt((double) teragonoA);

                    countL = 0;
                    float groupBdiafora;
                    float euclidianB;
                    float teragonoB = 0;
                    for (Map.Entry<String, Float> memberTermWithTfIdf : memberAllTermWithWithTfIdf.entrySet()) {
                        // Only the first L terms.
                        if (countL == L) {
                            break;
                        }
                        countL++;
                        for (Map.Entry<String, Float> groupBCenterTermWithTfIdf : groupBCenterVector.entrySet()) {

                            // I want common terms ONLY.
                            if (memberTermWithTfIdf.getKey().compareTo(groupBCenterTermWithTfIdf.getKey()) == 0) {
                                // Calc Euclidean Distance (current prof,center2)
                                if (memberTermWithTfIdf.getValue() == 0 && groupBCenterTermWithTfIdf.getValue() == 0) {
                                    teragonoB += 0;
                                } else if (memberTermWithTfIdf.getValue() == 0) {
                                    teragonoB += (float) Math.pow((double) groupBCenterTermWithTfIdf.getValue(), 2);
                                } else if (groupBCenterTermWithTfIdf.getValue() == 0) {
                                    teragonoB += (float) Math.pow((double) memberTermWithTfIdf.getValue(), 2);
                                } else {
                                    groupBdiafora = memberTermWithTfIdf.getValue() - groupBCenterTermWithTfIdf.getValue();
                                    teragonoB += (float) Math.pow((double) groupBdiafora, 2);
                                }
                            }

                        }
                    }
                    euclidianB = (float) Math.sqrt((double) teragonoB);

                    // Check for changes and write to files.
                    String DEPmember = memberVSM.getKey();
                    Float euclidianDist;
                    if (euclidianA > euclidianB) {  // Add to Group B. 
                        euclidianDist = euclidianB;
                        fwBgroup.append("( " + DEPmember + " , " + euclidianDist + " ) ");

                        if (!bGroup.containsKey(memberVSM.getKey())) {
                            changes++;
                            bGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
                            aGroup.remove(memberVSM.getKey());
                        }

                    } else if ((euclidianA < euclidianB)) { // Add to Group A. 

                        euclidianDist = euclidianA;
                        fwAgroup.append(" ( " + DEPmember + " , " + euclidianDist + " ) ");

                        if (!aGroup.containsKey(memberVSM.getKey())) {
                            changes++;
                            aGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
                            bGroup.remove(memberVSM.getKey());
                        }
                    } else { // (euclidianA == euclidianB) Add to Group A or B. 
                        Random random = new Random();
                        int coin = random.nextInt(2);
                        if (coin == 0) { // head-> Add to Group  B.  

                            euclidianDist = euclidianB;
                            fwBgroup.append(" ( " + DEPmember + " , " + euclidianDist + " ) ");

                            if (!bGroup.containsKey(memberVSM.getKey())) {
                                changes++;
                                bGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
                                aGroup.remove(memberVSM.getKey());
                            }
                        } else {    // tails -> Add to Group A.
                            euclidianDist = euclidianA;
                            fwAgroup.append(" ( " + DEPmember + " , " + euclidianDist + " ) ");

                            if (!aGroup.containsKey(memberVSM.getKey())) {
                                changes++;
                                aGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
                                bGroup.remove(memberVSM.getKey());
                            }
                        }
                    }
                } // End of first for

                // Show loop, Group A, Group B and changes.
                System.out.println("Loop #" + loopCounter + ":");
                System.out.println("A Group: " + aGroup.keySet());
                System.out.println("B Group: " + bGroup.keySet());
                System.out.println("Changes: " + changes);
                System.out.println();

                // If don't exist any changes the K-means stop.
                if (changes == 0) {
                    fwAgroup.append(System.getProperty("line.separator"));
                    fwAgroup.append("Don't exist any changes, k-means stop.");
                    fwAgroup.append(System.getProperty("line.separator"));
                    fwBgroup.append(System.getProperty("line.separator"));
                    fwBgroup.append("Don't exist any changes, k-means stop.");
                    fwBgroup.append(System.getProperty("line.separator"));
                    break;

                } else {
                    // Calcutate new Centroids.
                    calcGroupAnewCenter();
                    fwAgroup.append(System.getProperty("line.separator"));
                    calcGroupBnewCenter();
                    fwBgroup.append(System.getProperty("line.separator"));
                }

            } while (true);

            fwAgroup.close();
            fwBgroup.close();
        } catch (IOException ex) {
            ex.getMessage();
        }

        // Rename files if it is possible. 
        renameFiles();
    }

    // Calculate desirable centroid vector as initial plus groupA and groupB initial with desirable profs.
    private void cheatCenters() {

        groupACenterVector = allMembersVectors.get("Vassilakis"); // dont matter will overwrite i use only the terms
        ArrayList<String> tetyProfs = new ArrayList<>();
        tetyProfs.add("Lepouras");
        tetyProfs.add("Kolokotronis");
        tetyProfs.add("Vlachos");
        tetyProfs.add("Vassilakis");
        tetyProfs.add("Wallace");
        tetyProfs.add("Tryfonopoulos");
        tetyProfs.add("Platis");
        tetyProfs.add("Skiadopoulos");
        tetyProfs.add("Malamatos");
        tetyProfs.add("Simos");
        tetyProfs.add("Koutras");
        tetyProfs.add("Tselikas");
        tetyProfs.add("Masselos");

        groupBCenterVector = allMembersVectors.get("Boucouvalas");
        ArrayList<String> tettProfs = new ArrayList<>();
        tettProfs.add("Tsoulos");
        tettProfs.add("Maras");
        tettProfs.add("Athanasiadou");
        tettProfs.add("Politi");
        tettProfs.add("Kaloxylos");
        tettProfs.add("Peppas");
        tettProfs.add("Stavdas");
        tettProfs.add("Boucouvalas");
        tettProfs.add("Glentis");
        tettProfs.add("Yiannopoulos");
        tettProfs.add("Moscholios");
        tettProfs.add("Sagias");
        tettProfs.add("Blionas");

        for (Map.Entry<String, TreeMap<String, Float>> memberVSM : allMembersVectors.entrySet()) {
            TreeMap<String, Float> memberAllTermWithWithTfIdf = memberVSM.getValue();
            if (tetyProfs.contains(memberVSM.getKey())) {
                aGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
            } else if (tettProfs.contains(memberVSM.getKey())) {
                bGroup.put(memberVSM.getKey(), memberAllTermWithWithTfIdf);
            }
        }

        // Calculate initiale centers.
        int countL = 0;
        float aSum;
        int aSize;
        float aAvg;
        // All dictionary one-by-one groupACenterTermWithTfIdf.getKey() .
        for (Map.Entry<String, Float> groupACenterTermWithTfIdf : groupACenterVector.entrySet()) {
            // Only the first L terms.
            if (countL == L) {
                break;
            }
            countL++;
            aSum = 0;
            aSize = 0;
            for (Map.Entry<String, TreeMap<String, Float>> aMemberVSM : aGroup.entrySet()) {
                TreeMap<String, Float> aMemberAllTermWithWithTfIdf = aMemberVSM.getValue();
                for (Map.Entry<String, Float> aMemberTermWithTfIdf : aMemberAllTermWithWithTfIdf.entrySet()) {
                    aSum += aMemberTermWithTfIdf.getValue();
                    aSize++;
                }

            }

            if (aSize != 0) {
                aAvg = aSum / aSize;
            } else {
                aAvg = 0;
            }
            groupACenterVector.put(groupACenterTermWithTfIdf.getKey(), aAvg);
        }

        countL = 0;
        float bSum;
        int bSize;
        float bAvg;
        // All dictionary one-by-one groupBCenterTermWithTfIdf.getKey() .
        for (Map.Entry<String, Float> groupBCenterTermWithTfIdf : groupBCenterVector.entrySet()) {
            // Only the first L terms.
            if (countL == L) {
                break;
            }
            bSum = 0;
            bSize = 0;
            for (Map.Entry<String, TreeMap<String, Float>> bMemberVSM : bGroup.entrySet()) {
                TreeMap<String, Float> bMemberAllTermWithWithTfIdf = bMemberVSM.getValue();
                for (Map.Entry<String, Float> bMemberTermWithTfIdf : bMemberAllTermWithWithTfIdf.entrySet()) {
                    bSum += bMemberTermWithTfIdf.getValue();
                    bSize++;
                }
            }

            if (bSize != 0) {
                bAvg = bSum / bSize;
            } else {
                bAvg = 0;
            }

            groupBCenterVector.put(groupBCenterTermWithTfIdf.getKey(), bAvg);
        }
    }

    /* I read the last 3 lines because the last is empty, the pre-last have the msg:"Don't exist any changes, k-means stop."
     * and the pre-pre-pre last contain tha final group mebers.  I care only the last's loop members because some members 
     * maybe can leave the group before finish.
     */
    private void renameFiles() {
        File tety = new File(resultsFile + File.separator + "tety.txt");
        File tett = new File(resultsFile + File.separator + "tett.txt");

        // If contain tetyLandmark.
        String last3LinesOfGroupAFile = tailOfFile(groupAFile, 3);
        if (last3LinesOfGroupAFile.contains(tetyLandmark)) {
            groupAFile.renameTo(tety);
        }

        // If contain tettLandmark.
        String last3LinesOfGroupBFile = tailOfFile(groupBFile, 3);
        if (last3LinesOfGroupBFile.contains(tettLandmark)) {
            groupBFile.renameTo(tett);
        }

        if (mode == 0 || mode == 1 || mode == 2) {
            System.out.println("The renaming was successful!!!");
        } else {
            System.out.println("In random mode the renaming don't work always...");
        }

    }

    /* Return the last N lines from file C style code. 
     * http://stackoverflow.com/questions/686231/quickly-read-the-last-line-of-a-text-file .
     */
    private String tailOfFile(File file, int lines) {
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler
                    = new java.io.RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            // Code aka C.
            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            e.getMessage();
            return null;
        } catch (java.io.IOException e) {
            e.getMessage();
            return null;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // Calculate new centroid vector from Group A members.
    private void calcGroupAnewCenter() {
        int countL = 0;
        float aSum;
        int aSize;
        float aAvg;
        // All dictionary one-by-one groupACenterTermWithTfIdf.getKey() .
        for (Map.Entry<String, Float> groupACenterTermWithTfIdf : groupACenterVector.entrySet()) {
            // Only the first L terms.
            if (countL == L) {
                break;
            }
            countL++;
            aSum = 0;
            aSize = 0;
            for (Map.Entry<String, TreeMap<String, Float>> aMemberVSM : aGroup.entrySet()) {
                TreeMap<String, Float> aMemberAllTermWithWithTfIdf = aMemberVSM.getValue();
                for (Map.Entry<String, Float> aMemberTermWithTfIdf : aMemberAllTermWithWithTfIdf.entrySet()) {
                    aSum += aMemberTermWithTfIdf.getValue();
                    aSize++;
                }

            }

            if (aSize != 0) {
                aAvg = aSum / aSize;
            } else {
                aAvg = 0;
            }

            groupACenterVector.put(groupACenterTermWithTfIdf.getKey(), aAvg);
        }
    }

    // Calculate new centroid vector from Group B members.
    private void calcGroupBnewCenter() {
        int countL = 0;
        float bSum;
        int bSize;
        float bAvg;
        // All dictionary one-by-one groupBCenterTermWithTfIdf.getKey() .
        for (Map.Entry<String, Float> groupBCenterTermWithTfIdf : groupBCenterVector.entrySet()) {
            // Only the first L terms.
            if (countL == L) {
                break;
            }
            countL++;
            bSum = 0;
            bSize = 0;
            for (Map.Entry<String, TreeMap<String, Float>> bMemberVSM : bGroup.entrySet()) {
                TreeMap<String, Float> bMemberAllTermWithWithTfIdf = bMemberVSM.getValue();
                for (Map.Entry<String, Float> bMemberTermWithTfIdf : bMemberAllTermWithWithTfIdf.entrySet()) {
                    bSum += bMemberTermWithTfIdf.getValue();
                    bSize++;
                }

            }

            if (bSize != 0) {
                bAvg = bSum / bSize;
            } else {
                bAvg = 0;
            }

            groupBCenterVector.put(groupBCenterTermWithTfIdf.getKey(), bAvg);
        }

    }
}
