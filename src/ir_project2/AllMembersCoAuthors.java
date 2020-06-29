/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package ir_project2;

import Files.FilesOfFolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * AllMembersCoAuthors store all members Co-Authors from our department, the
 * data structure is a TreeMap<String, TreeMap<String, Float>> for more details
 * read report. Open all files content and store it in String and create for
 * each member Co-Authors (through class MemberCoAuthors ) and store them all
 * together.
 */
public class AllMembersCoAuthors {

    // All Dep members with Co-Authors and the number of the cooperations.
    private TreeMap<String, TreeMap<String, Float>> allMembersCoAuthors = new TreeMap<>();

    // Constructor.
    public AllMembersCoAuthors(String folderName, ArrayList<String> allMembersSurnames) {

        // Initialize allDEPsFilenames ArrayList.
        FilesOfFolder allFilenames = new FilesOfFolder(folderName);
        ArrayList<String> allDEPsFilenames = allFilenames.getFilesNames();

        String memberFileText;
        BufferedReader br = null;
        try {
            for (String DEPmemder : allDEPsFilenames) {
                memberFileText = "";
                br = new BufferedReader(new FileReader(folderName + File.separator + DEPmemder));
                String sCurrentLine;
                // Read all file line by line.
                while ((sCurrentLine = br.readLine()) != null) {
                    // Store all file text to memberFileText(String);
                    memberFileText = memberFileText + " \n" + sCurrentLine;
                }

                // Get each member co-authors and add to allMembersCoAuthors. 
                MemberCoAuthors oneMemberFileTokenization = new MemberCoAuthors(DEPmemder, memberFileText, allMembersSurnames);
                this.allMembersCoAuthors.put(oneMemberFileTokenization.getMemberName(), oneMemberFileTokenization.getMemberDictionary());
            }

            br.close();

        } catch (IOException e) {
            e.getMessage();
        }

    }

    // Getters.
    public TreeMap<String, TreeMap<String, Float>> getAllDEPmembersCoAuthors() {
        return allMembersCoAuthors;
    }

}
