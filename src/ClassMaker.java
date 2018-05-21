import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class ClassMaker {

    public static final int MINIMUM = 2;

    public static ArrayList<Category> list;
    public static ArrayList<String> mapping;
    public static boolean [] mappingFlag;
    public static ArrayList<String> mcMapping;

    public static Stack<String> stack;

    public static ArrayList<ArrayList<Category>> answer;

    public static void main(String[] args){

        Scanner scn = new Scanner(System.in);

        int N = scn.nextInt();
        list = new ArrayList<>();
        for(int i=0; i<N; i++){
            String line = scn.nextLine();
            if(line.equals("")){
                i -=1;
                continue;
            }
            ArrayList<String> temp = new ArrayList<>();
            for(String s : line.split(",")){
                temp.add(s);
            }
            for(int k=temp.size(); k<5; k++){
                temp.add("");
            }
            String [] features = new String[4];
            for(int k=1; k<temp.size(); k++){
                features[k-1] = temp.get(k);
            }
            list.add(new Category(temp.get(0), features));
        }


        mapping = new ArrayList<>();
        mcMapping = new ArrayList<>();


        for(int f=0; f<4; f++) { // *
            HashMap<String, Integer> map = new HashMap<>();
            for (Category category : list) {
                String key = category.features[f];
                if ("".equals(key)) {
                    continue;
                }
                if (map.containsKey(key)) {
                    map.put(key, map.get(key) + 1);
                } else {
                    map.put(key, 1);
                }
            }

            for (String key : map.keySet()) {
                System.out.println(String.format("%s -> %d", key, map.get(key)));
                if (map.get(key) > MINIMUM - 1) {
//                    mapping.add(f + "/" + key); // *
                    if(f != 3)
                        mapping.add(f + "/" + key);
                    else
                        mcMapping.add(f + "/" + key);
                }
            }
        }

        System.out.println(mapping);
        System.out.println();

        mappingFlag = new boolean[mapping.size()];
        stack = new Stack<>();
        DFS();

        System.out.println("-------------------------------------------------");
        System.out.println("Result: ");
        for(int c=0; c<answer.size(); c++){
            System.out.print("Class " + String.format("%2d", (c+1)) + "  |  " );
            for(Category category : answer.get(c)){
                System.out.print(category.name + " ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------------");

    }

    public static int DFS(){
        if(stack.size() >= mapping.size()){

            for(String s : mcMapping){
                stack.push(s);
            }

            boolean [] flag = new boolean[list.size()];
            ArrayList<ArrayList<Category>> result = new ArrayList<>();

            for(String item : stack){
//                System.out.print(item + " "); // *
                String [] temp = item.split("/");
                int featureIndex = Integer.parseInt(temp[0]);
                String feature = temp[1];

                ArrayList<Integer> indexList = new ArrayList<>();
                for(int c=0; c<list.size(); c++){
                    if(!flag[c]) {
                        Category category = list.get(c);
                        if (category.isSame(featureIndex, feature)) {
                            indexList.add(c);
                        }
                    }
                }

                if(indexList.size() >= MINIMUM) {
                    ArrayList<Category> classification = new ArrayList<>();
                    for (int index : indexList) {
                        classification.add(list.get(index));
                        flag[index] = true;
                    }

                    result.add(classification);
                }

            }
//            System.out.println(); // *

            for(String s : mcMapping){
                stack.pop();
            }



            ArrayList<Category> otherClass = new ArrayList<>();
            for(int i=0; i<flag.length; i++){
                if(!flag[i]){
                    otherClass.add(list.get(i));
                }
            }

            result.add(otherClass);

            if(answer == null){
                answer = result;
            }else{
                if(otherClass.size() < answer.get(answer.size()-1).size()){
                    answer = result;
                }
            }

            return -1;

        }else{

            int lastPop = -1;
            for(int i=0; i<mapping.size(); i++){

                if(!mappingFlag[i]){

                    mappingFlag[i] = true;
                    stack.push(mapping.get(i));
                    int returnValue = DFS();

                    int pop = Integer.parseInt(stack.pop().split("/")[0]);
                    mappingFlag[i] = false;

                    lastPop = pop;
                    if(returnValue < 0){
                        return pop;
                    }else if(returnValue == pop){
                        return returnValue;
                    }

                }

            }

            return lastPop;

        }
    }


    static class Category{
        String name;
        String [] features;

        public Category(String name, String [] features){
            this.name = name;
            this.features = features;
        }

        public boolean isSame(int featureIndex, String feature){
            if("".equals(features[featureIndex])){
                return false;
            }
            return features[featureIndex].equals(feature);
        }

    }

}
