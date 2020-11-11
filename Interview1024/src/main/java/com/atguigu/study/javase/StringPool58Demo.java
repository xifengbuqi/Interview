package com.atguigu.study.javase;

/**
 * @auther zsf
 * @create 2020-11-11 10:00
 * 面试题：
 *  第一问：str1 字符串引用，intern(),打印结果;
 *  第二问：为什么 str1 == str1.intern() 和 str2 == str2.intern() 打印结果不一样？
 *
 * 讲解：
 *  JDK 自带了一写信息字符串；
 *  不同版本的 JDK 自带的字符串可能不同；
 *
 * 考查点：
 *  1、intern()，判断 true/false；
 *  2、《深入理解java虚拟机》书原题 是否读过经典JM书籍
 *      第二章 2.4.3 方法区和本地方法栈溢出  代码清单 2-8
 *
 */
public class StringPool58Demo
{
    public static void main(String[] args)
    {
        String str1 = new StringBuilder("58").append("tongchneg").toString();
        System.out.println(str1);
        System.out.println(str1.intern());
        System.out.println(str1 == str1.intern());

        System.out.println();

        String str2 = new StringBuilder("ja").append("va").toString();
        //String str2 = new StringBuilder("open").append("jdk").toString();
        System.out.println(str2);
        System.out.println(str2.intern());
        System.out.println(str2 == str2.intern());
    }
}
