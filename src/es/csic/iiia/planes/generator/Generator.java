/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2012 Marc Pujol <mpujol@iiia.csic.es>.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute 
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.planes.generator;

import es.csic.iiia.planes.definition.DPlane;
import es.csic.iiia.planes.definition.DProblem;
import es.csic.iiia.planes.definition.DStation;
import es.csic.iiia.planes.definition.DTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
public class Generator {
    
    private long duration = 3600*24*30;
    private int width = 10000;
    private int height = 10000;
    private int num_planes = 10;
    private int num_tasks = 60*24*30;
    private int num_stations = 1;
    private int num_crisis = 4;
    
    private Random r = new Random();
    
    public static void main(String[] args) {
        Generator t = new Generator();
        t.run();
    }
    
    public void run() {
        DProblem p = createProblemDefinition();
        addPlanes(p);
        addTasks(p);
        addStations(p);
        
        writeProblem(p);
    }
    
    private void writeProblem(DProblem p) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(System.out, p);
        } catch (IOException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private DProblem createProblemDefinition() {
        DProblem p = new DProblem();
        
        p.setDuration(duration);
        p.setWidth(width);
        p.setHeight(height);
        
        return p;
    }
    
    private void addPlanes(DProblem p) {
        ArrayList<DPlane> planes = p.getPlanes();
        for (int i=0;i<num_planes;i++) {
            DPlane pl = new DPlane();
            pl.setSpeed(50/3.6);
            pl.setX(r.nextInt(p.getWidth()));
            pl.setY(r.nextInt(p.getHeight()));
            pl.setBattery(3600*3);
            pl.setBattery(5000);
            planes.add(pl);
        }
    }

    private void addTasks(DProblem p) {
        
        // Create the tasks, randomly located
        ArrayList<DTask> tasks = p.getTasks();
        for (int i=0;i<num_tasks;i++) {
            DTask t = new DTask();
            t.setX(r.nextInt(p.getWidth()));
            t.setY(r.nextInt(p.getHeight()));
            tasks.add(t);
        }
        
        // Set task times. Use the crisis model for now.
        
        // How is it done?
        // 1. Create a "base" uniform distribution between 0 and duration
        RealDistribution[] distributions = new RealDistribution[num_crisis+1];
        distributions[0] = new UniformRealDistribution(0, duration);
        distributions[0].reseedRandomGenerator(r.nextLong());
        
        // 2. Create one gaussian distribution for each crisis, trying to
        //    spread them out through time.
        for (int i=1; i<=num_crisis; i++) {
            double mean = r.nextDouble()*duration;
            double std = (duration/(double)num_crisis)*0.05;
            distributions[i] = new NormalDistribution(mean, std);
            distributions[i].reseedRandomGenerator(r.nextLong());
        }
        
        // 3. Uniformly sample task times from these distributions
        for (DTask t : tasks) {
            final int i = (int)(r.nextDouble()*(num_crisis+1));
            long time = (long)distributions[i].sample();
            while (time < 0 || time > duration) {
                time = (long)distributions[i].sample();
            }
            t.setTime(time);
        }
        
        // 4. Debug stuff
        printTaskHistogram(tasks);
    }
    
    private void printTaskHistogram(ArrayList<DTask> tasks) {
        
        double[] data = new double[tasks.size()];
        for (int i=0; i<tasks.size(); i++) {
            data[i] = tasks.get(i).getTime();
        }
        
        EmpiricalDistribution d = new EmpiricalDistribution(100);
        d.load(data);
        
        for (SummaryStatistics stats : d.getBinStats()) {
            StringBuilder buf = new StringBuilder();
            for (int i=0, len=Math.round(stats.getN()/10f); i<len; i++) {
                buf.append('#');
            }
            System.err.println(buf);
        }
    }
    
    private void addStations(DProblem p) {
        ArrayList<DStation> stations = p.getStations();
        for (int i=0; i<num_stations; i++) {
            DStation st = new DStation();
            st.setX(r.nextInt(p.getWidth()));
            st.setY(r.nextInt(p.getHeight()));
            stations.add(st);
        }
    }
    
}
