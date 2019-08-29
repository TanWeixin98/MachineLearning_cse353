import java.util.ArrayList;
import java.util.LinkedList;

public class DecisionTree {

    private class decisionNode{
        decisionNode leftNode, rightNode,parent;
        int feature;
        double threshHold,maxIG;
        int decision;
        ArrayList<String[]> data;
        private decisionNode(int label,ArrayList<String[]> data, decisionNode parent){
            this.decision =label;
            this.data=data;
            this.parent=parent;
        }
        private decisionNode(int feature, double threshHold,double maxIg,ArrayList<String[]> data,decisionNode parent){
            this.decision=-1;
            this.maxIG =maxIg;
            this.feature =feature;
            this.threshHold =threshHold;
            this.data=data;
            this.parent=parent;
        }
    }

    private decisionNode Root;
    private static int maxDepth;

    public DecisionTree(int maxDepth){
        this.maxDepth=maxDepth;
    }

    public DecisionTree buildTree(ArrayList<String[]> dataset){
        //define the features col
        LinkedList<Integer> features = new LinkedList();
        features.add(2);
        features.add(5);
        features.add(6);
        features.add(7);
        features.add(8);
        //features.add(9);
        features.add(10);
        features.add(11);
        features.add(12);


        double[] threshHoldandFeature=Utility.findThreshHoldandFeature(dataset,features);
        int bestFeature = (int)threshHoldandFeature[1];
        Root=new decisionNode(bestFeature,threshHoldandFeature[0],threshHoldandFeature[2],dataset,null);
        ArrayList[] groupedData = groupData(dataset, bestFeature,threshHoldandFeature[0]);
        //features.remove(new Integer(bestFeature));
        decisionNode left = buildTree(groupedData[0],features,1,Root);
        decisionNode right = buildTree(groupedData[1],features,1,Root);
        Root.leftNode=left;
        Root.rightNode=right;
        return this;
    }

    private decisionNode buildTree(ArrayList<String[]> dataset, LinkedList<Integer> features, int depth, decisionNode parent){
        //base cases
        ArrayList[] labels = getDataLabel(dataset);
        if(labels[0].size()==0){
            return new decisionNode(1,dataset,parent);
        }
        if(labels[1].size()==1){
            return new decisionNode(0,dataset,parent);
        }
        //if both data is split into equal 0 and 1 survival ratio
        if(labels[1].size()==labels[0].size()){//cant distinuish anymore
            ArrayList[] parentLabel =getDataLabel(parent.data);
            int label = (parentLabel[0].size()>parentLabel[1].size())?0:1;
            return new decisionNode(label,dataset,parent);
        }
        if(depth==maxDepth || features.size()==0){
            if(labels[0].size()>labels[1].size()){
                return new decisionNode(0,dataset,parent);
            }else{
                return new decisionNode(1,dataset,parent);
            }
        }
        //if best IG is 0
        double[] threshHoldandFeature=Utility.findThreshHoldandFeature(dataset,features);
        if(threshHoldandFeature[2]==0){
            if(labels[0].size()>labels[1].size()){
                return new decisionNode(0,dataset,parent);
            }else{
                return new decisionNode(1,dataset,parent);
            }
        }
        int bestFeature = (int)threshHoldandFeature[1];
        decisionNode currNode =new decisionNode(bestFeature,threshHoldandFeature[0],threshHoldandFeature[2],dataset,parent);
        ArrayList[] groupedData = groupData(dataset, bestFeature,threshHoldandFeature[0]);
        //features.remove(new Integer(bestFeature));
        decisionNode left = buildTree(groupedData[0],features,depth+1,currNode);
        decisionNode right = buildTree(groupedData[1],features,depth+1,currNode);
        currNode.leftNode=left;
        currNode.rightNode=right;
        return currNode;
    }
    //result[0] is data less than feature and result[] is data greater equal feature
    private ArrayList[] groupData(ArrayList<String[]> dataset, int featureCol, double threshHold) {
        ArrayList<String[]> dataLessThan = new ArrayList<>();
        ArrayList<String[]> dataGreaterThan = new ArrayList<>();
        for(String[] data: dataset){
            if(Double.parseDouble(data[featureCol])<threshHold){
               dataLessThan.add(data);
            }else{
                dataGreaterThan.add(data);
            }
        }
        return new ArrayList[]{dataLessThan,dataGreaterThan};
    }

    // ArrayList[0] is data labeled 0 and ArrayList[1] is data labeled 1
    private ArrayList[] getDataLabel(ArrayList<String[]> dataset){
        ArrayList<String[]> suriviveData = new ArrayList<>();
        ArrayList<String[]> deadData = new ArrayList<>();
        for(String[] data: dataset){
            if(Integer.parseInt(data[1])==1){
                suriviveData.add(data);
            }else{
                deadData.add(data);
            }
        }
        return new ArrayList[]{deadData,suriviveData};
    }
    public int labelData(String[] data){
        decisionNode currNode = Root;
        while(currNode!=null){
            if(currNode.decision!=-1){
                return currNode.decision;
            }
            double value = Double.parseDouble(data[currNode.feature]);
            if(value<currNode.threshHold){
                currNode = currNode.leftNode;
            }else{
                currNode=currNode.rightNode;
            }
        }
        return -1;
    }

    public double testData(ArrayList<String[]> dataset){
        double corrCount =0;
        for(String[] data: dataset){
            if( Integer.parseInt(data[1])==labelData(data)){
                corrCount++;
            }
            if(labelData(data)==-1){
                System.out.println("F");
            }
        }
        return corrCount/((double)dataset.size());
    }
}
