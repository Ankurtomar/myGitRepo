package Interface;
/*
interface A {
  void m1();            // 1
  public void m2();     // 2
  protected void m3();  // 3
  private void m4();    // 4
}
*/
interface B {
  final void m1();            // 1
  public void m2();     // 2
  void m3();  // 3
  void m4();    // 4
}
