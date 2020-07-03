package org.matsim.analysis.gruppeB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Grid {
    // Uncomment line below to printout your root folder
    // System.out.println(System.getProperty("user.dir"));

    int rows = 5;
    int cols = 5;
    int X_MIN = 4412639; // west
    int X_MAX = 4708887; // east
    int Y_MIN = 5634174; // south (!)
    int Y_MAX = 6020379; // north (!)

    public Grid(){

    }
    public Grid(int rows, int columns){
        this.rows = rows;
        this.cols = columns;
    }
    public Grid(int length){
        rows = length;
        cols = length;
    }

    int width = X_MAX-X_MIN;
    int height = Y_MAX-Y_MIN;

    public Map<String, Integer>[][] createGrid(){

        System.out.println("### Creating the grid... ###");

        int cellwidth = width/cols;
        int cellheight = height/rows;
        int extra_x = width%cols;       System.out.println("## Width of each cell is "+cellwidth+" with "+extra_x+" extra on last cell! ##");
        int extra_y = height%rows;      System.out.println("## Height of each cell is "+cellheight+" with "+extra_y+" extra on last cell! ##");

        ArrayList<ArrayList<Map<String,Integer>>> row = new ArrayList<>();
        ArrayList<Map<String, Integer>> col = new ArrayList<>();

        /*
        int[][] cellId = new int[rows][cols];
        int id = 1;
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++) {
                cellId[r][c] = id++;
            }
        }
        */
        //Map<String, Integer> content = new HashMap<>();
        //Map<Integer, Map<String, Integer>> cell = new HashMap<>();

       /*
        id = 0;
        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++) {
                content.put("x_start", X_MIN+cellwidth*c);
                content.put("y_start", Y_MIN+cellheight*r);
                if(c!=cols-1){
                    content.put("x_end", X_MIN+cellwidth*(c+1)-1);
                }
                else{
                    content.put("x_end", X_MIN+cellwidth*cols+extra_x);
                }
                if(r!=rows-1){
                    content.put("y_end", Y_MIN+cellheight*(r+1)-1);
                }
                else{
                    content.put("y_end", Y_MIN+cellheight*rows+extra_y);
                }
                //content.put("x_end", cellwidth*(c+1));
                //content.put("y_end", cellheight*(r+1));

            }
        }

        */

        Map<String, Integer>[][] cell = new HashMap[rows][cols];

        //cell[0][0].put("test", 12345);

        //System.out.println(cell[0][0].size());

        for(int r=0; r<rows; r++){

            /*
            for(int c=0; c<cols; c++) {
                cell[r][c].put("x_start", X_MIN+cellwidth*c);
                cell[r][c].put("y_start", Y_MIN+cellheight*r);
                if(c!=cols-1){
                    cell[r][c].put("x_end", X_MIN+cellwidth*(c+1)-1);
                }
                else{
                    cell[r][c].put("x_end", X_MIN+cellwidth*cols+extra_x);
                }
                if(r!=rows-1){
                    cell[r][c].put("y_end", Y_MIN+cellheight*(r+1)-1);
                }
                else{
                    cell[r][c].put("y_end", Y_MIN+cellheight*rows+extra_y);
                }
            */
            for(int c=0; c<cols; c++) {
                Map<String, Integer> content = new HashMap<>();
                content.put("x_start", X_MIN+cellwidth*c);
                content.put("y_start", Y_MIN+cellheight*r);
                if(c!=cols-1){
                    content.put("x_end", X_MIN+cellwidth*(c+1)-1);
                }
                else{
                    content.put("x_end", X_MIN+cellwidth*cols+extra_x);
                }
                if(r!=rows-1){
                    content.put("y_end", Y_MIN+cellheight*(r+1)-1);
                }
                else{
                    content.put("y_end", Y_MIN+cellheight*rows+extra_y);
                }
                cell[r][c] = content;
            }
        }

/*

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                Map<String, Integer> cell = new HashMap<>();
                cell.put("x_start", X_MIN+cellwidth*c);
                cell.put("y_start", Y_MIN+cellheight*r);
                if(c!=cols-1){
                    cell.put("x_end", X_MIN+cellwidth*(c+1)-1);
                }
                else{
                    cell.put("x_end", X_MIN+cellwidth*cols+extra_x);
                }
                if(r!=rows-1){
                    cell.put("y_end", Y_MIN+cellheight*(r+1)-1);
                }
                else{
                    cell.put("y_end", Y_MIN+cellheight*rows+extra_y);
                }
                //cell.put("x_end", cellwidth*(c+1));
                //cell.put("y_end", cellheight*(r+1));
                col.add(c,cell);
                row.add(r,col);
            }

            // Map<String, Integer> cell = new HashMap<>();
            // cell.put("x_start", cellwidth*(cols-1));
            // cell.put("x_end", cellwidth*cols+extra_x);
        }
        */

        /*
        Map<String, Integer> cell = new HashMap<>();
        cell.put("y_start", cellheight*(rows-1));
        cell.put("y_end", cellheight*rows+extra_y);

         */

        System.out.println("### DONE! ###");

        return cell;
    }

/*
    public void printGrid(ArrayList<ArrayList<Map<String,Integer>>> row){
        // Set URL or local Path as inputFile (from root folder, usually "Gruppe_B")

        BufferedWriter writer = null;

        System.out.println("### Try to print the grid! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder
            File file = new File("gruppeB_TXSandCSV/grid.csv");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
            String x1 = "x_start";
            String x2 = "x_end";
            String y1 = "y_start";
            String y2 = "y_end";

            for(int r=0; r<rows; r++){
                for(int c=0; c<cols; c++){
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();
                    System.out.println(x1+"\t"+row.get(r).get(c).get(x1));
                    writer.write(x1+"\t"+row.get(r).get(c).get(x1));
                    writer.newLine();
                    System.out.println(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.write(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.newLine();
                    System.out.println(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.write(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.newLine();
                    System.out.println(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.write(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.newLine();
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(writer!=null) {
                writer.close();
                System.out.println("### DONE! ###");
            }
        } catch (IOException ioException) {
            System.out.println("Error!");
        }
    }

    */

    public void printGrid_(Map<String, Integer>[][] grid){
        // Set URL or local Path as inputFile (from root folder, usually "Gruppe_B")

        BufferedWriter writer = null;

        System.out.println("### Try to print the grid! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder
            File file = new File("gruppeB_TXSandCSV/grid.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
            /*
            String x1 = "x_start";
            String x2 = "x_end";
            String y1 = "y_start";
            String y2 = "y_end";
             */



            for(int r=0; r<rows; r++){
                for(int c=0; c<cols; c++){
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();
                    /* Nice idea, but doesn't work as expected
                        for(String s:grid[r][c].keySet()){
                        System.out.println(s+"\t"+grid[r][c].get(s));
                        writer.write(s+"\t"+grid[r][c].get(s));
                        writer.newLine();
                    }
                     */
                    String[] keys = {"x_start","x_end","y_start","y_end"};
                    for(String s:keys) {
                        System.out.println(s + "\t" + grid[r][c].get(s));
                        writer.write(s + "\t" + grid[r][c].get(s));
                        writer.newLine();
                    }
                    /*
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();
                    System.out.println(x1+"\t"+grid[r][c].);
                    writer.write(x1+"\t"+row.get(r).get(c).get(x1));
                    writer.newLine();
                    System.out.println(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.write(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.newLine();
                    System.out.println(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.write(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.newLine();
                    System.out.println(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.write(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.newLine();

                     */
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(writer!=null) {
                writer.close();
                System.out.println("### DONE! ###");
            }
        } catch (IOException ioException) {
            System.out.println("Error!");
        }
    }

    public void printGrid(Map<String, Integer>[][] grid){
        // Set URL or local Path as inputFile (from root folder, usually "Gruppe_B")

        BufferedWriter writer = null;

        System.out.println("### Try to print the grid! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder
            File file = new File("gruppeB_TXSandCSV/grid.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
            /*
            String x1 = "x_start";
            String x2 = "x_end";
            String y1 = "y_start";
            String y2 = "y_end";
             */

            System.out.print("row\tcol\tx_start\ty_start\tx_end\ty_end");
            writer.write("row\tcol\tx_start\ty_start\tx_end\ty_end");
            for(int r=0; r<rows; r++){
                for(int c=0; c<cols; c++){
                    //System.out.println("### cell "+r+"|"+c+" ###");
                    //writer.write("### cell "+r+"|"+c+" ###");
                    //writer.newLine();
                    /* Nice idea, but doesn't work as expected
                        for(String s:grid[r][c].keySet()){
                        System.out.println(s+"\t"+grid[r][c].get(s));
                        writer.write(s+"\t"+grid[r][c].get(s));
                        writer.newLine();
                    }
                     */
                    System.out.println();
                    writer.newLine();
                    String[] keys = {"x_start","y_start","x_end","y_end"};
                    System.out.print(r + "\t" + c);
                    writer.write(r + "\t" + c);
                    for(String s:keys) {
                        System.out.print("\t" + grid[r][c].get(s));
                        writer.write("\t" + grid[r][c].get(s));
                    }

                    /*
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();
                    System.out.println(x1+"\t"+grid[r][c].);
                    writer.write(x1+"\t"+row.get(r).get(c).get(x1));
                    writer.newLine();
                    System.out.println(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.write(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.newLine();
                    System.out.println(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.write(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.newLine();
                    System.out.println(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.write(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.newLine();

                     */
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(writer!=null) {
                writer.close();
                System.out.println("### DONE! ###");
            }
        } catch (IOException ioException) {
            System.out.println("Error!");
        }
    }

    public void printGridLines_via(Map<String, Integer>[][] grid){
        // Set URL or local Path as inputFile (from root folder, usually "Gruppe_B")

        BufferedWriter writer = null;

        System.out.println("### Try to print the grid for VIA! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder
            File file = new File("gruppeB_TXSandCSV/grid4via.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
            /*
            String x1 = "x_start";
            String x2 = "x_end";
            String y1 = "y_start";
            String y2 = "y_end";
             */



            writer.write("x_start\ty_start\tx_end\ty_end");
            writer.newLine();
            for(int r=0; r<rows; r++) {
                System.out.println("### horizontal line "+r+" ###");
                System.out.println(X_MIN + "\t" + grid[r][0].get("y_start") + "\t" + X_MAX + "\t" + grid[r][0].get("y_end"));
                //writer.write("### cell "+r+"|"+c+" ###");
                //writer.newLine();
                    /* Nice idea, but doesn't work as expected
                        for(String s:grid[r][c].keySet()){
                        System.out.println(s+"\t"+grid[r][c].get(s));
                        writer.write(s+"\t"+grid[r][c].get(s));
                        writer.newLine();
                    }
                     */
                writer.write(X_MIN + "\t" + grid[r][0].get("y_start") + "\t" + X_MAX + "\t" + grid[r][0].get("y_end"));
                writer.newLine();
            }

               // writer.write("x_start\ty_start\tx_end\ty_end");
                for(int c=0; c<cols; c++){
                    System.out.println("### vertical line "+c+" ###");
                    System.out.println(grid[0][c].get("x_start")+"\t"+Y_MIN+"\t"+grid[0][c].get("x_end")+"\t"+Y_MAX);
                    //writer.write("### cell "+r+"|"+c+" ###");
                    //writer.newLine();
                    /* Nice idea, but doesn't work as expected
                        for(String s:grid[r][c].keySet()){
                        System.out.println(s+"\t"+grid[r][c].get(s));
                        writer.write(s+"\t"+grid[r][c].get(s));
                        writer.newLine();
                    }
                     */
                    writer.write(grid[0][c].get("x_start")+"\t"+Y_MIN+"\t"+grid[0][c].get("x_end")+"\t"+Y_MAX);
                    writer.newLine();
                    /*
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();
                    System.out.println(x1+"\t"+grid[r][c].);
                    writer.write(x1+"\t"+row.get(r).get(c).get(x1));
                    writer.newLine();
                    System.out.println(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.write(x2+"\t"+row.get(r).get(c).get(x2));
                    writer.newLine();
                    System.out.println(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.write(y1+"\t"+row.get(r).get(c).get(y1));
                    writer.newLine();
                    System.out.println(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.write(y2+"\t"+row.get(r).get(c).get(y2));
                    writer.newLine();

                     */
                }

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(writer!=null) {
                writer.close();
                System.out.println("### DONE! ###");
            }
        } catch (IOException ioException) {
            System.out.println("Error!");
        }
    }
}
