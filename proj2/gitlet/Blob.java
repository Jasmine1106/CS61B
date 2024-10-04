package gitlet;

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
    private byte[] file_contents;
    // a map that keep track of blob_id to file's path
    private Map<String, String> blob_IdToPath = new TreeMap<>();

    // creatr a new blob object, and add its blob_id and path mapping relationship into blob_IdToPath
    public Blob(File source_file){
        this.source = source_file;
        this.file_contents = Utils.readContents(source_file);
        String file_path = source_file.getPath();
        this.blob_id = Utils.sha1(file_contents, file_path);
    }


    public void saveTo(File filetoSave){
        Utils.writeObject(filetoSave, this);
    }

    public String get_BlobId() {
        return this.blob_id;
    }

}
