package lab2;

public class MyInt {
    private int val;

    public MyInt(int val) {
        this.val = val;
    }

    public void increment() {
        this.val++;
    }

    public void decrement() {
        this.val--;
    }

    public int getVal() {
        return this.val;
    }
}
