/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication10;

import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import org.jlab.clas.detector.DetectorCollection;
import org.root.basic.EmbeddedCanvas;
import org.root.func.F1D;
import org.root.histogram.GraphErrors;
import org.root.histogram.H1D;
import org.root.histogram.H2D;

/**
 *
 * @author fanchini
 */
public class fitBookAndFile {
    CanvasBook book  = new CanvasBook(4,4);
   JFrame fr = new JFrame();
    
    public void fitBookAndFile(){
    }
    
//    public void addlistener(){
//        book.crtListener();
//    }
    
    
 public void fitBookArray(ArrayList<DetectorCollection<H1D>> h){
         JFrame     frame = new JFrame();
        this.fr=frame;
        for(int key : h.get(0).getComponents(0, 0)){
            for(int i=0; i< h.size(); i++){
                if(h.get(i).hasEntry(0, 0, key)) {
                    h.get(i).get(0, 0, key).setLineColor(i+1);
                    h.get(i).get(0, 0, key).setLineWidth(2);
                    if(i==0){
                        String tt =h.get(i).get(0,0,key).getName();
                        h.get(i).get(0,0,key).setTitle(tt);
                        book.add(h.get(i).get(0,0,key)," ");
                    }
                    else book.add(h.get(i).get(0,0,key),"same");
                } 
            }     
        }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);
       
        
    }    
    
  public void fitBook(DetectorCollection<H1D> h, DetectorCollection<F1D> f, int color){
       JFrame     frame = new JFrame();
       this.fr=frame;
        for(int key : h.getComponents(0, 0)) { 
            if(h.hasEntry(0, 0, key)) {
               h.get(0, 0, key).setLineColor(color);
               String tt ="Comp. "+key;
               h.get(0,0,key).setTitle(tt);
               book.add(h.get(0,0,key)," ");
               if(f.hasEntry(0, 0, key)==false){
                   F1D ff = new F1D("landau", 0.0, 40.0);
                   ff.setLineColor(color);
                   book.add(ff,"same"); 
               }
               else{
                   f.get(0, 0, key).setLineColor(color);
                   book.add(f.get(0,0,key),"same");
               } 
            }
         }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);
       System.out.println("BASTA: "+book.getComponentListeners().length+"  "+
       book.getActionMap());
        
    }   
  
//  public ComponentListener[] getlistener(){
//      return book.getComponentListeners();
//  }
  
 public void fitBook(DetectorCollection<H1D> h, int color){
      JFrame     frame = new JFrame();
      this.fr=frame;
        if(color==0)color=1;
      
        
        for(int key : h.getComponents(0, 0)) { 
            if(h.hasEntry(0, 0, key)) {
               h.get(0, 0, key).setLineColor(color);
               book.add(h.get(0,0,key)," ");
               
            }
         }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);

    }      
 
 public void fitBook(DetectorCollection<H2D> h){
        JFrame     frame = new JFrame();
        this.fr=frame;
        for(int key : h.getComponents(0, 0)) { 
            if(h.hasEntry(0, 0, key)) {
               book.add(h.get(0,0,key)," ");
            }
         }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);
    
 }
 
 public void fitGraphBook(DetectorCollection<GraphErrors> h){
         JFrame     frame = new JFrame();
         this.fr=frame;
        for(int key : h.getComponents(0, 0)) { 
            if(h.hasEntry(0, 0, key)) {
               book.add(h.get(0,0,key)," ");
               
            }
         }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);
      
 }
 
 public void fitGraphBook(DetectorCollection<GraphErrors> h, String axis, int min,int max){
         JFrame     frame = new JFrame();
       this.fr=frame;
        for(int key : h.getComponents(0, 0)) { 
            if(h.hasEntry(0, 0, key)) {
               book.add(h.get(0,0,key)," ");
               book.fixAxisRange(axis, min, max);
            }
         }
        book.crtListener();
        book.reset();
        frame.add(book);
        frame.pack();
        frame.setVisible(true);
        book.drawNextBack(false);
       
        
 }
 
  public EmbeddedCanvas getCanvas(){
      return this.book.canvas;
  }
   public JFrame getFrame(){
      return this.fr;
  }
  
  public void setname(String name){
      System.out.println("pippo2: "+name);
      this.fr.setName(name);
      this.fr.setTitle(name);
      book.setname(name);
  }
 
    
}
