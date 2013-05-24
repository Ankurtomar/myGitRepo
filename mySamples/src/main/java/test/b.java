class a{
	int i;
	a(){
		System.out.println("a"+i);
		i=100;		
}
	a(int a){
		i=200;					System.out.println("aaa"+i);
		}
	void  rebuild(){
		//this();
		}
}

class b extends a{
	public static vod main(String[] args){
	b ob=new b();
}
	b(){
		super(10);
		}}