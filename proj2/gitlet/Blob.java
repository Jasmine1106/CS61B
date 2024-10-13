package gitlet;

import static gitlet.Utils.*;
import  static gitlet.Repository.BLOB_DIR;
import java.io.Serializable;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet blob object
 * @author Jasmine1106
 *
 *
 */
public class Blob implements Serializable {
    /** List all instance variables of the Blob class here with a useful comment above them
     * describing what that variable represents and how that variable is used.
     */
    private final File source;
    private final String blobID;
    private final byte[] fileContents;
    private final String filePath;
    private final File blobSavedFile;
    // a map that keep track of blobID to file's path
    private final Map<String, String> blobIdToPath = new TreeMap<>();

    // create a new blob object, and add its blobID and path mapping relationship into blob_IdToPath
    public Blob(File sourceFile) {
        this.source = sourceFile;
        this.fileContents = Utils.readContents(sourceFile);
        this.filePath = sourceFile.getPath();
        this.blobID = Utils.sha1(fileContents, filePath);
        this.blobSavedFile = generateBlobSavedName();
    }

    public void save() {
        Utils.writeObject(blobSavedFile, this);
    }

    public String getBlobId() {
        return this.blobID;
    }

    public String getBlobPath() {
        return this.source.getPath();
    }

    public String getFileName() {
        return source.getName();
    }

    public File getSourceFile() {
        return source;
    }

    public byte[] getFileContents() {
        return fileContents;
    }

    public File getBlobSavedFile() {
        return blobSavedFile;
    }

    private  File generateBlobSavedName()  {
        return join(BLOB_DIR, blobID);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Blob)) {
            return false;
        }
        Blob other = (Blob) obj;
        return this.blobID.equals(other.blobID);
    }

    @Override
    public int hashCode() {
        return blobID.hashCode();
    }

}
