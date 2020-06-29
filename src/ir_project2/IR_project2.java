/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package ir_project2;

import Files.Clustering_Kmeans;
import Files.PageRank;
import Files.RelevanceAndImportance;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Execute all the questions of exercise. Also in comments exist helpful
 * methods.
 */
public class IR_project2 {

    public static void main(String[] args) {

        // Create Results directory..
        File resultsFile = new File("Results");
        resultsFile.mkdirs();

        System.out.println("Question1 - Clustering K-means.");
        // Loading allMembersVectors.ser file from project1 VSMs from all DEP members.
        LoadVSMs lVSMs = new LoadVSMs("Load" + File.separator + "allMembersVectors.ser");
        HashMap<String, TreeMap<String, Float>> allMembersVectors = lVSMs.getAllMembersVectors(); //lVSMs.printAllVSMs(); 
        // Modes explained in Class Clustering_Kmeans.
        // Get mode pameter from user.
        int mode;
        Scanner scan = new Scanner(System.in);
        System.out.println("Mode 0 -> Cheat#1 : Calculate desirable centroid vector as initial.");
        System.out.println("Mode 1 -> Cheat#1+ : Calculate desirable centroid vector as initial plus groupA and groupB initial with desirable profs");
        System.out.println("Mode 2 -> Default DEP members, Vassilakis and Boucouvlas as initial centroid vectors");
        System.out.println("Mode 3 -> Random initial centroid vectors.");
        System.out.print("Please, select mode: ");
        mode = scan.nextInt();

        Clustering_Kmeans clustering_Kmeans = new Clustering_Kmeans(allMembersVectors, mode, resultsFile);
        clustering_Kmeans.runKmeans();
        System.out.println();

        System.out.println("Question2 - Link anlysis PageRank.");
        PageRank pagerank = new PageRank(resultsFile, (float) 0.60);
        // Question2.1 - Write Bmatrix to File.
        pagerank.pagerankMatrixToFile();
        // Question2.2 - Write PageRank first S-steps to File.
        pagerank.writePageRankStepsToFile();
        System.out.println();

        System.out.println("Question3 - Combination of Relevance and importance.");
        System.out.println("(Combination Of Cosine Similarity with Google Pagerank with damping.)");
        // Get Query results from project1.
        String testQuery = "Truth model driven system with enable the nearest database";
        LoadQueryResults loadQueryResults = new LoadQueryResults("Load" + File.separator + "" + testQuery + ".ser", testQuery);
        // All Dep members Relevance to testQuery from IR_project1.
        TreeMap<String, Float> simCosWithNames = loadQueryResults.getSimCosWithNames(); //loadQueryResults.printQueryResults();
        // All Dep members importance after S runs of page ranks.
        TreeMap<String, Float> wkPairs = pagerank.getWkPairs();
        // Combination of Relevance-Importance and write scores to file.
        RelevanceAndImportance relevanceAndImportance = new RelevanceAndImportance(simCosWithNames, wkPairs);
        relevanceAndImportance.writeScoresToFile(resultsFile, testQuery);

    }

}
