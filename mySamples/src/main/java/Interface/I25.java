package Interface;

interface Z {void m1();}  // 1
class D implements Z {public final void m1() {}} // 2
class E implements Z {public synchronized void m1() {}} // 3
class F implements Z {public strictfp void m1() {}} // 4
class I25 implements Z {public native void m1();} // 5
