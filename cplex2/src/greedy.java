
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class greedy
{
	final static int EXITNUM = 8;
	final static int FACILITYNUM = 53;
	//final static int TOTALNUM = EXITNUM*FACILITYNUM;
	static double[][] distance=new double[FACILITYNUM][EXITNUM];
	static double[] exitpeople=new double[EXITNUM];
	static double[] exitwide = new double[EXITNUM];
	static double[] exitarea = new double[EXITNUM];
	static double[] facilityarea = new double[FACILITYNUM];
	static double[] facilitypeople=new double[FACILITYNUM];
    static int[] solution = new int[FACILITYNUM];	
    static boolean[] solutioncheck = new boolean[FACILITYNUM];
public static void main(String[] args)
	{
		
			int i=0;
			for(i=0;i<FACILITYNUM;i++)
			{
				solution[i]=-1;
				
			}
			int j=0;
			String temp;
			try
			{
			FileReader fw = new FileReader("distance.txt");
			BufferedReader br = new BufferedReader(fw);
			for(i=0;i<FACILITYNUM;i++)
			{
				br.readLine();
				br.readLine();

				for(j=0;j<EXITNUM;j++)
				{
			temp =br.readLine();
			//temp2 =temp.substring(0, temp.length()-1);

			distance[i][j]=Double.parseDouble(temp);
//			System.out.println(String.valueOf(distance[i][j]));
			br.readLine();
				}
			}
			br.close();
			}
			catch(IOException e)
			{
				
			}
			//System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\");
			try
			{
			FileReader fw2 = new FileReader("facility.txt");
			BufferedReader br2 = new BufferedReader(fw2);
			for(i=0;i<FACILITYNUM;i++)
			{
				br2.readLine();
				br2.readLine();

				
			temp =br2.readLine();
			///people
			facilitypeople[i] = Double.parseDouble(temp);
	//		System.out.println(String.valueOf(facilitypeople[i]));
			br2.readLine();

			temp =br2.readLine();
			//area
			facilityarea[i] = Double.parseDouble(temp);

	//		System.out.println(String.valueOf(facilityarea[i]));
			br2.readLine();
			br2.readLine();
			br2.readLine();

			}
			br2.close();
			}
			catch(IOException e)
			{
				
			}

			try
			{
			FileReader fw3 = new FileReader("exit.txt");
			BufferedReader br3 = new BufferedReader(fw3);
			for(i=0;i<EXITNUM;i++)
			{
			br3.readLine();
			br3.readLine();
			temp = br3.readLine();
			exitwide[i] =Double.parseDouble(temp);
		//	System.out.println(String.valueOf(exitwide[i]));
			br3.readLine();
			temp = br3.readLine();
			exitarea[i]=Double.parseDouble(temp);
		//	System.out.println(String.valueOf(exitarea[i]));
			br3.readLine();

			}

			br3.close();
			}
			catch(IOException e)
			{
				
			}


			for(i=0;i<FACILITYNUM;i++)///solution condition setting false; 
			{
				solutioncheck[i]=false;
			}
			solve();
			
			for(i=0;i<FACILITYNUM;i++)
			{
				System.out.format("%d facility -> %d exit\n",i+1,solution[i]+1);
			}
			
		
		
	}
	
	public static void solve()
	{
		
		
	
		solvepeoplerate();
		improve();
	//	solvepeopledecrease();
	//	improve();
		
		
		
		
		
		
					
		
	
	}
	
	public static void solvepeoplerate()
	{
		System.out.println("greed solution start");
		int[] temp=new int[2];
		for(int i=0;i<FACILITYNUM;i++)
		{
		//	System.out.format("%d interation \n",i+1);
			temp =getminivalueindex();
			solution[temp[0]]=temp[1];//////temp[0] facilityindex temp[1] exit index
			exitpeople[temp[1]]+=facilitypeople[temp[0]];		
		}
		
		
		double totalvalue =gettotalvalue();
		double max=0.0;
		int maxindex=0;
		for(int i=0;i<EXITNUM;i++)
		{
			System.out.format("%d exit num: %f\n",i+1,exitpeople[i]);
		}
		
		
		for(int i=0;i<FACILITYNUM;i++)
		{
			//System.out.format("%d facility value : %f\n",i+1,getvalue(i));
			if(max<getvalue(i))
			{
				maxindex=i;
				max=(getvalue(i));
			}
		}
		
		System.out.format("total value : %f \n",totalvalue);
		System.out.format("%d facility %d exit maxvalue : %f\n",maxindex+1,solution[maxindex]+1,max);
		
		
	}
	
	public static void solvepeopledecrease()
	{
		System.out.println("other method decrease people num");
		for(int i=0;i<EXITNUM;i++)
		{
			exitpeople[i]=0;
		}
		
		double maxpeople=Double.MIN_VALUE;
		int maxindex2=0;
		for(int i=0;i<FACILITYNUM;i++)
		{
			
			for(int j=0;j<FACILITYNUM;j++)
			{
			
				if((maxpeople<facilitypeople[j])&&(solutioncheck[j]==true))
				{
					maxpeople=facilitypeople[j];
					maxindex2=j;
					
				}
				
				
			
			}
			
			maxpeople=Double.MIN_VALUE;
			solutioncheck[maxindex2]=false;
			
		
			int temp2=getminivalueindex(maxindex2);
			solution[maxindex2]=temp2;
			exitpeople[temp2]+=facilitypeople[maxindex2];
			
		}
		
		
		double totalvalue =gettotalvalue();
		double max=0.0;
		int maxindex=0;
		for(int i=0;i<EXITNUM;i++)
		{
			System.out.format("%d exit num: %f\n",i+1,exitpeople[i]);
		}
		
		
		for(int i=0;i<FACILITYNUM;i++)
		{
			//System.out.format("%d facility value : %f\n",i+1,getvalue(i));
			if(max<getvalue(i))
			{
				maxindex=i;
				max=(getvalue(i));
			}
		}
		
		System.out.format("total value : %f \n",totalvalue);
		System.out.format("%d facility %d exit maxvalue : %f\n",maxindex+1,solution[maxindex]+1,max);
		
		
	}
	public static void improve()
	{
		boolean loop=true;
		int index=0;
		int temp=0;
		int breaknum=0;
		int totalbreaknum=0;
		int changeindex=0;
		int save=0;
		double temp2;
		while(loop)
		{
		for(int j=0;j<FACILITYNUM;j++)
		{
			index =getmaxvalueindex();
			temp2=getvalue(index);
			//System.out.format("%d maxindex\n",index+1);
			double maxdiffer=Double.MIN_VALUE;
				
			for(int i=0;i<EXITNUM;i++)
			{
				exitpeople[solution[j]]-=facilitypeople[j];
				exitpeople[i]+=facilitypeople[j];
				save=solution[j];
				solution[j]=i;
				temp = getmaxvalueindex();
				double differ=temp2-getvalue(temp);
				//System.out.format("%d index %d -> %d differ : %f\n",j+1,save+1,i+1,differ);
				solution[j]=save;
				exitpeople[solution[j]]+=facilitypeople[j];
				exitpeople[i]-=facilitypeople[j];
				
				
				if(differ>0.0)
				{
				
					if(differ>maxdiffer)
					{
						maxdiffer=differ;
						changeindex=i;
					}
					
				}
				else
				{
					breaknum++;
					totalbreaknum++;
					
				}
				
			}
			
			if(breaknum==EXITNUM)
			{
				breaknum=0;
			}
			else
			{
				System.out.format("%d index %d ->%d is maxdiffer\n",j+1,save+1,changeindex+1);
				breaknum=0;
				exitpeople[solution[j]]-=facilitypeople[j];
				solution[j]=changeindex;
				exitpeople[changeindex]+=facilitypeople[j];
				
			}
			
			
			
		}
		
		if(totalbreaknum==FACILITYNUM*EXITNUM)
		{
			break;
		}
		else
		{
			totalbreaknum=0;
		}
		
		
		}
		
		

		double totalvalue =gettotalvalue();
		double max=0.0;
		int maxindex=0;
		for(int i=0;i<EXITNUM;i++)
		{
			System.out.format("%d exit num: %f\n",i+1,exitpeople[i]);
		}
		
		
		for(int i=0;i<FACILITYNUM;i++)
		{
			//System.out.format("%d facility value : %f\n",i+1,getvalue(i));
			if(max<getvalue(i))
			{
				maxindex=i;
				max=(getvalue(i));
			}
		}
		
		System.out.format("total value : %f \n",totalvalue);
		System.out.format("%d facility %d exit maxvalue : %f\n",maxindex+1,solution[maxindex]+1,max);
		
		
		
	}
	
	public static int getmaxvalueindex()
	{
		double maxvalue=Double.MIN_VALUE;
		int index=0;
		for(int i=0;i<FACILITYNUM;i++)
		{
			
			double temp =getvalue(i);
		//System.out.format("%d facility value : %f\n",i+1,temp);
			if(temp>maxvalue)
			{
				index=i;
				maxvalue=temp;
			}
			
		}
		
		
		return index;
		
	}
	
	public static int getminivalueindex(int facilityindex)
	{
		//int before=0;
		int returnindex=0;
		double minimum=Double.MAX_VALUE;
		for(int i=0;i<EXITNUM;i++)
		{
			//before=solution[i];
			solution[facilityindex]=i;
			exitpeople[i]+=facilitypeople[facilityindex];
			double temp=0;
	    temp = getvalue(facilityindex)/facilitypeople[facilityindex];
	   
	   // System.out.format("%d  facility %d  exit current value : %f \n",facilityindex+1,i+1,temp);
	    
	    //System.out.format("%d exitpeople :%f \n",i+1,exitpeople[i]);
	    
	    
		if((temp<minimum))
	    {
	    	returnindex=i;
	    	minimum=temp;
	    	
	    }
		
		exitpeople[i]=exitpeople[i]-facilitypeople[facilityindex];
	//	solution[i]=before;
		}
		return returnindex;
		
	}
	
	
	
	public static int[] getminivalueindex()
	{
		double minimum =Double.MAX_VALUE;
		int[] returnindex =new int[2];
		int facilityindex=0;
		int exitindex=0; 
		int before=0;
		for(int i=0;i<FACILITYNUM;i++)
		{
		
			for(int j=0;j<EXITNUM;j++)
			{
			
				before=solution[i];
				solution[i]=j;
				exitpeople[j]+=facilitypeople[i];
				double temp=0;
		    temp = getvalue(i)/facilitypeople[i];
		   if(solutioncheck[i]==false)
		    {
		    //System.out.format("%d  facility %d  exit current value : %f \n",i+1,j+1,temp);
		    
		  //  System.out.format("%d exitpeople :%f \n",j+1,exitpeople[j]);
		    }
		    
			if((temp<minimum)&&(solutioncheck[i]==false))
		    {
		    	facilityindex=i;
		    	exitindex=j;
		    	minimum=temp;
		    	
		    }
			exitpeople[j]=exitpeople[j]-facilitypeople[i];
			solution[i]=before;
			
			}
			
		}
		
		//System.out.format("%d facility  %d exit minimum\n",facilityindex+1,exitindex+1);
		solutioncheck[facilityindex]=true;
		returnindex[0]=facilityindex;
		returnindex[1]=exitindex; 
		return returnindex;
		
	}
	
	public static double getmaxvalueexit(int exit)
	{
		double maxvalue =Double.MIN_VALUE;
		for(int i=0;i<FACILITYNUM;i++)
		{
			
			if(solution[i]==exit)
			{
				if(maxvalue<getvalue(i))
				{
					maxvalue=getvalue(i);
				}
				
			}
			
			
		}
		return maxvalue;
	}
	
	public static double getvalue(int i) //facility i의 현재 값 구하기
	{
		double temp=0;
		temp =distance[i][solution[i]]*facilitypeople[i]/(1.129*facilityarea[i]) + exitpeople[solution[i]]/(1.129*exitwide[solution[i]]);
		   
		return temp;
	}
	public static double gettotalvalue()
	{
		double totalvalue=0;
		for(int i=0;i<FACILITYNUM;i++)
		{
			totalvalue +=getvalue(i);
			
		}
		
		return totalvalue;
	}
	
}