package dev.praneetha.shuntcalc;


import java.awt.*;
import java.awt.event.*;
import javax.script.*;


class Code extends Frame implements ActionListener

{
    //Declaring a label
    Label l;
    ExpressionEvaluator ee1=new ExpressionEvaluator();
    Main m=new Main();

    //declaring buttons
    Button b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,plus,minus,equal,bs,bc,leftb,rightb,div,mul;
    String updatedText;

    public void gui()
    {
        Frame f = new Frame("Calculator");
        GridLayout gridLayout = new GridLayout(0,3);
        f.setLayout(gridLayout);
        f.setSize(300, 400);
       

        //adding label
        l=new Label("");
        f.add(new Label());
        f.add(l);
        f.add(new Label());

    //adding buttons
  b1=new Button("1");
  b1.addActionListener(this);
  f.add(b1);
  b2=new Button("2");
  b2.addActionListener(this);
  f.add(b2);
  b3=new Button("3");
  b3.addActionListener(this);
  f.add(b3);
  b4=new Button("4");
   b4.addActionListener(this);
  f.add(b4);
  b5=new Button("5");
  b5.addActionListener(this);
  f.add(b5);
  b6=new Button("6");
  b6.addActionListener(this);
  f.add(b6);
  b7=new Button("7");
  b7.addActionListener(this);
  f.add(b7);
  b8=new Button("8");
  b8.addActionListener(this);
  f.add(b8);
  b9=new Button("9");
  b9.addActionListener(this);
  f.add(b9);
  rightb=new Button("(");
  rightb.addActionListener(this);
  f.add(rightb);
  b0=new Button("0");
  b0.addActionListener(this);
  f.add(b0);
  leftb=new Button(")");
  leftb.addActionListener(this);
  f.add(leftb);
 



  //symbols
  plus=new Button("+");
  plus.addActionListener(this);
  f.add(plus);
  minus=new Button("-");
  minus.addActionListener(this);
  f.add(minus);
  mul=new Button("*");
  mul.addActionListener(this);
  f.add(mul);
  div=new Button("/");
  div.addActionListener(this);
  f.add(div);

  bs=new Button("Clear");
  bs.addActionListener(this);
  f.add(bs);


  equal=new Button("=");
  equal.addActionListener(this);
  f.add(equal);

 /*  bc=new Button("Back");
  bc.addActionListener(this);
  f.add(bc);*/
  



        
       

       

        f.setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        Button c = (Button) e.getSource();
        String buttonText = c.getLabel();
        String previous = l.getText();
       if(buttonText!="=" && buttonText!= "Clear")
       {
        updatedText= previous + buttonText + " ";
        l.setText(updatedText);
       }
       
        if(buttonText=="="){
            boolean k=m.check(updatedText);
            if(k==true){
           l.setText(String.valueOf(ee1.stringss(updatedText)));
            }
            else{
                l.setText("Invalid");
            }

        }
        if(buttonText=="Clear"){
            l.setText("");
        }
       /*  if(buttonText=="Back"){
            previous = previous.substring(0, previous.length() - 1);
            l.setText(previous);
        }*/

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                f.dispose();
            }
        });
        
        
    }
    
    


}



class Calculator{
    public static void main(String st[]){
        Code c=new Code();
        c.gui();
        

        
    }

}
