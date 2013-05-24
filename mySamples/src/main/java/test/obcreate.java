public class obcreate{


static int dig;

obcreate(int i){
	dig=i;
}

public static void main(String[] args){
		obcreate ob=new obcreate(1);
		ob.display();
		obcreate ob1=new obcreate(2);
		ob1.display();
		ob.display();
}

public void display(){
	System.out.println(dig);
}
} 