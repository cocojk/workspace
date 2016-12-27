import java.math.*;
class test
{
	
	public static void main(String[] args)
	{
		int[] exitwide = new int[4];
		int[] facilitynum = new int[2];
		int[] exitnum = new int[4];
		
		facilitynum[0]=50;
		facilitynum[1]=100;
	
		int[][] time = new int[2][4];
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<4;j++)
			{
				time[i][j] = (int)(Math.random()*200); 
				System.out.format("time[%d][%d] : %d \n",i,j,time[i][j]);
			}
		}
		
		
		
		for(int i=0;i<4;i++)
		{
		exitnum[i]=0;
			exitwide[i]=(int)(Math.random()*10)+5;
			System.out.format("exit[%d] wide : %d \n",i,exitwide[i]);
		}
		double minimum = Double.MAX_VALUE;
		int[] index = new int[2];
		index[0]=0;
		index[1]=1;
		
		double totalvalue=0;
		for(int i=0;i<4;i++)
		{
		for(int j=0;j<4;j++)
		{
			double temp=0;
			totalvalue += time[0][i]+time[1][j];
			exitnum[i] +=facilitynum[0];
			exitnum[j] +=facilitynum[1];
			
			for(int k=0;k<4;k++)
			{
			
				temp += (double)exitnum[k]/(double)exitwide[k]; 
				
			}
			
			totalvalue += temp;
			
			exitnum[i] =0;
			exitnum[j] =0;
			
			if(minimum >totalvalue)
				{
				minimum = totalvalue;
				index[0]=i;
				index[1]=j;
				}
		
			totalvalue=0;
		}
			
		}
		
		System.out.format("minimum value : %f index : %d %d \n", minimum,index[0],index[1]);
		
		///////greed approach////
		
		
		
		
		
	
}
	
}