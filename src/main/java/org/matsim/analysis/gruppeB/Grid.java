package org.matsim.analysis.gruppeB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        Map<String, Integer>[][] cell = new HashMap[rows][cols];

        for(int r=0; r<rows; r++){
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
        System.out.println("### DONE! ###");
        return cell;
    }

    /** Use this print method "detail" to quickly lookup values for each cell */
    public void printGrid_detail(Map<String, Integer>[][] grid){
        BufferedWriter writer = null;
        System.out.println("### Try to print the grid_detail! ###");
        try {
            // Set name of output file *csv, *txt, ... again from root folder (Gruppe B)
            File file = new File("gruppeB_TXSandCSV/grid_detail.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);

            for(int r=0; r<rows; r++){
                for(int c=0; c<cols; c++){
                    System.out.println("### cell "+r+"|"+c+" ###");
                    writer.write("### cell "+r+"|"+c+" ###");
                    writer.newLine();

                    String[] keys = {"x_start","x_end","y_start","y_end"};
                    for(String s:keys) {
                        System.out.println(s + "\t" + grid[r][c].get(s));
                        writer.write(s + "\t" + grid[r][c].get(s));
                        writer.newLine();
                    }
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

    /** Classic print method for further use in methods AND in Via */
    public void printGrid(Map<String, Integer>[][] grid){
        BufferedWriter writer = null;

        System.out.println("### Try to print the grid! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder (Gruppe B)
            File file = new File("gruppeB_TXSandCSV/grid.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);

            System.out.print("row\tcol\tx_start\ty_start\tx_end\ty_end");
            writer.write("row\tcol\tx_start\ty_start\tx_end\ty_end");

            for(int r=0; r<rows; r++){
                for(int c=0; c<cols; c++){
                    System.out.println();
                    System.out.print(r + "\t" + c);
                    writer.newLine();
                    writer.write(r + "\t" + c);

                    String[] keys = {"x_start","y_start","x_end","y_end"};
                    for(String s:keys) {
                        System.out.print("\t" + grid[r][c].get(s));
                        writer.write("\t" + grid[r][c].get(s));
                    }
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

    /** Fast method to print grid lines for Via */
    /**  !! Somehow not identical with grid lines from classic print method !! */
    public void printGridLines_via(Map<String, Integer>[][] grid){
        BufferedWriter writer = null;

        System.out.println("### Try to print the grid_lines for VIA! ###");

        try {
            // Set name of output file *csv, *txt, ... again from root folder (Gruppe B)
            File file = new File("gruppeB_TXSandCSV/grid4via.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);

            writer.write("x_start\ty_start\tx_end\ty_end");
            writer.newLine();

            for(int r=0; r<rows; r++) {
                System.out.println("### horizontal line "+r+" ###");
                System.out.println(X_MIN + "\t" + grid[r][0].get("y_start") + "\t" + X_MAX + "\t" + grid[r][0].get("y_end"));
                writer.write(X_MIN + "\t" + grid[r][0].get("y_start") + "\t" + X_MAX + "\t" + grid[r][0].get("y_end"));
                writer.newLine();
            }

            for(int c=0; c<cols; c++){
                System.out.println("### vertical line "+c+" ###");
                System.out.println(grid[0][c].get("x_start")+"\t"+Y_MIN+"\t"+grid[0][c].get("x_end")+"\t"+Y_MAX);
                writer.write(grid[0][c].get("x_start")+"\t"+Y_MIN+"\t"+grid[0][c].get("x_end")+"\t"+Y_MAX);
                writer.newLine();
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
