import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.rmi.Naming;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import server.IServer;

// The code below uses 100 threads to manipulate the hashmap and identify if there is an issue with the thread-safety of the RMI server.
public class KeyValueStoreTest {
  @Test
  public void testThreadSafety() throws InterruptedException {
    int numThreads = 100;
    CountDownLatch latch = new CountDownLatch(numThreads);
    AtomicInteger adds = new AtomicInteger(0);
    AtomicInteger deletes = new AtomicInteger(0);
    Runnable addTask = () -> {
      try {
        IServer obj = (IServer) Naming.lookup("//localhost/TranslationServer");
        obj.put("key" + adds.incrementAndGet(), "value");
      } catch (Exception e) {
     
      }
      latch.countDown();
    };
    Runnable deleteTask = () -> {
      try {
        IServer obj = (IServer) Naming.lookup("//localhost/TranslationServer");
        obj.delete("key" + deletes.incrementAndGet());
      } catch (Exception e) {
  
      }
      latch.countDown();
    };

    for (int i = 0; i < numThreads; i++) {
      if (i % 2 == 0) {
        new Thread(addTask).start();
      } else {
        new Thread(deleteTask).start();
      }
    }
    latch.await();
    try {
      IServer obj = (IServer) Naming.lookup("//localhost/TranslationServer");
      int expectedSize = adds.get() - deletes.get();
      int actualSize = obj.getMapSize();
      assertEquals(expectedSize, actualSize);
    } catch (Exception e) {
   
    }
  }
}
