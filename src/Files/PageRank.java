/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package Files;

import ir_project2.AllMembersCoAuthors;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * PageRank is the implementation of question 2. PageRank implementation: create
 * matrixBnormalized and matrixR, next multiply d with matrixBnormalized and a
 * with matrixR. After create matrixGoogle which equals the sum of
 * d*matrixBnormalized and a*matrixR and finally rum formula: 
 * Wk = matrixGoogle * W(k-1), for S(given by user) times.
 */
public class PageRank {

    private final File resultsFile;
    private final ArrayList<String> allMembersSurnames;

    // Contain for each member's co-Authors with 
    private final TreeMap<String, TreeMap<String, Float>> allMembersCoAuthors;

    // Damping factor(Possibility of going to random outgoing link/page).
    private final float d;
    // Possibility of Teleport(a = 1 - d). 
    private final float a;

    // Useful matrices for pagerank implementation.
    private final Float[][] matrixBnormalized;
    private final Float[][] matrixR;
    private Float[][] matrixGoogle;

    // Store the last wS(Importance) for use in Question3.
    private Float[][] Ws = null;

    // Constructor.
    public PageRank(File resultsFile, float d) {

        // Initialization.
        this.resultsFile = resultsFile;

        // Get all names.
        String folderName = "papers";
        FilesOfFolder filesOfFolder = new FilesOfFolder(folderName);
        this.allMembersSurnames = filesOfFolder.getFilesNames();
        for (int i = 0; i < allMembersSurnames.size(); i++) {
            allMembersSurnames.set(i, allMembersSurnames.get(i).replace(".bib", ""));
        }
        // I choose to sort my arraylist for safety.  
        // Because arraylist is already sorted.Why? 
        // Because files are sorted in folder (READ ONE BY ONE FROM TOP TO BOTTOM OF FILE).
        Collections.sort(allMembersSurnames, String.CASE_INSENSITIVE_ORDER);

        // Get all DEP members co-authors from our allMembersSurnames.
        this.allMembersCoAuthors = new AllMembersCoAuthors(folderName, allMembersSurnames).getAllDEPmembersCoAuthors();

        // Initializations.
        this.d = d;
        this.a = 1 - d; // Always a= 1-d.
        this.matrixBnormalized = new Float[allMembersSurnames.size()][allMembersSurnames.size()];
        this.matrixR = new Float[allMembersSurnames.size()][allMembersSurnames.size()];

        // Create BnormalizedLinkMatrix. 
        createBnormalizedLinkMatrix();
        // Create Rmatrix (all values equal with 1/n).
        createRMatrix();
    }

    // Getter
    public TreeMap<String, Float> getWkPairs() {

        TreeMap<String, Float> WkPairs = new TreeMap<>();

        for (int i = 0; i < allMembersSurnames.size(); i++) {
            WkPairs.put(allMembersSurnames.get(i), Ws[i][0]);
        }
        return WkPairs;
    }

    /* Create and fill matrixBnormalized with help of allMembersCoAuthors.   
     *  from    from    from    ...  <-i
     *  to   percent percent percent ...
     *  to   percent percent percent ...
     *  to   percent percent percent ...
     *  .    percent percent percent ...
     *  .    percent percent percent ...
     *  .    percent percent percent ...
     *  j
     */
    private Float[][] createBnormalizedLinkMatrix() {

        //Normalization..
        // Normalazation constant G.
        Float constG;
        //FROM one DEP member TO all others DEP member.
        for (Map.Entry<String, TreeMap<String, Float>> memberCoAuthors : this.allMembersCoAuthors.entrySet()) {
            TreeMap<String, Float> memberLinksToOthers = memberCoAuthors.getValue();

            constG = (float) 0;
            for (Map.Entry<String, Float> oneLinkToOther : memberLinksToOthers.entrySet()) {
                constG += oneLinkToOther.getValue();
            }

            for (Map.Entry<String, Float> oneLinkToOther : memberLinksToOthers.entrySet()) {
                // Valueu / constG, Avoid division by zero give NaN.
                if (constG != 0) {
                    oneLinkToOther.setValue(oneLinkToOther.getValue() / constG);
                }
            }

        }

        // Fill the matrix.
        /*
         from    from    from    ... <-i
         to  percent percent percent ...
         to  percent percent percent ...
         to  percent percent percent ...
         .   percent percent percent ...
         .   percent percent percent ...
         .   percent percent percent ...
         j
         */
        int i = -1; // Matrix x-coord start from 0.
        int j;
        for (Map.Entry<String, TreeMap<String, Float>> memberCoAuthors : this.allMembersCoAuthors.entrySet()) {
            i++;
            j = -1; // Matrix y-coord start from 0.
            TreeMap<String, Float> memberLinksToOthers = memberCoAuthors.getValue();
            for (Map.Entry<String, Float> oneLinkToOther : memberLinksToOthers.entrySet()) {
                j++;
                matrixBnormalized[j][i] = oneLinkToOther.getValue();
            }
        }

        return matrixBnormalized;
    }

    // Create Rmatrix and fill all values with 1/n.
    private void createRMatrix() {

        for (int i = 0; i < matrixR.length; i++) {
            for (int j = 0; j < matrixR.length; j++) {
                matrixR[i][j] = (1 / (float) allMembersSurnames.size());
            }
        }
    }

