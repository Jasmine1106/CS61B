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
    private Map<String, String> blobIdToPath;

    public Stage() {
        blobIdToPath = new HashMap<>();
    }

    // get a list of blobs in the stage area
    public List<Blob> getBlobList() {
        Blob blob;
        List<Blob> blobList = new LinkedList<>();
        for (String blobID : blobIdToPath.keySet()) {
            blob = getBlobByID(blobID);
            blobList.add(blob);
        }
        return blobList;
    }

    // get a list of blobs name in the stage area
    public List<String> getBlobNameList() {
        String blobName;
        List<String> blobNameList = new LinkedList<>();
        for (String blobID : blobIdToPath.keySet()) {
            blobName = getBlobByID(blobID).getFileName();
            blobNameList.add(blobName);
        }
        return blobNameList;
    }

    // get the specific blob  by its file name
    public  Blob getBlobByFileName(String fileName) {
        List<Blob> blobList = getBlobList();
        for (Blob blob :blobList) {
            if (blob.getFileName().equals(fileName)) {
                return blob;
            }
        }
        return null;
    }

    // retrive a blob by its id
    public static Blob getBlobByID(String blobID) {
        File blobFile = join(Repository.BLOB_DIR, blobID);
        return readObject(blobFile, Blob.class);
    }

    // put a new blob into add_stage
    public void addBlobInMap(String blobID, String blobPath) {
        blobIdToPath.put(blobID, blobPath);
    }

    public void saveAddStage() {
        writeObject(Repository.ADDITION, this);
    }

    public void saveRemoveStage() {
        writeObject(Repository.REMOVAL, this);
    }

    // check if stage contains this blob
    public boolean ifContains(Blob blob) {
        String blobID = blob.getBlobId();
        return blobIdToPath.containsKey(blobID);
    }

    // delete this blob in the stage area
    public void delete(Blob blob) {
        String blobID = blob.getBlobId();
        blobIdToPath.remove(blobID);
    }

    // clear stage
    public void clear() {
        blobIdToPath.clear();
    }

    public boolean isEmpty() {
        return blobIdToPath.isEmpty();
    }

    // Dicitionaries order
    public void printBlobsName() {
        List<String> stagedBlobs = this.getBlobNameList();
        Collections.sort(stagedBlobs);
        for (String blobName : stagedBlobs) {
            System.out.println(blobName);
        }
    }


}
