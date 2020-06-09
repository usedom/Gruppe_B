package org.matsim.analysis.gruppeB;

import java.io.*;
import java.util.*;

public class HandleKMAPersonLists {

    private static BufferedWriter bufferedWriter;
    private  static BufferedReader bufferedReader;

    public Set<String> merge_all(String west_enter, String east_enter, String west_leave, String east_leave) throws IOException {

        List<String> allinputs = new ArrayList<>();
        allinputs.add(west_enter);
        allinputs.add(east_enter);
        allinputs.add(west_leave);
        allinputs.add(east_leave);

        List<String> output = new ArrayList<>();

        System.out.println("Run merge ...\n");

        for(int i=0;i<3;i++) {
            try {
                bufferedReader = new BufferedReader(new FileReader(allinputs.get(i)));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    output.add(str);
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

        System.out.println("### " + output.size() + " entries before merging ###");
        System.out.println(output.toString() + "\n");

        Set<String> outputset = new LinkedHashSet<>(output);

        System.out.println("### " + outputset.size() + " entries after merging ###");
        System.out.println(outputset);

        System.out.println("\nDONE!\n###\n");

        return outputset;
    }

    public void compareIDappearance(Set<String> kma, Set<String> kma_mod){
        List<String> onlykma = new ArrayList<>();
        List<String> onlykma_mod = new ArrayList<>();
        List<String> bothkma = new ArrayList<>();

        System.out.println("\nComparison ...\n");

        for(Object id:kma){
            if(!kma_mod.contains(id.toString())){
                onlykma.add((String) id);
            }
            else{
                bothkma.add((String) id);
            }
        }
        System.out.println(onlykma.size() + " IDs not anymore in modified network:");
        System.out.println(onlykma);
        for(Object id:kma_mod){
            if(!kma.contains(id.toString())){
                onlykma_mod.add((String) id);
            }
            else{
                if(!bothkma.contains(id.toString())) {
                    bothkma.add((String) id);
                }
            }
        }
        System.out.println(onlykma_mod.size() + " IDs new in modified network:");
        System.out.println(onlykma_mod);

        System.out.println(bothkma.size() + " in both networks:");
        System.out.println(bothkma);
        System.out.println("\nDONE!\n###\n");
    }

    public List<List<String>> compareIDappearance_list(Set<String> kma, Set<String> kma_mod){
        List<List<String>> output = new ArrayList<>();
        List<String> onlykma = new ArrayList<>();
        List<String> onlykma_mod = new ArrayList<>();
        List<String> bothkma = new ArrayList<>();

        output.add(0,onlykma);
        output.add(1,onlykma_mod);
        output.add(2,bothkma);

        System.out.println("\nComparison into List...\n");

        for(Object id:kma){
            if(!kma_mod.contains(id.toString())){
                onlykma.add((String) id);
            }
            else{
                bothkma.add((String) id);
            }
        }
        System.out.println(onlykma.size() + " IDs written into list 1: Not anymore in modified network:");
        for(Object id:kma_mod){
            if(!kma.contains(id.toString())){
                onlykma_mod.add((String) id);
            }
            else{
                if(!bothkma.contains(id.toString())) {
                    bothkma.add((String) id);
                }
            }
        }
        System.out.println(onlykma_mod.size() + " IDs written into list 2: New in modified network:");

        System.out.println(bothkma.size() + " IDs written into list 3: In both networks:");
        System.out.println("\nDONE!\n###\n");

        return output;
    }


    public void printout_set_txt(Set<String> output, String output_txt){
        System.out.println("Write into " +  output_txt);
        try {
            FileWriter fileWriter = new FileWriter(output_txt);
            bufferedWriter = new BufferedWriter(fileWriter);
            for(Object id:output){
                    bufferedWriter.write(id.toString());
                    bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
        System.out.println("\nDONE!\n###\n");
    }

    public void printout_list_txt(List<String> output, String output_txt) {
        System.out.println("Write into " + output_txt);
        try {
            FileWriter fileWriter = new FileWriter(output_txt);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (String id : output) {
                bufferedWriter.write(id);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException ee) {
            throw new RuntimeException(ee);
        }
        System.out.println("\nDONE!\n###\n");
    }
}
