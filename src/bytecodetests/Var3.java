package bytecodetests;

public class Var3 implements Runnable {
    int a;

    public Var3() {
        super();
    }

    class p1 implements Runnable {
        @Override
        public void run() {
            new p2().run();
        }
        class p2 implements Runnable {
            @Override
            public void run() {
                a = 42;
                //System.out.println(a);
            }
        }
    }



    public static void main(String[] args) {
        new Var3().run();
    }

    @Override
    public void run() {
        new p1().run();
    }

}