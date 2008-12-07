/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spikies;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
//import java.net.*;

/**
 *
 * @author mocksuwannatat
 */
public class Main implements ParametersPanelListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      main = new Main();
    }
    
    public Main() {
      initData();
      initComponents();
      initParametersPanel(); 
      initStates();
    }
    
    public void initStates() {
      playing = false;
      frameNo = 0;
    }
    
    public void initData() {
      nodes = new Vector<Node>();
      messages = new Vector<Message>();
      name2node = new Hashtable<String, Node>();
      loadFile();
      frameNo = 0;
    }
    
    private void loadFile() {
      String fname = C.DEFAULT_INPUT_FILE;
      Scanner sc = null;
      try {
        sc = new Scanner(new File(fname));
      } catch (IOException e) {
        System.out.printf("Grrr! can't open %s\n", fname);
        return;
      }
      messages.clear();
      int lineNo = 0;
      while (sc.hasNextLine()) {
        lineNo++;
        String line = sc.nextLine();
        if (lineNo > 1) {
          Message msg = new Message(line);
          if (messageFilterOK(msg)) {
            messages.add(msg);
          }
        }
        if (lineNo % 1000 == 0) {
          System.out.printf("loading line #%d\n", lineNo);
        }
      }
      System.out.printf("loading line #%d\n", lineNo);
      System.out.printf("number of messages (after filter) = %d\n",
          messages.size());
    }
    
    public void initComponents() {
      // drawing panel
      last_frame_time = frame_time = System.currentTimeMillis();
      panel = new JPanel() {
        public void paint(Graphics g) {
          panelPaint(g);
        }
      };
      frame = new JFrame("Packets Visualizer (better name here)");
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(panel, BorderLayout.CENTER);
      panel.setPreferredSize(new Dimension(C.PANEL_WIDTH, C.PANEL_HEIGHT));
//      panelPainting = false;
      
      // buttons 
      JPanel bottomPane = new JPanel();
      bottomPane.setLayout(new FlowLayout());
      btnReset = new JButton("Reset");
      btnPlayPause = new JButton(C.PLAY);
//      JButton btnPause = new JButton("Pause");
      btnNext = new JButton("Next");
      JLabel label2 = new JLabel("|");
      JLabel label3 = new JLabel("|");
      btnSave = new JButton(C.SAVE_LOCATIONS);
      btnLoad = new JButton(C.LOAD_LOCATIONS);
      JLabel label1 = new JLabel("frame: ");
      lblFrameNo = new JLabel("0");
      cbShowForce = new JCheckBox("/");
      cbShowForce.setSelected(false);
      cbShowForce.setForeground(Color.RED);
      bottomPane.add(btnReset);
      bottomPane.add(btnPlayPause);
//      bottomPane.add(btnPause);
      bottomPane.add(btnNext);
      bottomPane.add(label1);
      bottomPane.add(lblFrameNo);
      bottomPane.add(label2);
      bottomPane.add(btnSave);
      bottomPane.add(btnLoad);
      bottomPane.add(label3);
      bottomPane.add(cbShowForce);
      frame.getContentPane().add(bottomPane, BorderLayout.SOUTH);
      ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          onButtonClicked(evt);
        }
      };
      btnReset.addActionListener(al);
