import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by everseeker on 2017/3/6.
 */
public class Demo {
    private final static int spiderThreadNum = 3;

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(spiderThreadNum);
        final CountDownLatch countDownLatch = new CountDownLatch(spiderThreadNum);
        long start = System.currentTimeMillis();
        for (int i = 0; i < spiderThreadNum; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
        System.out.println(System.currentTimeMillis() - start);
    }

}
