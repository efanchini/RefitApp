/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication10;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.jlab.clas.detector.DetectorCollection;
import org.root.basic.EmbeddedCanvas;
import org.root.func.F1D;
import org.root.histogram.H1D;

/**
 *
 * @author fanchini
 */
public class Refit implements ActionListener{
    

    CalibrationData calib = new CalibrationData();
    fitBookAndFile fitbook = new fitBookAndFile();
    CanvasBook book  = new CanvasBook(4,4);
    
    
    ArrayList<DetectorCollection<H1D>> h_E = new ArrayList<DetectorCollection<H1D>>();
    ArrayList<DetectorCollection<F1D>> f_E = new ArrayList<DetectorCollection<F1D>>(); 
    ArrayList file = new ArrayList();
    ArrayList<EmbeddedCanvas> canvas = new ArrayList<EmbeddedCanvas>();
    int RunN;
    int nF;  
 
        public Refit() throws FileNotFoundException{
            // Change only this directory to the place you are copy the 2 hipo files//
            // The file is listing the directory looking for all .hipo files//
            // For each file open a canvas book showing all histor and fits//
            // clicking on each pad with the mouse it appears a button that allows you refit with my function the histograms//
            // but it works only for the last opened canvas book//
        String dir ="/project/Gruppo3/fiber6/fanchini/JLab/FwdT/analysis/Cosmicanalysis/";
        String[] args = new String[1];
        args[0] = "550";
        
        filePars(args,dir);
       
        for(int i=0; i<this.nF; i++){
            String st =(String) this.file.get(i);
            st=st.replace(".hipo","_Fit.txt");
            String filetxt = dir+"Refit2"+st;
            writeFitParFile(filetxt,  f_E.get(i),  h_E.get(i));
            String name ="C"+i;
            fitbook.setname(name);
            fitbook.fitBook(h_E.get(i), f_E.get(i), 1);
            this.canvas.add(this.fitbook.getCanvas());
            //ComponentListener[] cl= fitbook.getlistener();
            //crtListener(this.canvas.get(i));
        }
       crtListener();
       RefitButton();
       
        
        
    }
    
private void crtListener(EmbeddedCanvas ec){  
    ec.addMouseListener(new MousePopupListener(ec));
    for(int i=0; i<ec.canvasPads.size(); i++){
        ec.canvasPads.get(i).addMouseListener(new MousePopupListener(ec));
       }
}
private void crtListener(){  
    for(int j=0; j<this.canvas.size();j++){
        this.fitbook.getFrame().addMouseListener(new MousePopupListener(this.canvas.get(j)));
        this.canvas.get(j).addMouseListener(new MousePopupListener(this.canvas.get(j)));
        for(int i=0; i<this.canvas.get(j).canvasPads.size(); i++){
            this.canvas.get(j).canvasPads.get(i).addMouseListener(new MousePopupListener(this.canvas.get(j)));
       }
    }
}     
    public void RefitButton(){

        JPanel buttonPanel = new JPanel();    
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JButton  buttonRefit = new JButton("Save Refit");
        buttonRefit.addActionListener(this);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonRefit);

        JFrame bframe = new JFrame("OPSSSS");
        bframe.setSize(500,200);
        bframe.add(buttonPanel);
        bframe.pack();
        bframe.setVisible(true);
 
        
    }
    
       
       
 private void writeFitParFile(String filename, DetectorCollection<F1D> f, DetectorCollection<H1D> h) throws FileNotFoundException{
        //to be tested getparerror not working//
        // Format //
        // Cosmic fit with Landau + Exp //
        // Sector Layer Component N. Events NFiPar Amp E Sigma(E) err_sigma(E) P0  P1  P2  Chi^2 NDF// 
        PrintWriter fout = new PrintWriter(filename);
        int nevt=0;
        String dd = "";
        F1D fg = new F1D("landau",0,40);
        fg.setParameter(0, 0);
        fg.setParameter(1, 0);
        fg.setParameter(2, 0);
        fout.printf("Sector \t Layer \t Component \t N.Events \t NParametersFit \t Pi \t ErrPi \t ... \t fct_XMin \t fct_XMax \t Chi2 \t NDF \n");
        for(int key : h.getComponents(0, 0)) {
            
            if(f.hasEntry(0, 0, key)==false)f.add(0, 0, key, fg);
            F1D ff = f.get(0, 0, key);
            H1D hh= h.get(0, 0, key);
            if(hh.getEntries()!=0)hh.fit(ff);
            nevt=h.get(0, 0, key).getEntries();
            fout.printf("0 \t 0 \t"+key+"\t"+h.get(0, 0, key).getEntries()+"\t"+ff.getNParams()+"\t");
            for(int i=0; i<ff.getNParams(); i++){
                dd=String.format("%.3f",ff.getParameter(i));
                fout.printf(dd+"\t");
                dd=String.format("%.4f",ff.getParError(i));
                fout.printf(dd+"\t");
            }
            dd=String.format("%.1f",ff.getDataRegion().MINIMUM_X);
            fout.printf(dd+"\t");
            dd=String.format("%.1f",ff.getDataRegion().MAXIMUM_X);
            fout.printf(dd+"\t");
            dd =String.format("%.3f",ff.getChiSquare(h.get(0, 0, key),"NR"));
            fout.printf(dd+"\t"+ff.getNDF(h.get(0, 0, key).getDataSet())+"\n");  
        }  
        fout.close();
      
        
    }    
       

    
