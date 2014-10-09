package exercise4;

import exercise3.*;
import exercise3.MyRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        ExecutorService eggsecutor;
        eggsecutor = Executors.newFixedThreadPool(4);
        for( int i = 0; i < 4; i++)
        {
            eggsecutor.execute(new MyRunnable());
        }
        eggsecutor.shutdown();
        try {
            eggsecutor.awaitTermination(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException exc)
        {
            
        }
        System.out.println("FINISHED");
    }
}
