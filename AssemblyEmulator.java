/**
 *
 * @author Swapnil
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.TreeMap;
public class AssemblyEmulator {
    
    TreeMap labels=new TreeMap();
    int[] reg=new int[16];
    int pc=0;
    //byte[] mem=new byte[4096];
	int[] mem=new int[1024];
    boolean E=false,GT=false;
    int ni(int num){
        return (int)((short)num);
    }
    int ui(int num){
        return (int)((char)num);
    }
    int hi(int num){
        return num<<16;
    }
    int getInt(String str, AssemblyEmulator ab){
        String temp=str.substring(0,str.indexOf("["));
        //System.out.println(temp);
        String temp2=str.substring(str.indexOf("["));
        
        temp2=temp2.substring(1,temp2.length()-1);
     //   System.out.println(temp2);
        if (temp.length()>0){
        if (temp.startsWith("0x"))
            return ab.reg[Integer.parseInt(temp2.substring(1))]+Integer.parseInt(temp.substring(2),16);
        else
            return ab.reg[Integer.parseInt(temp2.substring(1))]+Integer.parseInt(temp);
        }
        else return ab.reg[Integer.parseInt(temp2.substring(1))];
    }
    /**
     * @param args the command line arguments
     */
    String converthex(String num){
        String hex="",temp;
        int x;
        for(int i=0;i<8;i++){
            temp=num.substring(i*4,(i+1)*4);
            x=Integer.parseInt(temp,2);
            
                switch(x){
                    case 10:
                        hex+="a";
                        break;
                    case 11:
                        hex+="b";
                        break;
                    case 12:
                        hex+="c";
                        break;
                    case 13:
                        hex+="d";
                        break;
                    case 14:
                        hex+="e";
                        break;     
                    case 15:
                        hex+="f";
                        break;
                    default:
                        hex+=x;
                        break;
                }
               }
        return "0x"+hex;
    }
   String convertbinary(int num,int i){
        String ret="";
        while(num>0){
            ret=num % 2+ret;
            num/=2;
           }
        while(ret.length()!=i){
            ret="0"+ret;
        }
        return ret;
    }
    public static void main(String[] args) {
        // TODO code application logic here
        AssemblyEmulator ae=new AssemblyEmulator();
		String buf="",temp="";
        try {
        Scanner sc = new java.util.Scanner (new File(args[0]));
       //Scanner sc=new java.util.Scanner(new File("sample.s"));
            int j=0;
        while (sc.hasNext ()) {
            temp=(sc.nextLine ()).replaceAll("\\s+", " ");
            temp=temp.trim();
            
            j=temp.indexOf("x");
            if (j!=-1) temp=temp.substring(0,j+1)+temp.substring(j+1).replaceAll("\\s","");
            buf=buf+temp+"\n";
        }
        sc.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        buf=buf.toLowerCase();
        buf=buf.replaceAll(":\\s+",":\n");
        buf=buf.replaceAll(",\\s+"," ");
        buf=buf.replaceAll(","," ");
        buf=buf.replaceAll("pc", "r13");
        buf=buf.replaceAll("sp", "r14");
        buf=buf.replaceAll("ra", "r15");
        while(true){
            if (buf.endsWith("\n")) buf=buf.substring(0,buf.length()-1);
            else break;
        }
        
//        System.out.println(Integer.parseInt("1023"));
        String[] prog=buf.split("\n");
        String[] elem;
      //  int labelCount=buf.length()-buf.replaceAll(":","").length();
        //System.out.println(labelCount);
        ae.reg[14]=1023;
        //System.out.println(ae.reg[14]);
        for(int l=0;l<prog.length;l++){
            temp=prog[l];
            if (temp.endsWith(":") && !(temp.equals(".encode:"))){
                    //System.out.println(temp);
                    ae.labels.put(temp.substring(0,temp.length()-1),(l<<2)+4);
                   }
        }
 //       System.out.println(ae.ni(Integer.parseInt("0xFFFF".substring(2),16)));
        //System.out.println((int)(short)(0xFFFF));
        ae.pc=(int)ae.labels.get(".main");
        //System.out.println(ae.getInt("4[r13]",ae));
        boolean ahd=true;
        int i=0,n=0;
        while(ae.pc<(prog.length<<2)) {
            //System.out.println(prog.length);
            temp=prog[ae.pc>>2];
          // System.out.println(ae.pc>>2);
		   //System.out.println(temp);
		   temp=temp.replaceAll("\\s+", " ");
            elem=temp.split(" ");
            ahd=true;
            if (elem[0].equals(".encode:")){
                ae.pc+=4;
                temp=prog[ae.pc>>2];
                temp=temp.replaceAll("\\s+", " ");
                //System.out.println(temp);
                elem=temp.split(" ");
                temp="";
                if (elem[0].equals("nop")){
                    temp+="01101";
                    while(temp.length()!=32){
                        temp+="0";
                    }
                }
                if (elem[0].contains("cmp")){
                    temp+="00101";
                    if (elem[2].startsWith("r")) temp+="0";
                    else temp+="1";
                    //temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+="0000";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    if (elem[2].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[2]), 16);
                    }
                }
                if (elem[0].contains("mov")){
                    temp+="01001";
                    if (elem[2].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    //temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    temp+="0000";
                    if (elem[2].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[2]), 16);
                    }
                }
                if (elem[0].contains("not")){
                    temp+="01000";
                    if (elem[2].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    //temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    temp+="0000";
                    if (elem[2].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[2]), 16);
                    }
                }
                if (elem[0].contains("add")){
                    temp+="00000";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("sub")){
                    temp+="00001";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("mul")){
                    temp+="00010";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("div")){
                    temp+="00011";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("mod")){
                    temp+="00100";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("and")){
                    temp+="00110";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("or")){
                    temp+="00111";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("lsl")){
                    temp+="01010";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("lsr")){
                    temp+="01011";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].contains("asr")){
                    temp+="01100";
                    if (elem[3].startsWith("r")) temp+="0";
                    else temp+="1";
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(1)),4);
                    if (elem[3].startsWith("r")){
                        temp+=ae.convertbinary(Integer.parseInt(elem[3].substring(1)),4);
                        while(temp.length()!=32)
                            temp+="0";
                        }
                    else {
                      if (elem[0].endsWith("u")) temp+="01";
                      else if (elem[0].endsWith("h")) temp+="10";
                      else temp+="00";
                      temp+=ae.convertbinary(Integer.parseInt(elem[3]), 16);
                    }
                }
                if (elem[0].equals("ld")){
                    temp+="011101";
                    
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(elem[2].indexOf("[")+2,elem[2].indexOf("]"))),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(0,elem[2].indexOf("["))), 18);
                 }
                if (elem[0].equals("st")){
                    temp+="011111";
                    
                    temp+=ae.convertbinary(Integer.parseInt(elem[1].substring(1)),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(elem[2].indexOf("[")+2,elem[2].indexOf("]"))),4);
                    temp+=ae.convertbinary(Integer.parseInt(elem[2].substring(0,elem[2].indexOf("["))), 18);
                 }
                //System.out.println(temp);
                System.out.println(ae.converthex(temp));
            }
            if (elem[0].equals("b")){
                    ae.pc=(int)ae.labels.get(elem[1].replaceAll(":", ""));
                    ahd=false;
            }
                if (elem[0].equals("beq")){
                    if (ae.E){
                    ae.pc=(int)ae.labels.get(elem[1].replaceAll(":", ""));
                    ahd=false;}
        }
               if(elem[0].equals("bgt")){ 
                    if (ae.GT){
                    ae.pc=(int)ae.labels.get(elem[1].replaceAll(":", ""));
                    ahd=false;}
               }
               if(elem[0].equals("call")){
                    ae.reg[15]=ae.pc+4;
                    ae.pc=(int)ae.labels.get(elem[1]);
                    ahd=false;
               }
                if(elem[0].equals("ret")){
                    ae.pc=ae.reg[15];
                    ahd=false;
                }
                if(elem[0].equals("ld")){
                    i=ae.getInt(elem[2], ae);
                    
                    ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.mem[i];
					//((int)ae.mem[i]+(int)(ae.mem[i+1]<<8)+(int)(ae.mem[i+2]<<16)+(int)(ae.mem[i+3]<<24));
                  //  System.out.println(ae.reg[Integer.parseInt(elem[1].substring(1))]);
                }
                if(elem[0].equals("st")){
                    i=ae.getInt(elem[2], ae);
                    n=ae.reg[Integer.parseInt(elem[1].substring(1))];
//                    ae.mem[i]=(byte)n;
  //                  ae.mem[i+1]=(byte)(n>>>8);
    //                ae.mem[i+2]=(byte)(n>>>16);
      //              ae.mem[i+3]=(byte)(n>>>24);
					ae.mem[i]=n;
                }
                if(elem[0].equals("add")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("addu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("addh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]+ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("sub")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("subu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("subh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]-ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("mul")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("mulu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("mulh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]*ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("div")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("divu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("divh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]/ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("mod")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("modu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("modh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]%ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("or")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.ni(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("oru")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("orh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]|ae.hi(Integer.parseInt(elem[3]));
                }
               if(elem[0].equals("and")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.ni(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.ni(Integer.parseInt(elem[3]));
               }
                if(elem[0].equals("andu")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.ui(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.ui(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("andh")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.hi(Integer.parseInt(elem[3].substring(2),16));
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]&ae.hi(Integer.parseInt(elem[3]));
                }
                if(elem[0].equals("lsl")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]<<ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]<<Integer.parseInt(elem[3].substring(2),16);
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]<<Integer.parseInt(elem[3]);
                }
                if(elem[0].equals("lsr")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>>ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>>Integer.parseInt(elem[3].substring(2),16);
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>>Integer.parseInt(elem[3]);
                }
                if(elem[0].equals("asr")){
                    if (elem[3].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>ae.reg[Integer.parseInt(elem[3].substring(1))];
                    }
                    else if (elem[3].startsWith("0x"))
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>Integer.parseInt(elem[3].substring(2),16);
                    else 
                        ae.reg[Integer.parseInt(elem[1].substring(1))]=ae.reg[Integer.parseInt(elem[2].substring(1))]>>Integer.parseInt(elem[3]);
                }
                if(elem[0].equals("nop")){}
                if(elem[0].equals("cmp")){
                    if (elem[2].startsWith("r")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.reg[Integer.parseInt(elem[2].substring(1))]);
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.ni(Integer.parseInt(elem[2].substring(2),16)));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.ni(Integer.parseInt(elem[2].substring(2),16)));
                    }
                    else {
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.ni(Integer.parseInt(elem[2])));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.ni(Integer.parseInt(elem[2])));
                    }
                }
                if(elem[0].equals("cmpu")){
                    if (elem[2].startsWith("r")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.reg[Integer.parseInt(elem[2].substring(1))]);
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.ui(Integer.parseInt(elem[2].substring(2),16)));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.ui(Integer.parseInt(elem[2].substring(2),16)));
                    }
                    else {
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.ui(Integer.parseInt(elem[2])));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.ui(Integer.parseInt(elem[2])));
                    }
                }
                if(elem[0].equals("cmph")){
                    if (elem[2].startsWith("r")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.reg[Integer.parseInt(elem[2].substring(1))]);
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.hi(Integer.parseInt(elem[2].substring(2),16)));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.hi(Integer.parseInt(elem[2].substring(2),16)));
                    }
                    else {
                        ae.E=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]==ae.hi(Integer.parseInt(elem[2])));
                        ae.GT=(ae.reg[(Integer.parseInt(elem[1].substring(1)))]>ae.hi(Integer.parseInt(elem[2])));
                    }
                }
                if(elem[0].equals("not")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.ni(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.ni(Integer.parseInt(elem[2]));
                }
                if(elem[0].equals("notu")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.ui(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.ui(Integer.parseInt(elem[2]));
                }
                 if(elem[0].equals("noth")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.hi(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=~ae.hi(Integer.parseInt(elem[2]));
                 }
                 if(elem[0].equals("mov")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.ni(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.ni(Integer.parseInt(elem[2]));
                 }
                 if(elem[0].equals("movu")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.ui(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.ui(Integer.parseInt(elem[2]));
                 }
                 if(elem[0].equals("movh")){
                    if (elem[2].startsWith("r")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=(ae.reg[Integer.parseInt(elem[2].substring(1))]);
                    }
                    else if (elem[2].startsWith("0x")){
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.hi(Integer.parseInt(elem[2].substring(2),16));
                    }
                    else
                        ae.reg[(Integer.parseInt(elem[1].substring(1)))]=ae.hi(Integer.parseInt(elem[2]));
                 }
                 if(elem[0].equals(".print")){
                     System.out.println(ae.reg[Integer.parseInt(elem[1].substring(1))]);
                 }
                 
            
                if (ahd) ae.pc+=4;
        }
        }
    
}