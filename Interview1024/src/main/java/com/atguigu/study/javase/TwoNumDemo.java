package com.atguigu.study.javase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zsf
 * @create 2020-11-11 14:13
 * @version 1.0
 *
 * 题目说明：
 *  力扣第一题：两数之和
 *  https://leetcode-cn.com/problems/two-sum/
 *
 * 面试题：
 *  给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的两个整数，并返回它们的下标；
 *  但是，数组中同一个元素不能使用两遍。
 *  示例：
 *      给定 nums = [2, 7, 11, 15], target = 9
 *      因为 num[0] + num[1] = 2 + 7 = 9
 *      所以返回[0, 1]
 *
 * 解法（先完成,再完美）:
 *  1、双重循环，暴力破解；可以完成，但是数组中同一个元素使用多次，自我感觉不符合题意。
 *  2、哈希（最优解），使用 map（k，v）存值，只需要循环一遍数组。
 *
 * 考查点：
 *  算法
 *  你都想来大厂了，算法居然从来没有刷过？
 *  呵呵，机会偏爱有准备有实力的头脑，不是白说的......
 */
public class TwoNumDemo
{
    // 所有算法最简单的暴力破解
    // 遍历  ---->  暴力破解
    /**
     * 通过双重循环遍历数组中所有元素的两两组合，当出现符合的和时返回两个元素的下标。
     * 这个算法的时间复杂度，两次循环，O n的平方；随着数组的增大，性能急剧下降，还有最优解。
     * @param nums
     * @param target
     * @return
     */
    public static int[] twoSum1(int[] nums, int target)
    {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if(target - nums[i] == nums[j])
                {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }


    /**
     * 通过一个循环遍历数组，同时使用map记录已经使用过的值；在map使用过的值中查找，当出现符合的和时返回两个元素的下标。
     * 使用数组中的值和马匹中的值配对，不符合数组下标+1，往后遍历，map添加元素；数组越来越短，map中元素越来越多直到数组的最后一个值。
     * 算法的 3 个时间复杂度 O(1)(最好的算法，一次就找到；redis kv；java hashMap)  O(Log2N)   O(n)
     * @param nums
     * @param target
     * @return
     */
    public static int[] twoSum2(int[] nums, int target)
    {
        Map<Integer, Integer> map =new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int partnerNumber = target - nums[i];
            if(map.containsKey(partnerNumber))
            {
                return new int[]{map.get(partnerNumber), i};
            }
            map.put(nums[i], i);
        }
        return null;
    }

    public static void main(String[] args)
    {
        int target = 9;
        int[] nums = new int[]{2, 7, 11, 15};

        //int[] myIndex = twoSum1(nums, target);
        int[] myIndex = twoSum2(nums, target);
        for (int element: myIndex) {
            System.out.println(element);
        }
    }
}