//      btnPause.addActionListener(al);
      btnPlayPause.addActionListener(al);
      btnNext.addActionListener(al);
      btnSave.addActionListener(al);
      btnLoad.addActionListener(al);
      
      // event handlers
      addMouseHandler();
      
      // show it
      frame.pack();
      frame.setVisible(true);
    }

    private void onButtonClicked(ActionEvent evt) {
      String cmd = evt.getActionCommand();
//      System.out.printf("cmd = %s\n", cmd); 
      
      if (cmd.equals("Reset")) {
        resetToFrameZero();
        frameNo = 0;
      } else if (cmd.equals(C.PLAY)) {
        play();
      } else if (cmd.equals(C.PAUSE)) {
        pause();
      } else if (cmd.equals("Next")) {
        goToNextFrame();
      } else if (cmd.equals(C.SAVE_LOCATIONS)) {
        commandSaveLocations();
      } else if (cmd.equals(C.LOAD_LOCATIONS)) {
        commandLoadLocations();
      } else {
        System.out.printf("Grrr unknown command [%s]!\n", cmd); 
      }
    }
    
    private void commandSaveLocations() {
      String fname = C.NODES_INFO_FILE;
      PrintWriter out = null;
      try { 
        out = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
      int num = nodes.size();
      for (int i = 0; i < num; i++) {
        Node node = nodes.get(i);
        out.printf("%s\n%f %f\n", node.name, node.location.x, node.location.y);
      }
      System.out.printf("%d nodes saved to %s", num, fname);
      
      out.close();
    }
    
    private void commandLoadLocations() {
      String fname = C.NODES_INFO_FILE;
      Scanner sc = new Scanner(fname);
      try { 
        sc = new Scanner(new File(fname));
      } catch (IOException e) {
        System.out.printf("Grrr. Node location file doesn't exist. (%s) " +
            "Will start from scratch.\n",
            fname);
        return;
      }
      while (sc.hasNext()) {
        String name = sc.nextLine();
        float x = sc.nextFloat();
        float y = sc.nextFloat();
        sc.nextLine();
        Node node = findOrCreateNode(name);
        node.location.x = x;
        node.location.y = y;
      }
      redrawThings();
    }
    
    private void addMouseHandler() {
      MyMouseHandler handler = new MyMouseHandler();
      panel.addMouseListener(handler);
      panel.addMouseMotionListener(handler);
    }
    
    private void play() {
      if (playing) return;
      
      TimerTask task = new TimerTask() {
        public void run() {
          goToNextFrame();
        }
      };
      playerTimer = new Timer();
      int period = C.DEFAULT_DELAY_BETWEEN_FRAMES_MS;
      playing = true;
      btnPlayPause.setText(C.PAUSE);
      playerTimer.schedule(task, 0, period);
    }
    
    private void pause() {
      if (!playing) return;
      
      System.out.printf("pausing\n");
      playerTimer.cancel();
      playing = false;
      btnPlayPause.setText(C.PLAY);
    }
    
    private void resetToFrameZero() {
      pause();
      frameNo = 0;
      // maybe we shouldn't do this?
      resetAllNodes();
//      nodes.clear();
//      name2node.clear();
      redrawThings();
    }
    
    private void resetAllNodes() { 
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        node.resetData();
      }
    }
    
    private void goToNextFrame() {
      if (frameNo >= messages.size() - 1) {
        pause();
        return;
      }
      frameNo ++;

      Message msg = messages.get(frameNo);
//      System.out.printf("msg = %s", msg);
      Node src = findOrCreateNode(msg.src);
      Node dst = findOrCreateNode(msg.dst);
      src.sendsMessage(msg);
      dst.receivesMessage(msg);
      
      redrawThings();
    }
    
    private boolean messageFilterOK(Message msg) {
      return
//          (
//          msg.protocol.equals("TCP") ||
//          msg.protocol.equals("TCPENCAP") ||
//          msg.protocol.equals("UDP") ||
//          msg.protocol.equals("HTTP")
//          )
//          &&
          looksLikeIP(msg.src) 
          &&
          looksLikeIP(msg.dst) 
          ;
    }
    
    public boolean looksLikeIP(String s) {
      //TODO make it real
      String[] a = s.replace(".", "\n").split("\n");
      boolean accepted = a.length == 4; 
      
      if (!accepted) {
        System.out.printf("REJECTING IP=%s  a.len=%d\n", s, a.length);
      }
      
      return accepted;
    }
    
    public Node findNode(String name) {
      return name2node.get(name);
    }
    
    private synchronized Node findOrCreateNode(String name) {
      Node node = name2node.get(name);
      if (node != null) return node;
      
      node = new Node(main, name);
      name2node.put(name, node);
      nodes.add(node);
//      System.out.printf("# nodes = %d\n", nodes.size());
      
      return node;
    }
    
    private void redrawThings() {
      panel.repaint();
      lblFrameNo.setText("" + frameNo);
    }
    
    private void panelPaint(Graphics g) {
//      if (panelPainting) return;
//      panelPainting = true;
      last_frame_time = frame_time;
      frame_time = System.currentTimeMillis();
      delta_time = last_frame_time - frame_time;
      
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
          RenderingHints.VALUE_ANTIALIAS_ON);
      
      //clear screen
      g2.setColor(Color.WHITE);
      g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
//      g2.setColor(Color.ORANGE);
//      g2.drawOval(200+frameNo, 200, 100, 100);
      
      //draw nodes
      maxVolSoFar = 0;
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        node.removeOldMessages();
        node.physics_calculate_netforce();
      }
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        node.physics_time_forward(delta_time);
      }
      if (cbShowForce.isSelected()) {
        for (int i = 0; i < nodes.size(); i++) {
          Node node = nodes.get(i);
          node.drawNetForce(g2);
        }
      }
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        node.drawOutgoingEdges(g2);
      }
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        node.drawNode(g2);
      }
      drawToolTipAt(g2, mouseX, mouseY);
      