    // Write B normalized link matrix to file.
    public void pagerankMatrixToFile() {

        // Create filename.
        String pageRankMatrix = resultsFile + File.separator + "pagerank-matrix.txt";

        // Open file and write header.
        try (FileWriter fw = new FileWriter(pageRankMatrix)) {
            fw.append("B Normalized link matrix: ");
            fw.append(System.getProperty("line.separator"));
            fw.append(System.getProperty("line.separator"));

            // Write B matrix to file.
            String corner = "to\\from";
            String formatedCorner = String.format("%13s", corner);
            fw.append(formatedCorner + "| ");
            for (String curMember : allMembersSurnames) {
                String formatedProf = String.format("%-13s", curMember);
                fw.append(formatedProf + "| ");
            }
            fw.append(System.getProperty("line.separator"));

            for (int x = 0; x < matrixBnormalized.length; x++) {
                String formatedProf = String.format("%13s", allMembersSurnames.get(x));
                fw.append(formatedProf + "| ");
                for (int y = 0; y < matrixBnormalized.length; y++) {
                    String formatedPercent = String.format("%-13s", matrixBnormalized[x][y]);
                    fw.append(formatedPercent + "| ");
                }
                fw.append(System.getProperty("line.separator"));
            }

            fw.append(System.getProperty("line.separator"));
            fw.append("Note: Yiannopoulos has as co-author someone with the same last name of Mr. D.S.Vlachos "
                    + "this happens because I considering only for the surnames. ");

            fw.close();
            System.out.println("File \"" + pageRankMatrix + "\" was created successfully.");
        } catch (IOException ex) {
            System.err.println("Could Open to: " + pageRankMatrix);
            System.err.println(ex.getMessage());
        }
    }

    // Implementation of Page rank algorithm with damping factor and Google matrix and write each step to file.
    public void writePageRankStepsToFile() {

        // Get S pameter from user.
        int S;
        Scanner scan = new Scanner(System.in);
        System.out.print("Please, give S value: ");
        S = scan.nextInt(); // The fist N-impotant elements.
        S++;

        // Create Google matrix= (d*matrixB) + (a*matrixR). Matrices B & R is already created and initialized from constractor.
        matrixGoogle = sumTwo2DArrays(numberMultiplyArray2D(d, matrixBnormalized), numberMultiplyArray2D(a, matrixR));

        // Wreate W0.
        Float[][] w0 = new Float[allMembersSurnames.size()][1];
        for (int i = 0; i < w0.length; i++) {
            w0[i][0] = (1 / (float) allMembersSurnames.size());
        }

        ArrayList<Float[][]> allWs = new ArrayList<>();
        allWs.add(w0);

        // Run formula Wk = matrixGoogle * W(k-1) .
        for (int k = 1; k < S; k++) {

            // Wk.add(matrixGoogle * W(k-1)) .
            allWs.add(array2DMultiplyArray1D(matrixGoogle, allWs.get(k - 1)));
        }

        // Create filename.
        String pageRankRun = resultsFile + File.separator + "pagerank-calc.txt";
        // Open file and write header.
        try (FileWriter fw = new FileWriter(pageRankRun)) {
            fw.append("Page Rank step by step: ");
            fw.append(System.getProperty("line.separator"));
            fw.append(System.getProperty("line.separator"));

            for (int i = 0; i < allWs.size(); i++) {
                String stepMsg = "Step #" + i + ": ";
                String formatedStepMsg = String.format("%11s", stepMsg);
                fw.append(formatedStepMsg);

                for (int j = 0; j < allWs.get(i).length; j++) {

                    String formatedProf = String.format("%" + (allMembersSurnames.get(j).length() + 1) + "s", allMembersSurnames.get(j));
                    String formatedPercent = String.format("%-12s", (allWs.get(i))[j][0]);
                    fw.append("(" + formatedProf + ", " + formatedPercent + ") ");

                    if (i == 0 && j == allWs.get(i).length - 1) {
                        fw.append(System.getProperty("line.separator"));
                    }
                }

                fw.append(System.getProperty("line.separator"));
            }
            fw.close();

            System.out.println("File \"" + pageRankRun + "\" was created successfully.");

        } catch (IOException ex) {
            System.err.println("Could Open to: " + pageRankRun);
            ex.getMessage();
        }

        // Store the last w for future use.
        Ws = allWs.get(allWs.size() - 1);
    }

    // Auxiliary method for multiply a number with a 2D array.
    private Float[][] numberMultiplyArray2D(float num, Float[][] array2D) {

        for (int i = 0; i < array2D.length; i++) {
            for (int j = 0; j < array2D.length; j++) {
                array2D[i][j] = num * array2D[i][j];
            }
        }

        return array2D;
    }

    // Auxiliary method for multiply a 1D or 2D array and a 2D array.
    private Float[][] array2DMultiplyArray1D(Float[][] m1, Float[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length.
        int m2RowLength = m2.length;    // m2 rows length.
        if (m1ColLength != m2RowLength) {
            return null; // Matrix multiplication is not possible.
        }
        int mRRowLength = m1.length;    // m result rows length.
        int mRColLength = m2[0].length; // m result columns length.

        // Initialization of m Result array.
        Float[][] mResult = new Float[mRRowLength][mRColLength];
        for (int i = 0; i < mRRowLength; i++) {
            for (int j = 0; j < mRColLength; j++) {
                mResult[i][j] = (float) 0;
            }
        }

        // Rows from m1
        for (int i = 0; i < mRRowLength; i++) {
            // columns from m2.
            for (int j = 0; j < mRColLength; j++) {
                // columns from m1.
                for (int k = 0; k < m1ColLength; k++) {
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    // Auxiliary method for sum two 2D arrays.
    private Float[][] sumTwo2DArrays(Float[][] arrayD1_1, Float[][] arrayD2_2) {

        Float[][] resultsArray2D = new Float[arrayD1_1.length][arrayD1_1.length];

        // Same size always.
        for (int i = 0; i < resultsArray2D.length; i++) {
            for (int j = 0; j < resultsArray2D.length; j++) {
                resultsArray2D[i][j] = arrayD1_1[i][j] + arrayD2_2[i][j];
            }
        }
        return resultsArray2D;
    }

}
