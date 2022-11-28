package bytecodetests;

public class proc2 implements Runnable {
    int a;

    public proc2() {
        super();
    }

    class p implements Runnable {
        class q implements Runnable {
            @Override
            public void run() {
                a = 1;
                System.out.println("in q, calling q1");
                new q1().run();
            }
        }
        @Override
        public void run() {
            System.out.println("in p");
            if(a>0){
                System.out.println("calling q");
                new q().run();
            }
        }

    }

    class q1 implements Runnable{
        public void run(){
            System.out.println("in q1 calling r procedure");
        }
    }



    public static void main(String[] args) {
        new proc2().run();
    }

    @Override
    public void run() {
        a = 1;
        System.out.println("in main calling p, a=1");
        new p().run();
        System.out.println("terminating back in main");

    }

}