class obcreate1{
	int a,b;
	obcreate1(){
		a=0;b=0;
	}
}

class obcreate extends obcreate1{

static int dig;
obcreate ob,ob1;

obcreate(int i){
	dig=i;
}

public static void main(String[] args){
	//ob=new obcreate(1);
	ob.display();
	//ob1=new obcreate(2);
	ob1.display();
	ob.display();
		
}

public void print1(int a){
	System.out.println(a);
	
}

public void display(){
	System.out.println(dig);
	ob.print1(super.a);
	ob.print1(super.b);
	

}
} 