package gitlet;

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;


/** a class that represents the stage for add and rm.
 *
 */
public class Stage  implements Serializable {
    // a map that keeps track of file that add into ADDITION, key is blob_id, value is path.
    private Map<String, String> BlobIdToPath;

    public Stage() {
        BlobIdToPath = new HashMap<>();
    }

    public Map<String, String> get_stage() {
        return BlobIdToPath;
    }

    // get a list of blobs in the stage area
    public List<Blob> getBlobList() {
        Blob blob;
        List<Blob> BlobList = new LinkedList<>();
        for (String blob_id : BlobIdToPath.keySet()) {
            blob = getBlobByID(blob_id);
            BlobList.add(blob);
        }
        return BlobList;
    }

    // retrive a blob by its id
    public static Blob getBlobByID(String blob_id) {
        File Blob_file = join(Repository.BLOB_DIR, blob_id);
        return readObject(Blob_file, Blob.class);
    }


    // put a new blob into add_stage
    public void addBlobInMap(String blob_id, String blob_path) {
        BlobIdToPath.put(blob_id, blob_path);
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
        return BlobIdToPath.containsKey(blob_id);
    }

    // delete this blob in the stage area
    public void delete(Blob blob) {
        String blob_id = blob.get_BlobId();
        BlobIdToPath.remove(blob_id);
    }

    // clear stage
    public void clear() {
        BlobIdToPath.clear();
    }

    public boolean isEmpty() {
        return BlobIdToPath.isEmpty();
    }

    public void printBlobs() {
        List<Blob> StagedBlobs = this.getBlobList();
        for (Blob blob : StagedBlobs) {
            System.out.println(blob.toString());
        }
    }


}
