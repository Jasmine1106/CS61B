package gitlet;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Commit.*;
import static gitlet.Branch.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jasmine1106
 */
public class Repository {
    /** List all instance variables of the Repository class here with a useful
     *  comment above them describing what that variable represents and how that
     *  variable is used. We've provided two examples for you.
     */
    /** The current working directory.
     * .GITLET_DIR/ -- hidden gitlet directory
     *    - OBJECT_DIR/ -- folder containing all Serializable object
     *         - BLOB_DIR/ -- all the reference of file object
     *         - COMMIT_DIR/ -- all commit
     *    - STAGE_DIR/ -- the staging area
     *         - ADDITION a file storing the blob object
     *         - REMOVAL
     *    - BRANCH_DIR/ -- all branches wo have
     *         - master   default branch name
     *         - ...(other branch)
     *    - HEAD  -- current commit_id corrsponding the current commit
     *    - BRANCH -- current branch name
     * */

    // Default branch name.
    private static final String DEFAULT_BRANCH_NAME = "master";
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");
    /** the first floor of the directory*/
    private static final File OBJECT_DIR = join(GITLET_DIR, "OBJECT_DIR");
    private static final File STAGE_DIR = join(GITLET_DIR, "STAGE_DIR");
    static File BRANCH_DIR = join(GITLET_DIR, "BRANCH_DIR");
    static File HEAD = join(GITLET_DIR, "HEAD");
    static File BRANCH = join(GITLET_DIR, "BRANCH");
    /** the second floor of the directory*/
    static File BLOB_DIR = join(OBJECT_DIR, "BLOB_DIR");
    static File COMMIT_DIR = join(OBJECT_DIR, "COMMIT_DIR");
    static File ADDITION = join(STAGE_DIR, "ADDITION");
    static File REMOVAL = join(STAGE_DIR, "REMOVAL");

    // instance variable
    static Stage addStage = new Stage();      // add stage
    static Stage removeStage = new Stage();  // remove stage



    // Does require filesystem operations to allow for persistence.

