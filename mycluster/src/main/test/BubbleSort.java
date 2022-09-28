/**
 * @Author lin_li
 * @Date 2022/9/26 9:50
 * <p>
 * 冒泡排序
 */
public class BubbleSort {

    public static void bubbleSort(int[] data) {
        System.out.println("开始排序");
        int arrLength = data.length;

        for (int i = 0; i < arrLength - 1; i++) {
            for (int j = 0; j < arrLength - 1 - i; j++) {
                if (data[j] < data[j + 1]) {
                    int tmp = data[j + 1];
                    data[j + 1] = data[j];
                    data[j] = tmp;
                }
            }

        }
    }

    public static void main(String[] args) {
        int[] data = {1, 9, -16, 21, 23, -30, -49, 21, 30, 30};
        System.out.println("排序前：\n" + java.util.Arrays.toString(data));

        bubbleSort(data);

        System.out.println("排序后：\n" + java.util.Arrays.toString(data));
    }

}
