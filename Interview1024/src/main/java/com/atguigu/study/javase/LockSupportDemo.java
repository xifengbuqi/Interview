package com.atguigu.study.javase;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className 锁中断
 * @description 线程等待唤醒机制
 * @author zsf
 * @version 1.0
 * @create 2020-11-11 22:43
 *
 * 1、线程等待唤醒机制(wait/notify)
 *  3种让线程等待和唤醒的方法
 *  方式1；使用Object中的wait()方法让线程等待， 使用Object中的notify()方法唤醒线程；
 *  方式2:使用JUC包中Condition的await()方法让线程等待，使用signal()方法唤醒线程 ；
 *  方式3:LockSupport类可以阻塞当前线程以及唤醒指定被阻塞的线程。
 *
 * 2、传统的synchronized和Lock实现等待唤醒通知的约束
 * 	2.1、线程先要获得并持有锁，必须在锁块（synchronized或lock）中
 * 	2.2、必须要先等待后唤醒，线程才能够被唤醒
 *
 * 3、LockSupport(俗称 锁中断)
 *  3.1 是什么
 *  LockSupport 是用来创建锁和其他同步类的基本线程阻塞原语。
 *  LockSupport中的park()和unpark()的作用分别是阻塞线程和解除阻塞线程。
 */
public class LockSupportDemo
{
    static Object objectLock = new Object();
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args)// main  方法，主线程一切程序入口
    {

    }

    /**
     * LockSupport类中的park等待和unpark唤醒
     * 1、LockSupport(俗称 锁中断)是什么
     *  LockSupport是用来创建锁和其他同步类的基本线程阻塞原语。
     *  LockSupport类使用了一种名为Permit(许可）的概念来做到阻塞和唤醒线程的功能，每个线程都有一个许可(permit),
     *  permit只有两个值1和零，默认是零。可以把许可看成是一种(0,1)信号量(Semaphore），但与Semaphore不同的是，许可的累加上限是1。
     *
     * 2、主要方法 API（底层都是调用 UNSAFE 中的 native 代码）
     *  2.1阻塞    park()/park(Object blocker)    阻塞当前线程/阻塞传入的具体线程
     *      permit默认是O，所以一开始调用park()方法，当前线程就会阻塞，直到别的线程将当前线程的permit设置为1时,
     *      park方法会被唤醒，然后会将permit再次设置为O并返回。
     *  2.2唤醒    unpark(Thread thread)    唤醒处于阻断状态的指定线程
     *      调用unpark(thread)方法后，就会将thread线程的许可permit设置成1(注意多次调用unpark方法，不会累加，permit值还是1)
     *      会自动唤醒thread线程，即之前阻塞中的LockSupport.park()方法会立即返回。
     *
     * 3、代码
     *  3.1、以前的两种方式:
     *      1.以前的等待唤醒通知机制必须synchronized里面有一个wait和notify;
     *      2.lock里面有await和signal;
     *  3.2、这上面这两个都必须要持有锁才能干，LockSupport它的解决的痛点
     *      1、LockSupport不用持有锁块，不用加锁，程序性能好；
     *      2、先后顺序（可以先执行unpack后执行pack，没有任何影响），不容易导致卡死。
     *  3.3、之前错误的先唤醒后等待，LockSupport照样支持。
     *      解释
     *      sleep 方法 3秒后醒来，执行 park 无效，没有阻塞效果，解释如下：
     *      先执行了 unpark(a)导致上面的 park 方法形同虚设无效，时间一样。
     *
     * 4、重点说明
     *  4.1
     *      LockSupport是用来创建锁和其他同步类的基本线程阻塞原语
     *      LockSupport是一个线程阻塞工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，阻塞之后也有对应的唤醒方法。
     *      LockSupport提供park()和unpark()方法实现阻塞线程和解除线程阻塞的过程
     *      LockSupport和每个使用它的线程都有一个许可(permit)关联。permit相当于1，0的开关，默认是0，
     *      调用一次unpark就加1变成1，调用一次park会消费permit，也就是将1变成o，同时park立即返回。
     *      如再次调用park会变成阻塞(因为permit为零了会阻塞在这里，一直到permit变为1)，这时调用unpark会把permit置为1。
     *      每个线程都有一个相关的permit, permit最多只有一个，重复调用unpark也不会积累凭证。
     *  4.2形象的理解
     *      线程阻塞需要消耗凭证(permit)，这个凭证最多只有1个。
     *      当调用park方法时：
     *          如果有凭证，则会直接消耗掉这个凭证然后正常退出;
     *          如果无凭证，就必须阻塞等待凭证可用;
     *      而unpark则相反，它会增加一个凭证，但凭证最多只能有1个（不能累积），累积无效。
     *
     * 5、面试题：
     *  5.1、为什么可以先唤醒线程后阻塞线程?
     *      因为unpark获得了一个凭证，之后再调用park方法，就可以名正言顺的凭证消费，故不会阻塞。
     *  5.2、为什么唤醒两次后阻塞两次，但最终结果还会阻塞线程?
     *      因为凭证的数量最多为1，连续调用两次unpark和调用一次unpark效果一样，只会增加一个凭证;
     *      而调用两次park却需要消费两个凭证，证不够，不能放行。
     */
    private static void lockSupportParkUnpark()
    {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + "come in" + System.currentTimeMillis());
            LockSupport.park();// 被阻塞......等待通知等待放行，它要通过需要许可证
            System.out.println(Thread.currentThread().getName() + "\t" + "被唤醒" + System.currentTimeMillis());
        },"a");
        a.start();

        // 暂停几秒钟线程
        try { TimeUnit.SECONDS.sleep(3); }catch (InterruptedException e){ e.printStackTrace(); }

        Thread b = new Thread(() -> {
            LockSupport.unpark(a);
            System.out.println(Thread.currentThread().getName() + "\t" + "通知");
        },"b");
        b.start();
    }

    /**
     * Condition 接口中的 await 后 signal 方法实现线程的等待和唤醒
     * await 方法和 signal 方法的限制
     *
     * 现象、结论一样
     */
    private static void lockAwaitSignalLimit() {
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t" + "come in");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t" + "被唤醒");
            }finally {
                lock.unlock();
            }
        },"A").start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t" + "通知");
                condition.signal();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        },"").start();
    }

    /**
     * Object类中的wait和notify方法实现线程等待和唤醒
     * wait方法和notify方法的限制
     * 1、代码正常运行，B线程执行后，唤醒A线程；
     *
     * 2、wait方法和notify方法，两个都去掉同步代码（注释synchronized (objectLock){ 和 }）块后看运行结果；
     *  2.1、异常情况
     *  Exception in thread "A" java.lang.IllegalMonitorStateException
     *  Exception in thread "B" java.lang.IllegalMonitorStateException
     *  2.2、结论
     *  Object 类中的 wait、notify、notifyAll 用于线程等待和唤醒的方法，都必须在 synchronized 内部执行（必须用到关键字 synchronized）；
     *
     * 3、将 notify 放在 wait 方法前面，B先 notify 了，3秒后A线程在执行 wait 方法；
     *  3.1程序一直无法结束；
     *  3.2结论
     *  先 wait 后 notify、notifuAll 方法，等待中的线程才会被唤醒，否则无法唤醒。
     *
     * 4、总结：
     *  wait和notify方法必须要在同步块或者方法里面且成对出现使用；
     *  先wait后notify才OK。
     */
    private static void synchronizedWaitNotifyLimit()
    {
        new Thread(() -> {
            // 暂停几秒钟线程
            // try { TimeUnit.SECONDS.sleep(3); }catch (InterruptedException e){ e.printStackTrace(); }
            synchronized (objectLock){
                System.out.println(Thread.currentThread().getName() + "\t" + "come in");
                try {
                    objectLock.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t" + "被唤醒");
            }
        },"A").start();

        new Thread(() -> {

            synchronized (objectLock){
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t" + "通知");
            }
        },"B").start();
    }
}
