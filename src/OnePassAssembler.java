import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class OnePassAssembler
{
    static class statements
    {
        String key;
        String miniObj;
        int Add;
        String stAdd;

        public statements(String key, String miniObj, int add, String stAdd) {
            this.key = key;
            this.miniObj = miniObj;
            Add = add;
            this.stAdd = stAdd;
        }

        public String getStAdd() {
            return stAdd;
        }

        public void setStAdd(String stAdd) {
            this.stAdd = stAdd;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getMiniObj() {
            return miniObj;
        }

        public void setMiniObj(String miniObj) {
            this.miniObj = miniObj;
        }

        public int getAdd() {
            return Add;
        }

        public void setAdd(int add) {
            Add = add;
        }
    }
    static  Map<String,String> opcodes = new HashMap<>();
    static Map<String,String> numsToBinary = new HashMap<>();
    static Map<String,String> objectCodes = new HashMap<>();
    static List<statements> fileStatemnts = new ArrayList<>();
    static Map<String,String> SymTab = new HashMap<>();
    static Map<String,String> binaryToNums = new HashMap<>();


    static  void numsToBin()
    {
        binaryToNums.put("0000","0");
        binaryToNums.put("0001","1");
        binaryToNums.put("0010","2");
        binaryToNums.put("0011","3");
        binaryToNums.put("0100","4");
        binaryToNums.put("0101","5");
        binaryToNums.put("0110","6");
        binaryToNums.put("0111","7");
        binaryToNums.put("1000","8");
        binaryToNums.put("1001","9");
    }
    static void initializeMap()
    {
        numsToBinary.put("0","0000");
        numsToBinary.put("1","0001");
        numsToBinary.put("2","0010");
        numsToBinary.put("3","0011");
        numsToBinary.put("4","0100");
        numsToBinary.put("5","0101");
        numsToBinary.put("6","0110");
        numsToBinary.put("7","0111");
        numsToBinary.put("8","1000");
        numsToBinary.put("9","1001");
    }
    public static void main(String[] args) throws Exception
    {

        initializeMap();
        numsToBin();

        File file = new File("D:\\InputProgam.txt");
        File opcodeFile  = new File("D:\\opcode.txt");

        BufferedReader ops = new BufferedReader(new FileReader(opcodeFile));

        String line;
        while ((line=ops.readLine())!=null)
        {
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            opcodes.put(stringTokenizer.nextToken(),stringTokenizer.nextToken());
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String st;

        Integer  nextAdd = 0;
        Integer startAddress =0;
        String headerRecord = "";

        System.out.println("OnePass Assembler");
        System.out.println("File Contains and Addresses");
        System.out.println("==========================================================================\n\n");
        while ((st=bufferedReader.readLine())!=null)
        {


            StringTokenizer stringTokenizer = new StringTokenizer(st);

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
                startAddress = Integer.parseInt(operand,16);
                System.out.println(Integer.toHexString(nextAdd)+": "+st);
            }
            else
            {
                if(label.equals(" "))
                {
                    String opnum = opcodes.get(opcode);

                    String ObjectCode  = "";

                    ObjectCode = numsToBinary.get(opnum.toCharArray()[0]+"")+ numsToBinary.get(opnum.toCharArray()[1]+"");
                    //System.out.println(ObjectCode);

                    ObjectCode += "110010";

                    ObjectCode = ObjectCode.substring(0,6)+ObjectCode.substring(8);

                    String miniObj = binaryToNums.get(ObjectCode.substring(0,4))+binaryToNums.get(ObjectCode.substring(4,8))+binaryToNums.get(ObjectCode.substring(8));
                    //System.out.println(miniObj);

                    System.out.println(Integer.toHexString(nextAdd)+":\t\t"+opcode+"\t\t"+operand);
                    //allAddress.add(Integer.toHexString(nextAdd));

                    nextAdd = nextAdd + 3;

                    if(!SymTab.containsKey(operand)||SymTab.get(operand).equals("UnKnown"))
                    {SymTab.put(operand,"UnKnown");
                        fileStatemnts.add(new statements(operand,miniObj,nextAdd,nextAdd-3+""));
                        objectCodes.put(nextAdd-3+"",miniObj);
                    }
                    else
                    {
                        int add =  Integer.parseInt(SymTab.get(operand));
                        String hex = Integer.toHexString(add-nextAdd);
                        objectCodes.put(nextAdd-3+"",miniObj+hex.substring(hex.length()-3));
                        //System.out.println(miniObj+hex.substring(hex.length()-3));
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
                            nextAdd+= Integer.parseInt(operand)*3;
                        else if ((opcode.equals("WORD")))
                            nextAdd+=3;

                        for (statements e:fileStatemnts)
                        {
                            if(e.key.equals(label))
                            {
                                int add =  Integer.parseInt(SymTab.get(label))-e.Add;
                                String adds = add+"";

                                if(adds.length()==1)
                                    adds="00"+add;
                                else if(adds.length()==2)
                                    adds="0"+add;

                                //opcodes.put(e.stAdd,e.miniObj+adds);
                                objectCodes.put(e.stAdd,e.miniObj+adds);
                                //System.out.println(e.miniObj+adds);

                            }
                        }
                    }
                    else
                    {
                        SymTab.put(label,nextAdd+"");

                        String opnum = opcodes.get(opcode);
                        String ObjectCode  = "";

                        ObjectCode = numsToBinary.get(opnum.toCharArray()[0]+"")+ numsToBinary.get(opnum.toCharArray()[1]+"");
                        //System.out.println(ObjectCode);

                        ObjectCode += "110010";

                        ObjectCode = ObjectCode.substring(0,6)+ObjectCode.substring(8);

                        String miniObj = binaryToNums.get(ObjectCode.substring(0,4))+binaryToNums.get(ObjectCode.substring(4,8))+binaryToNums.get(ObjectCode.substring(8));
                        //System.out.println(miniObj);

                        //System.out.println(nextAdd+":\t\t"+opcode+"\t\t"+operand);

                        nextAdd = nextAdd + 3;

                        SymTab.put(operand,"UnKnown");
                        fileStatemnts.add(new statements(operand,miniObj,nextAdd,nextAdd-3+""));
                        objectCodes.put(nextAdd-3+"",miniObj);
                    }
                }


            }
        }

        int size = (nextAdd-3)-startAddress;

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
                int sizeDiff =0;
                if(i+5>add.size())
                    sizeDiff = Integer.parseInt(add.get(add.size()-1),16)-Integer.parseInt(add.get(i),16);
                else
                    sizeDiff = Integer.parseInt(add.get(i+4),16)-Integer.parseInt(add.get(i),16);

                System.out.print("T^00"+add.get(i)+"^"+Integer.toHexString(sizeDiff));
                hh=false;
            }

            System.out.print("^"+add.get(i));
        }
        System.out.print("\nE^"+add.get(0));
        System.out.print("\n==========================================================================\n\n");
    }
}
