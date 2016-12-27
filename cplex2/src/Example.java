import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ilog.concert.*;
import ilog.cplex.*;
public class Example {
	final static int EXITNUM = 8;
	final static int FACILITYNUM = 53;
	final static int TOTALNUM = EXITNUM*FACILITYNUM;
public static void main(String[] args) throws FileNotFoundException {
try {
IloCplex cplex = new IloCplex();
double[] distance=new double[TOTALNUM];
double[] lb = new double[TOTALNUM];
double[] ub = new double[TOTALNUM];
//double[] exitpeople=new double[EXITNUM];
double[] exitwide = new double[EXITNUM];
double[] exitarea = new double[EXITNUM];
double[] facilityarea = new double[FACILITYNUM];
double[] facilitypeople=new double[FACILITYNUM];
int i=0;
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

distance[i*EXITNUM+j]=Double.parseDouble(temp);
System.out.println(String.valueOf(distance[i*EXITNUM+j]));
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
System.out.println(String.valueOf(facilitypeople[i]));
br2.readLine();

temp =br2.readLine();
//area
facilityarea[i] = Double.parseDouble(temp);

System.out.println(String.valueOf(facilityarea[i]));
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
System.out.println(String.valueOf(exitwide[i]));
br3.readLine();
temp = br3.readLine();
exitarea[i]=Double.parseDouble(temp);
System.out.println(String.valueOf(exitarea[i]));
br3.readLine();

}

br3.close();
}
catch(IOException e)
{
	
}




//double[] ub = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
//IloNumVar[] x = cplex.numVarArray(3, lb, ub);
IloIntVar[] x = cplex.boolVarArray(TOTALNUM);
IloNumVar[] t = cplex.numVarArray(TOTALNUM,lb,ub);
//double[] t = new double[TOTALNUM];

IloNumVar[] exitpeople = cplex.numVarArray(EXITNUM, lb, ub);
double[] id1 = new double[TOTALNUM];

//loNumEx
IloLinearNumExpr expr = cplex.linearNumExpr();
//cplex.addEq(expr, 0);

for(i=0;i<TOTALNUM;i++)
{
	id1[i]=0;
	//t[i]=0;
}////////initialize 0


cplex.addMinimize(cplex.scalProd(x, t));///목적식

//////////// 조건식 1
for(i=0;i<FACILITYNUM;i++)
{
	for(j=0;j<EXITNUM;j++)
	{
		id1[i*EXITNUM+j]=1;
	}
   // cplex.addEq(cplex.scalProd(x, id1), 1);/// 하나의 facility는 한개의 exit만 가능
    
    for(j=0;j<EXITNUM;j++)
    {
    	id1[i*EXITNUM+j]=0;
    }
}


/////////// 조건식 2
for(i=0;i<EXITNUM;i++)
{
	
	for(j=0;j<FACILITYNUM;j++)
	{
		expr.addTerm(facilitypeople[j], x[j*EXITNUM+i]);
		
		
	}

	cplex.addEq(cplex.sum(exitpeople[i],cplex.negative(expr)), 0);

	expr.clear();
}


//////////////////////// 조건식 3
for(i=0;i<FACILITYNUM;i++)
{
	
	for(j=0;j<EXITNUM;j++)
	{
	//	cplex.addEq(cplex.sum(t[i*EXITNUM+j],cplex.negative(cplex.prod(((double)1/(1.129/**facilityarea[i]*/)),exitpeople[j]))),(/*distance[i*EXITNUM+j]*facilitypeople[i]/(facilityarea[i]**/1.129/*)*/));

	}
	
}





//double[] objvals = {1.0, -2.0, 3.0};
//cplex.addMinimize(cplex.scalProd(x, y));
//cplex.addLe(y[0],numexit[0]);


//cplex.addMaximize(cplex.scalProd(x, objvals));
/*cplex.addLe(cplex.sum(cplex.prod(-1.0, x[0]),
cplex.prod( 1.0, x[1]),
cplex.prod( 1.0, x[2])), 20.0);
cplex.addLe(cplex.sum(cplex.prod( 1.0, x[0]),
cplex.prod(-3.0, x[1]),
cplex.prod( 1.0, x[2])), 30.0);
*/

if ( cplex.solve() ) {
	
System.out.println("Solution status = " + cplex.getStatus());
System.out.println("Solution value = " + cplex.getObjValue());
double[] val = cplex.getValues(x);
int ncols = cplex.getNcols();
for (j = 0; j < ncols; ++j)
System.out.println("Column: " + j + " Value = " + val[j]);
}
else
{
	//cplex.
	System.out.println("cplex objective : " +cplex.getObjective());
	
	System.out.println("error");
}

cplex.end();

}
catch (IloException e) {
System.err.println("Concert exception '" + e + "' caught");
}

}

}

