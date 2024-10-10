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

    // creat a new branch, add into BRANCH_DIR.
    public static File creatBranch(String branch_name) {
        return join(BRANCH_DIR, branch_name);
    }

    // change commit_id in that branch file
    public static void updateBranchPointer(File branch, String commitID) {
        writeContents(branch, commitID);
    }

    // BRANCH store the branch name of current branch
    public static void updateCurBranch(String branchName) {
        writeContents(BRANCH, commitID);
    }


}
