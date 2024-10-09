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
    private File source;
    private String blob_id;
    private byte[] fileContents;
    private String filePath;
    private File blobSavedFile;
    // a map that keep track of blob_id to file's path
    private Map<String, String> blob_IdToPath = new TreeMap<>();

    // creatr a new blob object, and add its blob_id and path mapping relationship into blob_IdToPath
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

    public String get_BlobId() {
        return this.blob_id;
    }

    public String get_BlobPath() {
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