private void filePars(String[] args, String dir){    
        if(args.length!=0 || args.length!=1){
            this.RunN = Integer.parseInt(args[0]);
        }
        else{
            System.out.println("ERROR: No or less arguments set.... 1 arguments needed!");
        }
        files(dir);
}
private void files(String dir){
   String nameS;
   System.out.println("Erica: "+dir+"  "+"Run"+"  "+this.RunN);
    File[] files = new File(dir).listFiles();
    int j=0;
    for (File ff : files) {
        
        if (ff.isFile() && ff.getName().endsWith(".hipo")) {
            System.out.println("Erica: "+files[j]);
            nameS =  dir +ff.getName();
            System.out.println("Erica: "+nameS);
            this.file.add(ff.getName());
            ReadSFile(nameS);
            j++;
            }
    }

    this.nF = file.size();
}


 public void ReadSFile(String filename){
         calib.getFile(filename);
         DetectorCollection<H1D> dc_hE = calib.getCollection("Energy_histo");
         DetectorCollection<F1D> dc_fE = calib.getCollection("Energy_fct");

         this.h_E.add(dc_hE);
         this.f_E.add(dc_fE);
         
    }
 
 private void printToFile() {
        // Does not work properly //
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("File.PNG"));
        int returnValue = fc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String outputFileName = fc.getSelectedFile().getAbsolutePath();
            String st =outputFileName;
            
            System.out.println("Saving calibration results to: " + st);
            for(int i=0; i<this.nF; i++){
                try {
                    st =outputFileName;
                    st=st.replace(".txt","_Fit3-"+i+".txt");
                    writeFitParFile(st,  f_E.get(i),  h_E.get(i));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Refit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    } 
 

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Save Refit") == 0) {
            this.printToFile();
           
        }
    } 
  
  
    public static void main(String[] args) throws FileNotFoundException {     
        Refit analysis = new Refit();
    }
  
    class MousePopupListener extends MouseAdapter {
        EmbeddedCanvas eb = new EmbeddedCanvas();

        public MousePopupListener(EmbeddedCanvas canv){
            this.eb = canv;
        }
                
        public void mousePressed(MouseEvent e) {
           checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            System.out.println("ERICA: "+e.getSource()+"  "+e.getSource().toString()+"  "+eb.getBounds().getLocation());
            
            
            if (e.isPopupTrigger()) {
                System.out.println("ERICA: Loc: "+e.getComponent()+"  "+e.getID()+"  "+e.getSource());
                System.out.println("ERICA: Loc on screen: x"+eb.getLocationOnScreen().x+"  y"+eb.getLocationOnScreen().y+"  "+eb.getName());
                System.out.println("ERICA: new padL:"+eb.getPadNumberByXY(e.getXOnScreen()-eb.getLocationOnScreen().x,e.getYOnScreen()-eb.getLocationOnScreen().y));
                //System.out.println("ERICA: "+e.getX()+"  "+e.getXOnScreen()+"  paddd: "+canvas.canvas.getCurrentPad()+"  "+canvas.canvas.getPadNumberByXY(e.getX(),e.getY())+"  "+canvas.canvas.getPadNumberByXY(e.getXOnScreen(),e.getYOnScreen()));
                int popupPad2=eb.getPadNumberByXY(e.getXOnScreen()-eb.getLocationOnScreen().x,e.getYOnScreen()-eb.getLocationOnScreen().y);
                CustomizeFit refit = new CustomizeFit();
                refit.FitPanel((H1D)eb.getPad(popupPad2).getDataSet(0), (F1D)eb.getPad(popupPad2).getDataSet(1), "LQ");
                eb.update(); 
            }
        }
    }
  
}