package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static gitlet.Utils.*;
import static gitlet.Commit.*;




// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jasmine1106
 */
public class Repository {
    /**
     * TODO: add instance variables here.
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
    public static Stage add_stage = new Stage();      // add stage
    public static Stage remove_stage = new Stage();  // remove stage



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
        writeContents(head_file, inital.getCommit_id());
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

    /** add [file name]
     *  Adds a copy of the file as it currently exists to the staging area.The staging area should be somewhere in .gitlet.
     *  If the current working version of the file is identical to the version in the current commit, do not stage it to be added,
     *  and remove it from the staging area if it is already there.
     * Special Case:
     *   .file_version is identical to current commit, don't add
     *   .file is in remove_stage, delete it
     */
    // a private method to search file recursively
    public static void add(String file_name) throws IOException {
        File source_file = SearchFile(CWD, file_name);
        if (source_file == null) {
            throw new IllegalArgumentException("File does not exist.");
        }
        Blob blob = new Blob(source_file);
        if (remove_stage.ifContains(blob) ) {
            remove_stage.delete(blob);
        }
        add_stage.add_blob(blob.get_BlobId(), blob.get_BlobPath());
        // save
        add_stage.saveAddStage();
        remove_stage.saveRemoveStage();
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
        if (add_stage.isEmpty() && remove_stage.isEmpty()) {
            exit("No changes added to the commit.");
        }
        if (message == null) {
            exit("Please enter a commit message.");
        }
        Commit parent_commit = readCurCommit();
        // update parents
        List<String> parents = update_parents(parent_commit);
        // update pathToBlobID
        Map<String, String> pathToBlobID = update_pathToBlobID(parent_commit);
        // create new commit and save it
        Commit new_commit = new Commit(message, parents, pathToBlobID );
        saveNewCommit(new_commit);

    }
    private static void saveNewCommit(Commit new_commit) {
        // save commit object and HEAD
        new_commit.save();
        save_HEAD(new_commit.getCommit_id());
        // reset stage area
        add_stage.clear();
        remove_stage.clear();
        add_stage.saveAddStage();
        remove_stage.saveRemoveStage();
    }

    // return current commit object
    private static Commit readCurCommit() {
        String commit_id = readContentsAsString(HEAD);
        return fromFile(commit_id);
    }

    private static Stage readAddStage() {
        if (!ADDITION.exists()) {
            return new Stage();
        }
        return readObject(ADDITION, Stage.class);
    }

    private static Stage readRemoveStage() {
        if (!REMOVAL.exists()) {
            return new Stage();
        }
        return readObject(REMOVAL, Stage.class);
    }

    private static List<String> update_parents(Commit parent_commit) {
        List<String> parents = parent_commit.getParents();
        parents.add(parent_commit.getCommit_id());
        return parents;
    }

    private static Map<String, String> update_pathToBlobID(Commit parent_commit) {
        Map<String, String> pathToBlobID = parent_commit.getPathToBlobID();
        Map<String, String> add_stage_map = calAddStageMap();
        Map<String, String> remove_stage_map = calRemoveStageMap();
        if (add_stage_map.size() != 0) {
            for (String blob_id : add_stage_map.keySet()) {
                pathToBlobID.put(add_stage_map.get(blob_id), blob_id);
            }
        }
        if (remove_stage_map.size() != 0) {
            for (String blob_id : remove_stage_map.keySet()) {
                pathToBlobID.remove(remove_stage_map.get(blob_id));
            }
        }
        return  pathToBlobID;
    }

    public static Map<String, String> calAddStageMap() {
        add_stage = readAddStage();
        Map<String, String> add_stage_map = new TreeMap<>();
        List<Blob> add_stage_blob = add_stage.getBlobList();
        for (Blob blob : add_stage_blob) {
            add_stage_map.put(blob.get_BlobId(), blob.get_BlobPath());
        }
        return add_stage_map;
    }
    public static Map<String, String> calRemoveStageMap() {
        remove_stage = readRemoveStage();
        Map<String, String> remove_stage_map = new TreeMap<>();
        List<Blob> remove_stage_blob = remove_stage.getBlobList();
        for (Blob blob : remove_stage_blob) {
            remove_stage_map.put(blob.get_BlobId(), blob.get_BlobPath());
        }
        return remove_stage_map;
    }


    /** rm [file name]
     *  Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit, stage it for removal
     *  and remove the file from the working directory if the user has not already done so
     *  (do not remove it unless it is tracked in the current commit).
     * */

    public static void rm(String file_name)  {
        File rm_file = new File(file_name);
        Blob rm_blob = new Blob(rm_file);
        // 1.check Addition folder
        if (add_stage.ifContains(rm_blob)) {
            add_stage.delete(rm_blob);
        }
        // 2.check current commit folder
        // TODO: finish current commit folder building


    }

    /** log
     *  Starting at the current head commit, display information about each commit backwards along the commit tree
     *  until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits.
     *  (In regular Git, this is what you get with git log --first-parent). This set of commit nodes is called the commit’s history.
     *  For every node in this history, the information it should display is the commit id,
     *  the time the commit was made, and the commit message.
     * */
    public static void log () {

    }

    /** global-log
     *  Like log, except displays information about all commits ever made. The order of the commits does not matter.
     *  Hint: there is a useful method in gitlet.Utils that will help you iterate over files within a directory.
     * */
    public static void global_log() {

    }

    /** find [commit message]
     *  Prints out the ids of all commits that have the given commit message, one per line.
     *  If there are multiple such commits, it prints the ids out on separate lines.
     *  The commit message is a single operand; to indicate a multiword message, put the operand in quotation marks,
     *  as for the commit command below. Hint: the hint for this command is the same as the one for global-log.
     * */


    public static void find(String commit_message) {

    }

    /** status
     *  Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     *  An example of the exact format it should follow is as follows.
     * */
    public static void status() {

    }

    /** Usages:
     *  1.checkout -- [file name]
     *  2.checkout [commit id] -- [file name]
     *  3.checkout [branch name]
     * */
    public static void checkout() {

    }


    /** branch [branch name]
     *  Creates a new branch with the given name, and points it at the current head commit.
     *  A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     *  This command does NOT immediately switch to the newly created branch (just as in real Git).
     *  Before you ever call branch, your code should be running with a default branch called “master”.
     * */
    public static void branch(String branch_name) {

    }

    /** rm-branch [branch name]
     *  Deletes the branch with the given name. This only means to delete the pointer associated with the branch;
     *  it does not mean to delete all commits that were created under the branch, or anything like that.
     */

    public static void rm_branch(String branch_name) {

    }

    /** reset [commit id]
     *  Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit.
     *  Also moves the current branch’s head to that commit node.
     *  See the intro for an example of what happens to the head pointer after using reset.
     *  The [commit id] may be abbreviated as for checkout. The staging area is cleared.
     *  The command is essentially checkout of an arbitrary commit that also changes the current branch head.
     */
    public static void reset(String commit_id) {

    }

    /** merge [branch name]
     *  Merges files from the given branch into the current branch. This method is a bit complicated,
     *  so here’s a more detailed description:
     */
    public static void merge(String branch_name) {

    }

}
