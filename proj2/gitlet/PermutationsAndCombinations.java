package gitlet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

/** 用户输入n个字符， 输出其全排列，
 * 并可在此基础上从n选取m个字符进行组合输出.(m < n)
 * @author: 徐成智
 **/

public class PermutationsAndCombinations {
    /** @param str_array 用户输入的字符串数组
     * */

    public List<List<String>> permute(String[] str_array) {
        int len = str_array.length;
        // 使用一个动态数组保存所有可能的全排列
        List<List<String>> result = new ArrayList<>();
        if (len == 0) {  // 处理异常情况
            throw new IllegalArgumentException("请输入字符");
        }
        // 创建used数组防止重复搜索
        boolean[] used = new boolean[len];
        // 跟踪搜索路径
        Deque<String> path = new ArrayDeque<>(len);
        // 调用深度优先搜索辅助方法
        dfs(str_array, len, 0, path, used, result);
        return result;
    }

    /** 使用深度优先搜索暴力搜索所有情况
     * @param str_array 字符串数组
     * @param len 字符串数组长度
     * @param depth 目前搜索深度
     * @param path 搜索路径
     * @param used 布尔数组，标记该节点是否访问过
     * @param result 输出全排列结果
     * */
    private void dfs(String[] str_array, int len, int depth,
                     Deque<String> path, boolean[] used,
                     List<List<String>> result) {
        // base case
        if (depth == len) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < len; i++) {
            if (!used[i]) {
                path.addLast(str_array[i]);
                used[i] = true;
                // 利用递归进行深度优先搜索
                dfs(str_array, len, depth + 1, path, used, result);
                // 逐步回溯
                used[i] = false;
                path.removeLast();
            }
        }
    }

    /** 在原来全排列程序的基础上，增加组合功能*/
    public List<List<String>> combine(String[] str_array, int m) {
        List<List<String>> result = new ArrayList<>();
        if (m > str_array.length) {
            throw new IllegalArgumentException("选取m不得超过n");
        }
        boolean[] used = new boolean[str_array.length];
        Deque<String> path = new ArrayDeque<>(m);
        dfsCombine(str_array, m, 0, path, used, result);
        return result;
    }

    private void dfsCombine(String[] str_array, int m, int depth,
                            Deque<String> path, boolean[] used,
                            List<List<String>> result) {
        if (depth == m) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < str_array.length; i++) {
            if (!used[i]) {
                path.addLast(str_array[i]);
                used[i] = true;

                dfsCombine(str_array, m, depth + 1, path, used, result);

                used[i] = false;
                path.removeLast();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要排列的字符数量：");
        int n = scanner.nextInt();
        scanner.nextLine(); // 读取nextInt后的换行符
        System.out.println("请输入 " + n + " 个字符，用空格分隔：");
        String input = scanner.nextLine();
        String[] strs = input.split(" ");

        PermutationsAndCombinations solution = new PermutationsAndCombinations();
        List<List<String>> permuteLists = solution.permute(strs);
        System.out.println("全排列结果：");
        for (List<String> list : permuteLists) {
            System.out.println(list);
        }

        System.out.println("请输入要组合的字符数量 m：");
        int m = scanner.nextInt();
        List<List<String>> combineLists = solution.combine(strs, m);
        System.out.println("组合结果：");
        for (List<String> list : combineLists) {
            System.out.println(list);
        }

        scanner.close();
    }

}