//      panelPainting = false;
      Thread t = new Thread() {
        public void run() {
          panel.repaint();
        }
      };
      t.start();
    }
    
    public int getPanelWidth() { 
      return C.PANEL_WIDTH;
    }
    
    public int getPanelHeight() { 
      return C.PANEL_HEIGHT;
    }
    
    private Node findNodeUnder(float x, float y) {
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        if (node.hitTest(x, y)) {
          return node;
        }
      }
      return null;
    }
    
    private void drawToolTipAt(Graphics2D g2, float x, float y) {
      for (int i = 0; i < nodes.size(); i++) {
        Node node = nodes.get(i);
        if (node.drawTip(g2, x, y)) {
          return;
        }
      }
    }
    
    private class MyMouseHandler 
        extends MouseAdapter
        implements MouseMotionListener
    {
      
      private float pressX, pressY;
      private Node targetNode;

      public void mousePressed(MouseEvent evt) {
        //TODO
        float mx, my;
        int button;
        mx = evt.getX();
        my = evt.getY();
        button = evt.getButton();
        
        pressX = mx;
        pressY = my;
        targetNode = findNodeUnder(mx, my);
        
//        System.out.printf("mouse pressed at targetNode = %s\n",
//            targetNode);
        mouseX = 0;
        mouseY = 0;
      }
      
      public void mouseReleased(MouseEvent evt) {
        if (targetNode != null) { 
          targetNode.printMessages();
        }
      }
      
      public void mouseDragged(MouseEvent evt) {
        float mx, my;
        int button;
        mx = evt.getX();
        my = evt.getY();
        button = evt.getButton();
        //TODO
        
        if (targetNode == null) return;
        
        // left button ?
        if (button == evt.BUTTON1) {
          float dx, dy;
          dx = mx - pressX;
          dy = my - pressY; 
          targetNode.moveBy(dx, dy);
          pressX = mx;
          pressY = my;
          panel.repaint();
        }
      }

      public void mouseMoved(MouseEvent evt) {
        float mx, my;
        int button;
        mx = evt.getX();
        my = evt.getY();
        button = evt.getButton();

        mouseX = mx;
        mouseY = my;
        if (!playing) panel.repaint();
      }
      
    }
//    
//    private void initParametersPanel() {
//      paramFrame = new JFrame("Parameters");
//      JPanel pnl = new JPanel();
//      paramFrame.getContentPane().add(pnl);
//      
//      //TODO
//      
////      paramFrame.setVisible(true);
//    }
    
//    private JFrame paramFrame; 
    
    private void initParametersPanel() {
      paramsPanel = new ParametersPanel();
      paramsPanel.setListener(this);
      frame.getContentPane().add(paramsPanel, BorderLayout.EAST);
      valuesApplied();
      frame.pack();
    }
    
    public void valuesApplied() {
      DEFAULT_T = paramsPanel.getFloat("DEFAULT_T", C.DEFAULT_T);
      BYTES_PER_KG = paramsPanel.getFloat("BYTES_PER_KG", C.BYTES_PER_KG);
      TIME_FACTOR = paramsPanel.getFloat("TIME_FACTOR", C.TIME_FACTOR);
      SPRING_RESTING_DISTANCE = paramsPanel.getFloat("SPRING_RESTING_DISTANCE", C.SPRING_RESTING_DISTANCE);
      SPRING_CONSTANT = paramsPanel.getFloat("SPRING_CONSTANT", C.SPRING_CONSTANT);
      DAMPING_FACTOR = paramsPanel.getFloat("DAMPING_FACTOR", C.DAMPING_FACTOR);
      NODES_REPEL_FACTOR = paramsPanel.getFloat("NODES_REPEL_FACTOR", C.NODES_REPEL_FACTOR);
      SPEED_LIMIT = paramsPanel.getFloat("SPEED_LIMIT", C.SPEED_LIMIT);
      // = paramsPanel.getFloat("", C.);
    }
    
    private ParametersPanel paramsPanel; 
    float DEFAULT_T;
    float BYTES_PER_KG;
    float TIME_FACTOR;
    float SPRING_RESTING_DISTANCE;
    float SPRING_CONSTANT;
    float DAMPING_FACTOR;
    float NODES_REPEL_FACTOR;
    float SPEED_LIMIT;
    
    
    public static Main main = null;
    private JFrame frame;
    private JPanel panel;
    private JButton btnReset, btnPlayPause, btnNext, btnSave, btnLoad;
    private JLabel lblFrameNo;
//    private boolean panelPainting;
    long last_frame_time, frame_time, delta_time;
    float latestTime;
    private JCheckBox cbShowForce; 

    Vector<Node> nodes;
    private Vector<Message> messages;
    private Hashtable<String, Node> name2node; 
    int maxVolSoFar;
    float mouseX, mouseY;
    
    // player controls
    private int frameNo;
    private boolean playing;
    private Timer playerTimer;

    
}
