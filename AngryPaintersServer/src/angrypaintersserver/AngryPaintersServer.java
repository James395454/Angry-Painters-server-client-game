/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angrypaintersserver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/**
 *
 * @author Jawes
 */
public class AngryPaintersServer extends javax.swing.JFrame implements Runnable, KeyListener {

    JPanel panel;
    BufferedImage bf;
    Image redhouse, bluehouse, redpainter, bluepainter, bird;
    Toolkit toolkit;
    int framewidth, frameheight, dx, dy, counthit, linex, liney, counthit2, bx, startx, starty;
    Thread t1;
    ImageIcon background;
    Thread thread;
    boolean gameover, startpainting, moveredballoon, yourturn, redballoonvisible, select, selectmouse, win, moveblueballoon, blueballoonvisible, hitbird, select2 = false;
    JButton b1, b2;
    JLabel wait;

    house house1, house2;
    balloon redballoon, blueballoon;
    Bird bird1;

    PrintWriter out;
    BufferedReader in;
    Socket clientSocket;
    ServerSocket serverSocket;
    Socket kkSocket;
    boolean isserver, isclient;

    public AngryPaintersServer() {

        initComponents();

        startpage();
    }

    public void startpage() {
        t1 = new Thread(this);
        t1.start();
        wait = new JLabel();
        wait.setText("If you start a game server, you must wait for another player to join!");
        win = false;
        bx = 1;
        counthit = 1;
        counthit2 = 1;
        moveredballoon = false;
        moveblueballoon = false;
        selectmouse = false;
        select = false;
        redballoonvisible = false;
        blueballoonvisible = false;
        dx = 50;
        dy = 0;
        yourturn = true;
        startpainting = false;
        hitbird = false;

        framewidth = 1938;
        frameheight = 1020;
        house1 = new house(100, frameheight - 300, 300, 300);
        house2 = new house(framewidth - 400, frameheight - 300, 300, 300);
        linex = house1.x + 275;
        liney = house1.y - 155;
        bird1 = new Bird(linex + 200, liney + 200, 50, 50);
        startx = bird1.x;
        redballoon = new balloon(house1.x + 275, house1.y - 155, 60, 60);
        blueballoon = new balloon(house2.x - 25, house2.y - 155, 60, 60);
        isserver = false;
        isclient = false;
        kkSocket = null;
        clientSocket = null;
        serverSocket = null;
        setSize(framewidth, frameheight);
        panel = new JPanel();
        b1 = new JButton();
        b2 = new JButton();

        b1.setText("Start Game Server");
        b2.setText("Join Game Server");
        b1.setActionCommand("start");
        b2.setActionCommand("join");

        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionperformed(evt);
            }
        });
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionperformed(evt);
            }
        });

        panel.add(b1);
        panel.add(b2);
        panel.add(wait);
        setContentPane(panel);

        gameover = false;

    }

    public void init() {
        setSize(framewidth, frameheight);
        toolkit = Toolkit.getDefaultToolkit();
        redhouse = toolkit.getImage("Red1.png");
        redpainter = toolkit.getImage("redpainter.jpg");
        bluepainter = toolkit.getImage("bluepainter.jpg");
        bluehouse = toolkit.getImage("Blue1.png");
        background = new ImageIcon("Background.jpg");
        bird = toolkit.getImage("spaceship (2).jpg");
        clientSocket = null;
        serverSocket = null;
    }

    public void design() {
        jButton1.setActionCommand("shoot");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionperformed(evt);
            }
        });

    }

    @Override
    public void paint(Graphics g) {
        if (startpainting) {
            jLabel1.repaint();
            jLabel2.repaint();
            jLabel3.repaint();
            bf = new BufferedImage(framewidth, frameheight, BufferedImage.TYPE_INT_RGB);
            try {
                noflickering(bf.getGraphics());
                g.drawImage(bf, 0, 0, null);
            } catch (Exception ex) {

            }
        } else {
            b1.repaint();
            b2.repaint();
            wait.repaint();
        }
    }

    void noflickering(Graphics g) {
        super.paint(g);
        jButton1.repaint();
        g.setColor(Color.red);
        g.drawImage(background.getImage(), 0, 0, framewidth, frameheight, this);
        g.drawImage(redhouse, house1.x, house1.y, house1.width, house1.height, this);
        g.drawImage(bluehouse, house2.x, house2.y, house2.width, house2.height, this);
        g.drawImage(redpainter, 100 + 200, frameheight - 300 - 100, 100, 100, this);
        g.drawImage(bluepainter, framewidth - 400, frameheight - 300 - 100, 100, 100, this);
        if (yourturn) {
            g.drawImage(bird, bird1.x, bird1.y, bird1.width, bird1.height, this);
        }
        if (redballoonvisible) {
            g.fillOval(redballoon.x, redballoon.y, redballoon.width, redballoon.height);

        }
        if (blueballoonvisible) {
            g.setColor(Color.blue);
            g.fillOval(blueballoon.x, blueballoon.y, blueballoon.width, blueballoon.height);
        }
        g.setColor(Color.black);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(10));
        g2.draw(new Line2D.Float(linex, liney, linex + dx, liney + dy));
        //g.drawLine(linex, liney, linex + dx, liney + dy);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1938, 1020));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jButton1.setText("jButton1");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("jLabel1");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 51));
        jLabel2.setText("jLabel2");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(259, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(267, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

    }//GEN-LAST:event_formKeyPressed
    public void textinit() {
        jButton1.setText("Shoot!");
    }

    public void makeserver() {    //if server

        isserver = true;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            //System.exit(1);

        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        select2 = true;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        try {

            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        yourturn = true;
    }

    public void makeclient() {  //if client
        isclient = true;
        try {
            kkSocket = new Socket("localhost", 4444);
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out = new PrintWriter(kkSocket.getOutputStream(), true);

        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        yourturn = false;
        select2 = true;
    }

    void checkred() {   //check collision with bird
        if (select) {
            if ((redballoon.y + redballoon.height + dy > bird1.y) && (redballoon.y + redballoon.height + dy <= bird1.y + bird1.height) && (((redballoon.x >= bird1.x) && (redballoon.x < bird1.x + bird1.width)) || ((redballoon.x < bird1.x) && (redballoon.x + redballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((redballoon.y < bird1.y + bird1.height) && (redballoon.y >= bird1.y) && (((redballoon.x >= bird1.x) && (redballoon.x < bird1.x + bird1.width)) || ((redballoon.x < bird1.x) && (redballoon.x + redballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((redballoon.y + redballoon.height > bird1.y) && (redballoon.y + redballoon.height <= bird1.y + bird1.height) && (((redballoon.x >= bird1.x) && (redballoon.x < bird1.x + bird1.width)) || ((redballoon.x < bird1.x) && (redballoon.x + redballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((redballoon.x < bird1.x + bird1.width) && (redballoon.x >= bird1.x) && (((redballoon.y >= bird1.y) && (redballoon.y < bird1.y + bird1.height)) || ((redballoon.y < bird1.y) && (redballoon.y + redballoon.height > bird1.y)))) {
                hitbird = true;
            }
        }
    }
    /* void checkblue() {
     if ((blueballoon.y + blueballoon.height + dy > bird1.y) && (blueballoon.y + blueballoon.height + dy <= bird1.y + bird1.height) && (((blueballoon.x >= bird1.x) && (blueballoon.x < bird1.x + bird1.width)) || ((blueballoon.x < bird1.x) && (blueballoon.x + blueballoon.width > bird1.x)))) {
     hitbirdblue = true;
     }
     if ((blueballoon.y < bird1.y + bird1.height) && (blueballoon.y >= bird1.y) && (((blueballoon.x >= bird1.x) && (blueballoon.x < bird1.x + bird1.width)) || ((blueballoon.x < bird1.x) && (blueballoon.x + blueballoon.width > bird1.x)))) {
     hitbirdblue = true;
     }
     }*/

    void movebirds() {    //move bird

        if (select) {
            //System.out.print("here");
            if ((bird1.x <= 1400) && (bx == 1)) {
                bird = toolkit.getImage("spaceship (2).jpg");
                bx = 1;
                bird1.x += (50 * bx);
            } else {
                bx = -1;
            }
            if ((bird1.x >= startx) && (bx == -1)) {
                bird1.x += (50 * bx);
                bird = toolkit.getImage("spaceship (2)left.jpg");
            } else {
                bx = 1;
            }
            // if (bird1.x>=startx)
        }
    }

    public void moveballoon() {   //move balloon
        if (!hitbird) {
            redballoon.x += dx;
            redballoon.y += dy;
        } else {
            redballoon.y += dy;
        }
        //System.out.print("redx" + redballoon.x);

        out.println(Integer.toString(redballoon.x));
        out.println(Integer.toString(redballoon.y));
        if ((redballoon.x + redballoon.width >= framewidth) || (redballoon.y <= 0) || (redballoon.y + redballoon.height >= frameheight)) {
            redballoonvisible = false;
            redballoon = new balloon(house1.x + 275, house1.y - 155, 50, 50);
            moveredballoon = false;
            //moveblueballoon=true;
            //blueballoonvisible=true;
            yourturn = false;
            jLabel3.setText("Blue Player's Turn!");
            dx = 50;
            dy = 0;
            hitbird = false;
            requestFocus();
        } else if ((redballoon.y + redballoon.height + dy > house2.y) && (redballoon.y + redballoon.height + dy <= house2.y + house2.height) && (((redballoon.x >= house2.x) && (redballoon.x < house2.x + house2.width)) || ((redballoon.x < house2.x) && (redballoon.x + redballoon.width > house2.x)))) {
            redballoonvisible = false;
            yourturn = false;
            jLabel3.setText("Blue Player's Turn!");
            redballoon = new balloon(house1.x + 275, house1.y - 155, 50, 50);
            moveredballoon = false;
            //blueballoonvisible=true;
            dx = 50;
            dy = 0;
            requestFocus();
            if (counthit < 4) {
                counthit++;
                bluehouse = toolkit.getImage("Blue" + Integer.toString(counthit) + ".png");
            } else {
                bluehouse = toolkit.getImage("Red1.png");
                gameover = true;
                win = true;
                jLabel3.setText("GameOver");
                jLabel2.setText("You Win!");
            }
        }
    }

    public void checkblueballoon() {  //check status of blue balloon
        try {
            if ((blueballoon.x + blueballoon.width >= framewidth) || (blueballoon.y <= 0) || (blueballoon.y + blueballoon.height >= frameheight) || (blueballoon.x <= 0)) {
                blueballoonvisible = false;
                blueballoon = new balloon(house2.x - 25, house2.y - 155, 50, 50);
                yourturn = true;
                jLabel3.setText("My Turn!");
            } else if ((blueballoon.x < house1.x + house1.width) && (blueballoon.x >= house1.x) && (((blueballoon.y >= house1.y) && (blueballoon.y < house1.y + house1.height)) || ((blueballoon.y < house1.y) && (blueballoon.y + blueballoon.height > house1.y)))) {
                blueballoonvisible = false;
                blueballoon = new balloon(house2.x - 25, house2.y - 155, 50, 50);
                yourturn = true;
                jLabel3.setText("My Turn!");
                if (counthit2 < 4) {
                    counthit2++;
                    redhouse = toolkit.getImage("Red" + Integer.toString(counthit2) + ".png");
                } else {
                    redhouse = toolkit.getImage("Blue1.png");
                    gameover = true;
                    win = true;
                    jLabel3.setText("GameOver");
                    jLabel2.setText("You Lose!");
                }
            }

        } catch (NullPointerException e) {
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        new AngryPaintersServer().setVisible(true);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.print("hit");
        if ((!moveredballoon) && (select) && (yourturn)) {
            if ((e.getKeyCode() == KeyEvent.VK_UP) && (dy > -90)) {
                dy -= 5;
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN) && (dy < 90)) {
                dy += 5;
            }
        }
    }

    public void actionperformed(ActionEvent evt) {
        if (!gameover) {
            if (evt.getActionCommand().equals("start")) {
                makeserver();
                remove(wait);
                remove(b1);
                remove(b2);
                remove(panel);
                init();
                initComponents();
                jLabel1.setText("Aim Using the up and down arrow keys!");
                jLabel2.setText("");
                jLabel3.setText("My Turn!");
                addKeyListener(this);
                setFocusable(true);
                requestFocus();
                textinit();
                design();
                startpainting = true;
                revalidate();
                select = true;

            } else if (evt.getActionCommand().equals("join")) {
                makeclient();
                remove(b1);
                remove(b2);
                remove(wait);
                remove(panel);
                init();
                initComponents();
                jLabel1.setText("Aim Using the up and down arrow keys!");
                jLabel2.setText("");
                jLabel3.setText("Blue Player's Turn!");
                addKeyListener(this);
                setFocusable(true);
                requestFocus();
                textinit();
                design();
                startpainting = true;
                revalidate();
                select = true;
            } else if ((evt.getActionCommand().equals("shoot")) && (yourturn)) {

                redballoonvisible = true;
                moveredballoon = true;

            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
    @Override
    public void run() {
        while (!gameover) {
            checkred();
            movebirds();
            checkblueballoon();
            try {
                t1.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isserver) {
                try {

                    in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException e) {
                    //System.out.print("Null");
                }
            } else if (isclient) {
                try {

                    in = new BufferedReader(
                            new InputStreamReader(kkSocket.getInputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException e) {
                    //System.out.print("Null");
                }
            }
            try {
                //  System.out.print(yourturn);
                if ((in != null) && (!yourturn)) {
                    blueballoon.x = Integer.parseInt(in.readLine());   //read from other app
                    blueballoon.y = Integer.parseInt(in.readLine());   //read from other app
                    blueballoonvisible = true;
                    // moveblueballoon = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(AngryPaintersServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e) {
                //System.out.print("Null");
            }
            if (moveredballoon) {
                moveballoon();   //move this balloon
            }
            repaint();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
