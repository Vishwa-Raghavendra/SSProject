import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class OnePassAssembler
{

    static class references
    {
        String key;
        String miniObj;
        int nextAddress;
        String currentAddress;

        references(String key, String miniObj, int nextAddress, String currentAddress)
        {
            this.key = key;
            this.miniObj = miniObj;
            this.nextAddress = nextAddress;
            this.currentAddress = currentAddress;
        }
    }


    private static  Map<String,String> opcodes = new HashMap<>();
    private static Map<String,String> objectCodes = new HashMap<>();
    private static Map<String,String> SymTab = new HashMap<>();
    private static List<references> forwardReferences = new ArrayList<>();

    static int nextAdd = 0;
    static int previousAddress =0;


    public static void main(String[] args) throws Exception
    {

        File inputProgramFile = new File("D:\\InputProgam2.txt");
        File opcodeFile  = new File("D:\\opcode.txt");


        //Read Opcodes from the files
        BufferedReader reader = new BufferedReader(new FileReader(opcodeFile));
        String opcodeLine;
        while ((opcodeLine=reader.readLine())!=null)
        {
            StringTokenizer stringTokenizer = new StringTokenizer(opcodeLine);
            opcodes.put(stringTokenizer.nextToken(),stringTokenizer.nextToken());
        }



        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputProgramFile));
        String inputProgramLine;

        int startAddress =0;

        String headerRecord = "";

        System.out.println("OnePass Assembler");
        System.out.println("File Contents and Addresses");
        System.out.println("==========================================================================\n\n");

        while ((inputProgramLine=bufferedReader.readLine())!=null)
        {

            StringTokenizer stringTokenizer = new StringTokenizer(inputProgramLine);

            int length = stringTokenizer.countTokens();
            String label="",opcode="",operand="";

            if(length==2)
            {
                label =" ";
                opcode = stringTokenizer.nextToken();
                operand = stringTokenizer.nextToken();

            }
            else if(length==3)
            {
                label = stringTokenizer.nextToken();
                opcode = stringTokenizer.nextToken();
                operand = stringTokenizer.nextToken();
            }

            if(opcode.equals("START"))
            {
                headerRecord = "H^"+label+"^"+operand+"^";
                nextAdd = Integer.parseInt(operand,16);
                startAddress = nextAdd;
                System.out.println(Integer.toHexString(nextAdd)+": "+inputProgramLine);
            }
            else if(opcode.equals("END"))
            {
                System.out.println(Integer.toHexString(nextAdd)+":\t\t"+opcode+"\t\t"+operand);
            }
            else
            {
                if(label.equals(" "))
                {
                    String miniObj = getObjectCode(opcode,operand);
                    System.out.println(Integer.toHexString(nextAdd)+":\t\t"+opcode+"\t\t"+operand);


                    setAddress(nextAdd,nextAdd + Data.getFormat(opcode));
                    //nextAdd = nextAdd + 3;

                    if(!SymTab.containsKey(operand)||SymTab.get(operand).equals("UnKnown"))
                    {
                        SymTab.put(operand,"UnKnown");
                        forwardReferences.add(new references(operand,miniObj,nextAdd,previousAddress+""));
                        objectCodes.put(previousAddress+"",miniObj);
                    }
                    else
                    {
                        int add =  Integer.parseInt(SymTab.get(operand));
                        String hex = Integer.toHexString(add-nextAdd);
                        objectCodes.put(previousAddress+"",miniObj+hex.substring(hex.length()-3));
                    }
                }
                else
                {
                    System.out.println(Integer.toHexString(nextAdd)+": "+label+"\t"+opcode+"\t"+operand);

                    if(SymTab.containsKey(label))
                    {
                        if(SymTab.get(label).length()==7)
                            SymTab.put(label,nextAdd+"");

                        if(opcode.equals("RESW"))
                            setAddress(nextAdd,nextAdd+= Integer.parseInt(operand)*3);
                           // nextAdd+= Integer.parseInt(operand)*3;
                        else if ((opcode.equals("WORD")))
                            setAddress(nextAdd,nextAdd+=3);
                            //nextAdd+=3;

                        for (references e: forwardReferences)
                        {
                            if(e.key.equals(label))
                            {
                                int add =  Integer.parseInt(SymTab.get(label))-e.nextAddress;
                                String adds = add+"";

                                if(adds.length()==1)
                                    adds="00"+add;
                                else if(adds.length()==2)
                                    adds="0"+add;

                                //opcodes.put(e.currentAddress,e.miniObj+adds);
                                objectCodes.put(e.currentAddress,e.miniObj+adds);
                                //System.out.println(e.miniObj+adds);

                            }
                        }
                    }
                    else
                    {
                        SymTab.put(label,nextAdd+"");

                        setAddress(nextAdd,nextAdd = nextAdd + Data.getFormat(opcode));

                        if(!(Data.getFormat(opcode)==3))
                        {
                            if(Data.getFormat(opcode)==2)
                            {
                                objectCodes.put(previousAddress+"",opcodes.get(opcode)+Data.registers.get(operand)+"0");
                            }
                        }
                        else
                        {
                            String miniObj = getObjectCode(opcode,operand);
                            SymTab.put(operand,"UnKnown");
                            forwardReferences.add(new references(operand,miniObj,nextAdd,previousAddress+""));
                            objectCodes.put(previousAddress+"",miniObj);
                        }

                    }
                }
            }
        }

        int size = previousAddress-startAddress;

        headerRecord+=Integer.toHexString(size);


        ArrayList<String> add = new ArrayList<>();
        TreeMap<String,String> gg = new TreeMap<>(objectCodes);

        for(Map.Entry<String,String> e:gg.entrySet())
        {
            add.add(Integer.toHexString(Integer.parseInt(e.getKey())));
        }

        System.out.println("\n\nSymTab Contains");
        System.out.println("==========================================================================");
        for (Map.Entry<String ,String> e:SymTab.entrySet())
        {
            System.out.println(e.getKey()+"\t-->\t"+Integer.toHexString(Integer.parseInt(e.getValue())));
        }
        System.out.println("==========================================================================\n\n");

        System.out.println("\nHTE File");
        System.out.println("==========================================================================\n");

        System.out.println(headerRecord);

        boolean hh = true;
        for(int i=0;i<add.size();i++)
        {
            if((i)%5==0&&i!=0)
            {
                hh=true;
                System.out.println();
            }

            if(hh)
            {
                int sizeDiff;
                if(i+5>add.size())
                    sizeDiff = Integer.parseInt(add.get(add.size()-1),16)-Integer.parseInt(add.get(i),16);
                else
                    sizeDiff = Integer.parseInt(add.get(i+4),16)-Integer.parseInt(add.get(i),16);

                System.out.print("T^00"+add.get(i)+"^"+Integer.toHexString(sizeDiff));
                hh=false;
            }

            System.out.print("^"+objectCodes.get(Integer.parseInt(add.get(i),16)+""));
        }
        System.out.print("\nE^"+add.get(0));
        System.out.print("\n==========================================================================\n\n");
    }

    private static String getObjectCode(String opcode,String operand)
    {
        String opcodeOriginal = opcode;
        if(opcode.contains("+"))
           opcode=  opcode.replace("+","");

        String opnum = opcodes.get(opcode);

        String ObjectCode = Data.numsToBinary.get(opnum.toCharArray()[0]+"")+ Data.numsToBinary.get(opnum.toCharArray()[1]+"");


        ObjectCode += getNIXBPE(opcodeOriginal,operand);

        ObjectCode = ObjectCode.substring(0,6)+ObjectCode.substring(8);

        String miniObj = Data.binaryToNums.get(ObjectCode.substring(0,4))+Data.binaryToNums.get(ObjectCode.substring(4,8))+Data.binaryToNums.get(ObjectCode.substring(8));

        return miniObj;
    }

    static void setAddress(int oldValue,int newValue)
    {
        previousAddress = oldValue;
        nextAdd = newValue;
    }

     private static String getNIXBPE(String opcode,String operand)
    {
        String n ="1",i="1",x="0",b="0",p="1",e="0";

        if(Data.getFormat(opcode)==4)
            e="1";

        if(operand.contains("#"))
            n="0";
        else if(operand.contains("@"))
            i="0";

        return n+i+x+b+p+e;
    }
}
