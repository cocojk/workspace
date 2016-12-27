import java.util.BitSet;

public class test {
  public static void main(String[] argv) throws Exception {
	  String ggg="hell";
	  //byte[] ab =new byte[1];
	  //byte[] ab =ggg.getBytes(); 
	  //BitSet temp3=;
	  //temp3.set(0, 1);
	  //
	  //System.out.println(ab[0]);
	  //BitSet temp2 = fromByteArray();
	  //byte[] abc =temp3.toByteArray(); /// 순서 역행
	  //System.out.println(temp);
	  
	  //BitSet temp6 =fromByteArray(ab);
	  //byte[] cc=temp6.toByteArray();//순서역행
	  //String gggg = new String(cc);
	  //System.out.println(gggg);

	  BitSet temp=new BitSet();
	  temp.set(0,5);
	  temp.set(6,11);
	  byte[] temp2 =temp.toByteArray();
	  int length=temp2.length;
	  System.out.println(length);
	  byte[] temp3 =new byte[length];
	  for(int i=0;i<length;i++)
	  { 
			temp3[i]=temp2[length-1-i];
				 
			
	  }
	  byte[] temp4 =setbitstuffing(temp3);
	  System.out.println(temp4.length);
	  BitSet temp5= fromByteArray(temp4);
	  String temp6="";
	  for(int i=0;i<temp4.length*8;i++)
	  {
		  if(temp5.get(i)==true)
		  {
			  temp6 = "1" +temp6;
		  }
		  else
		  {
			  temp6 = "0" +temp6;
		  }
	  }
	  System.out.println(temp6);
	  
	  byte[] temp7 =clearbitstuffing(temp4);
	  BitSet temp8 =fromByteArray(temp7);
	  String temp9="";
	  for(int i=0;i<temp7.length*8;i++)
	  {
		  if(temp8.get(i)==true)
		  {
			  temp9 = "1" +temp9;
		  }
		  else
		  {
			  temp9 = "0" +temp9;
		  }
	  }
	  System.out.println(temp9);
	  
	  
	  // System.out.println(ab.length);
	//  System.out.println(abc.length);
	 // System.out.println(ab[0]);
	  //System.out.println(abc[0]);
	  
	  //System.out.println(temp2.length());
	 /* for(int i=0;i<ab.length*8;i++)
	  {
		  if(temp2.get(i)==true)
		  {
			  temp = "1"+temp;
		  }
		  else
		  {
			  temp = "0"+temp;
		  }
	  }*/
	//  byte[] temp3 = bits.toByteArray();
	 // System.out.println(temp3.length);
	  //System.out.println(new String(temp3));
	  
	//  System.out.println(temp);
    // System.out.println(fromByteArray(ab));
  }

  // Returns a bitset containing the values in bytes.
  public static BitSet fromByteArray(byte[] bytes) {
    BitSet bits = new BitSet(bytes.length*8);
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
        bits.set(i);
       // System.out.println("dsd");
      }
      
    }
    return bits;
  }
  
  public static byte[] setbitstuffing(byte[] bytes)///flag 포함 시키면 x
  {
	  BitSet bits = new BitSet();
	  bits =fromByteArray(bytes);
	  int check=0;
	  int index=0;
	  for(int i=0;i<bytes.length*8;i++)
	  {
		  if(bits.get(index)==true)
		  {
			  check++;
			  index++;
		  }
		  else
		  {
			  check=0;
			  index++;
		  }
		  
		  if(check==5)
		  {
			   BitSet temp =bits.get(index,bytes.length*8);
			   bits.set(index,false);
			   for(int j=0;j<(bytes.length*8-index+1);j++)
			   {
			   
				   bits.set(index+1+j,temp.get(j));
				   
			   }
			   
			   index++;
			   check=0;
			   
		  }
	  }
	  
	 byte[] temp2 = bits.toByteArray();
	 byte[] temp3 = changebyteorder(temp2);
	 
	 
	  return temp3;
  }
  
  public static byte[] clearbitstuffing(byte[] bytes)
  {
	  BitSet bits = new BitSet();
	  bits =fromByteArray(bytes);
	  int check=0;
	  int index=0;
	  for(int i=0;i<bytes.length*8;i++)
	  {
		  if(bits.get(index)==true)
		  {
			  check++;
			  index++;
		  }
		  else
		  {
			  check=0;
			  index++;
		  }
		  
		  if(check==5)
		  {
			   BitSet temp =bits.get(index+1,bytes.length*8);
			   //bits.set(index,false);
			   for(int j=0;j<(bytes.length*8-index);j++)
			   {
			   
				   bits.set(index+j,temp.get(j));
				   
			   }
			   
			   index++;
			   check=0;
			   
		  }
	  }
	  
	 byte[] temp2 = bits.toByteArray();
	 byte[] temp3 = changebyteorder(temp2);
	 
	  return temp3;
  }

  public static byte[] changebyteorder(byte[] bytes)
  {
	  byte[] temp=new byte[bytes.length];
	  for(int i=0;i<bytes.length;i++)
	  {
		  temp[i]=bytes[bytes.length-i-1];
	  }
	  
	  return temp;
  }
  


}