package bytecodetests;

public class Var1 implements Runnable{
    int a;
    String b;
    boolean c;

    public Var1() {
        super();
    }

    @Override
    public void run() {
        a = 42;
        b = "hello";
        c = true;
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

    public static void main(String[] args) {
        new Var1().run();
    }
}