import java.util.*;
public class CO
{
    int cacheSize,setSize,offset,blockSize,numBlocks,tagLength,numSets,indexLength;
    int head[];
    String tagArray[];
    long dataArray[][];
    CO(int c,int b,int s,int o)
    {
        cacheSize=c;
        blockSize=b;
        setSize=s;
        offset=o;
        numBlocks=(c/b);
        numSets=(numBlocks/s);
        indexLength=(int)(Math.log(numSets)/Math.log(2));
        tagLength=32-offset-indexLength;
        tagArray=new String[numBlocks];
        dataArray=new long[numBlocks][blockSize];
        head=new int[numSets];
    }
    int decmaker(String s)
    {
        int dec=0;
        int i=0;
        while(i<s.length())
        {
            if(s.charAt(i)=='1')
                dec+=Math.pow(2,s.length()-i-1);

            i++;
        }
        return dec;
    }

    String binmaker(int decimal)
    {
        String tr="";
        while(decimal>0)
        {
            tr+=(decimal%2);
            decimal/=2;
        }
        if(tr.length()<indexLength)
            while(tr.length()<indexLength)
            {
                tr="0"+tr;
            }
        return tr;

    }
    void writedata(String address,int data,int ch)
    {

        int id=decmaker(address.substring(tagLength,tagLength+indexLength));

        if(indexLength==0)
            id=0;

        int ofs=decmaker(address.substring(32-offset,32));

        int flag=-1;

        String tag=address.substring(0,tagLength);


        for(int i=id*setSize;i<(id+1)*setSize;i++)
        {

            if(tagArray[i]!=null )
            {
                if(tagArray[i].equals(tag)) {
                    System.out.println("Write Hit Occurred");
                    dataArray[i][ofs] = data;
                    System.out.println("Cache has been Updated");
                    flag = 1;
                    break;
                }
            }
        }

        if(flag<0)
        {
            System.out.println("Write Miss Occurred");
            if((head[id]==setSize && ch!=1) || (ch==1) && head[id]==numBlocks)
            {
                System.out.println("Replaced this tag: "+tagArray[id*setSize]+binmaker(id));
                int i=id*setSize+1;
                do{
                    int j=0;
                    do{
                        dataArray[i-1][j]=dataArray[i][j];
                        j++;}while(j<blockSize);
                    tagArray[i - 1] = tagArray[i];
                    i++;
                }while(i<(id+1)*setSize);
                dataArray[(id+1)*setSize-1][ofs]=data;
                tagArray[(id+1)*setSize-1]=tag;
            }
            else
            {
                int f=id*setSize+head[id];
                tagArray[f]=tag;
                dataArray[f][ofs]=data;
                head[id]++;
                System.out.println("No Replacement");
            }
            System.out.println("Cache has been Updated");
        }

    }

    void readadr(String adr,int ch)
    {

        int id=decmaker(adr.substring(tagLength,tagLength+indexLength));
        int ofs=decmaker(adr.substring(32-offset,32));
        int flag=-1;
        String tag=adr.substring(0,tagLength);
        int i=id*setSize;

        while(i<(id+1)*setSize)
        {
            if(tagArray[i]!=null )
                if(tagArray[i].equals(tag))
                {
                    System.out.println("Read Hit Occurred");
                    System.out.println("Data Present:"+dataArray[i][ofs]);
                    System.out.println("Cache has been Updated");
                    flag=1;
                    break;
                }

            i++;
        }

        if(flag==-1)
        {
            System.out.println("Read Miss Occurred");
            if((head[id]==setSize && ch!=1) || (ch==1) && head[id]==numBlocks)
            {
                int j=id*setSize+1;
                System.out.println("Replaced: "+tagArray[setSize*id]+binmaker(id));
                while(j<(id+1)*setSize)
                {
                    int k=0;
                    do{
                        dataArray[j-1][k] = dataArray[j][k];
                        k++;}while(k<blockSize);
                    tagArray[j-1] = tagArray[j];
                    j++;
                }
                int in=(id+1)*setSize-1;
                tagArray[in]=tag;
                int c=0;
                do
                {
                    dataArray[in][c]=0;
                    c++;
                }while(c<blockSize);
            }
            else
            {
                int f=head[id]+id*setSize;
                tagArray[f]=tag;
                ++head[id];
            }
            System.out.println("Cache has been Updated");
        }


    }

