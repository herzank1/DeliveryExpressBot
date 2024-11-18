/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.telegram.Xupdate;

/**
 *
 * @author DeliveryExpress
 */
public abstract class Quiz {

    String userId;
    int step;
    boolean finalized;

    public Quiz(String userId) {
        this.userId = userId;
        this.step = 0;
    }

    abstract void execute(Xupdate xupdate);

    void next() {
        this.step += 1;
    }

   
    void goTo(int step) {
        this.step = step;
    }
   
    void back() {
        this.step -= 1;
    }

    void destroy(){
    QuizesControl.destroy(this);
    }
    
    boolean isFinalized() {
        return this.finalized;
    }
    
    void finalized(){
    this.finalized = true;
    }

    @Override
    public String toString() {
        return "Quiz{" + "userId=" + userId + ", step=" + step + ", finalized=" + finalized + '}';
    }
    
    

}
