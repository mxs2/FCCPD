import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CorrecaoConcorrencia {

    // CORREÇÃO 1: Uso de AtomicInteger para evitar Race Condition
    static AtomicInteger contador = new AtomicInteger(0);

    static final Object recursoA = new Object();
    static final Object recursoB = new Object();

    static ReentrantLock lock = new ReentrantLock();

    static class Tarefa1 implements Runnable {
        public void run() {

            for (int i = 0; i < 10000; i++) {
                contador.incrementAndGet(); // Incremento atômico
            }

            synchronized (recursoA) {
                System.out.println("Tarefa1 bloqueou A");

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }

                synchronized (recursoB) {
                    System.out.println("Tarefa1 bloqueou B");
                }
            }
        }
    }

    static class Tarefa2 implements Runnable {
        public void run() {

            for (int i = 0; i < 10000; i++) {
                contador.incrementAndGet(); // Incremento atômico
            }

            // CORREÇÃO 2: Ordem de aquisição de locks igual à Tarefa1 para evitar Deadlock
            synchronized (recursoA) {
                System.out.println("Tarefa2 bloqueou A");

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }

                synchronized (recursoB) {
                    System.out.println("Tarefa2 bloqueou B");
                }
            }
        }
    }

    static class Tarefa3 implements Runnable {
        public void run() {
            // CORREÇÃO 3: Uso de lock.lock() evita o busy-waiting e a inanição (Starvation)
            // de CPU
            lock.lock();
            try {
                System.out.println("Tarefa3 conseguiu o lock");
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(new Tarefa1());
        Thread t2 = new Thread(new Tarefa2());
        Thread t3 = new Thread(new Tarefa3());

        t1.start();
        t2.start();
        t3.start();
    }
}