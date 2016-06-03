/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication10;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.root.base.IDataSet;
import org.root.basic.EmbeddedCanvas;
import org.root.basic.EmbeddedPad;
import org.root.func.F1D;
import org.root.histogram.H1D;


/**
 *
 * @author fanchini
 */


public class CanvasBook extends JPanel implements ActionListener {
    
        private List<IDataSet> container = new ArrayList<IDataSet>();
        private List<String>   options   = new ArrayList<String>();
        public  EmbeddedCanvas canvas    = new EmbeddedCanvas();
        private int            nDivisionsX = 1;
        private int            nDivisionsY = 1;
        private int            currentPosition = 0;
        private boolean backward = false; 
        JComboBox comboDivide = null;
        private int elements =1;
        private int popupPad=0;
        JPanel buttonPanel = new JPanel();
        
    public CanvasBook(int dx, int dy){
            super();
//        TStyle.setFrameFillColor(250, 250, 255);
        this.setLayout(new BorderLayout());
        
        this.nDivisionsX = dx;
        this.nDivisionsY = dy;
        
        
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JButton  buttonPrev = new JButton("< Previous");
        JButton  buttonNext = new JButton("Next >");
        JButton  buttonSave = new JButton("Print...");
        buttonNext.addActionListener(this);
        buttonPrev.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonPrev);
        buttonPanel.add(buttonNext);
        buttonPanel.add(buttonSave);
        this.canvas.divide(this.nDivisionsX, this.nDivisionsY);
        
        
    
        JPanel canvasPane = new JPanel();
        canvasPane.setLayout(new BorderLayout());
        canvasPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        canvasPane.add(this.canvas,BorderLayout.CENTER);
        canvasPane.setPreferredSize(new Dimension(1100,900));
        this.add(canvasPane,BorderLayout.CENTER);
        this.add(buttonPanel,BorderLayout.PAGE_END);
        
    }

   

    public void initCanvas() {
        // event canvas
        this.canvas.setGridX(false);
        this.canvas.setGridY(false);
        this.canvas.setAxisFontSize(10);
        this.canvas.setTitleFontSize(16);
        this.canvas.setAxisTitleFontSize(14);
        this.canvas.setStatBoxFontSize(8);
      
    }
    
    
    public void fixAxisRange(String axis, int min, int max){
            this.canvas.setAxisRange(axis, min, max);
    }

    public void add(IDataSet ds, String opt){
        this.container.add(ds);
        this.options.add(opt);
    }
    
    public void drawNextBack(boolean back){
        int npads   = this.nDivisionsX*this.nDivisionsY;
        int counter = 0, npp=1;
        findelement();
        
        if(back){
            this.currentPosition-=(2*this.elements*npads);            
            if(this.currentPosition>=this.container.size() ){
                this.currentPosition = this.container.size()-(2*this.elements*npads);
            }      
            else if (this.currentPosition<=0){ 
                if(this.currentPosition<=-1*npads){
                    this.currentPosition= this.container.size()-(1*this.elements*npads);
                }
                else{     
                this.currentPosition = 0;
                }
                findelement();
            }
        }
        else{
            if(this.currentPosition>=this.container.size() || this.currentPosition<=0){
            this.currentPosition = 0;
            findelement();
            }
           }
       
         
        this.canvas.divide(this.nDivisionsX,this.nDivisionsY);
        if(counter==0) crtListener();
        this.canvas.cd(counter);
        
        while(this.currentPosition<this.container.size()&&counter<npads){
            IDataSet ds = this.container.get(this.currentPosition);
            String   op = this.options.get(this.currentPosition);    
       
            if(op.contains("same")==false){
                //System.out.println(" (    ) "  + this.currentPosition + 
                //        " on pad " + counter+"   "+ds.getName());
                this.canvas.cd(counter);
                this.initCanvas();
                this.canvas.draw(ds,op);
                
            } else {
                //System.out.println(" (same) "  + this.currentPosition + 
                //        " on pad " + counter+"   "+ds.getName());
                this.canvas.draw(ds, "op+same");
            }
            
            if(npp==this.elements){
                counter++;
                npp=0;
            }
            this.currentPosition++;
            npp++;
        }
        
    }
   
    public void findelement(){
        int first=0;
        this.elements=1;
        for(int i=this.currentPosition; i<this.container.size(); i++){
            if(this.options.get(i).contains("same") && first<=1){
               this.elements++;
            }
            else if(this.options.get(i).contains("same")==false)first++;
            else break;
        }        
    }
    
    public void reset(){
        this.currentPosition = 0;        
    }

    public void actionPerformed(ActionEvent e) {
       
        if(e.getActionCommand().compareTo("Next >")==0){
            this.backward = false;
            this.drawNextBack(backward);
            crtListener();
        }
        
        if(e.getActionCommand().compareTo("< Previous")==0){
           this.backward = true;
           this.drawNextBack(backward);
           crtListener();
        }
        
        if (e.getActionCommand().compareTo("Print...") == 0) {
            this.printToFile();
           
        }
    }
    
    private void printToFile() {
        // Does not work properly //
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("File.PNG"));
        int returnValue = fc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String outputFileName = fc.getSelectedFile().getAbsolutePath();
            this.canvas.save(outputFileName);
            System.out.println("Saving calibration results to: " + outputFileName);
        }
    }  
    
    public void setname(String name){
        this.canvas.setName(name);
        System.out.println("pippo3: "+this.canvas.getName());
    }
    
    public EmbeddedCanvas getCanvas(){
        return this.canvas;
    }
    
    
    
    public void crtListener(){
        for(int j=0; j<this.canvas.canvasPads.size(); j++){
            System.out.println("pippo4: "+this.canvas.getName());
            String padname = this.canvas.getName()+"j"+j;
            this.canvas.getPad(j).setName(padname);
            this.canvas.addMouseListener(new MousePopupListener());
            this.canvas.getPad(j).addMouseListener(new MousePopupListener());
            //this.canvas.addMouseListener(new MousePopupListener(this.canvas,this.canvas.getPad(j)));
            //this.canvas.getPad(j).addMouseListener(new MousePopupListener(this.canvas,this.canvas.getPad(j)));
            System.out.println("ERICA pad name: "+padname);
        }
        System.out.println("BASTA BASTA: "+
        this.canvas.getFocusListeners().length+"  ");
    }

 
