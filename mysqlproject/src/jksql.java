import java.sql.*;
import java.util.Scanner;

public class jksql
{
	static Scanner scan = new Scanner(System.in);
	static Connection con;
    
	public static void main(String[] args)
	{
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jkdb","root","powerkk");
			
		    int terminal=1;
			
            while(true)
            {
            System.out.println("0.Exit");
			System.out.println("1.Seller Menu");
			System.out.println("2.Customer Menu");
			System.out.println("3.view talk");
			System.out.println("4.administer menu");
			terminal =scan.nextInt();
			scan.nextLine();
		
			switch(terminal)
			{
			case 0:
				con.close();
				System.exit(0);
			break;
			
			case 1:
				sellerlogin();
				break;
				
			case 2:
				customerlogin();
				break;
				
			case 3:
				talkmenu();
				break;
			
			case 4:
				administerlogin();
			default :
				System.out.println("please write 0~3 number");     
				break;
			}
			
            }
			
		} catch(Exception e){
			
			e.printStackTrace();
			
		}
	
		
		
		
		
	}
	
	static void administerlogin()
	{
		int terminal;
		while(true)
		{
		System.out.println("0. return to previous menu");     
		System.out.println("1. administer login");
		terminal = scan.nextInt();
		scan.nextLine();
		switch(terminal)
		{
		case 0:
		return ;
		
		case 1:
		String password;	
		System.out.println("enter password :");     
	    password = scan.nextLine();
	  
	   try{ 
	    Statement stmt = con.createStatement();
	    ResultSet result =stmt.executeQuery("select * from administer where Password ='" +password+"'");
	    if(result.next())
	    {
	     	System.out.println("administer login");     
	 	   administermenu();
	    	break;
	    }
	    else
	    {
	    	System.out.println("check id or password");     
		    
	    	
	    }
	    result.close();
	    stmt.close();
	} catch(Exception e){
			
			e.printStackTrace();
			
		}
	
	     
	    	    
	    break;
	    
			
		default :
			System.out.println("please write 0~1 number");     
			break;
		
		
		}
		}
		
		
		
		
	}
	static void administermenu()
	{
	
		int terminal;
		while(true)
		{
		System.out.println("0. return to previous menu");     
		System.out.println("1. view order pastdeadline");
		System.out.println("2. change orderinfo");
		terminal = scan.nextInt();
		scan.nextLine();
		switch(terminal)
		{
		case 0:
		return ;
		
		case 1:
	        viewpastdeadline();
			break;
			
		case 2:
			changeorderinfo();
			break;
			
			default:
				System.out.println("please write 0~2 number");     
							
				break;
				
		}
		}
	}
	
	static void viewpastdeadline()
	{
		  try{ 
			    Statement stmt = con.createStatement();
			    Statement stmt2 = con.createStatement();
			    String state;
			    String deadline;
			    String deadline2;
			    String seller="";
			    String customer="";
			    long Sssn;
			    long Cssn;
			    long ordernumber;
			   Date currentday;
			   long current=0;
			   long day=0;
			   Date pastday;
			   long past=0;
			   Date pastday2;
			   long past2=0;
			   
			   int setdeadline;
			   int setdeadline2;
			    ResultSet temp = stmt.executeQuery("select now()");
			    
				   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			
			    
			    
			    ResultSet result =stmt.executeQuery("select * from orderinfo ");
			    while(result.next())
			    {
			    
			    	setdeadline = result.getInt("Deadlineseller");
			    	setdeadline2 = result.getInt("Deadlinecustomer");
			    pastday = result.getDate("Dateseller");
			    past = pastday.getTime();
			    past = past/1000;
			   pastday2 = result.getDate("Datecustomer");
			   past2 = pastday2.getTime();
			   past2 = past2/1000;
			    Sssn = result.getLong("Sssnum");
			    Cssn = result.getLong("Cssnum");
			    /*
			    ResultSet result2 = stmt.executeQuery("select * from seller where Ssn ="+String.valueOf(Sssn));
			    while(result2.next())
			    {
			    	
			    	seller = result2.getString("Name");
			    }
			    
			    result2 = stmt.executeQuery("select * from customer where Ssn ="+String.valueOf(Cssn));
			    while(result2.next())
			    {
			    	customer = result2.getString("Name");
			    	
			    	
			    }
			    */
			    day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    if(setdeadline!=0)
			    {
			    deadline = "past deadline";	
			    state =result.getString("Stateseller");
			    ordernumber = result.getLong("Ordernumber");
			    System.out.println("----------order info------------");
			    //System.out.println("seller : "+seller);
			    System.out.println("Seller ssn : " +String.valueOf(Sssn));
			    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
			    System.out.println("state : " + state + "(0: before deposit, 1: shipping, 2: rate)");
			    System.out.println("--------------------------------"); 	
		    	
			    }
			    
			    
			    
			    }
			
			    day = (current - past2)/(60*60*24);
				   
			    if((int)day>setdeadline2)
			    {
			    	
			    	deadline = "past deadline";	
				    state =result.getString("Statecustomer");
				    ordernumber = result.getLong("Ordernumber");
				    System.out.println("----------order info------------");
				  //  System.out.println("customer : "+customer);
				    System.out.println("customer ssn : " +String.valueOf(Cssn));
				    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
				    System.out.println("state : " + state + " (0: deposit, 1: purchasing decision, 2: exchange,take back, 3: rate)");
					System.out.println("--------------------------------"); 	
			    	
			    	
			    	
			    }
			    
			    
			    
			    
			    
                }
			    
			    while(true)
			    {
			    	
			    int selectnum;
				System.out.println("enter Ordernumber to remove (if you don't want enter 0)");             
				selectnum = scan.nextInt();
				scan.nextLine();
              if(selectnum!=0)
              {
            	  stmt.executeUpdate("delete from orderinfo where Ordernumber ="+String.valueOf(selectnum));
              	
            	  
              }
              else
            	  break;
			    }
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
			
			
		
		
	}
	
	
	static void changeorderinfo()
	{
	
		long ordernumber;
        int select;
	System.out.println("enter ordernumber (if you want cancle enter 0)");
	ordernumber = scan.nextLong();
	   scan.nextLine(); 
	   if(ordernumber!=0)
	   {
	   
	 System.out.println("enter customer or seller (seller : 0 , customer : 1)");
	 select = scan.nextInt();
	 scan.nextLine();
	   
	 changeorderinfo2(ordernumber,select);
	   }	
	
	}
	
	static void changeorderinfo2(long ordernumber,int select)
	{
		switch(select)
		{
		
		case 0: //seller  
			   try{ 
			    Statement stmt = con.createStatement();
                Statement stmt2 = con.createStatement();			
 			    String state;
			    String deadline;
			   Date currentday;
			   long current=0;
			   long day=0;
			   Date pastday;
			   long past=0;
			   
			   int setdeadline;
			    ResultSet temp = stmt.executeQuery("select now()");
			    
				   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			
			    
			    
			    ResultSet result =stmt.executeQuery("select * from orderinfo where ordernumber = " + String.valueOf(ordernumber));
			    while(result.next())
			    {
			    state =result.getString("Stateseller");
			    setdeadline = result.getInt("Deadlineseller");
			    System.out.println("----------order info------------");
			    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
			    System.out.println("state : " + state + "(0: before deposit, 1: shipping, 2: rate)");
			    int temp2 = Integer.parseInt(state);
			    
			    pastday = result.getDate("Dateseller");
			    past = pastday.getTime();
			    past = past/1000;
			    
			    day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
			    System.out.println("deadline : " +deadline);
			    System.out.println("--------------------------------");
			    
			    }
			    
				int tempinput;
				System.out.println("enter change state (0: no thanks, 1: shipping, 2: rate)");
				tempinput=scan.nextInt();
				scan.nextLine();
				switch(tempinput)
				{
				case 0:
					
					break;
				case 1:
					
					 stmt2.executeUpdate("update orderinfo set  Dateseller = (select now()),Stateseller = '1',Deadlineseller =  7 where Ordernumber ="+String.valueOf(ordernumber));	
					break;
				case 2:
					 stmt2.executeUpdate("update orderinfo set  Dateseller = (select now()),Stateseller = '2',Deadlineseller =  40 where Ordernumber ="+String.valueOf(ordernumber));
		            	
					break;
					
				default:
						System.out.println("system error");
						System.exit(0);
						break;
				
				
				}
				

			    	
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
			
			
			break;
			
		case 1: //customer
			   try{ 
				    Statement stmt = con.createStatement();
				    Statement stmt2 = con.createStatement();
				    String state;
				    String deadline;
				    Date currentday;
					   long current=0;
					   long day=0;
					   Date pastday;
					   long past=0;
					   
					   int setdeadline;
					    ResultSet temp = stmt.executeQuery("select now()");
					    
						   
					    while(temp.next())
					    {
					 
					
					    	currentday =temp.getDate("now()");
					    	current = currentday.getTime();
					    	current =current /1000;
					        
					    }
					    temp.close(); 
					
					
				    
				    
				    ResultSet result =stmt.executeQuery("select * from orderinfo where Ordernumber= " + String.valueOf(ordernumber));
				    while(result.next())
				    {
				    state =result.getString("Statecustomer");
				    setdeadline = result.getInt("Deadlinecustomer");
				    ordernumber = result.getLong("Ordernumber");
				    System.out.println("----------order info------------");
				    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
				    System.out.println("state : " + state + " (0: deposit, 1: purchasing decision, 2: exchange,take back, 3: rate)");
				    int temp2 = Integer.parseInt(state);
				    pastday = result.getDate("Datecustomer");
				    past = pastday.getTime();
				    past = past/1000;
				    day = (current - past)/(60*60*24);
				    
				    if((int)day>setdeadline)
				    {
				    deadline = "past deadline(please deal with auction)";	
				    	
				    }
				    else
				    {
				    deadline = String.valueOf(setdeadline-day);	
				    	
				    }
					
								    
				    System.out.println("deadline : " +deadline);
				    System.out.println("--------------------------------");
				    }
				    	int tempinput;
						System.out.println("enter change state (0: no thanks, 1: purchasing decision, 2: exchange,take back, 3: rate)");
						tempinput=scan.nextInt();
						scan.nextLine();
						switch(tempinput)
						{
						case 0:
							
							break;
						case 1:
							
							 stmt2.executeUpdate("update orderinfo set  Datecustomer = (select now()),Statecustomer = '1',Deadlinecustomer =  7 where Ordernumber ="+String.valueOf(ordernumber));	
							break;
						case 2:
							 stmt2.executeUpdate("update orderinfo set  Datecustomer = (select now()),Statecustomer = '2',Deadlinecustomer =  10 where Ordernumber ="+String.valueOf(ordernumber));
				            	
							break;
						
						case 3:
							stmt2.executeUpdate("update orderinfo set  Datecustomer = (select now()),Statecustomer = '3',Deadlinecustomer =  40 where Ordernumber ="+String.valueOf(ordernumber));
				            
							break;
						default:
						
								System.out.println("system error");
								System.exit(0);
								break;
						
						

						}


				    
				    
				    	
				    
				    result.close();
				    stmt.close();
				} catch(Exception e){
						
						e.printStackTrace();
						
					}
			break;
			
	
		default:
				System.out.println("In vieworder parameter disting error");
				break;
			
		
	
		}
		
}	
		
	
	
	static void sellerlogin()
	{
		int terminal;
		while(true)
		{
		System.out.println("0. return to previous menu");     
		System.out.println("1. seller login");
		System.out.println("2. register seller");
		terminal = scan.nextInt();
		scan.nextLine();
		switch(terminal)
		{
		case 0:
		return ;
		
		case 1:
		String id;
		String password;	
		System.out.println("enter id(name) :");
		id = scan.nextLine();
		System.out.println("enter password :");     
	    password = scan.nextLine();
	  
	   try{ 
	    Statement stmt = con.createStatement();
	    ResultSet result =stmt.executeQuery("select * from seller where Name = '"+id+"' AND Password ='"+password+"'");
	    if(result.next())
	    {
	     	System.out.println("seller " +id+ " login");     
	 	   sellermenu(result.getLong("Ssn"));
		    
	    	break;
	    }
	    else
	    {
	    	System.out.println("check id or password");     
		    
	    	
	    }
	    result.close();
	    stmt.close();
	} catch(Exception e){
			
			e.printStackTrace();
			
		}
	
	     
	    	    
	    break;
	    
		case 2:
		String regid=null;
		String regpassword;
		String ssn;
		String gender;
		String address;
		String birthdate;
			Statement stmt;
			try {
				stmt = con.createStatement();
			
	    	
		System.out.println("enter id(Name)");
		    
		    regid=scan.nextLine();
		    
		    ResultSet resresult =stmt.executeQuery("select * from seller where Name = '"+regid+"'");
			   
		    while(true)
			{
				if(resresult.next())
				{
					System.out.println("this id alreay exists ");
					System.out.println("enter new id(name) ");
				    regid = scan.nextLine();       
				    resresult =stmt.executeQuery("select * from seller where Name = '"+regid+"'");
					
					
				}
				else
				{
					
				break;	
				}
				
				
			}
		    
		    System.out.println("enter password");
		    regpassword = scan.nextLine();       
		    
		    System.out.println("enter social security number");
		    ssn = scan.nextLine();       
			
		    System.out.println("enter gender(0:Man,1:FEMANLE)");
		    gender = scan.nextLine();
			
		    System.out.println("enter address");
		    address = scan.nextLine();       
			
		    System.out.println("enter birthdate(ex. 12.25)");
		    birthdate = scan.nextLine();       
		
		    regid = "'" +regid +"'";
		    gender = "'" +gender +"'";
		    address = "'" +address +"'";
		    birthdate = "'" +birthdate +"'";
		    regpassword = "'" +regpassword +"'";
		    
		    stmt.executeUpdate("insert into seller values ("+regid+","+ssn+","+gender+","+address+",'C',"+birthdate+","+regpassword+")");
		    resresult.close();
		    stmt.close();
		    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			break;
			
		default :
			System.out.println("please write 0~1 number");     
			break;
		
		
		}
		}
		
		
	}
	
	static void sellermenu(long inputssn)
	{
		int terminal;
		while(true)
		{
			System.out.println("0. return to previous menu");     
			System.out.println("1. view my order");     
			System.out.println("2. view my product");
			System.out.println("3. view my talk");     
			System.out.println("4. register new product");
			System.out.println("5. remove product");
			System.out.println("6. deal with auction");
			System.out.println("7. register new talk");
			terminal = scan.nextInt();
			scan.nextLine();
			
			switch(terminal)
			{
			case 0:
				return;
				
			case 1:
				vieworder(inputssn,0);
				break;
			case 2:
				viewproduct(inputssn);
				break;
			case 3:
				   Statement stmt;
				try {
					stmt = con.createStatement();
				
				   long talknum;
				    String writer;
				    float rate;
				    String title;
				  ResultSet result =stmt.executeQuery("select * from talk where Sssno ="+String.valueOf(inputssn));
				    while(result.next())
				    {
				    writer =result.getString("Writer");
				    talknum = result.getInt("Talknumber");
				    title =result.getString("Title");
				    rate = result.getFloat("Rate");
				    System.out.println("----------talk info-------------");
				    System.out.println("Talknumber	Title		Writer		Rate");
				    System.out.println(String.valueOf(talknum)+"	"+title+"	"+writer+"	"+String.valueOf(rate));
				    System.out.println("--------------------------------");
				    }
				    result.close();
				    stmt.close();
				    long selectnum;
				    while(true)
				    {
					System.out.println("enter talknumber to see detail (if you don't want enter 0)");             
					selectnum = scan.nextInt();
					scan.nextLine();
                  if(selectnum!=0)
				    viewtalk(selectnum);
                  else
                  	break;
				    
				    }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
				break;
				
			case 4:
				addnewproduct(inputssn);
			    break;
			case 5:
				removeproduct(inputssn);
				break;
			case 6:
				viewauction(inputssn);
				break;
			case 7:
				registertalk(inputssn,0);
			    break;
			default :
			    System.out.println("please write 0~7 number");     
				break;
			
			}
			
				
			
			
			
			
		}
		
		
	}
	
	static void viewauction(long inputssn)
	{
		  try{ 
			    Statement stmt = con.createStatement();
			    long pnumber;
			    String pname="";
			    String category;
			    int price;
			    int auctionprice;
			    Date pastday;
			    Date currentday;
			    long past;
			    long current=0;
			    long day;
			    int setdeadline;
			    long temp2;
			    String deadline;
			    int num;
			    long customer=0;
			    
			    ResultSet temp = stmt.executeQuery("select now()");
			    
			   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			    ResultSet result =stmt.executeQuery("select * from product where Ossn = " + String.valueOf(inputssn) + " and Method_of_sale ="+"'0'"+" and Assn is not null");
				  
			    System.out.println("-------auction product-----------");
			    System.out.println("productnumber	productname		category	price	auctionprice	deadline");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    auctionprice = result.getInt("Auctionprice");
			    pastday = result.getDate("Pdate");
			    setdeadline = result.getInt("Deadline");
			    past = pastday.getTime();
			    past = past /1000;
			    
			 
				day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(auctionprice)+"	"+deadline);
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
				
			
			    long selectnum;
			    while(true)
			    {
				System.out.println("enter productnumber to sell (if you don't want enter 0)");             
				selectnum = scan.nextInt();
				scan.nextLine();
                if(selectnum!=0)
                {
           	 
                	
                	result =stmt.executeQuery("select * from product where Pnumber = "+String.valueOf(selectnum));
                	while(result.next())
                	{
                	customer = result.getLong("Assn");

                	pname = result.getString("Pname");
                	}
                	stmt.executeUpdate("insert into orderinfo (Datecustomer,Sssnum,Cssnum,Statecustomer,Stateseller,Deadlinecustomer) (select now(),"+String.valueOf(inputssn)+","+String.valueOf(customer)+","+"'0'"+","+"'0'"+","+"3)");
                    long ordernumber = getmaxprimary();
                    stmt.executeUpdate("update orderinfo set Dateseller = Datecustomer where Ordernumber ="+String.valueOf(ordernumber));
            		
                    stmt.executeUpdate("insert into order_contents values ("+String.valueOf(ordernumber)+",'"+pname+"',1)");
                	
                	stmt.executeUpdate("delete from product where Pnumber ="+String.valueOf(selectnum));
                	
                	
                	
			    }
			    else
                	break;
			    
			    }
			    
			    
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
	
		
		
		
	}
	
	static long getmaxprimary()
	{
		long ordernumber=0;
		  long temp;
	  try {
		  
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery("select * from orderinfo");
		while(result.next())
		{
			
		  temp = result.getLong("Ordernumber");
		  if(temp>ordernumber)
		  {
			ordernumber=temp;  
		  }
			
		
		}
		
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
		
	  return ordernumber;
	}
	static void removeproduct(long inputssn)
	{
		 try{ 
			    Statement stmt = con.createStatement();
			    long pnumber;
			    String pname;
			    String category;
			    int price;
			    int auctionprice;
			    Date pastday;
			    Date currentday;
			    long past;
			    long current=0;
			    long day;
			    int setdeadline;
			    long temp2;
			    String deadline;
			    int num;
			    
			    
			    
			    ResultSet temp = stmt.executeQuery("select now()");
			    
			   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			    ResultSet result =stmt.executeQuery("select * from product where Ossn = " + String.valueOf(inputssn) + " and Method_of_sale ="+"'0'");
				  
			    System.out.println("-------auction product-----------");
			    System.out.println("productnumber	productname		category	price	auctionprice	amount	deadline");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    auctionprice = result.getInt("Auctionprice");
			    pastday = result.getDate("Pdate");
			    setdeadline = result.getInt("Deadline");
			    past = pastday.getTime();
			    past = past /1000;
			    num = result.getInt("Pnum");
			 
				day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(auctionprice)+"	"+String.valueOf(num)+"		"+deadline);
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
				
				result =stmt.executeQuery("select * from product where Ossn = " + String.valueOf(inputssn) + " and Method_of_sale =" +"'1'");
			    System.out.println("-------fixed cost product-----------");
			    System.out.println("productnumber	productname		category	price	amount");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    num = result.getInt("Pnum");
			 
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(num));
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
			
			    
			    long selectnum;
			    while(true)
			    {
				System.out.println("enter productnumber to remove (if you don't want enter 0)");             
				selectnum = scan.nextInt();
				scan.nextLine();
                if(selectnum!=0)
                {
           	 
                	stmt.executeUpdate("delete from product where Pnumber ="+String.valueOf(selectnum));
                	
			    }
			    else
                	break;
			    
			    }
			    
			    
			    
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}	
		
	
	}
	
	static void addnewproduct(long inputssn)
	{
	    String productname;
		String category="";
		int method;
		String method2;
		String description;
		int price;
		int auctionprice;
		int deadline;
		int num;
		
			Statement stmt;
			try {
				stmt = con.createStatement();
			
	    	
		System.out.println("enter product name");
		productname =scan.nextLine();
		    
		    int temp;
		    System.out.println("select category 1. computer 2. notebook 3. other");
		    temp = scan.nextInt();
		    scan.nextLine();
		    switch(temp)
		    {
		    case 1:
		    	category="computer";
		    	break;
		    case 2:
		    	category="notebook";
		    	break;
		    case 3:
		    	category="other";
		    	break;
		    	
		    default :
		    		System.out.println("wrong category insert");
		    		System.exit(0);
		    		break;
		    
		    
		    }
		    
		    
		    System.out.println("enter product description");
		    description = scan.nextLine();
			 
		    System.out.println("select method_of_sale 0. auction, 1. fixed cost sell");
		    method = scan.nextInt();
		    scan.nextLine();
		    
		
		    switch(method)
		    {
		    case 0:
		    	 System.out.println("enter  immediately purchase price");
		    	 price = scan.nextInt();
				 scan.nextLine();
				 
				System.out.println("enter  auctionprice");
			    auctionprice = scan.nextInt();
			    scan.nextLine();
				
			    System.out.println("enter  deadline (unit:day)");
			    deadline = scan.nextInt();
			    scan.nextLine();
			    num =1;
			    
			    productname = "'" +productname +"'";
			    category = "'" +category +"'";
			    description = "'" +description +"'";
			     method2 = String.valueOf(method);
			    method2 = "'" +method2 +"'";
			    
			    
			    stmt.executeUpdate("insert into product (Pdate,Ossn,Pname,Category,Method_of_sale,Description,Price,Auctionprice,Deadline,Pnum) (select now(),"+String.valueOf(inputssn)+","+productname+","+category+","+method2+","+description+","+String.valueOf(price)+","+String.valueOf(auctionprice)+","+String.valueOf(deadline)+","+String.valueOf(num)+")");
			    
			    
		    	break;
		    	
		    case 1:
		    	  System.out.println("enter sell amount ");
				    num = scan.nextInt();
				    scan.nextLine();
				  
		    	
		    	System.out.println("enter price");
		    	 price = scan.nextInt();
				 scan.nextLine();
				
				 
				 productname = "'" +productname +"'";
				    category = "'" +category +"'";
				    description = "'" +description +"'";
				    method2 = String.valueOf(method);
				    method2 = "'" +method2 +"'";
				    
				    stmt.executeUpdate("insert into product (Pdate,Ossn,Pname,Category,Method_of_sale,Description,Price,Pnum) (select now(),"+String.valueOf(inputssn)+","+productname+","+category+","+method2+","+description+","+String.valueOf(price)+","+String.valueOf(num)+")");
				    
		    	break;
		    	
		    	default :
		    		System.out.println("wrong method_of_sale insert");
		    		System.exit(0);
		    		break;
		    
		    
		    
		    }
		    
		    
		    
		    stmt.close();
		    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
			
	}
	
	@SuppressWarnings("deprecation")
	static void viewproduct(long inputssn)
	{
		
		  try{ 
			    Statement stmt = con.createStatement();
			    long pnumber;
			    String pname;
			    String category;
			    int price;
			    int auctionprice;
			    Date pastday;
			    Date currentday;
			    long past;
			    long current=0;
			    long day;
			    int setdeadline;
			    long temp2;
			    String deadline;
			    int num;
			    
			    
			    
			    ResultSet temp = stmt.executeQuery("select now()");
			    
			   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			    ResultSet result =stmt.executeQuery("select * from product where Ossn = " + String.valueOf(inputssn) + " and Method_of_sale ="+"'0'");
				  
			    System.out.println("-------auction product-----------");
			    System.out.println("productnumber	productname		category	price	auctionprice	amount	deadline");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    auctionprice = result.getInt("Auctionprice");
			    pastday = result.getDate("Pdate");
			    setdeadline = result.getInt("Deadline");
			    past = pastday.getTime();
			    past = past /1000;
			    num = result.getInt("Pnum");
			 

			    
			    
			    day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(auctionprice)+"	"+String.valueOf(num)+" 	"+deadline);
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
				
				result =stmt.executeQuery("select * from product where Ossn = " + String.valueOf(inputssn) + " and Method_of_sale =" +"'1'");
			    System.out.println("-------fixed cost product-----------");
			    System.out.println("productnumber	productname		category	price	amount");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    num = result.getInt("Pnum");
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(num));
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
			
			    
			    
			    
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
		
	}
	
	static void customerlogin()
	{
		int terminal;
		while(true)
		{
		System.out.println("0. return to previous menu");     
		System.out.println("1. customer login");
		System.out.println("2. register customer");
		terminal = scan.nextInt();
		scan.nextLine();
		switch(terminal)
		{
		case 0:
		return ;
		
		case 1:
		String id;
		String password;	
		System.out.println("enter id(name) :");
		id = scan.nextLine();
		System.out.println("enter password :");     
	    password = scan.nextLine();
	  
	   try{ 
	    Statement stmt = con.createStatement();
	    ResultSet result =stmt.executeQuery("select * from customer where Name = '"+id+"' AND Password ='"+password+"'");
	    if(result.next())
	    {
	    	System.out.println("customer " +id+ " login");     
	    	customermenu(result.getLong("Ssn"));
		    
	    	break;
	    }
	    else
	    {
	    	System.out.println("check id or password");     
		    
	    	
	    }
	    result.close();
	    stmt.close();
	} catch(Exception e){
			
			e.printStackTrace();
			
		}
	
	     
	    	    
	    break;
	    
		case 2:
		String regid=null;
		String regpassword;
		String ssn;
		String gender;
		String address;
		String birthdate;
			Statement stmt;
			try {
				stmt = con.createStatement();
			
	    	
		System.out.println("enter id(Name)");
		    
		    regid=scan.nextLine();
		    
		    ResultSet resresult =stmt.executeQuery("select * from customer where Name = '"+regid+"'");
			   
		    while(true)
			{
				if(resresult.next())
				{
					System.out.println("this id alreay exists ");
					System.out.println("enter new id(name) ");
				    regid = scan.nextLine();       
				    resresult =stmt.executeQuery("select * from customer where Name = '"+regid+"'");
					
					
				}
				else
				{
					
				break;	
				}
				
				
			}
		    
		    System.out.println("enter password");
		    regpassword = scan.nextLine();       
		    
		    System.out.println("enter social security number");
		    ssn = scan.nextLine();       
			
		    System.out.println("enter gender(0:Man,1:FEMANLE)");
		    gender = scan.nextLine();
			
		    System.out.println("enter address");
		    address = scan.nextLine();       
			
		    System.out.println("enter birthdate(ex. 12.25)");
		    birthdate = scan.nextLine();       
		
		    regid = "'" +regid +"'";
		    gender = "'" +gender +"'";
		    address = "'" +address +"'";
		    birthdate = "'" +birthdate +"'";
		    regpassword = "'" +regpassword +"'";
		    
		    stmt.executeUpdate("insert into customer values ("+regid+","+ssn+","+gender+","+address+",'C',"+birthdate+","+regpassword+")");
		    resresult.close();
		    stmt.close();
		    
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			break;
			
		default :
			System.out.println("please write 0~1 number");     
			break;
		
		
		}
		}
		
		
		
		
	
	}
	
	static void customermenu(long inputssn)
	{
		int terminal;
		while(true)
		{
			System.out.println("0. return to previous menu");     
			System.out.println("1. view my order");     
			System.out.println("2. view product");
			System.out.println("3. view my talk");       
			System.out.println("4. view my auction");
			System.out.println("5. register new talk");
			terminal = scan.nextInt();
			scan.nextLine();
			
			switch(terminal)
			{
			case 0:
				return;
				
			case 1:
				vieworder(inputssn,1);
				break;
			case 2:
				viewproductcustomer(inputssn);
				break;
			case 3:
				   Statement stmt;
					try {
						stmt = con.createStatement();
					
					   long talknum;
					    String writer;
					    float rate;
					    String title;
					  ResultSet result =stmt.executeQuery("select * from talk where Cssno ="+String.valueOf(inputssn));
					    while(result.next())
					    {
					    writer =result.getString("Writer");
					    talknum = result.getInt("Talknumber");
					    title =result.getString("Title");
					    rate = result.getFloat("Rate");
					    System.out.println("----------talk info-------------");
					    System.out.println("Talknumber	Title		Writer		Rate");
					    System.out.println(String.valueOf(talknum)+"	"+title+"	"+writer+"	"+String.valueOf(rate));
					    System.out.println("--------------------------------");
					    }
					    result.close();
					    stmt.close();
					    long selectnum;
					    while(true)
					    {
						System.out.println("enter talknumber to see detail (if you don't want enter 0)");             
						selectnum = scan.nextInt();
						scan.nextLine();
	                  if(selectnum!=0)
					    viewtalk(selectnum);
	                  else
	                  	break;
					    
					    }
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					  
				
				break;

			case 4:
				viewcustomerauction(inputssn);
				break;
			case 5:
				registertalk(inputssn,1);
				break;
			default :
			    System.out.println("please write 0~5 number");     
				break;
			
			}
			
					
		}
		
		
	}
	
	static void registertalk(long inputssn,int disting)
	{
		
		switch(disting)
		{
		
		case 0: //seller  
			   try{ 
			    Statement stmt = con.createStatement();
			    String state;
			    String deadline;
			    long ordernumber;
			   Date currentday;
			   long current=0;
			   long day=0;
			   Date pastday;
			   long past=0;
			   
			   int setdeadline;
			    ResultSet temp = stmt.executeQuery("select now()");
			    
				   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			
			    
			    
			    ResultSet result =stmt.executeQuery("select * from orderinfo where Sssnum = " + String.valueOf(inputssn)+" and Stateseller = '2'");
			    while(result.next())
			    {
			    state =result.getString("Stateseller");
			    setdeadline = result.getInt("Deadlineseller");
			    ordernumber = result.getLong("Ordernumber");
			    System.out.println("----------order info------------");
			    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
			    System.out.println("state : " + state + "(0: before deposit, 1: shipping, 2: rate)");
			    int temp2 = Integer.parseInt(state);
			    
			    pastday = result.getDate("Dateseller");
			    past = pastday.getTime();
			    past = past/1000;
			    
			    day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
			
			    
			    switch(temp2)
			    {
			
			    
			  
			    case 0:
			    	break;
			    	
			    case 1:
			    	
			    case 2:
			    	System.out.println("deadline : " +deadline);
				    	
			    	break;
			    	
			    	default :
			    		System.out.println("state error");
			    		System.exit(0);
			    		break;
			    }
			    System.out.println("--------------------------------");
			    
	
			    System.out.println("enter ordernumber to rate (if you don't want enter : 0)");
			    long inputorder = scan.nextLong();
			    scan.nextLine();
			    if(inputorder!=0)
			    registertalk2(inputssn,0,inputorder);
			    
			    
			    
			    
			    }
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
			
			
			break;
			
		case 1: //customer
			   try{ 
				    Statement stmt = con.createStatement();
				    String state;
				    String deadline;
				    long ordernumber;
				    Date currentday;
					   long current=0;
					   long day=0;
					   Date pastday;
					   long past=0;
					   
					   int setdeadline;
					    ResultSet temp = stmt.executeQuery("select now()");
					    
						   
					    while(temp.next())
					    {
					 
					
					    	currentday =temp.getDate("now()");
					    	current = currentday.getTime();
					    	current =current /1000;
					        
					    }
					    temp.close(); 
					
					
				    
				    
				    ResultSet result =stmt.executeQuery("select * from orderinfo where Cssnum = " + String.valueOf(inputssn)+" and Statecustomer = '3'");
				    while(result.next())
				    {
				    state =result.getString("Statecustomer");
				    setdeadline = result.getInt("Deadlinecustomer");
				    ordernumber = result.getLong("Ordernumber");
				    System.out.println("----------order info------------");
				    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
				    System.out.println("state : " + state + " (0: deposit, 1: purchasing decision, 2: exchange,take back, 3: rate)");
				    int temp2 = Integer.parseInt(state);
				    pastday = result.getDate("Datecustomer");
				    past = pastday.getTime();
				    past = past/1000;
				    day = (current - past)/(60*60*24);
				    
				    if((int)day>setdeadline)
				    {
				    deadline = "past deadline(please deal with auction)";	
				    	
				    }
				    else
				    {
				    deadline = String.valueOf(setdeadline-day);	
				    	
				    }
					
				    
				    
				    switch(temp2)
				    {
				    case 0:
				    	
				    case 1:
				    	
				    case 2:
				    case 3:
				    	System.out.println("deadline : " +deadline);
					    	
				    	break;
				    	
				    	default :
				    		System.out.println("state error");
				    		System.exit(0);
				    		break;
				    }
				    
				    
				    System.out.println("--------------------------------");
				    
				    
				    

				    System.out.println("enter ordernumber to rate (if you don't want enter : 0)");
					 long inputorder = scan.nextLong();
				    scan.nextLine();
				    if(inputorder!=0)
				    registertalk2(inputssn,1,inputorder);
				    
				    
				    }
				    result.close();
				    stmt.close();
				} catch(Exception e){
						
						e.printStackTrace();
						
					}
			break;
			
	
		default:
				System.out.println("In vieworder parameter disting error");
				break;
			
		
	
		}
		
		
		
		
		
	}
	
	
	
	static void registertalk2(long inputssn,int disting,long ordernumber)
	{
	  switch(disting)
	  {
	  case 0://seller
		  try {
			Statement stmt = con.createStatement();
		    ResultSet result;
		    String seller="";
			float rate;
			String title;
			String content;
		    result =stmt.executeQuery("select * from seller where Ssn = "+String.valueOf(inputssn));
		    while(result.next())
		    {
		    	seller = result.getString("Name");		    	
		    }
		  
		    System.out.println("enter title");
		    title = scan.nextLine();
		    System.out.println("enter content");
		    content = scan.nextLine();
		    System.out.println("enter rate (0.0~5.0)");
		    rate = scan.nextFloat();
		    
		    result = stmt.executeQuery("select * from talk where Ordernumber ="+String.valueOf(ordernumber));
		    
		    if(result.next())
		    {
		    	
		    	System.out.println("you alreay rate this order");
		    }
		    else
		    {
		    stmt.executeUpdate("insert into talk (Writer,Content,Rate,Title,Sssno,Ordernumber) values ( '"+seller+"','"+content+"',"+String.valueOf(rate)+",'"+title+"',"+String.valueOf(inputssn)+","+String.valueOf(ordernumber)+")");
		    }
		    
		  
		  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  break;
	  case 1://customer
		  try {
				Statement stmt = con.createStatement();
			    ResultSet result;
			    String customer="";
				float rate;
				String title;
				String content;
			    result =stmt.executeQuery("select * from customer where Ssn = "+String.valueOf(inputssn));
			    while(result.next())
			    {
			    	customer = result.getString("Name");		    	
			    }
			  
			    System.out.println("enter title");
			    title = scan.nextLine();
			    System.out.println("enter content");
			    content = scan.nextLine();
			    System.out.println("enter rate (0.0~5.0)");
			    rate = scan.nextFloat();
			    
			    result = stmt.executeQuery("select * from talk where Ordernumber ="+String.valueOf(ordernumber));
			    
			    if(result.next())
			    {
			    	
			    	System.out.println("you alreay rate this order");
			    }
			    else
			    {
			    stmt.executeUpdate("insert into talk (Writer,Content,Rate,Title,Cssno,Ordernumber) values ( '"+customer+"','"+content+"',"+String.valueOf(rate)+",'"+title+"',"+String.valueOf(inputssn)+","+String.valueOf(ordernumber)+")");
			    }
			  
			  
			  
			  } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		  
		  
		  break;
		  
		  default:
			  System.out.println("system error");
			  System.exit(0);
			  break;
	  }
		
	}
	
	static void viewcustomerauction(long input)
	{
		
		try{ 
		    Statement stmt = con.createStatement();
		    long pnumber;
		    String pname="";
		    String category;
		    int price;
		    int auctionprice;
		    Date pastday;
		    Date currentday;
		    long past;
		    long current=0;
		    long day;
		    int setdeadline;
		    long temp2;
		    String deadline;
		    int num;
		    long customer=0;
		    
		    ResultSet temp = stmt.executeQuery("select now()");
		    
		   
		    while(temp.next())
		    {
		 
		
		    	currentday =temp.getDate("now()");
		    	current = currentday.getTime();
		    	current =current /1000;
		        
		    }
		    temp.close(); 
		    ResultSet result =stmt.executeQuery("select * from product where Assn = " + String.valueOf(input) + " and Method_of_sale ="+"'0'"+" and Assn is not null");
			  
		    System.out.println("-------auction product-----------");
		    System.out.println("productnumber	productname		category	price	auctionprice	deadline");
		    while(result.next())
		    {
		    pnumber = result.getLong("Pnumber");
		    pname = result.getString("Pname");
		    category = result.getString("Category");
		    price = result.getInt("Price");
		    auctionprice = result.getInt("Auctionprice");
		    pastday = result.getDate("Pdate");
		    setdeadline = result.getInt("Deadline");
		    past = pastday.getTime();
		    past = past /1000;
		    
		 
			day = (current - past)/(60*60*24);
		    if((int)day>setdeadline)
		    {
		    deadline = "past deadline(please deal with auction)";	
		    	
		    }
		    else
		    {
		    deadline = String.valueOf(setdeadline-day);	
		    	
		    }
			
			System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(auctionprice)+"	"+deadline);
		    
		    
		    }
		    
		    System.out.println("--------------------------------");
		    
		    result.close();
		    stmt.close();
		} catch(Exception e){
				
				e.printStackTrace();
				
			}

		
	
	}
	
	static void viewfixedcost(long input)
	{
		  try{ 
			    Statement stmt = con.createStatement();
			    long pnumber;
			    String pname="";
			    String category;
			    int price;
			    int auctionprice;
			    Date pastday;
			    Date currentday;
			    long past;
			    long current=0;
			    long day;
			    int setdeadline;
			    long temp2;
			    String deadline;
			    int num=0;
			    
				
				ResultSet result =stmt.executeQuery("select * from product where  Method_of_sale =" +"'1'");
			    System.out.println("-------fixed cost product-----------");
			    System.out.println("productnumber	productname		category	price	amount");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    num = result.getInt("Pnum");
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(num));
			    
			    
			    }
		  
			    System.out.println("--------------------------------");
	            
			    
			    long selectnum;
			    while(true)
			    {
				System.out.println("enter productnumber to purchase (if you don't want enter 0)");             
				selectnum = scan.nextInt();
				scan.nextLine();
              if(selectnum!=0)
              {
            	  int amount;
            	  long sellerssn=0;
            	
  				System.out.println("enter purchase product amount");             
  				amount = scan.nextInt();
  				scan.nextLine();
  				
                 result =stmt.executeQuery("select * from product where Pnumber = "+String.valueOf(selectnum));
 
                 while(result.next())
 			    {
 			    num = result.getInt("Pnum");
 				pname = result.getString("Pname");
                sellerssn=result.getLong("Ossn");
 			    
 			    }
                 
                 while(amount>num)
                 {
                     System.out.println("out of bound");
                	 System.out.println("enter purchase product amount");             
       				amount = scan.nextInt();
       				scan.nextLine();
       				
      
                	 
                 }
            
                 ResultSet temp = stmt.executeQuery("select now()");
 			    
  			   
 			    while(temp.next())
 			    {
 			 
 			
 			    	currentday =temp.getDate("now()");
 			    	current = currentday.getTime();
 			    	current =current /1000;
 			        
 			    }
 			    temp.close(); 
 			
                	
            		stmt.executeUpdate("insert into orderinfo (Datecustomer,Sssnum,Cssnum,Statecustomer,Stateseller,Deadlinecustomer) (select now(),"+String.valueOf(sellerssn)+","+String.valueOf(input)+","+"'0'"+","+"'0'"+","+"3)");
            		long ordernumber = getmaxprimary();
            		stmt.executeUpdate("update orderinfo set Dateseller = Datecustomer where Ordernumber ="+String.valueOf(ordernumber));
            		stmt.executeUpdate("insert into order_contents values ("+String.valueOf(ordernumber)+",'"+pname+"',1)");
                	
                	stmt.executeUpdate("update product set Pnum ="+String.valueOf((num-amount)) +" where Pnumber ="+String.valueOf(selectnum));
            	  
                  
              }
              else
              	break;
			    
			    }
			    
			    
			    
			    
			    
			    
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
	
		
	}
	
	static void viewauctioncost(long input)
	{
		  try{ 
			    Statement stmt = con.createStatement();
			    long pnumber;
			    String pname="";
			    String category;
			    int price;
			    int auctionprice;
			    Date pastday;
			    Date currentday;
			    long past;
			    long current=0;
			    long day;
			    int setdeadline;
			    long temp2;
			    String deadline;
			    int num=0;
			    
			    
			    
			    ResultSet temp = stmt.executeQuery("select now()");
			    
			   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			    ResultSet result =stmt.executeQuery("select * from product where Method_of_sale ="+"'0'");
				  
			    System.out.println("-------auction product-----------");
			    System.out.println("productnumber	productname		category	price	auctionprice	amount	deadline");
			    while(result.next())
			    {
			    pnumber = result.getLong("Pnumber");
			    pname = result.getString("Pname");
			    category = result.getString("Category");
			    price = result.getInt("Price");
			    auctionprice = result.getInt("Auctionprice");
			    pastday = result.getDate("Pdate");
			    setdeadline = result.getInt("Deadline");
			    past = pastday.getTime();
			    past = past /1000;
			    num = result.getInt("Pnum");
			 
				day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
				System.out.println(pnumber+"	"+pname+"	"+category+"	"+String.valueOf(price)+"	"+String.valueOf(auctionprice)+"	"+String.valueOf(num)+" 	"+deadline);
			    
			    
			    }
			    
			    System.out.println("--------------------------------");
				
			    long selectnum;
			    while(true)
			    {
				System.out.println("enter productnumber to auction (if you don't want enter 0)");             
				selectnum = scan.nextInt();
				scan.nextLine();
              if(selectnum!=0)
              {
            	int tempswitch;
            	System.out.println("1. immediately purchase");
            	System.out.println("2. auction purchase");
            	
				tempswitch = scan.nextInt();
				scan.nextLine();
              
            	  switch(tempswitch)
            	  {
            	  
            	  case 1:
                      long sellerssn=0;
            		  result =stmt.executeQuery("select * from product where Pnumber = "+String.valueOf(selectnum));
                      
                      while(result.next())
      			    {
                    	  pname = result.getString("Pname");
      			      sellerssn=result.getLong("Ossn");
      			    
      			    }
                      
                            
            		  
            		  stmt.executeUpdate("insert into orderinfo (Datecustomer,Sssnum,Cssnum,Statecustomer,Stateseller,Deadlinecustomer) (select now(),"+String.valueOf(sellerssn)+","+String.valueOf(input)+","+"'0'"+","+"'0'"+","+"3)");
              		long ordernumber = getmaxprimary();
              		stmt.executeUpdate("update orderinfo set Dateseller = Datecustomer where Ordernumber ="+String.valueOf(ordernumber));
              		stmt.executeUpdate("insert into order_contents values ("+String.valueOf(ordernumber)+",'"+pname+"',1)");
              		stmt.executeUpdate("delete from product where Pnumber ="+String.valueOf(selectnum));
                	
            		  break;
            	  case 2:
            	  long currentprice=0;
                  result =stmt.executeQuery("select * from product where Pnumber = "+String.valueOf(selectnum));
                  
                  while(result.next())
  			    {
  			    currentprice = result.getInt("Auctionprice");
  			    }
            	 
                  while(true)
                  {
  				System.out.println("enter auction price");             
  				auctionprice = scan.nextInt();
  				scan.nextLine();
  				if(auctionprice>currentprice)
  					break;
  				else
  					System.out.println("current price is lower than auction price please rewrite price");
  				
                  }
        		
                	stmt.executeUpdate("update product set Auctionprice ="+String.valueOf(auctionprice) +",Assn ="+String.valueOf(input)+" where Pnumber ="+String.valueOf(selectnum));
            	  
            		  break;
            		  
            		  default :
            			  System.out.println("uncorrect input");
            			  System.exit(0);
            			  break;
            	  }
              }
              else
              	break;
			    
			    }
			    
			    
			    
			    
			    
			    
			    
			    
			    
			    
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
		
		
	}
	
	static void viewproductcustomer(long input)
	{
		int terminal;
		while(true)
		{
			System.out.println("0. return to previous menu");     
			System.out.println("1. fixedcost product");     
			System.out.println("2. auction product");
			terminal = scan.nextInt();
			scan.nextLine();
			
			switch(terminal)
			{
			case 0:
				return;	
			
			case 1:
				viewfixedcost(input);
				break;
			case 2:
				viewauctioncost(input);
				break;
				default :
					System.out.println("in viewproductcustomer system error");
					System.exit(0);
					
				break;
				
			}
		}
	}
	
	
	static void vieworder2(long input)
	{
		   try{ 
			    Statement stmt = con.createStatement();
			    int amount;
			    String name;
			    ResultSet result =stmt.executeQuery("select * from order_contents where Ordernumber = " + String.valueOf(input));
			    while(result.next())
			    {
			    name =result.getString("Order_product_name");
			    amount = result.getInt("Order_amount");
			    System.out.println("product name	amount");  
			    System.out.println(name + " : " +String.valueOf(amount));
			    }
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
		
		
	}
	
	static void vieworder(long inputssn,int disting)// disting :0 seller 1 customer 
	{
		switch(disting)
		{
		
		case 0: //seller  
			   try{ 
			    Statement stmt = con.createStatement();
			    String state;
			    String deadline;
			    long ordernumber;
			   Date currentday;
			   long current=0;
			   long day=0;
			   Date pastday;
			   long past=0;
			   
			   int setdeadline;
			    ResultSet temp = stmt.executeQuery("select now()");
			    
				   
			    while(temp.next())
			    {
			 
			
			    	currentday =temp.getDate("now()");
			    	current = currentday.getTime();
			    	current =current /1000;
			        
			    }
			    temp.close(); 
			
			    
			    
			    ResultSet result =stmt.executeQuery("select * from orderinfo where Sssnum = " + String.valueOf(inputssn));
			    while(result.next())
			    {
			    state =result.getString("Stateseller");
			    setdeadline = result.getInt("Deadlineseller");
			    ordernumber = result.getLong("Ordernumber");
			    System.out.println("----------order info------------");
			    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
			    System.out.println("state : " + state + "(0: before deposit, 1: shipping, 2: rate)");
			    int temp2 = Integer.parseInt(state);
			    
			    pastday = result.getDate("Dateseller");
			    past = pastday.getTime();
			    past = past/1000;
			    
			    day = (current - past)/(60*60*24);
			    if((int)day>setdeadline)
			    {
			    deadline = "past deadline(please deal with auction)";	
			    	
			    }
			    else
			    {
			    deadline = String.valueOf(setdeadline-day);	
			    	
			    }
				
			
			    
			    switch(temp2)
			    {
			
			    
			  
			    case 0:
			    	break;
			    	
			    case 1:
			    	
			    case 2:
			    	System.out.println("deadline : " +deadline);
				    	
			    	break;
			    	
			    	default :
			    		System.out.println("state error");
			    		System.exit(0);
			    		break;
			    }
			    
			    vieworder2(ordernumber);
			    System.out.println("--------------------------------");
			    }
			    result.close();
			    stmt.close();
			} catch(Exception e){
					
					e.printStackTrace();
					
				}
			
			
			break;
			
		case 1: //customer
			   try{ 
				    Statement stmt = con.createStatement();
				    String state;
				    String deadline;
				    long ordernumber;
				    Date currentday;
					   long current=0;
					   long day=0;
					   Date pastday;
					   long past=0;
					   
					   int setdeadline;
					    ResultSet temp = stmt.executeQuery("select now()");
					    
						   
					    while(temp.next())
					    {
					 
					
					    	currentday =temp.getDate("now()");
					    	current = currentday.getTime();
					    	current =current /1000;
					        
					    }
					    temp.close(); 
					
					
				    
				    
				    ResultSet result =stmt.executeQuery("select * from orderinfo where Cssnum = " + String.valueOf(inputssn));
				    while(result.next())
				    {
				    state =result.getString("Statecustomer");
				    setdeadline = result.getInt("Deadlinecustomer");
				    ordernumber = result.getLong("Ordernumber");
				    System.out.println("----------order info------------");
				    System.out.println("Ordernumber : " +String.valueOf(ordernumber));
				    System.out.println("state : " + state + " (0: deposit, 1: purchasing decision, 2: exchange,take back, 3: rate)");
				    int temp2 = Integer.parseInt(state);
				    pastday = result.getDate("Datecustomer");
				    past = pastday.getTime();
				    past = past/1000;
				    day = (current - past)/(60*60*24);
				    
				    if((int)day>setdeadline)
				    {
				    deadline = "past deadline(please deal with auction)";	
				    	
				    }
				    else
				    {
				    deadline = String.valueOf(setdeadline-day);	
				    	
				    }
					
				    
				    
				    switch(temp2)
				    {
				    case 0:
				    	
				    case 1:
				    	
				    case 2:
				    case 3:
				    	System.out.println("deadline : " +deadline);
					    	
				    	break;
				    	
				    	default :
				    		System.out.println("state error");
				    		System.exit(0);
				    		break;
				    }
				    
				    
				    vieworder2(ordernumber);
				    System.out.println("--------------------------------");
				    }
				    result.close();
				    stmt.close();
				} catch(Exception e){
						
						e.printStackTrace();
						
					}
			break;
			
	
		default:
				System.out.println("In vieworder parameter disting error");
				break;
			
		
	
		}
		
	
	}
	
	static void viewtalk(long inputtalknum)
	{
		try{
	    Statement stmt = con.createStatement();
        long talknum;
	    String writer;
	    float rate;
	    String title;
	    String content;
	    ResultSet result =stmt.executeQuery("select * from talk where Talknumber = "+String.valueOf(inputtalknum));
	    while(result.next())
	    {
	    writer =result.getString("Writer");
	    talknum = result.getInt("Talknumber");
	    title =result.getString("Title");
	    rate = result.getFloat("Rate");
	    content = result.getString("Content");
	    System.out.println("----------talk info-------------");
	    System.out.println("Talknumber	Title		Writer		Rate");
	    System.out.println(String.valueOf(talknum)+"	"+title+"	"+writer+"	"+String.valueOf(rate));
	    System.out.println("-----------Content--------------");
	    System.out.println(content);
	    System.out.println("--------------------------------");
		  
	    }
	    result.close();
	    stmt.close();
		}
		catch(Exception e){
			
			e.printStackTrace();
			
		}

		return;
				
	}
	
	static void talkmenu()
	{
		
		int terminal;
		while(true)
		{
			System.out.println("0. return to previous menu");     
			System.out.println("1. view hole talk");     
			System.out.println("2. view by writer");        
			terminal = scan.nextInt();
			scan.nextLine();
			
			switch(terminal)
			{
			case 0:
				return;
				
			case 1:
				 try{ 
					    Statement stmt = con.createStatement();
					    long talknum;
					    String writer;
					    float rate;
					    String title;
					    ResultSet result =stmt.executeQuery("select * from talk ");
					    while(result.next())
					    {
					    writer =result.getString("Writer");
					    talknum = result.getInt("Talknumber");
					    title =result.getString("Title");
					    rate = result.getFloat("Rate");
					    System.out.println("----------talk info-------------");
					    System.out.println("Talknumber	Title		Writer		Rate");
					    System.out.println(String.valueOf(talknum)+"	"+title+"	"+writer+"	"+String.valueOf(rate));
					    System.out.println("--------------------------------");
					    }
					    result.close();
					    stmt.close();
					    long selectnum;
					    while(true)
					    {
						System.out.println("enter talknumber to see detail (if you don't want enter 0)");             
						selectnum = scan.nextInt();
						scan.nextLine();
	                    if(selectnum!=0)
					    viewtalk(selectnum);
	                    else
	                    	break;
					    
					    }
					} catch(Exception e){
							
							e.printStackTrace();
							
						}
				
					break;

			case 2:
				try{
				String inputwriter;
				long talknum;
			    String writer;
			    float rate;
			    String title;
				System.out.println("enter writer");             
				inputwriter = scan.nextLine();	
			    Statement stmt = con.createStatement(); 
				ResultSet result =stmt.executeQuery("select * from talk where Writer = '"+inputwriter+"'");
				   
				    
						if(result.next())
						{
							writer =result.getString("Writer");
						    talknum = result.getInt("Talknumber");
						    title =result.getString("Title");
						    rate = result.getFloat("Rate");
						    System.out.println("----------talk info-------------");
						    System.out.println("Talknumber	Title		Writer		Rate");
						    System.out.println(String.valueOf(talknum)+"	"+title+"	"+writer+"	"+String.valueOf(rate));
						    System.out.println("--------------------------------");
						    			
						}
						else
						{
							System.out.println("there is no match writer");
							break;
						}
						
					
					    result.close();
					    stmt.close();
					    long selectnum;
					    while(true)
					    {
						System.out.println("enter talknumber to see detail (if you don't want enter 0)");             
						selectnum = scan.nextInt();
						scan.nextLine();
	                    if(selectnum!=0)
					    viewtalk(selectnum);
	                    else
	                    	break;
					    
					    }
					    
					    
					} catch(Exception e){
							
							e.printStackTrace();
							
						}
				break;
					
			default :
			    System.out.println("please write 0~2 number");     
				break;
			
			}
			
					
		}
		
		
		
		
	}

	
	
	
	
}