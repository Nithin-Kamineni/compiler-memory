package bytecodetests;

public class whileLop implements Runnable{
    int a;
    int minus1;

    public whileLop() {
        super();
    }

    @Override
    public void run() {
        a = 6;
        minus1 = 0-1;
        while(a>minus1){
            System.out.println(a);
            a = a-1;
        }
    }

    public static void main(String[] args) {
        new whileLop().run();
    }
}