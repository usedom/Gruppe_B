package org.matsim.analysis.gruppeB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompareU5PassengerList {

    public static void main(String[] args) {

        List<String> u5_004_ori = new ArrayList<>();
        List<String> u5_004_exp = new ArrayList<>();

        List<String> output = new ArrayList<>();


        BufferedReader bufferedReader = null;

        try {
                bufferedReader = new BufferedReader(new FileReader("C:/Users/djp/Desktop/task 3/passengers_pt_U5.txt"));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    u5_004_ori.add(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        try {
            bufferedReader = new BufferedReader(new FileReader("C:/Users/djp/Desktop/task 3/passengers_pt_U5_mod.txt"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                u5_004_exp.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



            List<String> onlykma = new ArrayList<>();
            List<String> onlykma_mod = new ArrayList<>();
            List<String> bothkma = new ArrayList<>();

            System.out.println("\nComparison ...\n");

            for(String id:u5_004_ori){
                if(!u5_004_exp.contains(id)){
                    onlykma.add((String) id);
                }
                else{
                    bothkma.add((String) id);
                }
            }
            System.out.println(onlykma.size() + " IDs not anymore in modified network:");
            System.out.println(onlykma);
            for(String id:u5_004_exp){
                if(!u5_004_ori.contains(id)){
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


}
