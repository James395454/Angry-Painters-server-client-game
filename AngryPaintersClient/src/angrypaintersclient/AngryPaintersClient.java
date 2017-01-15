/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package angrypaintersclient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Jawes
 */
public class AngryPaintersClient extends javax.swing.JFrame implements Runnable, KeyListener {

    JPanel panel;
    BufferedImage bf;
    Image redhouse, bluehouse, redpainter, bluepainter, bird;
    Toolkit toolkit;
    int framewidth, frameheight, dx, dy, counthit, linex, liney, count, counthit2, bx = 1, by = 1, startx;
    Thread t1;
    ImageIcon background;
    Thread thread;
    boolean gameover, startpainting, moveredballoon, yourturn, redballoonvisible, select, selectmouse, win, moveblueballoon, blueballoonvisible, hitbird;
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

    public AngryPaintersClient() {
        initComponents();
        startpage();
    }

    public void startpage() {
        t1 = new Thread(this);
        t1.start();
        wait = new JLabel();
        wait.setText("If you start a game server, you must wait for another player to join!");
        count = 0;
        win = false;
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
        yourturn = false;
        startpainting = false;

        framewidth = 1938;
        frameheight = 1020;
        house1 = new house(100, frameheight - 300, 300, 300);
        house2 = new house(framewidth - 400, frameheight - 300, 300, 300);
        linex = house2.x - 25;
        liney = house2.y - 155;
        bird1 = new Bird(house1.x + 275 + 200, house1.y - 155 + 200, 50, 50);
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
        g2.draw(new Line2D.Float(linex, liney, linex - dx, liney + dy));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1938, 1020));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(92, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addGap(100, 100, 100))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(232, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void textinit() {
        jButton1.setText("Shoot!");
    }

    public void makeserver() {
        isserver = true;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            //System.exit(1);

        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        try {

            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        yourturn = true;
    }

    public void makeclient() {
        isclient = true;
        try {
            kkSocket = new Socket("localhost", 4444);
        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            out = new PrintWriter(kkSocket.getOutputStream(), true);

        } catch (IOException ex) {
            Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            //System.out.print("Null");
        }
        yourturn = false;
    }

    void checkblue() {
        if (select) {
            if ((blueballoon.y + blueballoon.height + dy > bird1.y) && (blueballoon.y + blueballoon.height + dy <= bird1.y + bird1.height) && (((blueballoon.x >= bird1.x) && (blueballoon.x < bird1.x + bird1.width)) || ((blueballoon.x < bird1.x) && (blueballoon.x + blueballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((blueballoon.y < bird1.y + bird1.height) && (blueballoon.y >= bird1.y) && (((blueballoon.x >= bird1.x) && (blueballoon.x < bird1.x + bird1.width)) || ((blueballoon.x < bird1.x) && (blueballoon.x + blueballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((blueballoon.y + blueballoon.height > bird1.y) && (blueballoon.y + blueballoon.height <= bird1.y + bird1.height) && (((blueballoon.x >= bird1.x) && (blueballoon.x < bird1.x + bird1.width)) || ((blueballoon.x < bird1.x) && (blueballoon.x + blueballoon.width > bird1.x)))) {
                hitbird = true;
            }
            if ((blueballoon.x < bird1.x + bird1.width) && (blueballoon.x >= bird1.x) && (((blueballoon.y >= bird1.y) && (blueballoon.y < bird1.y + bird1.height)) || ((blueballoon.y < bird1.y) && (blueballoon.y + blueballoon.height > bird1.y)))) {
                hitbird = true;
            }
        }
    }

    void movebirds() {

        try {
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
        } catch (NullPointerException e) {
        }
    }

    public void moveballoon() {
        if (!hitbird) {
            blueballoon.x -= dx;
            blueballoon.y += dy;
        } else {
            blueballoon.y += dy;
        }
        //try
        //{

        out.println(Integer.toString(blueballoon.x));
        out.println(Integer.toString(blueballoon.y));
        //}catch (NullPointerException e){
        //}

        if ((blueballoon.x + blueballoon.width >= framewidth) || (blueballoon.y <= 0) || (blueballoon.y + blueballoon.height >= frameheight) || (blueballoon.x <= 0)) {
            blueballoonvisible = false;
            blueballoon = new balloon(house2.x - 25, house2.y - 155, 50, 50);
            moveblueballoon = false;
            yourturn = false;
            hitbird = false;
            jLabel3.setText("Red Player's Turn!");
            dx = 50;
            dy = 0;
            requestFocus();
        } else if ((blueballoon.x < house1.x + house1.width) && (blueballoon.x >= house1.x) && (((blueballoon.y >= house1.y) && (blueballoon.y < house1.y + house1.height)) || ((blueballoon.y < house1.y) && (blueballoon.y + blueballoon.height > house1.y)))) {
            blueballoonvisible = false;
            blueballoon = new balloon(house2.x - 25, house2.y - 155, 50, 50);
            moveblueballoon = false;
            yourturn = false;

            jLabel3.setText("Red Player's Turn!");
            dx = 50;
            dy = 0;
            requestFocus();
            if (counthit < 4) {
                counthit++;
                redhouse = toolkit.getImage("Red" + Integer.toString(counthit) + ".png");
            } else {
                redhouse = toolkit.getImage("Blue1.png");
                gameover = true;
                win = true;
                jLabel3.setText("GameOver");
                jLabel2.setText("You Win!");
            }
        }
    }

    public void checkredballoon() {
        try {
            if ((redballoon.x + redballoon.width >= framewidth) || (redballoon.y <= 0) || (redballoon.y + redballoon.height >= frameheight)) {
                redballoonvisible = false;
                redballoon = new balloon(house1.x + 275, house1.y - 155, 50, 50);
                moveredballoon = false;
                jLabel3.setText("My Turn!");
                yourturn = true;
                //requestFocus();
            } else if ((redballoon.y + redballoon.height + dy > house2.y) && (redballoon.y + redballoon.height + dy <= house2.y + house2.height) && (((redballoon.x >= house2.x) && (redballoon.x < house2.x + house2.width)) || ((redballoon.x < house2.x) && (redballoon.x + redballoon.width > house2.x)))) {
                redballoonvisible = false;
                redballoon = new balloon(house1.x + 275, house1.y - 155, 50, 50);
                moveredballoon = false;
                jLabel3.setText("My Turn!");
                yourturn = true;
                //requestFocus();
                if (counthit2 < 4) {
                    counthit2++;
                    bluehouse = toolkit.getImage("Blue" + Integer.toString(counthit2) + ".png");
                } else {
                    bluehouse = toolkit.getImage("Red1.png");
                    gameover = true;
                    win = true;
                    jLabel3.setText("GameOver");
                    jLabel2.setText("You Lose!");
                }
            }
        } catch (NullPointerException e) {
        }
    }

    public static void main(String args[]) {

        new AngryPaintersClient().setVisible(true);

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if ((!moveblueballoon) && (select) && (yourturn)) {

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
                remove(b1);
                remove(b2);
                remove(wait);
                remove(panel);
                init();
                initComponents();
                jLabel1.setText("Aim Using the up and down arrow keys!");
                jLabel2.setText("");
                jLabel3.setText("My Turn!");
                //addMouseListener(this);
                //addMouseMotionListener(this);
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
                jLabel3.setText("Red Player's Turn!");
                //addMouseListener(this);
                //addMouseMotionListener(this);
                addKeyListener(this);
                setFocusable(true);
                requestFocus();
                textinit();
                design();
                startpainting = true;
                revalidate();
                select = true;
            } else if ((evt.getActionCommand().equals("shoot")) && (yourturn)) {
                blueballoonvisible = true;
                moveblueballoon = true;

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
            checkblue();
            movebirds();
            checkredballoon();
            try {
                t1.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isserver) {
                try {

                    in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException e) {
                    //System.out.print("Null");
                }
            } else if (isclient) {
                try {

                    in = new BufferedReader(
                            new InputStreamReader(kkSocket.getInputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException e) {
                    //System.out.print("Null");
                }
            }
            try {
                if ((in != null) && (!yourturn)) {
                    redballoon.x = Integer.parseInt(in.readLine());
                    redballoon.y = Integer.parseInt(in.readLine());
                    redballoonvisible = true;
                    //moveredballoon = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(AngryPaintersClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e) {
                //System.out.print("Null");
            }
            if (moveblueballoon) {
                moveballoon();
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
        //hrow new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
