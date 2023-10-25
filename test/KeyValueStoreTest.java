import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import server.IServer;

// The code below uses 100 threads to manipulate the hashmap and identify if there is an issue with the thread-safety of the RMI server.
public class KeyValueStoreTest {
  @Test
  public void testThreadSafety() throws InterruptedException, RemoteException, NotBoundException, MalformedURLException {
    int numThreads = 100;
    CountDownLatch addLatch = new CountDownLatch(numThreads / 2);
    CountDownLatch deleteLatch = new CountDownLatch(numThreads / 2);
    AtomicInteger adds = new AtomicInteger(0);
    AtomicInteger deletes = new AtomicInteger(0);
    IServer obj = (IServer) Naming.lookup("//localhost/TranslationServer");

    Runnable addTask = () -> {
      try {
        obj.put("key" + adds.incrementAndGet(), "value");
      } catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      } finally {
        addLatch.countDown();
      }
    };

    Runnable deleteTask = () -> {
      try {
        addLatch.await();
        obj.delete("key" + deletes.incrementAndGet());
      } catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      } finally {
        deleteLatch.countDown();
      }
    };

    for (int i = 0; i < numThreads; i++) {
      if (i % 2 == 0) {
        new Thread(addTask).start();
      } else {
        new Thread(deleteTask).start();
      }
    }

    deleteLatch.await();

    try {
      int expectedSize = adds.get() - deletes.get();
      int actualSize = obj.getMapSize();
      assertEquals(expectedSize, actualSize);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
