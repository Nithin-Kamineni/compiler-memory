package bytecodetests;

public class rec1 implements Runnable {
    int a;

    public rec1() {
        super();
    }

    class p implements Runnable {

        @Override
        public void run() {
            System.out.println("in p, a=");
            System.out.println(a);
            a = a-1;
            if(a>=0){
                run();
            }
        }
    }

    public static void main(String[] args) {
        new rec1().run();
    }

    @Override
    public void run() {
        a = 6;
        new p().run();
    }

}