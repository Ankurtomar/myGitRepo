package inheritance;

class contruct{
	contruct(){
		this();
		System.out.println("Called construction");
	}}

class construct2{
	public static void main(String[] args){
		contruct ob=new contruct();
	}}