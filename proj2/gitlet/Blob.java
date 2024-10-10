package gitlet;

import static gitlet.Utils.*;
import  static gitlet.Repository.BLOB_DIR;
import java.io.Serializable;
import java.io.File;
import java.util.List;
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
    private final String blob_id;
    private final byte[] fileContents;
    private final String filePath;
    private final File blobSavedFile;
    // a map that keep track of blob_id to file's path
    private final Map<String, String> blobIdToPath = new TreeMap<>();

    // create a new blob object, and add its blob_id and path mapping relationship into blob_IdToPath
    public Blob(File source_file){
        this.source = source_file;
        this.fileContents = Utils.readContents(source_file);
        this.filePath = source_file.getPath();
        this.blob_id = Utils.sha1(fileContents, filePath);
        this.blobSavedFile = generateBlobSavedName();
    }


    public void save(){
        Utils.writeObject(blobSavedFile, this);
    }

    public String getBlobId() {
        return this.blob_id;
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

    private  File generateBlobSavedName(){
        return join(BLOB_DIR, blob_id);
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
        return this.blob_id.equals(other.blob_id);
    }

    @Override
    public int hashCode() {
        return blob_id.hashCode();
    }

}
