package bytecodetests;

public class proc6 implements Runnable {
    int a;

    public proc6() {
        super();
    }

    class p implements Runnable {
        @Override
        public void run() {
            System.out.println("in p");
            System.out.println(a);
            //            if(a>0){
//                System.out.println("calling q");
//                new q().run();
//            }
        }
        class q implements Runnable {
            @Override
            public void run() {
                a = 1;
                System.out.println("in q");
            }
        }
    }



    public static void main(String[] args) {
        new proc6().run();
    }

    @Override
    public void run() {
        a = 1;
        new p().run();
    }

}