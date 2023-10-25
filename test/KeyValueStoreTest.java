import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import server.IServer;
import server.Server;

/**
 * This is a Junit test class for the RMI server.
 */
public class KeyValueStoreTest {
  private IServer obj;

  /**
   * Instantiates and run the RMI server at port 1099.
   */
  @BeforeEach
  void setUp() {
    try {
      this.obj = new Server(1099);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Tests the thread safety of the RMI server by using 100 threads to manipulate the hashmap.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Test
  public void testThreadSafety() throws InterruptedException {
    int numThreads = 100;
    CountDownLatch addLatch = new CountDownLatch(numThreads / 2);
    CountDownLatch deleteLatch = new CountDownLatch(numThreads / 2);
    AtomicInteger adds = new AtomicInteger(0);
    AtomicInteger deletes = new AtomicInteger(0);

    Runnable addTask = () -> {
      try {
        obj.put("key" + adds.incrementAndGet(), "value");
      } catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      } finally {
        addLatch.countDown(); // start the add operations countdown
      }
    };

    Runnable deleteTask = () -> {
      try {
        addLatch.await(); // wait for the add operations to finish
        obj.delete("key" + deletes.incrementAndGet());
      } catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      } finally {
        deleteLatch.countDown(); // start the delete operations countdown
      }
    };

    for (int i = 0; i < numThreads; i++) { // spun the threads
      if (i % 2 == 0) {
        new Thread(addTask).start();
      } else {
        new Thread(deleteTask).start();
      }
    }

    deleteLatch.await(); // wait for the delete operations to finish

    try {
      int expectedSize = adds.get() - deletes.get(); // compute the expected hashmap size
      int actualSize = obj.getMapSize(); // get the actual hashmap size
      assertEquals(expectedSize, actualSize); // compare the two values
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Shuts the RMI server down gracefully.
   */
  @AfterEach
  void tearDown() {
    try {
      obj.shutdown();
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
