/**
 * @author Πλέσσιας Αλέξανδρος (ΑΜ.:2025201100068).
 */
package Files;

import java.io.File;
import java.util.ArrayList;

/**
 * FilesOfFolder get all files-names from folderPath. 
 * Return a ArrayList with all file names contained
 * in folderPath.
 */
public class FilesOfFolder {

    private final ArrayList<String> filesNames;

    // Constructor.
    public FilesOfFolder(String folderPath) {
        this.filesNames = new ArrayList<>();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                filesNames.add(listOfFile.getName());
            }
        }

        
    }

    // Getter.
    public ArrayList<String> getFilesNames() {
        return filesNames;
    }

}
