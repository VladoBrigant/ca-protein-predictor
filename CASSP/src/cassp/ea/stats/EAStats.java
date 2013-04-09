/**
*   EAStats.java
*
*   Copyright (c) 2013 Vladimir Brigant
*   This software is distributed under the terms of the GNU General Public License.
*/

package cassp.ea.stats;


import java.util.*;
import java.io.*;

import org.jgnuplot.*;
import org.apache.log4j.*;



/**
* Evolutionary algorithm statistics.
*/
public class EAStats{

    static double MIN_Y_RANGE = 40;
    static double MAX_Y_RANGE = 60;

    static Logger logger = Logger.getLogger(EAStats.class);

    private ArrayList<GenStats> generations;


    public EAStats(){
        this.generations = new ArrayList<GenStats>();
    }

    public void addGenStats(GenStats gs){
        this.generations.add(gs);
    }

    /**
    * Create PNG image of EA evolution with name <name> to directory <dir>.
    */
    public void createImage(String dir, String name){
        File d = new File(dir);
        try{
            dir = d.getCanonicalPath();
        }catch (IOException e){
            System.err.println(e);
        }
        String path = dir + "/" + name;

        // init jgnuplot
        Plot.setGnuplotExecutable("gnuplot");
        Plot.setPlotDirectory(dir);

        // basic configuration
        Plot plot = new Plot();
        plot.setTitle("Fitness value evolution");
        plot.setKey("right bottom box");
        plot.setXLabel("Generation");
        plot.setYLabel("Fitness");
        plot.setRanges("[1:" + this.generations.size() + "] ["
            + EAStats.MIN_Y_RANGE + ":" + EAStats.MAX_Y_RANGE + "]");

        // create tmp file with data
        File tmpFile = null;
        try{
            tmpFile = File.createTempFile("temp",".tmp");
            FileWriter fo = new FileWriter(tmpFile);
            BufferedWriter bw = new BufferedWriter(fo);

            for (int i = 0; i < this.generations.size(); i++) {
                bw.write((this.generations.get(i).getGeneration() + 1) + "\t");
                bw.write(this.generations.get(i).getMean() + "\t");
                bw.write(this.generations.get(i).getMax() + "\t");
                bw.write(String.valueOf(this.generations.get(i).getMin()));
                bw.write("\n");
            }
            bw.close();
        }
        catch (IOException e){
            logger.error("\n" + e);
        }

        plot.pushGraph(new Graph(tmpFile.getAbsolutePath(), "1:2 smooth bezier", Axes.NOT_SPECIFIED,
            "mean", Style.LINES));
        plot.pushGraph(new Graph(tmpFile.getAbsolutePath(), "1:3 smooth bezier", Axes.NOT_SPECIFIED,
            "max", Style.LINES));
        plot.pushGraph(new Graph(tmpFile.getAbsolutePath(), "1:4 smooth bezier", Axes.NOT_SPECIFIED,
            "min", Style.LINES));

        // save image
        plot.setOutput(Terminal.PNG, path, "640, 480");
        try{
            plot.plot();
        }catch (Exception e){
            logger.error("\n" + e);
        }

        tmpFile.deleteOnExit();
    }
}
