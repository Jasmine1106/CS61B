package gitlet;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/** 用户输入n个字符， 输出其全排列，
 * 并可在此基础上从n选取m个字符进行组合输出.(m < n)
 * @author: 徐成智
 * */

public class Permutations {
    /** 静态全排列方法， 输入字符数组arr， 起始和结束索引 start, end， 打印出char[start]到 char[end]的全排列
     * 利用递归， base case 为 start == end时
     * 利用辅助方法swap交换char_array的两个元素*/
    private static void permute(char[] char_array, int start, int end) {
        if (start == end) {    // base case
            System.out.println(String.valueOf(char_array));
        } else {
            for (int i = start; i <= end; i += 1) {
                swap(char_array, start, i);     // 私有辅助方法， 交换char_array任意两个元素
                permute(char_array, start + 1, end); // 递归调用全排列方法
                swap(char_array, start, i); // backtrack
            }
        }
    }

    // 交换数组中的两个元素
    private static void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // 组合方法
    private static void combine(char[] arr, int start, int m, List<Character> tempList, int n) {
        if (m == 0) {
            System.out.println(tempList);
            return;
        }
        for (int i = start; i <= n - m; i++) {
            tempList.add(arr[i]);
            combine(arr, i + 1, m - 1, tempList, n);
            tempList.remove(tempList.size() - 1); // backtrack
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入字符数量n：");
        int n = scanner.nextInt();
        System.out.print("请输入" + n + "个字符：");
        char[] chars = new char[n];
        for (int i = 0; i < n; i++) {
            chars[i] = scanner.next().charAt(0);
        }

        // 输出全排列
        System.out.println("全排列如下：");
        permute(chars, 0, chars.length - 1);

        // 输出组合
        System.out.print("请输入要选取的字符数量m：");
        int m = scanner.nextInt();
        System.out.println("从" + n + "个字符中选取" + m + "个字符的组合如下：");
        combine(chars, 0, m, new ArrayList<>(), chars.length);
    }
}