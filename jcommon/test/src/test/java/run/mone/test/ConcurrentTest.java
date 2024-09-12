package run.mone.test;

import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2024/9/3 09:30
 */
public class ConcurrentTest {

    @Test
    public void testPhaser() {
        Phaser phaser = new Phaser(2);
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            phaser.arriveAndDeregister();
            System.out.println("thread");
        }).start();
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndDeregister();
        System.out.println(phaser.isTerminated());
        System.out.println("finish");
    }


    @Test
    public void testPhaser4() {
        Phaser phaser = new Phaser(1);
        IntStream.range(0, 3).forEach(it -> {
            try {
                phaser.arrive();
                phaser.awaitAdvanceInterruptibly(phaser.getPhase() - 1);
                System.out.println(phaser);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


    }


    @SneakyThrows
    @Test
    public void testPhaser2() {
        Phaser phaser = new Phaser(1);
        //一次注册2个
        phaser.bulkRegister(2);
        new Thread(() -> {
            System.out.println("thread1");
            phaser.arriveAndAwaitAdvance();
        }).start();

        new Thread(() -> {
            System.out.println("thread2");
            phaser.arriveAndAwaitAdvance();
        }).start();

        phaser.arriveAndAwaitAdvance();
        System.out.println("finish1:" + phaser.getPhase());

        System.out.println(phaser.isTerminated());

        //查询注册了多少个
        System.out.println(phaser.getRegisteredParties());

        //反注册一个
        bulkDeregister(phaser, 1);


        phaser.arrive();
        phaser.arriveAndAwaitAdvance();
        System.out.println("finish2:" + phaser.getPhase());

        System.out.println(phaser.getRegisteredParties());
    }


    //反注册一定数量
    public static void bulkDeregister(Phaser phaser, int count) {
        for (int i = 0; i < count; i++) {
            phaser.arriveAndDeregister();
        }
    }


    @SneakyThrows
    @Test
    public void testCancel() {
        Future<?> f = Executors.newSingleThreadExecutor().submit(() -> {
            boolean v = true;
            try {
                TimeUnit.SECONDS.sleep(5);
                v = false;
            } catch (Throwable e) {
                System.out.println(e);
//                throw new RuntimeException(e);
            } finally {
                System.out.println(v);
            }
        });
        TimeUnit.SECONDS.sleep(1);
        boolean v = f.cancel(true);
        System.out.println(v);
    }


    @SneakyThrows
    @Test
    public void testCancel2() {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        System.out.println(pool);
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
                System.out.println("task finish1");
            } catch (InterruptedException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            } finally {
                System.out.println("task finish");
            }
        }, pool);

//        TimeUnit.MILLISECONDS.sleep(10);
        boolean v = f.cancel(true);
        System.out.println(v);

        System.out.println("finish");
        TimeUnit.SECONDS.sleep(8);
    }


    @SneakyThrows
    @Test
    public void testCancel3() {
        Phaser phaser = new Phaser(1);
        phaser.register();
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("run");
                phaser.awaitAdvanceInterruptibly(phaser.getPhase(), 1, TimeUnit.HOURS);
                System.out.println("finish:" + phaser + "," + phaser.getUnarrivedParties() + "," + phaser.getPhase());
            } catch (Throwable e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        }, pool);

        TimeUnit.SECONDS.sleep(2);

        phaser.forceTermination();
//        phaser.arrive();
//        phaser.arrive();

//        phaser.arriveAndDeregister();
//        phaser.arriveAndDeregister();

        System.out.println("termination");

        TimeUnit.SECONDS.sleep(10);

    }


    @Test
    public void testMap() {
        A a = new A();
        a.m.put("a", "b");
        A b = new A();
        b.m.put("b", "c");
        System.out.println(A.m);
    }


    @Test
    public void testPhaser3() {
        Phaser phaser = new Phaser();
        try {
            phaser.awaitAdvanceInterruptibly(phaser.getPhase(), 1, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            System.out.println(ex);
        }
    }


    public String testReturn() {
        try {
            System.out.println("run");
        } finally {
            return "abc";
        }
    }


    @Test
    public void test3() {
        Phaser phaser = new Phaser(2);
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CompletableFuture.runAsync(() -> {
            if (true) {
                System.out.println("error");
                phaser.forceTermination();
                throw new RuntimeException("error");
            }
            phaser.arrive();
        }, pool);

        phaser.arriveAndAwaitAdvance();
        System.out.println("finish");
    }


    @Test
    public void test4() {
        Phaser phaser = new Phaser(1);
        phaser.forceTermination();
        phaser.arriveAndAwaitAdvance();
    }

    @Test
    public void test5() throws InterruptedException {
        Phaser p = new Phaser(1);

        Phaser phaser = new Phaser(1);
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

        CompletableFuture.runAsync(() -> {
            System.out.println("in");
            p.arrive();
            System.out.println(phaser);
            System.out.println("run2");
        }, pool);

        p.awaitAdvanceInterruptibly(p.getPhase());

        CompletableFuture.runAsync(() -> {
            phaser.arrive();
            System.out.println("run1");
        }, pool);

        System.out.println(p);


        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    public void test6() {
        Phaser phaser = new Phaser(1);
        phaser.forceTermination();
        phaser.forceTermination();
        System.out.println("finish");
    }
}
