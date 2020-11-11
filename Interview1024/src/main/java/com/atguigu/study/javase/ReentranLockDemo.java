package com.atguigu.study.javase;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zsf
 * @version 1.0
 * @create 2020-11-11 17:07
 *
 * 可重入锁(隐式锁，synchronized)的代码验证
 * 可重入锁：可重复可递归调用的锁，在外层使用锁之后，在内层仍然可以使用，并且不发生死锁，这样的锁叫做可重入锁。
 * 在一个 Synchronized 修饰的方法或代码的内部；
 * 调用本类的其他 Synchronized 修饰的方法或代码块时，是永远可以得到锁的。
 *
 * 可重入锁种类：
 *  隐式锁（即 synchronized 关键字使用的锁，JVM层面控制）默认是可重入锁；（同步块/同步方法）
 *  显示锁（即Lock，需要显示的lock和unlock）也有ReentranLock这样的可重入锁。
 *
 * Synchronized 的重入的实现机理
 *  每个锁对象拥有一个锁计数器和一个指向持有该锁的线程的指针。
 *
 * 当执行 monitorenter（第一次进入，加锁）时，如果目标锁对象的计数器为零，那么说明它没有被其他线程所持有；
 * Java虚拟机会将该锁对象的持有线程设置为当前线程，并且将其计数器加1。
 *
 * 在目标锁对象的计数器不为零的情况下，如果锁对象的持有线程是当前线程；
 * 那么 Java 虚拟机可以将其计数器加1，否则需要等待直至持有线程释放该锁。
 *
 * 当执行 monitorexit 时，Java 虛拟机则需将锁对象的计数器减1。计数器为零代表锁已被释放。
 */
public class ReentranLockDemo
{
    static Object objectLockA = new Object();
    static Lock lock = new ReentrantLock();

    // 同步代码块（同步块）
    /**public static void m1()
    {
        new Thread(() -> {
                synchronized(objectLockA) {
                    System.out.println(Thread.currentThread().getName() + "------外层调用");
                    synchronized (objectLockA) {
                        System.out.println(Thread.currentThread().getName() + "------中层调用");
                        synchronized (objectLockA) {
                            System.out.println(Thread.currentThread().getName() + "------内层调用");
                        }
                    }
                }
        },"t1").start();
    }*/

    // 同步方法
    /**public static void m1()
    {
        System.out.println("======外层");
        m2();
    }

    private static void m2()
    {
        System.out.println("======中层");
        m3();
    }

    private static void m3()
    {
        System.out.println("======内层");
    }*/

    public static void main(String[] args)
    {
        // m1();
        lock.lock();
        try {
            System.out.println("====外层");
            lock.lock();
            try {
                System.out.println("====中层");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // lock 手动加锁（synchronized 自动加锁），所以加锁次数和释放次数必须一样，否则肯造成死锁。
            // 这里故意注释，实现加锁次数和释放次数不一样；
            // 由于次数不一样，第二个线程无法获取到锁，导致一直等待。
            lock.unlock();
        }

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t" + "调用开始");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        },"t2").start();
    }
}
