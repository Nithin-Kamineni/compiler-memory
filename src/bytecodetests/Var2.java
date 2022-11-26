package bytecodetests;

public class Var2 implements Runnable {
    int a;
    String b;
    boolean c;

    public Var2() {
        super();
    }

    class p implements Runnable {

        @Override
        public void run() {
            a = 42;
            b = "hello";
            c = true;
            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
        }
    }

    public static void main(String[] args) {
        new Var2().run();
    }

    @Override
    public void run() {
        new p().run();
    }

}