    void print(int choice)
    {
        if(choice==1)
        {
            int i=0;
            while(i < numBlocks) {
                if (!(tagArray[i] == null)) {
                    System.out.println("Tag Address: " + tagArray[i]);
                    System.out.println("Data Present: ");
                    for (int j = 0; j < blockSize; j++) {
                        if (dataArray[i][j] != 0)
                            System.out.println("offset:"+binmaker(j) + " " +"data present:"+ dataArray[i][j]);
                    }
                }
                i++;
            }
        }
        else {
            int i=0;
            do{
                if (tagArray[i] != null) {
                    System.out.println("Tag Address: " + tagArray[i] + " Index id: " + binmaker(i / setSize));
                    int j=0;
                    do{
                        if (dataArray[i][j] != 0)
                            System.out.println("offset:"+binmaker(j) + " " +"data present:"+ dataArray[i][j]);
                        j++;
                    }while(j<blockSize);
                }
                i++;
            }while(i<numBlocks);
        }
    }

    public static void main(String[] args)
    {
        Scanner inp=new Scanner(System.in);
        System.out.println("Enter Cache Size and Block_Size:");
        int cache_size=inp.nextInt();
        int block_size=inp.nextInt();
        int k=2;
        int fll=0,fllf=0;
        for(int i=0;i<30;i++)
        {
            if(k==cache_size)
            {
                fll=1;
                break;
            }
            k=k*2;
        }
        k=2;
        for(int i=0;i<30;i++)
        {
            if(k==block_size)
            {
                fllf=1;
                break;
            }
            k=k*2;
        }
        if(cache_size<=block_size || fll==0 || fllf==0)
            System.out.println("Wrong Input");
        else{
            System.out.println();
            System.out.println("Press 1 for Fully Associative map");
            System.out.println("Press 2 for Direct Mapping");
            System.out.println("Press 3 for Set Associative Mapping");
            int ch=inp.nextInt();
            int set_size;
            CO cache_obj;
            int yoyo=0;
            if(ch==1) {
                set_size = cache_size / block_size;
                cache_obj = new CO(cache_size, block_size, set_size,(int)(Math.log(block_size)/Math.log(2)));//Fully Associative Mapping
            }
            else if(ch==2) {
                cache_obj = new CO(cache_size, block_size, 1,(int)(Math.log(block_size)/Math.log(2)));//Direct Mapping
            }
            else {
                System.out.println("Enter size of set");
                set_size = inp.nextInt();// set associative mapping
                if(set_size>block_size)
                {
                    yoyo =1;
                    
                }
                cache_obj = new CO(cache_size, block_size, set_size,(int)(Math.log(block_size)/Math.log(2)));
            }
            if(yoyo==0){
            System.out.println("No of queries:");
            int que = inp.nextInt();
            System.out.println("Size of Cache: "+cache_obj.cacheSize+"   " +"Size of Bloc:"+cache_obj.blockSize);
        
            System.out.println("Number of Blocks: "+cache_obj.numBlocks+"    "+"tagLength: " + cache_obj.tagLength);
            if(ch!=1)
                System.out.println("Length of Index: "+cache_obj.indexLength);
            if(ch==3)
                System.out.println("Number of Sets: "+cache_obj.numSets+"    "+"Size of Set: "+cache_obj.setSize);
            System.out.println("Offset Bits: "+cache_obj.offset);

            while(que>0)
            {
                System.out.println();
                System.out.println("Enter the option you want to perform");
                System.out.println("1.Write");
                System.out.println("2.Read");
                System.out.println("3.Print Cache");
                int str=inp.nextInt();
                if(str==1)
                {
                    String st=inp.next();
                    int n=inp.nextInt();
                    cache_obj.writedata(st,n,str);
                }
                else if(str==2){

                    cache_obj.readadr(inp.next(),str);
                }
                else if(str==3)
                    cache_obj.print(ch);
                else
                    System.out.print("Wrong Input");
                que--;
            }}
            else
                {
                    System.out.println("Wrong");
                }
        }}
}