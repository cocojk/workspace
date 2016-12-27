
import java.util.Scanner;
class test
{
	public static void main(String ars[]){
		
		String login ="helo/my";
		Scanner scan =new Scanner(login);
		
		scan.useDelimiter("/");
		String myName =scan.next();
		System.out.println(myName);
		
	}
	
	
}