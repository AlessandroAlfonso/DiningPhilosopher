import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private final Philosopher[] philosophers;
    private final Chopstick[] chopstick;

    public Main(int num_of_Phil) {
        this.philosophers = new Philosopher[num_of_Phil];
        this.chopstick = new Chopstick[num_of_Phil];

        for (int i = 0; i < num_of_Phil; i++){
            chopstick[i] = new Chopstick();
        }

        for(int i = 0; i < num_of_Phil; i++){
            philosophers[i] = new Philosopher(i, chopstick[i], chopstick[(i + 1) % num_of_Phil], i != 0 );
        }
    }


    public void start(){
        for (Philosopher philosopher: philosophers){
            new Thread(philosopher).start();
        }
    }

    public static void main(String[] args){
        int num_of_Phil = Integer.valueOf(args[0]);
        Main main = new Main(num_of_Phil);
        main.start();
    }

    public class Chopstick{
        private final Semaphore mutex = new Semaphore(1);
        public void pickup() throws InterruptedException{
            mutex.acquire();
        }
        public void putDown(){
            mutex.release();
        }
    }

    public class Philosopher implements Runnable{
        private final int id;
        private final Chopstick firstStick;
        private final Chopstick secondStick;
        private final boolean rightHand;

        public Philosopher(int id, Chopstick rightStick, Chopstick leftStick, boolean rightHand){
            this.id = id;
            if(rightHand){
                this.firstStick = rightStick;
                this.secondStick = leftStick;
            }
            else {
                this.firstStick = leftStick;
                this.secondStick = rightStick;
            }
            this.rightHand = rightHand;

        }

        private void action(String action) throws InterruptedException{
            long time = ThreadLocalRandom.current().nextInt(0,100);
            System.out.printf("Philosopher %d is '%s' for %d millis\n", id, action, time);
        }

        private String hand(Chopstick chopstick){
            if(firstStick.equals(chopstick)){
                return rightHand ? "right" : "left";
            }
            else {
                return rightHand ? "left" : "right";
            }
        }

        private void pickup() throws InterruptedException{
            firstStick.pickup();
            System.out.printf("Philosopher %d picked up %s chopstick\n", id, hand(firstStick));

            secondStick.pickup();
            System.out.printf("Philosopher %d picked up %s chopstick\n", id, hand(secondStick));
        }

        private void putdown(){
            firstStick.putDown();
            System.out.printf("Philosopher %d put down %s chopstick\n", id, hand(firstStick));

            secondStick.putDown();
            System.out.printf("Philosopher %d put down %s chopstick\n", id, hand(secondStick));
        }

        @Override
        public void run() {
            try{
                while (true){
                    action("Thinking");
                    pickup();
                    action("Eating");
                    putdown();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }
    }
}