package snake;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Snake extends JFrame{
    JPanel fondo;
    static JPanel panelFlechitas[] = new JPanel[4];
    static JPanel panelEspacio = new JPanel();
    static JLabel labelPuntos = new JLabel();
    static Color verde = new Color(115,255,55); //65,145,31
    boolean up, left, down, right, lastUp, lastLeft, lastDown, lastRight, volverEmpezar, mecate;
    int puntos;
    Font font;
    
    public Snake(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        if(width==1366){ //1366, 768
            width = 1000; height = 720;
        }
        else if(width==1920){ //1920, 1080
            width = 1280; height = 1060;
        }
        initVar();
        setTitle("Snake - Graphics 2D");
        fondo = new JPanel();
        getContentPane().add(fondo);
        addComponents(width,height);
        addKeyListener(new FlechitasEvent());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(width,height));
        setVisible(true);
        pack();
    }
    
    public  final void initVar(){
        for (int i = 0; i < 4; i++) {
            panelFlechitas[i] = new JPanel();
        }
        up=left=down=lastUp=lastLeft=lastDown=false;
        right=lastRight=true;
        volverEmpezar=mecate=false;
        puntos=0;
        font=new Font("Gadugi", Font.PLAIN, 100);
    }
    
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        
        Snake s = new Snake();
        System.out.println("Res: "+width+"x"+height);
    }
    
    public final void addComponents(int w, int h){
        fondo.setLayout(new FlowLayout());
        
        //Panel Snake
        SnakePanel sp;
        JPanel panelon = new JPanel();
        if(w==1000){ //1366, 768
            sp = new SnakePanel(700,710);
            panelon.setPreferredSize(new Dimension(700,700));
            panelon.add(sp);
        }
        else if(w==1280){ //1920, 1080
            sp = new SnakePanel(1000,1040);
            panelon.setPreferredSize(new Dimension(1000,1000+6));
            panelon.add(sp);
        }
        
        //Instanciar caja
        Box caja = Box.createVerticalBox();
        
        //Panel Flechitas
        JPanel panelFlechas = new JPanel();
        panelFlechas.setLayout(new GridLayout(2,3,5,5));
        panelFlechas.setPreferredSize(new Dimension(240,160));
        JLabel labelFlechitas[] = new JLabel[4];
        labelFlechitas[0] = new JLabel("↑");
        labelFlechitas[1] = new JLabel("←");
        labelFlechitas[2] = new JLabel("↓");
        labelFlechitas[3] = new JLabel("→");
        for (int i = 0; i < 4; i++) {
            panelFlechitas[i].setBackground(Color.GRAY);
            labelFlechitas[i].setFont(new Font("Consolas", Font.BOLD, 60)); 
            labelFlechitas[i].setForeground(Color.BLACK); 
            panelFlechitas[i].add(labelFlechitas[i]);
        }
        panelFlechas.add(new JPanel());
        panelFlechas.add(panelFlechitas[0]);
        panelFlechas.add(new JPanel());
        panelFlechas.add(panelFlechitas[1]);
        panelFlechas.add(panelFlechitas[2]);
        panelFlechas.add(panelFlechitas[3]);
        
        //Panel espacio
        panelEspacio.setBackground(Color.GRAY);
        panelEspacio.setPreferredSize(new Dimension(240,60));
        
        //Panel puntos
        JPanel panelPuntos = new JPanel();
        labelPuntos.setForeground(Color.BLACK);
        labelPuntos.setText(""+puntos);
        labelPuntos.setFont(font); 
        panelPuntos.add(labelPuntos);
        
        //Añadir a la caja
        caja.add(Box.createVerticalStrut(100));
        caja.add(panelFlechas);
        caja.add(Box.createVerticalStrut(15));
        caja.add(panelEspacio);
        caja.add(Box.createVerticalStrut(5));
        caja.add(panelPuntos);
        
        
        //Añadir al fondo
        fondo.add(panelon);
        fondo.add(caja);
    }
    
    class SnakePanel extends JPanel implements ActionListener{
        int w,h,p,x,y,xp,yp,size;
        final int stopMilis=100;
        ArrayList<Cuadro> snake;
        boolean agarroPunto;
        Cuadro punto;
        GlyphVector gv;
        
        public SnakePanel(int w, int h){
            this.w=w;
            this.h=h;
            setPreferredSize(new Dimension(w,h+6));
            p=w/10;
            snake = new ArrayList<>();
            snake.add(new Cuadro(3,4));
            snake.add(new Cuadro(2,4));
            snake.add(new Cuadro(1,4));
            agarroPunto=true;
            Timer t = new Timer(stopMilis,this);
            t.start();
        }
        
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            FontRenderContext frc = g2d.getFontRenderContext();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            Shape shape;
            
            //Volver a empezar
            if(volverEmpezar){
                snake.clear();
                snake.add(new Cuadro(3,4));
                snake.add(new Cuadro(2,4));
                snake.add(new Cuadro(1,4));
                mecate=false;
                agarroPunto=false;
                volverEmpezar=false;
                up=left=down=lastUp=lastLeft=lastDown=false;
                right=lastRight=true;
                puntos=0;
                labelPuntos.setText(""+puntos);
            }
            
            //Pintar fondo negro
            g2d.setColor(new Color(21,21,21));
            shape = new Rectangle2D.Double(0,0,w,w);
            g2d.fill(shape);
            size=snake.size();
            
            if(!mecate){
                //Pintar punto 
                if(agarroPunto){
                    agarroPunto=false;
                    boolean igual;
                    do{
                        igual=false;
                        xp=(int)(Math.random()*(10-0)-0);
                        yp=(int)(Math.random()*(10-0)-0);
                        for (int i = 0; i < size; i++) {
                            x=snake.get(i).getX();
                            y=snake.get(i).getY();
                            if(xp==x && yp==y){
                                igual=true;
                                break;
                            }
                        }
                    }while(igual);
                    punto = new Cuadro(xp,yp);
                    g2d.setColor(Color.WHITE);
                    shape = new Rectangle2D.Double(xp*p,yp*p,p,p);
                    g2d.fill(shape);
                }else{
                    xp=punto.getX();
                    yp=punto.getY();
                    g2d.setColor(Color.WHITE);
                    shape = new Rectangle2D.Double(xp*p,yp*p,p,p);
                    g2d.fill(shape);
                }

                //Mover culebra
                x=snake.get(0).getX();
                y=snake.get(0).getY();
                if(up){
                    y-=1;
                    if(y==-1) y=9;
                    lastLeft=lastDown=lastRight=false;
                    lastUp=true;
                }
                else if(left){
                    x-=1;
                    if(x==-1) x=9;
                    lastUp=lastDown=lastRight=false;
                    lastLeft=true;
                }
                else if(down){
                    y+=1;
                    if(y==10) y=0;
                    lastUp=lastLeft=lastRight=false;
                    lastDown=true;
                }
                else if(right){
                    x+=1;
                    if(x==10) x=0;
                    lastUp=lastLeft=lastDown=false;
                    lastRight=true;
                }
                //Perdio
                for (int i = 1; i < size; i++) {
                    if(x==snake.get(i).getX() && y==snake.get(i).getY()){
                        mecate=true;
                        break;
                    }
                }
                //Agarro el punto?
                if(!mecate){
                    if(x==xp && y==yp){ //Si
                        agarroPunto=true;
                        puntos++;
                    }
                    else{ //No
                        snake.remove(size-1);
                    }
                    snake.add(0, new Cuadro(x,y));
                    
                    //Pintar culebra - Color inicial = 115,255,55 - Color final = 37,84,17
                    int parteR=(int)(115-37)/(size-1);
                    int parteG=(int)(255-84)/(size-1);
                    int parteB=(int)(55-17)/(size-1);
                    g2d.setColor(new Color(115,255,55));
                    x=snake.get(0).getX();
                    y=snake.get(0).getY();
                    shape = new Rectangle2D.Double(x*p,y*p,p,p);
                    g2d.fill(shape);
                    for (int i = 1; i < size-1; i++) {
                        g2d.setColor(new Color(115-parteR*i,255-parteG*i,55-parteB*i));
                        x=snake.get(i).getX();
                        y=snake.get(i).getY();
                        shape = new Rectangle2D.Double(x*p,y*p,p,p);
                        g2d.fill(shape);
                    }
                    g2d.setColor(new Color(115-parteR*(size-1),255-parteG*(size-1),55-parteB*(size-1)));
                    x=snake.get(size-1).getX();
                    y=snake.get(size-1).getY();
                    shape = new Rectangle2D.Double(x*p,y*p,p,p);
                    g2d.fill(shape);
                }
                labelPuntos.setText(""+puntos);
                
                //Pintar cuadricula
                g2d.setColor(new Color(0,0,0));
                int stroke = 8;
                g2d.setStroke(new BasicStroke(stroke));
                for (int i = 0; i < 11; i++) {
                    shape = new Line2D.Double(0,i*p,w,i*p);
                    g2d.draw(shape);
                    //shape = new Line2D.Double(i*p,0,h,i*p);
                    shape = new Line2D.Double(i*p,0,i*p,h);
                    g2d.draw(shape);
                }
            }
            else{
                //Pintar culebra
                g2d.setColor(Color.RED);
                for (int i = 0; i < size; i++) {
                    x=snake.get(i).getX();
                    y=snake.get(i).getY();
                    shape = new Rectangle2D.Double(x*p,y*p,p,p);
                    g2d.fill(shape);
                }
                
                //Pintar cuadricula
                g2d.setColor(new Color(0,0,0));
                int stroke = 8;
                g2d.setStroke(new BasicStroke(stroke));
                for (int i = 0; i < 11; i++) {
                    shape = new Line2D.Double(0,i*p,w,i*p);
                    g2d.draw(shape);
                    //shape = new Line2D.Double(i*p,0,h,i*p);
                    shape = new Line2D.Double(i*p,0,i*p,h);
                    g2d.draw(shape);
                }
                
                //Pintar mensaje alentador
                g2d.setColor(Color.WHITE);
                gv = font.createGlyphVector(frc, "Estas bien mkt");
                shape = gv.getOutline(25,300);
                g2d.fill(shape);
                gv = font.createGlyphVector(frc, "Apachurrale espacio");
                shape = gv.getOutline(25,400);
                g2d.fill(shape);
                gv = font.createGlyphVector(frc, "pa volver a empezar");
                shape = gv.getOutline(25,500);
                g2d.fill(shape);
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent ae){
            repaint();
        }
    }
    
    class Cuadro{
        int x, y;
        public Cuadro(int x, int y){
            this.x=x;
            this.y=y;
        }
        public int getX(){
            return x;
        }
        public int getY(){
            return y;
        }
    }
    
    class FlechitasEvent implements KeyListener{
        @Override
        public void keyTyped(KeyEvent ke) {}
        @Override
        public void keyPressed(KeyEvent ke) {
            switch(ke.getKeyCode()){
                case 87: case 119: case KeyEvent.VK_UP:
                    up=left=down=right=false;
                    panelFlechitas[0].setBackground(verde);
                    if(!lastDown) up=true;
                    else down=true;
                    break;
                case 65: case 97:  case KeyEvent.VK_LEFT:
                    up=left=down=right=false;
                    panelFlechitas[1].setBackground(verde);
                    if(!lastRight) left=true;
                    else right=true;
                    break;
                case 83: case 115: case KeyEvent.VK_DOWN:
                    up=left=down=right=false;
                    panelFlechitas[2].setBackground(verde);
                    if(!lastUp) down=true;
                    else up=true;
                    break;
                case 68: case 100: case KeyEvent.VK_RIGHT:
                    up=left=down=right=false;
                    panelFlechitas[3].setBackground(verde);
                    if(!lastLeft) right=true;
                    else left=true;
                    break;
                case 32:
                    panelEspacio.setBackground(verde);
                    if(mecate) volverEmpezar=true;
            }
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            switch(ke.getKeyCode()){
                case 87: case 119: case KeyEvent.VK_UP:
                    panelFlechitas[0].setBackground(Color.GRAY);
                    break;
                case 65: case 97:  case KeyEvent.VK_LEFT:
                    panelFlechitas[1].setBackground(Color.GRAY);
                    break;
                case 83: case 115: case KeyEvent.VK_DOWN:
                    panelFlechitas[2].setBackground(Color.GRAY);
                    break;
                case 68: case 100: case KeyEvent.VK_RIGHT:
                    panelFlechitas[3].setBackground(Color.GRAY);
                    break;
                case 32:
                    panelEspacio.setBackground(Color.GRAY);
            }
        }
    }
}