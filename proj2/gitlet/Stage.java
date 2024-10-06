package gitlet;

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/** a class that represents the stage for add and rm.
 *
 */
public class Stage  implements Serializable {
    // a map that keeps track of file that add into ADDITION, key is blob_id, value is path.
    private Map<String, String> blob_IdToPath;

    public Stage() {
        blob_IdToPath = new HashMap<>();
    }

    public Map<String, String> get_stage() {
        return blob_IdToPath;
    }

    // get a list of blobs in the stage area
    public List<Blob> getBlobList() {
        Blob blob;
        List<Blob> BlobList = new ArrayList<>();
        for (String blob_id : blob_IdToPath.keySet()) {
            blob = getBlobById(blob_id);
            BlobList.add(blob);
        }
        return BlobList;
    }

    // retrive a blob by its id
    public Blob getBlobById(String blob_id) {
        File Blob_file = join(Repository.BLOB_DIR, blob_id);
        return readObject(Blob_file, Blob.class);
    }

    // put a new blob into add_stage
    public void add_blob(String blob_id, String blob_path) {
        blob_IdToPath.put(blob_id, blob_path);
    }

    public void saveAddStage() {
        writeObject(Repository.ADDITION, this);
    }

    public void saveRemoveStage() {
        writeObject(Repository.REMOVAL, this);
    }



    // check if stage contains this blob
    public boolean ifContains(Blob blob) {
        String blob_id = blob.get_BlobId();
        return blob_IdToPath.containsKey(blob_id);
    }

    // delete this blob in the stage area
    public void delete(Blob blob) {
        String blob_id = blob.get_BlobId();
        blob_IdToPath.remove(blob_id);
    }



    // clear stage
    public void clear() {
        blob_IdToPath.clear();
    }

}
