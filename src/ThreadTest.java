public class ThreadTest {

  private static volatile int step;
  private static volatile boolean doNotGiveUp = true;

  static class LeftLeg extends Thread {

    private final Object locker;

    public LeftLeg(Object locker) {
      this.locker = locker;
    }

    @Override
    public void run() {
      while (doNotGiveUp) {
        synchronized (locker) {
          while (step != 1) {
            try {
              locker.wait();
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
          System.out.println("Left step");
          step = 2;
          locker.notify();
        }
      }
    }
  }

  static class RightLeg extends Thread {

    private final Object locker;

    public RightLeg(Object locker) {
      this.locker = locker;
    }

    @Override
    public void run() {
      while (doNotGiveUp) {
        synchronized (locker) {
          while (step != 2) {
            try {
              locker.wait();
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
          System.out.println("Right step");
          step = 1;
          locker.notify();
        }
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Object o = new Object();
    LeftLeg leftLeg = new LeftLeg(o);
    RightLeg rightLeg = new RightLeg(o);
    ThreadTest.step = 1;
    leftLeg.start();
    rightLeg.start();
    Thread.sleep(10000);
    doNotGiveUp = false;
  }
}