    /** init
     *  1. creat the gitlet directory
     *  2. initial commit
     *  3. set current branch to master and update BRANCH_DIR
     */
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();
        // create initial commit
        Commit inital = new Commit();
        inital.save();
        // branch
        writeContents(BRANCH, DEFAULT_BRANCH_NAME);
        File headFile = join(BRANCH_DIR, DEFAULT_BRANCH_NAME);
        writeContents(headFile, inital.getCommitID());
    }

    /** create the basic structrue of gitlet directory */
    private static void setupPersistence() throws IOException {
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        STAGE_DIR.mkdir();
        BLOB_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH_DIR.mkdir();
        ADDITION.createNewFile();
        REMOVAL.createNewFile();
        HEAD.createNewFile();
        BRANCH.createNewFile();
    }

    public static void checkIfInited() {
        if (!GITLET_DIR.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
    }


    /** add [file name]
     *  Adds a copy of the file as it currently exists to the staging area.
     *  The staging area should be somewhere in .gitlet.
     *  If the current working version of the file is identical
     *  to the version in the current commit, do not stage it to be added,
     *  and remove it from the staging area if it is already there.
     */
    // a private method to search file recursively
    public static void add(String fileName) {
        addStage = readAddStage();
        removeStage = readRemoveStage();
        Commit curCommit = getCurCommit();
        File sourceFile = searchFile(CWD, fileName);
        if (sourceFile == null) {
            exit("File does not exist.");
        }
        Blob blob = new Blob(sourceFile);
        // System.out.println("Blob ID: " + blob.get_BlobId()); // 调试输出
        // System.out.println("Blob Path: " + blob.get_BlobPath()); // 调试输出
        // can't add trcked file
        if (!curCommit.getBlobList().contains(blob)) {
            addStage.addBlobInMap(blob.getBlobId(), blob.getBlobPath());
        } else if (removeStage.ifContains(blob)) {
            removeStage.delete(blob);
        }
        // save
        blob.save();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }

    /** commit [message]
     *  Saves a snapshot of tracked files in the current commit and staging area
     *  so they can be restored at a later time,
     *  creating a new commit. The commit is said to be tracking the saved files.
     *  By default, each commit’s snapshot of files will be exactly the same as
     *  its parent commit’s snapshot of files; it will keep versions of files exactly
     *  as they are, and not update them.
     *  A commit will only update the contents of files it is tracking that have been staged
     *  for addition at the time of commit, in which case the commit will now include the
     *  version of the file that was staged instead of the version it got from its parent.
     *  A commit will save and start tracking any files that were staged for addition
     *  but weren’t tracked by its parent. Finally, files tracked in the current commit
     *  may be untracked in the new commit as a result being staged for removal by  rm.
     */
    public static void commit(String message) {
        if (stageIsEmpty()) {
            exit("No changes added to the commit.");
        }
        commit(message, null);
    }

    public static void commit(String message, String mergeCommitID) {
        if (message == null) {
            exit("Please enter a commit message.");
        }
        String parentCommit = getCurCommit().getCommitID();
        // update parents
        List<String> parents = updateParents(parentCommit, mergeCommitID);
        // update pathToBlobID
        Map<String, String> pathToBlobID = updatePathToBlobID(parentCommit);
        // create new commit and save it
        Commit newCommit = new Commit(message, parents, pathToBlobID);
        saveNewCommit(newCommit);
    }

    private static List<String> updateParents(String parentCommitID, String mergeCommitID) {
        List<String> parents = new LinkedList<>();
        parents.add(parentCommitID);
        if (mergeCommitID != null) {
            parents.add(mergeCommitID);
        }
        return parents;
    }

    private static Map<String, String> updatePathToBlobID(String parentCommitID) {
        Map<String, String> pathToBlobID = getCommitByID(parentCommitID).getPathToBlobID();
        Map<String, String> addStageMaptageMap = calAddStageMap();
        Map<String, String> removeStageMap = calRemoveStageMap();
        if (addStageMaptageMap.size() != 0) {
            for (String blobID : addStageMaptageMap.keySet()) {
                pathToBlobID.put(addStageMaptageMap.get(blobID), blobID);
            }
        }
        if (removeStageMap.size() != 0) {
            for (String blobID : removeStageMap.keySet()) {
                pathToBlobID.remove(removeStageMap.get(blobID));
            }
        }
        return pathToBlobID;
    }


    private static  boolean stageIsEmpty() {
        addStage = readAddStage();
        removeStage = readRemoveStage();
        return addStage.isEmpty() && removeStage.isEmpty();
    }

    private static void saveNewCommit(Commit newCommit) {
        // save commit object and HEAD
        newCommit.save();
        saveHEAD(newCommit.getCommitID());
        // update current branch pointer
        Branch.updateCurBranchPointer(newCommit.getCommitID());
        // reset stage area
        addStage.clear();
        removeStage.clear();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }

    // return current commit object
    public static Commit getCurCommit() {
        String commitID = readContentsAsString(HEAD);
        return getCommitByID(commitID);
    }

    private static Stage readAddStage() {
        if (ADDITION.length() == 0) {
            return new Stage();
        }
        return readObject(ADDITION, Stage.class);
    }

    private static Stage readRemoveStage() {
        if (REMOVAL.length() == 0) {
            return new Stage();
        }
        return readObject(REMOVAL, Stage.class);
    }


    private static Map<String, String> calAddStageMap() {
        addStage = readAddStage();
        Map<String, String> addStageMap = new TreeMap<>();
        List<Blob> addStageBlobList = addStage.getBlobList();
        for (Blob blob : addStageBlobList) {
            addStageMap.put(blob.getBlobId(), blob.getBlobPath());
        }
        return addStageMap;
    }
    private static Map<String, String> calRemoveStageMap() {
        removeStage = readRemoveStage();
        Map<String, String> removeSrageMap = new TreeMap<>();
        List<Blob> removeStageBlob = removeStage.getBlobList();
        for (Blob blob : removeStageBlob) {
            removeSrageMap.put(blob.getBlobId(), blob.getBlobPath());
        }
        return removeSrageMap;
    }


    /** rm [file name]
     *  Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it for removal
     *  and remove the file from the working directory if the user has not already done so
     *  (do not remove it unless it is tracked in the current commit).
     * */
    public static void rm(String fileName)  {
        // update stage area
        addStage = readAddStage();
        removeStage = readRemoveStage();
        Blob stageBlob = addStage.getBlobByFileName(fileName);
        Blob rmBlob = stageBlob != null ? stageBlob : getTrackedBlobByName(fileName);
        if (rmBlob == null) {
            exit("No reason to remove the file.");
        }
        rmBlob.save();
        Commit curCommit = getCurCommit();
        // 1.check Addition folder
        if (addStage.ifContains(rmBlob)) {
            addStage.delete(rmBlob);
        } else if (curCommit.getPathToBlobID().containsKey(rmBlob.getBlobPath())) {
            // 2.check file_name is tracked by current commit
            Blob trackedBlob = getTrackedBlobByName(fileName);
            removeStage.addBlobInMap(trackedBlob.getBlobId(), trackedBlob.getBlobPath());
            // remove file if it is in CWD
            File cwdFile = searchFile(CWD, fileName);
            if (cwdFile != null) {
                cwdFile.delete();
            }
        }
        // save
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }


    /** log
     *  Starting at the current head commit, display information about each commit
     *  backwards along the commit tree until the initial commit,
     *  following the first parent commit links,
     *  ignoring any second parents found in merge commits.
     *  (In regular Git, this is what you get with git log --first-parent).
     *  This set of commit nodes is called the commit’s history.
     *  For every node in this history, the information it should display is the commit id,
     *  the time the commit was made, and the commit message.
     * */
    public static void log() {
        Commit curCommit = getCurCommit();
        List<String> parent = curCommit.getParents();
        curCommit.print();
        while (!parent.isEmpty()) {
            String parentID = parent.get(0);
            Commit parentCommit = getCommitByID(parentID);
            parentCommit.print();
            parent = parentCommit.getParents();
        }

    }

    /** global-log
     *  Like log, except displays information about all commits ever made.
     *  The order of the commits does not matter.
     * */
    public static void globalLog() {
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        for (String commitID : commitList) {
            Commit commitObject = getCommitByID(commitID);
            commitObject.print();
        }
    }

    /** find [commit message]
     *  Prints out the ids of all commits that have the given commit message, one per line.
     *  If there are multiple such commits, it prints the ids out on separate lines.
     *  The commit message is a single operand; to indicate a multiword message,
     *  put the operand in quotation marks, as for the commit command below.
     * */

    public static void find(String commitMessage) {
        Set<String> commitSet = new HashSet<>(plainFilenamesIn(COMMIT_DIR));
        boolean ifFound = false;
        for (String commitID: commitSet) {
            Commit commitObject = getCommitByID(commitID);
            if (commitMessage.equals(commitObject.getMessage())) {
                System.out.println(commitObject.getCommitID());
                ifFound = true;
            }
        }
        if (!ifFound) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** status
     *  Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     *  An example of the exact format it should follow is as follows.
     * */
    public static void status() {
        // === branches ===
        String curBranchName = getCurBranchName();
        List<String> branchesList = plainFilenamesIn(BRANCH_DIR);
        System.out.println("=== Branches ===");
        System.out.println("*" + curBranchName);
        for (String branchName : branchesList) {
            if (branchName.equals(curBranchName)) {
                continue;
            }
            System.out.println(branchName);
        }
        addStage = readAddStage();
        removeStage = readRemoveStage();
        System.out.println();
        System.out.println("=== Staged Files ===");
        addStage.printBlobsName();
        System.out.println();
        System.out.println("=== Removed Files ===");
        removeStage.printBlobsName();
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> modNotStage = calModifiedButNotStage();
        for (String file : modNotStage) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        List<String> untracked = calUntracked();
        for (String file : untracked) {
            System.out.println(file);
        }
        System.out.println();
    }

    /** Four cases:
     *  Tracked in the current commit, changed in the working directory, but not staged; or
     *  Staged for addition, but with different contents than in the working directory; or
     *  Staged for addition, but deleted in the working directory; or
     *  Not staged for removal, but tracked in the current commit and
     *  deleted from the working directory.
     *
     *  */
    public static List<String> calModifiedButNotStage() {
        List<String> modifiedNotStageFiles = new ArrayList<>();
        Commit curCommit = getCurCommit();
        addStage = readAddStage();
        removeStage = readRemoveStage();
        Set<Blob> curCommitBlobSet = new HashSet<>(curCommit.getBlobList());
        Set<Blob> addStagedBlobSet = new HashSet<>(addStage.getBlobList());
        Set<Blob> removeStageBlobSet = new HashSet<>(removeStage.getBlobList());

        for (Blob blob: curCommitBlobSet) {
            File fileInCWD = join(CWD, blob.getFileName());
            // 1. search Blob_dir, uneaqul version in CWD, not in staged(modified)
            if (!addStagedBlobSet.contains(blob)
                    && !removeStageBlobSet.contains(blob)
                    && fileInCWD.isFile()) {
                byte[] cwdFileContents = readContents(fileInCWD);
                if (!Arrays.equals(cwdFileContents, blob.getFileContents())) {
                    modifiedNotStageFiles.add(blob.getFileName() + "(modified)");
                }
            }
            // 4. Blob_dir, not in CWD, not staged for removal(deleted)
            else if (!removeStageBlobSet.contains(blob)
                    && !fileInCWD.exists()) {
                modifiedNotStageFiles.add(blob.getFileName() + "(deleted)");
            }
        }
        // 2. staged for addition, in CWD, but unequal version(modified)
        // 3. staged for addition, but not in CWD(deleted)
        for (Blob blob : addStagedBlobSet) {
            File fileInCWD = join(CWD, blob.getFileName());
            if (fileInCWD.isFile()) {
                byte[] cwdFileContents = readContents(fileInCWD);
                if (!Arrays.equals(cwdFileContents, blob.getFileContents())) {
                    modifiedNotStageFiles.add(blob.getFileName() + "(modified)");
                }
            } else if (!fileInCWD.exists()){
                modifiedNotStageFiles.add(blob.getFileName() + "(deleted)");
            }
        }
        Collections.sort(modifiedNotStageFiles);
        return modifiedNotStageFiles;
    }

    /** for files present in the working directory but neither staged for addition nor tracked.
     *  This includes files that have been staged for removal,
     *  but then re-created without Gitlet’s knowledge.
     *  */
    public static List<String> calUntracked() {
        List<String> untrackedFiles = new LinkedList<>();
        addStage = readAddStage();
        removeStage = readRemoveStage();
        Set<Blob> addStageBlobset = new HashSet<>(addStage.getBlobList());
        Set<Blob> removeStageBlobSet = new HashSet<>(removeStage.getBlobList());
        Set<Blob> historyTrackedBlobsSet = new HashSet<>(getTrackedBlobList());
        File[] cwdFiles = CWD.listFiles();

        if (cwdFiles != null) {
            for (File file : cwdFiles) {
                if (file.isFile()) {
                    // check file isn't a directory or other special file
                    Blob blob = new Blob(file);
                    if (!historyTrackedBlobsSet.contains(blob)
                            && !addStageBlobset.contains(blob)
                    || removeStageBlobSet.contains(blob)) {
                        untrackedFiles.add(file.getName());
                    }
                }
            }
        }
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }


    /** Usages:
     *  1.checkout -- [file name]
     *  2.checkout [commit id] -- [file name]
     *  3.checkout [branch name]
     * */

    /** Takes the version of the file as it exists in the head commit
     *  and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     */
    public static void checkoutFromHEAD(String fileName) {
        Commit curCommit = getCurCommit();
        checkoutFromCommit(curCommit.getCommitID(), fileName);
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     *  and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     */
    public static void checkoutFromCommit(String commitID, String fileName) {
        // handle the abbrev from of commitID
        Commit checkedCommit = getCommitByAbbrevID(commitID);
        if (checkedCommit == null) {
            exit("No commit with that id exists.");
        }
        List<Blob> blobList = checkedCommit.getBlobList();
        boolean ifFileFound = false;
        if (!blobList.isEmpty()) {
            for (Blob blob : blobList) {
                if (blob.getFileName().equals(fileName)) {
                    writeBlobContentsIntoCWD(blob);
                    ifFileFound = true;
                    break;
                }
            }
        }
        if (!ifFileFound) {
            exit("File does not exist in that commit.");
        }
    }

    private static Commit getCommitByAbbrevID(String commitID) {
        Commit checkedCommit = getCommitByID(commitID);
        if (checkedCommit == null) {
            String fullCommitID = getCommitIDByAbbreb(commitID);
            if (fullCommitID != null) {
                checkedCommit = getCommitByID(fullCommitID);
            }
        }
        return checkedCommit;
    }

    /** Takes all files in the commit at the head of the given branch,
     *  and puts them in the working directory,
     *  overwriting the versions of the files that are already there if they exist.
     *  Also, at the end of this command, the given branch will now be considered as HEAD.
     *  Any files that are tracked in the current branch
     *  but are not present in the checked-out branch are deleted.
     *  The staging area is cleared, unless the checked-out branch is the current branch
     */
    public static void checkoutBranch(String branchName) {
        if (readCurBranch().equals(branchName)) {
            exit("No need to checkout the current branch.");
        }
        // search BranchDIR if branchName exist
        File branchFile = Branch.getBranchFileByName(branchName);
        if (branchFile == null) {
            exit("No such branch exists.");
        }
        // if has sth untracked
        if (!checkIfFilesTracked()) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        // above is code is do some checking
        clearCWD();
        String branchHeadCommitID = readContentsAsString(branchFile);
        updateCWDFromCommit(branchHeadCommitID);
        Branch.updateCurBranch(branchName);
        updateHEAD(branchHeadCommitID);
        clearStage();        // already saved
    }



    private static void updateCWDFromCommit(String commitID) {
        Commit checkedCommit = getCommitByID(commitID);
        List<Blob> checkedBlobList = checkedCommit.getBlobList();
        for (Blob blob : checkedBlobList) {
            writeBlobContentsIntoCWD(blob);
        }
    }

    private static void writeBlobContentsIntoCWD(Blob blob) {
        File fileName = join(CWD, blob.getFileName());
        byte[] fileContent = blob.getFileContents();
        // DEBUG:
        writeContents(fileName, fileContent);
    }

    private static void updateHEAD(String commitID) {
        writeContents(HEAD, commitID);
    }

    private static String readCurBranch() {
        return readContentsAsString(BRANCH);
    }

    private static boolean checkIfFilesTracked() {
        return calUntracked().isEmpty();
    }

    private static void clearCWD() {
        File[] allCWDFiles = CWD.listFiles();
        if (allCWDFiles != null) {
            for (File file : allCWDFiles) {
                file.delete();
            }
        }
    }


    private static void clearStage() {
        addStage = readAddStage();
        removeStage = readRemoveStage();
        addStage.clear();
        removeStage.clear();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }


    /** branch [branch name]
     *  Creates a new branch with the given name, and points it at the current head commit.
     *  A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     *  This command does NOT immediately switch to the newly created branch (as in real Git).
     *  Before you ever call branch, you should be running with a default branch called “master”.
     * */
    public static void branch(String branchName) {
        String curCommitID = getCurCommit().getCommitID();
        File newBranch = join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            exit("A branch with that name already exists.");
        }
        writeContents(newBranch, curCommitID);
    }

    /** rm-branch [branch name]
     *  Deletes the branch with the given name.
     *  This only means to delete the pointer associated with the branch;
     *  it does not mean to delete all commits that were created under the branch,
     *  or anything like that.
     */

    public static void rmBranch(String branchName) {
        String curBranch = getCurBranchName();
        if (branchName.equals(curBranch)) {
            exit("Cannot remove the current branch.");
        }
        File branchFile = Branch.getBranchFileByName(branchName);
        if (branchFile == null) {
            exit("A branch with that name does not exist.");
        }
        branchFile.delete();
    }

    /** reset [commit id]
     *  Checks out all the files tracked by the given commit.
     *  Removes tracked files that are not present in that commit.
     *  Also moves the current branch’s head to that commit node.
     *  See the intro for an example of what happens to the head pointer after using reset.
     *  The [commit id] may be abbreviated as for checkout. The staging area is cleared.
     *  The command is essentially checkout of an arbitrary commit that
     *  also changes the current branch head.
     */
    public static void reset(String commitID) {
        if (!checkIfFilesTracked()) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        Commit checkedCommit = getCommitByAbbrevID(commitID);
        if (checkedCommit == null) {
            exit("No commit with that id exists.");
        }
        clearCWD();
        updateCWDFromCommit(commitID);
        Branch.updateCurBranchPointer(commitID);
        updateHEAD(commitID);
        clearStage();
    }

    /** merge [branch name]
     *  Merges files from the given branch into the current branch.
     */
    public static void merge(String givenBranchName) {
        // failure cases:
        if (!calUntracked().isEmpty()) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        } if (!stageIsEmpty()) {
            exit("You have uncommitted changes.");
        } if (readCurBranch().equals(givenBranchName)) {
            exit("Cannot merge a branch with itself.");
        } if (Branch.getBranchFileByName(givenBranchName) == null) {
            exit("A branch with that name does not exist.");
        }

        boolean ifMergeConflict = false;
        Commit curBranchHeadCommit = getCurCommit();
        Commit givenBranchHeadCommit = getBranchHead(givenBranchName);
        Commit splitPointCommit = getCommitByID(getSpiltPointCommitID(getCurBranchName(), givenBranchName));
        Map<String, String> curBranchFileMap = getBlobIdToFileNameMap(curBranchHeadCommit);
        Map<String, String> givenBranchFileMap = getBlobIdToFileNameMap(givenBranchHeadCommit);
        Map<String, String> spiltPointFileMap = getBlobIdToFileNameMap(splitPointCommit);
        Map<String, String> allFileMap = mergeAllMap(curBranchFileMap, givenBranchFileMap, spiltPointFileMap);
        // first two cases : these two branches actually in same line
        if (splitPointCommit.equals(givenBranchHeadCommit)) {
            // SAD! painful debug ,forgot to override eaquls of commit object
            exit("Given branch is an ancestor of the current branch.");
        } else if (splitPointCommit.equals(curBranchHeadCommit)) {
            checkoutBranch(givenBranchName);
            exit("Current branch fast-forwarded.");
        } else {
            // iterate allFileMap
            for (Map.Entry<String, String> entry : allFileMap.entrySet()) {
                String blobID = entry.getKey();
                String fileName = entry.getValue();
                byte[] curBranchFileContents = getBlobContentsByFileName(curBranchHeadCommit, fileName);
                byte[] givenBranchFileContents = getBlobContentsByFileName(givenBranchHeadCommit, fileName);
                byte[] mergedFileContents = mergeConflictFile(curBranchFileContents, givenBranchFileContents);

                if (spiltPointFileMap.containsKey(blobID)
                && curBranchFileMap.containsKey(blobID)
                && !givenBranchFileMap.containsKey(blobID)) {
                    if (givenBranchFileMap.containsValue(fileName)) {
                        // if file in given branch was changed
                        checkoutFromCommit(givenBranchHeadCommit.getCommitID(), fileName);
                    } else {
                        // if file in given branch was deleted
                        rm(getBlobByID(blobID).getSourceFile().getName());
                    }
                } else if (spiltPointFileMap.containsKey(blobID)
                && !curBranchFileMap.containsKey(blobID)
                && givenBranchFileMap.containsKey(blobID)) {
                    continue; // no matter the file is changed or deleted, just keep the same
                } else if (spiltPointFileMap.containsKey(blobID)
                && !curBranchFileMap.containsKey(blobID)
                && !givenBranchFileMap.containsKey(blobID)) {
                    // start deal with conflict case
                    if (!Arrays.equals(curBranchFileContents,givenBranchFileContents)) {
                        File curbranchFile = getBlobByFileName(curBranchHeadCommit, fileName).getSourceFile();
                        writeContents(curbranchFile, mergedFileContents);
                        add(fileName);
                        ifMergeConflict = true;
                    }
                } else if (!spiltPointFileMap.containsKey(blobID)
                        && curBranchFileMap.containsKey(blobID)
                        && !givenBranchFileMap.containsKey(blobID)) {
                    if (!Arrays.equals(curBranchFileContents, givenBranchFileContents)
                            && curBranchFileContents != null
                            && givenBranchFileContents != null) {
                        File curbranchFile = getBlobByFileName(curBranchHeadCommit, fileName).getSourceFile();
                        writeContents(curbranchFile, mergedFileContents);
                        add(fileName);
                        ifMergeConflict = true;
                    }
                } else if (!spiltPointFileMap.containsKey(blobID)
                        && !curBranchFileMap.containsKey(blobID)
                        && givenBranchFileMap.containsKey(blobID)) {
                    if (!Arrays.equals(curBranchFileContents, givenBranchFileContents)
                            && curBranchFileContents != null
                            && givenBranchFileContents != null) {
                        File curbranchFile = getBlobByFileName(curBranchHeadCommit, fileName).getSourceFile();
                        writeContents(curbranchFile, mergedFileContents);
                        add(fileName);
                        ifMergeConflict = true;
                    } else if (curBranchFileContents == null
                    && givenBranchFileContents != null) {
                        checkoutFromCommit(givenBranchHeadCommit.getCommitID(), fileName);
                        add(fileName);
                    }
                }

            }
            if (ifMergeConflict) {
                System.out.println("Encountered a merge conflict.");
            }
            commit("Merged " + givenBranchName + " into " + getCurBranchName() + ".", givenBranchHeadCommit.getCommitID());
            // auto update head pointer and branch pointer, as well clearing staging area in commit method
        }

    }



    private static byte[] mergeConflictFile(byte[] curBranchFile, byte[] givenBranchFile) {
        String curBranchFileContents = curBranchFile != null ? new String(curBranchFile) : "";
        String givenBranchFileContents = givenBranchFile != null ? new String(givenBranchFile) : "";
        String mergedStrings = "<<<<<<< HEAD\n"
                              + curBranchFileContents
                              + "=======\n"
                              + givenBranchFileContents
                              + ">>>>>>>\n";
        byte[] mergedContents = mergedStrings.getBytes(StandardCharsets.UTF_8);
        return mergedContents;
    }


    private static Map<String, String> getBlobIdToFileNameMap(Commit commit) {
        Map<String, String> blobIdToFileNameMap = new HashMap<>();
        List<Blob> blobList = commit.getBlobList();
        for (Blob blob : blobList) {
            blobIdToFileNameMap.put(blob.getBlobId(), blob.getFileName());
        }
        return blobIdToFileNameMap;
    }

    private static Map<String, String> mergeAllMap(
            Map<String, String> tom,
            Map<String, String> jerry,
            Map<String, String> mario) {
        Map<String, String> allBlobsMap =  new HashMap<>();
        allBlobsMap.putAll(tom);
        allBlobsMap.putAll(jerry);
        allBlobsMap.putAll(mario);
        return allBlobsMap;
    }

    private static Blob getBlobByFileName(Commit commit, String fileName) {
        Map<String, String> blobIdToFileNameMap = getBlobIdToFileNameMap(commit);
        for (Map.Entry<String, String> entry : blobIdToFileNameMap.entrySet()) {
            String commitBlobID = entry.getKey();
            String commitFileName = entry.getValue();
            if (fileName.equals(commitFileName)) {
                return getBlobByID(commitBlobID);
            }
        }
        return null;
    }

    private static byte[] getBlobContentsByFileName(Commit commit, String fileName) {
        Blob blob = getBlobByFileName(commit, fileName);
        if (blob != null) {
            return blob.getFileContents();
        }
        return null;
    }


    // using BFS to get the nearest split point between two branches
    private static String getSpiltPointCommitID(String curBranchName, String givenBranchName) {
        Commit curBranchCommit = getBranchHead(curBranchName);
        Commit givenBranchCommit = getBranchHead(givenBranchName);
        String curBranchCommitID = curBranchCommit.getCommitID();
        String givenBranchCommitID = givenBranchCommit.getCommitID();
        if (curBranchCommit.equals(givenBranchCommit)) {
            exit("this tow branch points the same commit");
        }
        Queue<String> curQueue = new LinkedList<>();
        Queue<String> givenQueue = new LinkedList<>();
        Set<String> curVisited = new HashSet<>();
        Set<String> givenVisited = new HashSet<>();
        // add head commits of both branches into queues and sets
        curQueue.offer(curBranchCommitID);
        curVisited.add(curBranchCommitID);
        givenQueue.offer(givenBranchCommitID);
        givenVisited.add(givenBranchCommitID);

        // BFS implement by Queue
        while (!curQueue.isEmpty() || !givenQueue.isEmpty()) {
            // travesal curren branch
            if (!curQueue.isEmpty()) {
                String curID = curQueue.poll();
                if (givenVisited.contains(curID)) {
                    return  curID;
                }
                // find parents
                Commit curCommit = getCommitByID(curID);
                List<String> curParents = curCommit.getParents();
                if (!curParents.isEmpty()) {
                    String father = curParents.get(0);
                    if (!curVisited.contains(father)) {
                        curQueue.offer(father);
                        curVisited.add(father);
                    }
                    if (curParents.size() > 1 && !curVisited.contains(curParents.get(1))) {
                        curQueue.offer(curParents.get(1));
                        curVisited.add(curParents.get(1));
                    }
                }
            }
            // travesal given branch
            if (!givenQueue.isEmpty()) {
                String givenID = givenQueue.poll();
                if (curVisited.contains(givenID)) {
                    return givenID;
                }
                Commit givenCommit = getCommitByID(givenID);
                List<String> givenParents = givenCommit.getParents();
                if (!givenParents.isEmpty()) {
                    String dad = givenParents.get(0);
                    if (!givenVisited.contains(dad)) {
                        givenQueue.offer(dad);
                        givenVisited.add(dad);
                    }
                    if (givenParents.size() > 1 && !givenVisited.contains(givenParents.get(1))) {
                        givenQueue.offer(givenParents.get(1));
                        givenVisited.add(givenParents.get(1));
                    }
                }
            }
        }
        return null;
    }





}
