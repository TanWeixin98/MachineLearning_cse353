import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Utility {
    static double accuracy(Matrix w, List<double[]> dataSets){
        int missCount=0;
        for(double[] dataPoint:dataSets){
            int y_i=(dataPoint[5]==0) ?-1 :1;

            Matrix x_i= new Matrix(6,1);
            x_i.set(0,0,1);
            x_i.set(1,0,dataPoint[0]);
            x_i.set(2,0,dataPoint[1]);
            x_i.set(3,0,dataPoint[2]);
            x_i.set(4,0,dataPoint[3]);
            x_i.set(5,0,dataPoint[4]);

            double dotProdcutValue= w.times(x_i).get(0,0);
            int predict = (dotProdcutValue>0)?1 :-1;
            if(y_i!=predict){
                missCount++;
            }
        }
        return 100-(missCount/569.0)*100;
    }
    static Matrix convertDataToMatrix(double[] dataPoint){
        Matrix dataMatrix= new Matrix(6,1);
        dataMatrix.set(0,0,1);
        dataMatrix.set(1,0,dataPoint[0]);
        dataMatrix.set(2,0,dataPoint[1]);
        dataMatrix.set(3,0,dataPoint[2]);
        dataMatrix.set(4,0,dataPoint[3]);
        dataMatrix.set(5,0,dataPoint[4]);

        return dataMatrix;
    }

    static ArrayList<double[]> readData(String csvFileName){
        ArrayList<double[]> dataSets = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(csvFileName));
            String line;
            br.readLine();
            while((line=br.readLine())!=null){
                String[] temp = line.split(",");
                int size =temp.length;
                double[] dataTuple = new double[size];
                for(int i =0;i<size;i++) {
                    dataTuple[i] = Double.parseDouble(temp[i]);
                }
                dataSets.add(dataTuple);
            }
            return dataSets;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
