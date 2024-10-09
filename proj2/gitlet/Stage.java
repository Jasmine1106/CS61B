package gitlet;

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;



/** a class that represents the stage for add and rm.
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

    // get a list of blobs name in the stage area
    public List<String> getBlobNameList() {
        String blobName;
        List<String> BlobNameList = new LinkedList<>();
        for (String blob_id : BlobIdToPath.keySet()) {
            blobName = getBlobByID(blob_id).getFileName();
            BlobNameList.add(blobName);
        }
        return BlobNameList;
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

    // Dicitionaries order
    public void printBlobsName() {
        List<String> StagedBlobs = this.getBlobNameList();
        Collections.sort(StagedBlobs);
        for (String blobName : StagedBlobs) {
            System.out.println(blobName);
        }
    }


}
