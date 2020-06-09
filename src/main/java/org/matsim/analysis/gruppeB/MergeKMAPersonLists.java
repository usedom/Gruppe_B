package org.matsim.analysis.gruppeB;

import javax.swing.text.Element;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MergeKMAPersonLists {

    private static BufferedWriter bufferedWriter;
    private  static BufferedReader bufferedReader;

    public Set<String> merge_all(String west_enter, String east_enter, String west_leave, String east_leave) throws IOException {

        List<String> allinputs = new ArrayList<>();
        allinputs.add(west_enter);
        allinputs.add(east_enter);
        allinputs.add(west_leave);
        allinputs.add(east_leave);

        /*
        List<String> output = new ArrayList<String>();
        try {
            in = new BufferedReader(new FileReader(allinputs.get(0)));
            String str;
            while ((str = in.readLine()) != null) {
                output.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }

         */

        List<String> output = new ArrayList<String>();

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
        /*
        for(int i=1;i<3;i++)
        try {
            in = new BufferedReader(new FileReader(allinputs.get(i)));
            String str;
            while ((str = in.readLine()) != null) {
                if(!output.contains(str)) {
                    output.add(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }

         */
        return outputset;
    }

    public void printout_terminal(Set<String> output){
        System.out.println("### " + output.size() + " entries after merging ###");
        System.out.println(output + "\n");
    }

    public void printout_txt(Set<String> output, String output_txt){
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
    }

}
