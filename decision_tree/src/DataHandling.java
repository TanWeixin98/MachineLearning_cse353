import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DataHandling {

    public static ArrayList<String[]> readData(String fileName){
        ArrayList<String []> data = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            String line;
            while((line=br.readLine())!=null){
                String[] dataInstance = line.split(",");
                if(dataInstance.length<13){
                    String[]temp = new String[13];
                    System.arraycopy(dataInstance, 0, temp, 0, 12);
                    temp[12]="";
                    data.add(temp);
                    continue;
                }
                data.add(dataInstance);
            }
            return fillIn(extractCabinAndTicket(converToNumeric(data)));
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    return LinkedList[] of partioned data
    LinkedList[0] is the train data and LinkedList[1] is test data
    */
    public static ArrayList[] divideData(ArrayList<String[]> data,double trainRatio){
        ArrayList[] partionedData = new ArrayList[2];
        ArrayList<String[]> trainData = new ArrayList<>();
        ArrayList<String[]> testData =  new ArrayList<>();
        int dataSize= data.size();
        int partionValue = (int)(trainRatio* dataSize);
        for(int i=0; i<dataSize;i++){
            if (i<=partionValue)
                trainData.add(data.get(i));
            else
                testData.add(data.get(i)) ;
        }
        partionedData[0]=trainData;
        partionedData[1]=testData;
        return partionedData;
    }

    private static ArrayList<String[]> fillIn(ArrayList<String[]>dataset){
        int[] filleInCol = {2,5,6,7,8,9,10,12};
        double[] averageList = new double[filleInCol.length];
        for(int i=0;i<filleInCol.length;i++){
            double average = getAverage(filleInCol[i],dataset);
            averageList[i]= Math.round(average);
        }
        for(String[] data: dataset){
            for(int i=0;i<filleInCol.length;i++){
                if (data[filleInCol[i]].length()==0){
                    data[filleInCol[i]] =Double.toString(averageList[i]);
                }
            }
        }
        return dataset;
    }

    private static ArrayList<String[]> extractCabinAndTicket(ArrayList<String[]> dataset){
        for(int i=0;i<dataset.size();i++){
            String[] data = dataset.get(i);
            if(data[11].length()!=0){
                data[11]="0";
            }else{
                data[11]="1";
            }
            dataset.set(i,data);
            if(data[9].length()!=0){
                data[9]=getLastInt(data[9]);
                dataset.set(i,data);
            }
        }

        return dataset;
    }



    private static String getLastInt(String str){
        String value="";
        for(int i=str.length()-1;i>=0;i--){
            char currChar = str.charAt(i);
            if(Character.isDigit(currChar)){
                value=currChar+value;
            }else{
                value=Integer.toString((int) currChar)+value;
            }
        }
        return value;

    }
    private static String getFirstInt(String str){
        String value="";
        boolean startDigit=false;
        for(int i=0;i<str.length();i++){
            char currChar = str.charAt(i);
            if(Character.isDigit(currChar)){
                value=value+currChar;
                startDigit=true;
            }else if(startDigit){
                break;
            }
        }
        return value;
    }
    private static double getAverage(int col, ArrayList<String[]> dataset){
        double sum=0;
        double count =0;
        for(String[] data: dataset){
            if(data[col].length()!=0){
                sum=sum+Double.parseDouble(data[col]);
                count++;
            }
        }
        return sum/count;
    }
    private  static double getMode(int col, ArrayList<String[]> dataset){
        HashMap<Integer,Integer> frequency = new HashMap<>();
        int maxCount =-1, maxValue=-1;
        for(String[] data: dataset){
            if(data[col].length()==0){
                continue;
            }
            int count =0;
            int value =  Integer.parseInt(data[col]);
            if(frequency.containsKey(value)){
                count= frequency.get(value)+1;
            }else{
                count=1;
            }
            frequency.put(value,count);
            if(count>maxCount){
                maxCount=count;
                maxValue=value;
            }
        }
        return maxValue;
    }
    /* convert data to numeric
        male =0 and female =1 for sex
        S=0, C=1, Q=2 for embraked
    */
    private static ArrayList<String[]> converToNumeric(ArrayList<String[]> dataset){
        int size = dataset.size();
        for(int i=0;i<size;i++){
            String[] data =dataset.get(i);
            if(data[5].equals("female")){
                data[5]="1";
            }else if(data[5].equals("male")){
                data[5]="0";
            }
            switch (data[12]) {
                case "S":
                    data[12] = "0";
                    break;
                case "C":
                    data[12] = "1";
                    break;
                case "Q":
                    data[12] = "2";
                    break;
            }
            dataset.set(i, data);
        }
        return dataset;
    }
}
