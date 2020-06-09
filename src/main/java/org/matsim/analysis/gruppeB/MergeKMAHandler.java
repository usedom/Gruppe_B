package org.matsim.analysis.gruppeB;

import java.io.IOException;
import java.util.Set;

public class MergeKMAHandler {

    public static void main(String[] args) throws IOException {

        // Set your working folder
        String inputfolder = "C:/Users/Julia/Desktop/git/Gruppe_B/outputs/personcount/";
        System.setProperty("user.dir",inputfolder);

        String kma_ori_west_enter = System.getProperty("user.dir")+"Agents entering KMA on west Link 97508.txt";
        String kma_ori_east_enter = System.getProperty("user.dir")+"Agents entering KMA on east Link 54738.txt";
        String kma_ori_west_leave = System.getProperty("user.dir")+"Agents leaving KMA on west Link 126333.txt";
        String kma_ori_east_leave = System.getProperty("user.dir")+"Agents leaving KMA on east Link 99708.txt";

        String kma_mod_west_enter = System.getProperty("user.dir")+"agents entering KMA_mod on west link 97508.txt";
        String kma_mod_east_enter = System.getProperty("user.dir")+"agents entering KMA_mod on east link 54738.txt";
        String kma_mod_west_leave = System.getProperty("user.dir")+"agents leaving KMA_mod on west link 126333.txt";
        String kma_mod_east_leave = System.getProperty("user.dir")+"agents leaving KMA_mod on east link 99708.txt";

        HandleKMAPersonLists mergeKM = new HandleKMAPersonLists();

        System.out.println("Merge ALL - Ori:");
        Set<String> ori_merge_all = mergeKM.merge_all(kma_ori_west_enter, kma_ori_east_enter, kma_ori_west_leave, kma_ori_east_leave);
        mergeKM.printout_terminal(ori_merge_all);

        System.out.println("Merge ALL - Mod:");
        Set<String> mod_merge_all = mergeKM.merge_all(kma_mod_west_enter, kma_mod_east_enter, kma_mod_west_leave, kma_mod_east_leave);
        mergeKM.printout_terminal(mod_merge_all);

        // Set output file txt:
        String output_txt = System.getProperty("user.dir")+"merge_all_ori.txt";
        mergeKM.printout_txt(ori_merge_all, output_txt);

        mergeKM.compareIDappearance(ori_merge_all, mod_merge_all);

    }
}