//    class MousePopupListener extends MouseAdapter {
//        EmbeddedCanvas canvas2;
//        EmbeddedPad pad2;
//        public MousePopupListener(EmbeddedCanvas canv,EmbeddedPad pad){
//            this.canvas2 = canv;
//            this.pad2=pad2;
//        }
//                
//        @Override
//        public void mousePressed(MouseEvent e) {
//           checkPopup(e);
//        }
//
//        private void checkPopup(MouseEvent e) {
//            
//            if (e.isPopupTrigger()) {
//                System.out.println("ERICA: Loc on screen: x"+canvas2.getLocationOnScreen().x+"  y"+canvas2.getLocationOnScreen().y+"  "+canvas2.getName());
//                System.out.println("ERICA: new padL:"+canvas2.getPadNumberByXY(e.getXOnScreen()-canvas2.getLocationOnScreen().x,e.getYOnScreen()-canvas2.getLocationOnScreen().y));
//                //System.out.println("ERICA: "+e.getX()+"  "+e.getXOnScreen()+"  paddd: "+canvas.canvas.getCurrentPad()+"  "+canvas.canvas.getPadNumberByXY(e.getX(),e.getY())+"  "+canvas.canvas.getPadNumberByXY(e.getXOnScreen(),e.getYOnScreen()));
//                int popupPad2=canvas2.getPadNumberByXY(e.getXOnScreen()-canvas2.getLocationOnScreen().x,e.getYOnScreen()-canvas2.getLocationOnScreen().y);
//                CustomizeFit refit = new CustomizeFit();
//                refit.FitPanel((H1D)canvas2.getPad(popupPad2).getDataSet(0), (F1D)canvas2.getPad(popupPad2).getDataSet(1), "LQ");
//                canvas2.update(); 
//            }
//        }
//    }

   class MousePopupListener extends MouseAdapter {

        public MousePopupListener(){
          
        }
                
        @Override
        public void mousePressed(MouseEvent e) {
           checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {
                System.out.println("ERICA: Loc on screen: x"+CanvasBook.this.canvas.getLocationOnScreen().x+"  y"+CanvasBook.this.canvas.getLocationOnScreen().y+"  "+CanvasBook.this.canvas.getName());
                System.out.println("ERICA: new padL:"+CanvasBook.this.canvas.getPadNumberByXY(e.getXOnScreen()-CanvasBook.this.canvas.getLocationOnScreen().x,e.getYOnScreen()-CanvasBook.this.canvas.getLocationOnScreen().y));
                //System.out.println("ERICA: "+e.getX()+"  "+e.getXOnScreen()+"  paddd: "+canvas.canvas.getCurrentPad()+"  "+canvas.canvas.getPadNumberByXY(e.getX(),e.getY())+"  "+canvas.canvas.getPadNumberByXY(e.getXOnScreen(),e.getYOnScreen()));
                int popupPad2=CanvasBook.this.canvas.getPadNumberByXY(e.getXOnScreen()-CanvasBook.this.canvas.getLocationOnScreen().x,e.getYOnScreen()-CanvasBook.this.canvas.getLocationOnScreen().y);
                CustomizeFit refit = new CustomizeFit();
                refit.FitPanel((H1D)CanvasBook.this.canvas.getPad(popupPad2).getDataSet(0), (F1D)CanvasBook.this.canvas.getPad(popupPad2).getDataSet(1), "LQ");
                CanvasBook.this.canvas.update(); 
            }
        }
    }
    
}


