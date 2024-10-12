package gitlet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import java.io.File;

/** some helper method of branches
 * */
public class Branch {

    public static File getBranchFileByName(String branchName) {
        File[] branchesList = BRANCH_DIR.listFiles();
        if (branchesList != null) {
            for (File branch : branchesList) {
                if (branch.getName().equals(branchName)) {
                    return branch;
                }
            }
        }
        return null;
    }

    // change commit_id in that branch file
    public static void updateBranchPointer(File branch, String commitID) {
        writeContents(branch, commitID);
    }

    // change commitID in current branch file
    public static void updateCurBranchPointer(String commitID) {
        String curBranchName = readContentsAsString(BRANCH);
        File curBranchFile = getBranchFileByName(curBranchName);
        if (curBranchFile != null && curBranchFile.isFile()) {
            writeContents(curBranchFile, commitID);
        }
    }

    // BRANCH store the branch name of current branch
    public static void updateCurBranch(String branchName) {
        writeContents(BRANCH, branchName);
    }

    public static String getCurBranchName() {
        return readContentsAsString(BRANCH);
    }

    // get the head commit in that spec branch
    public static Commit getBranchHead(String branchName) {
        File branchFile = getBranchFileByName(branchName);
        if (branchFile != null) {
            String HeadID = readContentsAsString(branchFile);
            return Commit.getCommitByID(HeadID);
        }
        return null;
    }

}
