package gitlet;



import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.*;
import static gitlet.Utils.*;
import static gitlet.Commit.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jasmine1106
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
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
    public static final String DEFAULT_BRANCH_NAME = "master";
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** the first floor of the directory*/
    public static final File OBJECT_DIR = join(GITLET_DIR, "OBJECT_DIR");
    public static final File STAGE_DIR = join(GITLET_DIR, "STAGE_DIR");
    public static File BRANCH_DIR = join(GITLET_DIR, "BRANCH_DIR");
    public static File HEAD = join (GITLET_DIR, "HEAD");
    public static File BRANCH = join(GITLET_DIR, "BRANCH");
    /** the second floor of the directory*/
    public static File BLOB_DIR = join(OBJECT_DIR, "BLOB_DIR");
    public static File COMMIT_DIR = join(OBJECT_DIR, "COMMIT_DIR");
    public static File ADDITION = join(STAGE_DIR, "ADDITION");
    public static File REMOVAL = join(STAGE_DIR, "REMOVAL");

    // instance variable
    public static Stage addStage = new Stage();      // add stage
    public static Stage removeStage = new Stage();  // remove stage



    /* TODO: fill in the rest of this class. */
    // Does require filesystem operations to allow for persistence.

    /** init
     *  Creates a new Gitlet version-control system in the current directory.
     *  This system will automatically start with one commit: a commit that contains no files and has the commit message: initial commit.
     *  It will have a single branch: master, which initially points to this initial commit, and master will be the current branch.
     *  The timestamp for this initial commit will be 00:00:00 UTC,*Thursday, 1 January 1970
     *  in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     */
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        /** 1. creat the gitlet directory
         *  2. initial commit
         *  3. set current branch to master and update BRANCH_DIR
         */
        setupPersistence();
        // create initial commit
        Commit inital = new Commit();
        inital.save();
        // branch
        writeContents(BRANCH, DEFAULT_BRANCH_NAME);
        File head_file = join(BRANCH_DIR, DEFAULT_BRANCH_NAME);
        writeContents(head_file, inital.getCommitID());
    }

    /** create the basic structrue of gitlet directory */
    private static void setupPersistence() throws IOException {
        // TODO
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
     *  Adds a copy of the file as it currently exists to the staging area.The staging area should be somewhere in .gitlet.
     *  If the current working version of the file is identical to the version in the current commit, do not stage it to be added,
     *  and remove it from the staging area if it is already there.
     */
    // a private method to search file recursively
    public static void add(String file_name) {
        addStage = readAddStage();
        removeStage = readRemoveStage();
        Commit curCommit = readCurCommit();
        File sourceFile = searchFile(CWD, file_name);
        if (sourceFile == null) {
            exit("File does not exist.");
        }
        Blob blob = new Blob(sourceFile);
        // System.out.println("Blob ID: " + blob.get_BlobId()); // 调试输出
        // System.out.println("Blob Path: " + blob.get_BlobPath()); // 调试输出
        // can't add trcked file
        if (!curCommit.getBlobList().contains(blob)) {
            addStage.addBlobInMap(blob.getBlobId(), blob.getBlobPath());
        }
        else if (removeStage.ifContains(blob) ) {
            removeStage.delete(blob);
        }
        // save
        blob.save();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }




    /** commit [message]
     *  Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time,
     *  creating a new commit. The commit is said to be tracking the saved files. By default,
     *  each commit’s snapshot of files will be exactly the same as its parent commit’s snapshot of files;
     *  it will keep versions of files exactly as they are, and not update them.
     *  A commit will only update the contents of files it is tracking that have been staged for addition at the time of commit,
     *  in which case the commit will now include the version of the file that was staged instead of the version it got from its parent.
     *  A commit will save and start tracking any files that were staged for addition but weren’t tracked by its parent. Finally,
     *  files tracked in the current commit may be untracked in the new commit as a result being staged for removal by the rm command(below).
     *
     */
    public static void commit(String message) {
        addStage = readAddStage();
        removeStage = readRemoveStage();
        if (addStage.isEmpty() && removeStage.isEmpty()) {
            exit("No changes added to the commit.");
        }
        if (message == null) {
            exit("Please enter a commit message.");
        }
        Commit parent_commit = readCurCommit();
        // update parents
        List<String> parents = updateParents(parent_commit);
        // update pathToBlobID
        Map<String, String> pathToBlobID = updatePathToBlobID(parent_commit);
        // create new commit and save it
        Commit newCommit = new Commit(message, parents, pathToBlobID );
        saveNewCommit(newCommit);

    }
    private static void saveNewCommit(Commit new_commit) {
        // save commit object and HEAD
        new_commit.save();
        saveHEAD(new_commit.getCommitID());
        // reset stage area
        addStage.clear();
        removeStage.clear();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }

    // return current commit object
    public static Commit readCurCommit() {
        String commitID = readContentsAsString(HEAD);
        return fromFile(commitID);
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

    private static List<String> updateParents(Commit parentCommit) {
        List<String> parents = parentCommit.getParents();
        parents.add(parentCommit.getCommitID());
        return parents;
    }

    private static Map<String, String> updatePathToBlobID(Commit parentCommit) {
        Map<String, String> pathToBlobID = parentCommit.getPathToBlobID();
        Map<String, String> addStageMaptageMap = calAddStageMap();
        Map<String, String> remove_stage_map = calRemoveStageMap();
        if (addStageMaptageMap.size() != 0) {
            for (String blob_id : addStageMaptageMap.keySet()) {
                pathToBlobID.put(addStageMaptageMap.get(blob_id), blob_id);
            }
        }
        if (remove_stage_map.size() != 0) {
            for (String blob_id : remove_stage_map.keySet()) {
                pathToBlobID.remove(remove_stage_map.get(blob_id));
            }
        }
        return pathToBlobID;
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
        List<Blob> remove_stage_blob = removeStage.getBlobList();
        for (Blob blob : remove_stage_blob) {
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
        } rmBlob.save();
        Commit curCommit = readCurCommit();
        // 1.check Addition folder
        if (addStage.ifContains(rmBlob)) {
            addStage.delete(rmBlob);
        }
        // 2.check file_name is tracked by current commit
        else if (curCommit.getPathToBlobID().containsKey(rmBlob.getBlobPath())) {
            Blob trackedBlob = getTrackedBlobByName(fileName);
            removeStage.addBlobInMap(trackedBlob.getBlobId(), trackedBlob.getBlobPath());
            // remove file if it is in CWD
            File cwdFile = searchFile(CWD, fileName);
            if (cwdFile != null) { cwdFile.delete(); }
        }

        // save
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }



    /** log
     *  Starting at the current head commit, display information about each commit backwards along the commit tree
     *  until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits.
     *  (In regular Git, this is what you get with git log --first-parent). This set of commit nodes is called the commit’s history.
     *  For every node in this history, the information it should display is the commit id,
     *  the time the commit was made, and the commit message.
     * */
    public static void log () {
        Commit curCommit = readCurCommit();
        List<String> history = curCommit.getParents();
        curCommit.print();
        while (!history.isEmpty()) {
            String parentID = history.get(history.size() - 1);
            Commit parentCommit = fromFile(parentID);
            parentCommit.print();
            history = parentCommit.getParents();
        }
        // TODO: branch case:

    }

    /** global-log
     *  Like log, except displays information about all commits ever made. The order of the commits does not matter.
     *  Hint: there is a useful method in gitlet.Utils that will help you iterate over files within a directory.
     * */
    public static void global_log() {
        List<String> commit_list = plainFilenamesIn(COMMIT_DIR);
        for (String commit_id : commit_list) {
            Commit commit_object = fromFile(commit_id);
            commit_object.print();
        }
    }

    /** find [commit message]
     *  Prints out the ids of all commits that have the given commit message, one per line.
     *  If there are multiple such commits, it prints the ids out on separate lines.
     *  The commit message is a single operand; to indicate a multiword message, put the operand in quotation marks,
     *  as for the commit command below. Hint: the hint for this command is the same as the one for global-log.
     * */

    public static void find(String commitMessage) {
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        boolean ifFound = false;
        for (String commitID: commitList) {
            Commit commit_object = fromFile(commitID);
            if (commitMessage.equals(commit_object.getMessage())) {
                System.out.println(commit_object.getCommitID());
                ifFound = true;
            }
        }
        if (!ifFound) {exit("Found no commit with that message.");}
    }

    /** status
     *  Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     *  An example of the exact format it should follow is as follows.
     * */
    public static void status() {
        // === branches ===
        String curBranch = readContentsAsString(BRANCH);
        List<String> branchesList = plainFilenamesIn(BRANCH_DIR);
        System.out.println("=== Branches ===");
        System.out.println("*" + curBranch);
        for (String branch_name : branchesList) {
            if (branch_name.equals(curBranch)) {
                continue;
            }
            System.out.println(branch_name);
        }
        Stage add_stage = readAddStage();
        Stage remove_stage = readRemoveStage();
        System.out.println();
        System.out.println("=== Staged Files ===");
        add_stage.printBlobsName();
        System.out.println();
        System.out.println("=== Removed Files ===");
        remove_stage.printBlobsName();
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
     *  Not staged for removal, but tracked in the current commit and deleted from the working directory.
     *
     *  */
    public static List<String> calModifiedButNotStage() {
        List<String> modifiedNotStageFiles = new ArrayList<>();
        Commit cur_commit = readCurCommit();
        Stage addStage = readAddStage();
        Stage removeStage = readRemoveStage();
        Set<Blob> curCommitBlobSet = new HashSet<>(cur_commit.getBlobList());
        Set<Blob> addStagedBlobSet = new HashSet<>(addStage.getBlobList());
        Set<Blob> removeStageBlobSet = new HashSet<>(removeStage.getBlobList());

        for (Blob blob: curCommitBlobSet) {
            File fileInCWD = join(CWD, blob.getFileName());
            // 1. search Blob_dir, uneaqul version in CWD, not in staged(modified)
            if (!addStagedBlobSet.contains(blob) && !removeStageBlobSet.contains(blob)) {
                    byte[] CWDFileContents = readContents(fileInCWD);
                    if (!Arrays.equals(CWDFileContents, blob.getFileContents())) {
                         modifiedNotStageFiles.add(blob.getFileName() + "(modified)");
                    }
                }
            // 4. Blob_dir, not in CWD, not staged for removal(deleted)
            if (!removeStageBlobSet.contains(blob) && !fileInCWD.exists()) {
                modifiedNotStageFiles.add(blob.getFileName() + "(deleted)");
            }
        }
        // 2. staged for addition, in CWD, but uneaqul version(modified)
        // 3. staged for addition, but not in CWD(deleted)
        for (Blob blob : addStagedBlobSet) {
            File fileInCWD = join(CWD, blob.getFileName());
            if (fileInCWD.exists()) {
                byte[] CWDFileContents = readContents(fileInCWD);
                if (!Arrays.equals(CWDFileContents, blob.getFileContents())) {
                    modifiedNotStageFiles.add(blob.getFileName() + "(modified)");
                }
            } else {
                modifiedNotStageFiles.add(blob.getFileName() + "(deleted)");
            }
        }
        Collections.sort(modifiedNotStageFiles);
        return modifiedNotStageFiles;
    }

    /** for files present in the working directory but neither staged for addition nor tracked.
     *  This includes files that have been staged for removal, but then re-created without Gitlet’s knowledge. */
    public static List<String> calUntracked() {
        List<String> untrackedFiles = new ArrayList<>();
        Stage addStage = readAddStage();
        Set<Blob> addStageBlobSset = new HashSet<>(addStage.getBlobList());
        Set<Blob> historyTrackedBlobsSet = new HashSet<>(getTrackedBlobList());
        File[] cwdFiles = CWD.listFiles();

        if (cwdFiles != null) {
            for (File file : cwdFiles) {
                if (file.isFile()) { // check file isn't a directory or other special file
                    Blob blob = new Blob(file);
                    if (!historyTrackedBlobsSet.contains(blob) && !addStageBlobSset.contains(blob)) {
                        untrackedFiles.add(file.getName());
                    }
                }
            }
        }
        Collections.sort(untrackedFiles);
        return untrackedFiles;
    }


    /* Usages:
     *  1.checkout -- [file name]
     *  2.checkout [commit id] -- [file name]
     *  3.checkout [branch name]
     * */

    /* Takes the version of the file as it exists in the head commit and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. The new version of the file is not staged.
     */
    public static void checkoutFromHEAD(String file_name) {
        Commit cur_commit = readCurCommit();
        checkoutFromCommit(cur_commit.getCommitID(), file_name);
    }

    /* Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. The new version of the file is not staged.
     */
    public static void checkoutFromCommit(String commitID, String fileName) {
        Commit checkedCommit = fromFile(commitID) != null ? fromFile(commitID) : fromFile(getCommitIDByAbbreb(commitID));
        if (checkedCommit == null) {
            exit("No commit with that id exists.");
        }
        boolean ifFileFound = false;
        List<Blob> blobList = checkedCommit.getBlobList();
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

    /** Takes all files in the commit at the head of the given branch, and puts them in the working directory,
     *  overwriting the versions of the files that are already there if they exist. Also, at the end of this command,
     *  the given branch will now be considered the current branch (HEAD).
     *  Any files that are tracked in the current branch but are not present in the checked-out branch are deleted.
     *  The staging area is cleared, unless the checked-out branch is the current branch (see Failure cases below).
     */
    public static void checkoutBranch(String branchName) {
        if (readCurBranch().equals(branchName)) {
            exit("No need to checkout the current branch.");
        }
        // search BranchDIR
        File branchFile = Branch.getBranchFileByName(branchName);
        // if branch_name doesn't exist
        if (branchFile == null) { exit("No such branch exists.");}
        // if has sth untracked
        if (!checkIfFilesTracked()) { exit("There is an untracked file in the way; delete it, or add and commit it first.");}
        // above is code is do some checking
        clearCWD();
        String commitID = readContentsAsString(branchFile);
        updateCWDFromCommit(commitID);
        Branch.updateCurBranch(branchName);
        updateHEAD(commitID);
        clearStage();        // already saved
    }

    private static void updateCWDFromCommit(String commitID) {
        Commit checkedCommit = fromFile(commitID);
        List<Blob> checkedBlobList = checkedCommit.getBlobList();
        for (Blob blob : checkedBlobList) {
            writeBlobContentsIntoCWD(blob);
        }
    }

    private static void updateHEAD(String commit_id) {
        writeContents(HEAD, commit_id);
    }

    private static String readCurBranch() {
        return readContentsAsString(BRANCH);
    }

    private static boolean checkIfFilesTracked () { return calUntracked().isEmpty();}

    private static void clearCWD() {
        File[] allCWDFiles = CWD.listFiles();
        if (allCWDFiles != null) {
            for (File file : allCWDFiles) {
                restrictedDelete(file);
            }
        }
    }


    private static void clearStage() {
        Stage addStage = readAddStage();
        Stage removeStage = readRemoveStage();
        addStage.clear();
        removeStage.clear();
        addStage.saveAddStage();
        removeStage.saveRemoveStage();
    }

    private static void writeBlobContentsIntoCWD(Blob blob) {
        File fileName = join(CWD, blob.getFileName());
        byte[] fileContent = blob.getFileContents();
        writeContents(fileName, new String(fileContent, StandardCharsets.UTF_8));

    }


    /** branch [branch name]
     *  Creates a new branch with the given name, and points it at the current head commit.
     *  A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     *  This command does NOT immediately switch to the newly created branch (just as in real Git).
     *  Before you ever call branch, your code should be running with a default branch called “master”.
     * */
    public static void branch(String branchName) {
        String curCommitID = readCurCommit().getCommitID();
        File newBranch = join(BRANCH_DIR, branchName);
        if (newBranch.exists()) {
            exit("A branch with that name already exists.");
        }
        writeContents(newBranch, curCommitID);
    }

    /** rm-branch [branch name]
     *  Deletes the branch with the given name. This only means to delete the pointer associated with the branch;
     *  it does not mean to delete all commits that were created under the branch, or anything like that.
     */

    public static void rm_branch(String branchName) {
        String curBranch = readContentsAsString(BRANCH);
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
     *  Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit.
     *  Also moves the current branch’s head to that commit node.
     *  See the intro for an example of what happens to the head pointer after using reset.
     *  The [commit id] may be abbreviated as for checkout. The staging area is cleared.
     *  The command is essentially checkout of an arbitrary commit that also changes the current branch head.
     */
    public static void reset(String commitID) {
        Commit checkedCommit = fromFile(commitID);
        if (checkedCommit == null) {
            exit("No commit with that id exists.");
        }
        clearCWD();
        updateCWDFromCommit(commitID);
        Branch.updateBranchPointer(BRANCH, commitID);
        clearStage();
    }

    /** merge [branch name]
     *  Merges files from the given branch into the current branch. This method is a bit complicated,
     *  so here’s a more detailed description:
     */
    public static void merge(String branchName) {

    }

}
