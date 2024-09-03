package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jasmine1106
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     * 1.init
     * -- Creates a new Gitlet version-control system in the current directory.
     *    This system will automatically start with one commit: a commit that contains no files and has the commit message
     *    initial commit. It will have a single branch: master, which initially points to this initial commit,
     *    and master will be the current branch. The timestamp for this initial commit will be 00:00:00 UTC,*Thursday, 1 January 1970
     *    in whatever format you choose for dates (this is called “The (Unix) Epoch”, represented internally by the time 0.)
     * 2.add [file name]
     * -- Adds a copy of the file as it currently exists to the staging area.The staging area should be somewhere in .gitlet.
     *    If the current working version of the file is identical to the version in the current commit, do not stage it to be added,
     *    and remove it from the staging area if it is already there.
     * 3.commit [message]
     * -- Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time,
     *    creating a new commit. The commit is said to be tracking the saved files. By default, each commit’s snapshot of files
     *    will be exactly the same as its parent commit’s snapshot of files; it will keep versions of files exactly as they are,
     *    and not update them. A commit will only update the contents of files it is tracking that have been staged for addition
     *    at the time of commit, in which case the commit will now include the version of the file that was staged instead of the
     *    version it got from its parent. A commit will save and start tracking any files that were staged for addition but
     *    weren’t tracked by its parent. Finally, files tracked in the current commit may be untracked in the new commit as a result
     *    being staged for removal by the rm command (below).
     * 4.rm [file name]
     * -- Unstage the file if it is currently staged for addition. If the file is tracked in the current commit,
     *    stage it for removal and remove the file from the working directory if the user has not already done
     *    so (do not remove it unless it is tracked in the current commit).
     * 5.log
     * -- Starting at the current head commit, display information about each commit backwards along the commit tree until
     *    the initial commit, following the first parent commit links, ignoring any second parents found in merge commits.
     *    (In regular Git, this is what you get with git log --first-parent). This set of commit nodes is called the commit’s history.
     *    For every node in this history, the information it should display is the commit id, the time the commit was made,
     *    and the commit message.
     * 6.status
     * -- Displays what branches currently exist, and marks the current branch with a *. Also displays what files have been staged
     * for addition or removal. An example of the exact format it should follow is as follows.
     * 7.checkout
     * -- Checkout is a kind of general command that can do a few different things depending on what its arguments are.
     *    There are 3 possible use cases. In each section below, you’ll see 3 numbered points.
     *    Each corresponds to the respective usage of checkout.
     * 8.branch
     * -- Creates a new branch with the given name, and points it at the current head commit. A branch is nothing more than
     * a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created
     * branch (just as in real Git). Before you ever call branch, your code should be running with a default branch called “master”.
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args == null) {
            System.out.println("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(firstArg, args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(firstArg, args, 2);
                Add.add(args[1]);
                break;
            case "commit":
                // TODO: handle the `commit [message]` command
                validateNumArgs(firstArg, args, 2);
                break;
            case "rm":
                // TODO: handle the `rm [file name]` command
                validateNumArgs(firstArg, args, 2);
                break;
            case "log":
                // TODO: handle the `log` command
                validateNumArgs(firstArg, args, 1);
                break;
            case "status":
                // TODO: handle the `status` command
                validateNumArgs(firstArg, args, 1);
            case "checkout":
                // TODO: handle the `checkout` command
                validateNumArgs(firstArg, args, 1);
            case "branch":
                // TODO: handle the `branch` command
                validateNumArgs(firstArg, args, 1);
            // TODO: FILL THE REST IN
            default:
                System.out.println("No command with that name exists");
        }

    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
