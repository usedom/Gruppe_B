package org.matsim.analysis.gruppeB;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Tripduration {

    private static BufferedWriter bufferedWriter;
    private  static BufferedReader bufferedReader;
    public   List<String> EnteringLeavingPerson = new ArrayList<>();
    public List<String> outputEnter = new ArrayList<String>();
    public List<String> outputLeave = new ArrayList<String>();

    public Set<String> merge_all(String west_enter, String east_enter, String west_leave, String east_leave) throws IOException {

        List<String> EnterKMAinputs = new ArrayList<>();
        List<String> LeaveKMAinputs = new ArrayList<>();

        EnterKMAinputs.add(west_enter);
        EnterKMAinputs.add(east_enter);
        LeaveKMAinputs.add(west_leave);
        LeaveKMAinputs.add(east_leave);

        //Reading the necessary File
        List<String> outputJulia = new ArrayList<String>();
        List<String> outputSabrina = new ArrayList<String>();

        for(int i=0;i<2;i++) {
            try {
                bufferedReader = new BufferedReader(new FileReader(EnterKMAinputs.get(i)));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    outputJulia.add(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }

        for(int i=0;i<2;i++) {
            try {
                bufferedReader = new BufferedReader(new FileReader(LeaveKMAinputs.get(i)));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    outputSabrina.add(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }

         System.out.println("### " + outputJulia.size() + "Entering IDs before merging");
         System.out.println(outputJulia.toString() + "\n");
         outputEnter = outputJulia;

         System.out.println("### " + outputSabrina.size() + "Leaving IDs before merging");
         System.out.println(outputSabrina.toString() + "\n");
         outputLeave = outputSabrina;

        Set<String> outputset = new LinkedHashSet<>(outputJulia);

        return outputset;
    }

    public void compareIDappearance(){
       int Counter = 0;

      if (outputEnter.size()>=outputLeave.size()){
            Counter = outputEnter.size();
        }else{ Counter = outputLeave.size();}
        System.out.println(Counter);

        for (int z=0; z<Counter; z++){
            if (outputEnter.contains(outputLeave.get(z))){
                EnteringLeavingPerson.add(outputLeave.get(z));
            }
        }
    }

    public void printout_terminal(Set<String> EnteringLeavingPerson){
        System.out.println("### Number of Entries after Merging" + EnteringLeavingPerson.size());
        System.out.println("PersonIDs that enter and leave the KMA" + EnteringLeavingPerson);
    }

    public void printout_txt(Set<String> EnteringLeavingPerson, String output_txt){
        try {
            FileWriter fileWriter = new FileWriter(output_txt);
            bufferedWriter = new BufferedWriter(fileWriter);
            for(Object id:EnteringLeavingPerson){
                bufferedWriter.write(id.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
        System.out.println("Hier gibt er es aus" + EnteringLeavingPerson.size());
    }

}
