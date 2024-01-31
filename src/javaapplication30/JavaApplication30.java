package javaapplication30;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

class Ex5 extends JFrame{
   
   GamePanel p;   // ��������1
   GamePanel2 p2;   // ��������2
   JLabel timeLabel;   // �ð��� ǥ���� ���̺�
    Timer timer;   // Ÿ�̸�
    JLabel resultLabel;   // ���ȭ��
    JButton startButton;   // ���۹�ư
    JButton homeButton;   // Ȩ��ư
    JButton nextStageButton = new JButton("Next Stage");   // ���� �������� ��ư
   
    Ex5(){
        this.setTitle("��� ����");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        
        // start ��ư
        ImageIcon startImg = new ImageIcon("start.png");
        startButton = new JButton(startImg);
        startButton.setBounds(0, 0, startImg.getIconWidth(), startImg.getIconHeight());
        this.add(startButton);
        
        // ���� �г�
        p = new GamePanel();
        p.setBounds(0, 0, 400, 300);
        p.setVisible(false);
        
        p2 = new GamePanel2();
        p2.setBounds(0, 0, 400, 300);
        p2.setVisible(false);
        
        startButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
              startButton.setVisible(false);
              p.setVisible(true);
              p.startGame();
              startTimer();   // Ÿ�̸� ����
           }
        });
        
        timeLabel = new JLabel("Time: 30");   // �ʱ� �ð� ����
        timeLabel.setBounds(10, 220, 80, 20);
        timeLabel.setVisible(true);
        this.add(timeLabel);
        
        resultLabel = new JLabel();
        resultLabel.setBounds(0, 0, 400, 300);
        resultLabel.setVisible(false);
        this.add(resultLabel);
        
        this.add(p);
        this.add(p2);
        p2.setVisible(false);
        this.setLocationRelativeTo(null);
        this.setSize(400, 300);
        this.setResizable(false);
        this.setVisible(true);
    }

    
    // Ÿ�̸� ���� �޼ҵ�
    public void startTimer() {
       int timeLimit = 30;   // Ÿ�̸� ���� �ð� ����(�� ����)
       
       timer = new Timer(1000, new ActionListener() {
          int time = timeLimit;
          
          @Override
          public void actionPerformed(ActionEvent e) {
             if (time > 0) {
                time--;
                timeLabel.setText("Time: " + time);
                timeLabel.setVisible(true);   // ���̺��� ���̵��� ����
             }
             else {
                timer.stop();   // ���� �ð��� ����Ǹ� Ÿ�̸� ����
                timeLabel.setText("Time: 0");
                timeLabel.setVisible(false);
                
                // ������ Ȯ���ϰ� ������ �̹��� ǥ��
                if (p.score >= 300) {
                    ImageIcon gameClearImg = new ImageIcon("GameClear.png");
                    resultLabel.setIcon(gameClearImg);
                    resultLabel.add(createNextStageButton());
                    nextStageButton.setVisible(true);
                    resultLabel.add(createHomeButton());
                } else if (p2.score >= 200) {
                    ImageIcon gameClearImg = new ImageIcon("GameClear.png");
                    resultLabel.setIcon(gameClearImg);
                    if (p.resultLabel != null) {
                        ((Window) p.resultLabel).setVisible(false);
                    }
                    resultLabel.add(createHomeButton());
                    nextStageButton.setVisible(false);
                } else {
                    ImageIcon gameOverImg = new ImageIcon("GameOver.png");
                    resultLabel.setIcon(gameOverImg);
                    resultLabel.add(createHomeButton());
                    nextStageButton.setVisible(false);
                }

                resultLabel.setVisible(true);

                
                // ���� ���߱�
                p.stopGame();
                p2.stopGame();
             }
          }
       });
       
       timer.start();   // Ÿ�̸� ����
    }
    
    
    // Next Stage ��ư ����
    public JButton createNextStageButton() {
       nextStageButton = new JButton("Next Stage");
       nextStageButton.setBounds(150, 180, 100, 30);
       
       nextStageButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             // ���� ���������� �̵��ϴ� �ڵ� �߰�
             p.score = 0;
             p.setVisible(false);
             resultLabel.setVisible(false);
             resultLabel.setText("Score: 0");
             p2.setVisible(true);
             p2.startGame();
             startTimer();
          }
       });      
       nextStageButton.setVisible(false);
       return nextStageButton;
    }
    
    // Home ��ư ����
    public JButton createHomeButton() {
       homeButton = new JButton("Home");
       homeButton.setBounds(150, 220, 100, 30);
       
       homeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             // Ȩ���� �̵��ϴ� �ڵ�
             dispose();
             new Ex5();
          }
       });
       
       return homeButton;
    }

}

class GamePanel extends JPanel {
   
   protected Object resultLabel;

public void paintComponent(Graphics g) {
      Dimension d = getSize();
      ImageIcon back = new ImageIcon("background.png");   // ��� �̹���
      g.drawImage(back.getImage(), 0, 0, d.width, d.height, null);
   }
   
    Ex5 parentFrame;   // �θ� ������
    TargetThread targetThread;
    JLabel base;   // ���ñ��
    JLabel bullet;   // �Ѿ�
    JLabel target;   // ��ǥ��
    JLabel explosion;   // ���� ȿ��
    
    int score;   // ��������
    JLabel scoreLabel;   // ������ ǥ���� ���̺�
    boolean running;   // ���� ���� ���θ� �����ϴ� ����
    
    GamePanel(){
       
       score = 0;   // ���� �ʱ�ȭ
       running = false;   // ���� ���� ���� �ʱ�ȭ
       
       // ������ ǥ���� ���̺� ����
       scoreLabel = new JLabel("Score: 0");
        scoreLabel.setBounds(10, 240, 80, 20);
        this.add(scoreLabel);
       
       
       // ���� ��� �̹���
        this.setLayout(null);
        ImageIcon img3 = new ImageIcon("roundShot.png");
        base = new JLabel(img3);
        base.setSize(img3.getIconWidth(), img3.getIconHeight());
        //base.setOpaque(true);
        //base.setBackground(Color.black);
        
        // Ÿ�� �̹���
        ImageIcon img = new ImageIcon("red monster.png");
        target = new JLabel(img);
        //�̹��� ũ�⸸ŭ ���̺� ũ�� ����
        target.setSize(img.getIconWidth(),img.getIconHeight());
        
        // �Ѿ� �̹���
        ImageIcon img2 = new ImageIcon("bullet2.png");
        bullet = new JLabel(img2);
        bullet.setSize(img2.getIconWidth(),img2.getIconHeight());
        //bullet.setOpaque(true);
        //bullet.setBackground(Color.red);
        
        this.add(base);
        this.add(target);
        this.add(bullet);
       
        
        ImageIcon explosionImg = new ImageIcon("explo_1.png");
        explosion = new JLabel(explosionImg);
        explosion.setSize(explosionImg.getIconWidth(), explosionImg.getIconHeight());
        explosion.setVisible(false); // �ʱ⿡�� ���� �̹����� ������ �ʵ��� ����
        this.add(explosion);
        //URL url = Ex5.class.getResource("LASER.wav");
        //sound = Applet.newAudioClip(url);
    }
    
    public void startGame(){
       running = true;   // ���� ���� ���·� ����
       
        base.setLocation(this.getWidth()/2-17, this.getHeight()-75);
        bullet.setLocation(this.getWidth()/2-5, this.getHeight()-90);
        target.setLocation(0, 0);
        //Ÿ���� �����̴� ������
        targetThread = new TargetThread(target);
        targetThread.start();
        
        //���̽��� ������ �ΰ� ����Ű �Է¿� ���� bullet������ ����
        base.requestFocus();
        base.addKeyListener(new KeyListener(){
            BulletThread bulletThread = null;
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyChar()==KeyEvent.VK_ENTER){
                    //�����尡 �׾��ִ� �������� Ȯ��
                    if(bulletThread==null || !bulletThread.isAlive()){
                        //sound.play();
                        //�Ѿ˷� Ÿ���� ������� Ȯ���ϱ� ���� 2���� ���̺�� Ÿ�ٽ����带 �Ѱ��ش�.
                        bulletThread = new BulletThread(bullet,target,targetThread);
                        bulletThread.start();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
            
            
        });
    }
    
    public void stopGame() {
       running = false;   // ���� ���� ���� ���·� ����
    }
    
    class TargetThread extends Thread{
        JLabel target;
        
        TargetThread(JLabel target){
            this.target=target;
            target.setLocation(0, 0);
        }
        public void run(){
            while(running){
                int x=target.getX()+5;//5�ȼ��� �̵�
                int y=target.getY();
                
                //������ ������ �������
                if(x>GamePanel.this.getWidth())
                    target.setLocation(0, 0);
                //������ �ȿ� ������� 5�ȼ��� �̵�
                else
                    target.setLocation(x, y);
                
                //0.02�ʸ��� �̵�
                try{
                    sleep(20);
                }
                //�����尡 �װԵǸ� �ʱ� ��ġ�� ��ġ�ϰ�, 0.5�ʸ� ��ٸ���.
                catch(Exception e){
                    target.setLocation(0, 0);
                    try{
                        //sleep(500);
                    }
                    catch(Exception e2){}
                }
            }
        }
    }

    
    class BulletThread extends Thread{
        JLabel bullet,target;
        Thread targetThread;
        
        public BulletThread(JLabel bullet, JLabel target, Thread targetThread){
            this.bullet=bullet;
            this.target=target;
            this.targetThread=targetThread;
        }
        
        public void run(){
            while(running){
                if(hit()){//Ÿ���� �¾Ҵٸ�
                   
                   // ���� ����
                   score += 50;
                   scoreLabel.setText("Score: " + score);
                   scoreLabel.setVisible(true);
                    targetThread.interrupt();//Ÿ�� �����带 ���δ�.
                    
                 // �浹�� ��ġ�� ���� �̹��� ǥ��
                    int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
                    int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
                    explosion.setLocation(explosionX, explosionY);
                    explosion.setVisible(true);

                    // 0.3�� ���� ���� �̹��� ǥ��
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // ���� �̹��� �����
                    explosion.setVisible(false);
                    
                    //�Ѿ��� ���� ��ġ�� �̵�
                    bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
                    return;//�Ѿ� �����嵵 ���δ�.
                }
                else{
                    int x=bullet.getX();
                    int y=bullet.getY()-5;//5�ȼ��� ���� �̵��Ѵ�.=�Ѿ� �ӵ��� 5�ȼ�
                    //�Ѿ��� ������ ������ ������ ��
                    if(y<0){
                        //�Ѿ� ���� ��ġ�� �̵�
                        bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
                        return;//�Ѿ� �����带 ���δ�.
                    }
                    //�Ѿ��� ������ �ȿ� ������ 5�ȼ��� �̵��Ѵ�.
                    else
                        bullet.setLocation(x, y);
                }
                //0.02�ʸ��� 5�ȼ��� �̵�
                try{
                    sleep(20);
                }
                
                catch(Exception e){}
            }
        }
        
        private boolean hit(){
            int x=bullet.getX();
            int y=bullet.getY();
            int w=bullet.getWidth();
            int h=bullet.getHeight();
            
            if(targetContains(x,y)
                    ||targetContains(x+w-1,y)
                    ||targetContains(x+w-1,y+h-1)
                    ||targetContains(x,y+h-1))
                return true;
            else
                return false;
        }
        
        private boolean targetContains(int x, int y){
            //Ÿ���� x��ǥ�� �Ѿ� x��ǥ���� �۰ų� ������ �Ѿ� x��ǥ���� Ÿ�� x��ǥ + Ÿ���� ���� ���̰� ũ�� 
            if (target.getX()<=x && x<target.getX()+target.getWidth() 
                    //Ÿ���� y��ǥ�� �Ѿ� y��ǥ���� �۰ų� ������ �Ѿ� y��ǥ���� Ÿ�� y��ǥ + Ÿ���� ���� ���̰� ũ��
                    && target.getY()<=y && y<target.getY()+target.getHeight())
                return true;
            
            else
                return false;
        }
    }
}

class GamePanel2 extends JPanel {
	   
	   public void paintComponent(Graphics g) {
	      Dimension d = getSize();
	      ImageIcon back = new ImageIcon("background.png");   // ��� �̹���
	      g.drawImage(back.getImage(), 0, 0, d.width, d.height, null);
	   }
	   
	    Ex5 parentFrame;   // �θ� ������
	    TargetThread targetThread;
	    TargetThread2 targetThread2;
	    TargetThreadB targetThreadB;
	    JLabel base;   // ���ñ��
	    JLabel bullet;   // �Ѿ�
	    JLabel target;   // ��ǥ��
	    JLabel target2;   // ��ǥ��2
	    JLabel bomb;   // ��ź
	    JLabel explosion;   // ���� ȿ��
	    
	    int score;   // ��������
	    JLabel scoreLabel;   // ������ ǥ���� ���̺�
	    boolean running;   // ���� ���� ���θ� �����ϴ� ����
	    
	    GamePanel2(){
	       
	       score = 0;   // ���� �ʱ�ȭ
	       running = false;   // ���� ���� ���� �ʱ�ȭ
	       
	       // ������ ǥ���� ���̺� ����
	       scoreLabel = new JLabel("Score: 0");
	        scoreLabel.setBounds(10, 240, 80, 20);
	        this.add(scoreLabel);
	       
	       
	       // ���� ��� �̹���
	        this.setLayout(null);
	        ImageIcon img3 = new ImageIcon("roundShot.png");
	        base = new JLabel(img3);
	        base.setSize(img3.getIconWidth(), img3.getIconHeight());
	        //base.setOpaque(true);
	        //base.setBackground(Color.black);
	        
	        // Ÿ�� �̹���
	        ImageIcon img = new ImageIcon("red monster.png");
	        target = new JLabel(img);
	        //�̹��� ũ�⸸ŭ ���̺� ũ�� ����
	        target.setSize(img.getIconWidth(),img.getIconHeight());
	        
	        // Ÿ��2 �̹���
	        ImageIcon img4 = new ImageIcon("colorful monster.png");
	        target2 = new JLabel(img4);
	        //�̹��� ũ�⸸ŭ ���̺� ũ�� ����
	        target2.setSize(img4.getIconWidth(),img4.getIconHeight());
	        
	        // ��ź �̹���
	        ImageIcon bombImg = new ImageIcon("bomb.png");
	        bomb = new JLabel(bombImg);
	        bomb.setSize(bombImg.getIconWidth(), bombImg.getIconHeight());
	        
	        // �Ѿ� �̹���
	        ImageIcon img2 = new ImageIcon("bullet2.png");
	        bullet = new JLabel(img2);
	        bullet.setSize(img2.getIconWidth(),img2.getIconHeight());
	        //bullet.setOpaque(true);
	        //bullet.setBackground(Color.red);
	        
	        this.add(base);
	        this.add(target);
	        this.add(bullet);
	        this.add(target2);
	        this.add(bomb);
	       
	        
	        ImageIcon explosionImg = new ImageIcon("explo_1.png");
	        explosion = new JLabel(explosionImg);
	        explosion.setSize(explosionImg.getIconWidth(), explosionImg.getIconHeight());
	        explosion.setVisible(false); // �ʱ⿡�� ���� �̹����� ������ �ʵ��� ����
	        this.add(explosion);
	        //URL url = Ex5.class.getResource("LASER.wav");
	        //sound = Applet.newAudioClip(url);
	    }
	    
	    public void startGame(){
	       running = true;   // ���� ���� ���·� ����
	       
	        base.setLocation(this.getWidth()/2-17, this.getHeight()-75);
	        bullet.setLocation(this.getWidth()/2-5, this.getHeight()-90);
	        target.setLocation(0, 0);
	        target2.setLocation(365, 20);
	        bomb.setLocation(0, 40);
	        
	        //Ÿ���� �����̴� ������
	        targetThread = new TargetThread(target);
	        targetThread.start();
	        
	        // Ÿ��2�� �����̴� ������
	        targetThread2 = new TargetThread2(target2);
	        targetThread2.start();
	        
	        // bomb�� �����̴� ������
	        targetThreadB = new TargetThreadB(bomb);
	        targetThreadB.start();
	        
	        //���̽��� ������ �ΰ� ����Ű �Է¿� ���� bullet������ ����
	        base.requestFocus();
	        base.addKeyListener(new KeyListener(){
	            BulletThread bulletThread = null;
	            @Override
	            public void keyTyped(KeyEvent ke) {
	            }

	            @Override
	            public void keyPressed(KeyEvent ke) {
	                if(ke.getKeyChar()==KeyEvent.VK_ENTER){
	                    //�����尡 �׾��ִ� �������� Ȯ��
	                    if(bulletThread==null || !bulletThread.isAlive()){
	                        //sound.play();
	                        //�Ѿ˷� Ÿ���� ������� Ȯ���ϱ� ���� 2���� ���̺�� Ÿ�ٽ����带 �Ѱ��ش�.
	                        bulletThread = new BulletThread(bullet,target,target2, bomb, targetThread, targetThread2, targetThreadB);
	                        bulletThread.start();
	                    }
	                }
	            }

	            @Override
	            public void keyReleased(KeyEvent ke) {
	            }
	            
	            
	        });
	    }
	    
	    public void stopGame() {
	       running = false;   // ���� ���� ���� ���·� ����
	    }
	    
	    class TargetThread extends Thread{
	        JLabel target;
	        
	        TargetThread(JLabel target){
	            this.target=target;
	            target.setLocation(0, 0);
	        }
	        public void run(){
	            while(running){
	                int x=target.getX()+5;//5�ȼ��� �̵�
	                int y=target.getY();
	                
	                //������ ������ �������
	                if(x>GamePanel2.this.getWidth())
	                    target.setLocation(0, 0);
	                //������ �ȿ� ������� 5�ȼ��� �̵�
	                else
	                    target.setLocation(x, y);
	                
	                //0.02�ʸ��� �̵�
	                try{
	                    sleep(20);
	                }
	                //�����尡 �װԵǸ� �ʱ� ��ġ�� ��ġ�ϰ�, 0.5�ʸ� ��ٸ���.
	                catch(Exception e){
	                    target.setLocation(0, 0);
	                    try{
	                        //sleep(500);
	                    }
	                    catch(Exception e2){}
	                }
	            }
	        }
	    }
	    
	    class TargetThread2 extends Thread{
	        JLabel target2;
	        
	        TargetThread2(JLabel target2){
	            this.target2=target2;
	            target2.setLocation(365, 20);
	        }
	        public void run(){
	            while(running){
	                int x=target2.getX()-7;//-7�ȼ��� �̵�
	                int y=target2.getY();
	                
	                //������ ������ �������
	                if(x<0)
	                    target2.setLocation(365, 20);
	                //������ �ȿ� ������� -7�ȼ��� �̵�
	                else
	                    target2.setLocation(x, y);
	                
	                //0.02�ʸ��� �̵�
	                try{
	                    sleep(20);
	                }
	                //�����尡 �װԵǸ� �ʱ� ��ġ�� ��ġ�ϰ�, 0.5�ʸ� ��ٸ���.
	                catch(Exception e){
	                    target2.setLocation(365, 20);
	                    try{
	                        //sleep(500);
	                    }
	                    catch(Exception e2){}
	                }
	            }
	        }
	    }
	    
	    class TargetThreadB extends Thread{
	        JLabel bomb;
	        
	        TargetThreadB(JLabel bomb){
	            this.bomb=bomb;
	            bomb.setLocation(0, 40);
	        }
	        public void run(){
	            while(running){
	                int x=bomb.getX()+9;//9�ȼ��� �̵�
	                int y=bomb.getY();
	                
	                //������ ������ �������
	                if(x>GamePanel2.this.getWidth())
	                   bomb.setLocation(0, 40);
	                //������ �ȿ� ������� 9�ȼ��� �̵�
	                else
	                   bomb.setLocation(x, y);
	                
	                //0.02�ʸ��� �̵�
	                try{
	                    sleep(20);
	                }
	                //�����尡 �װԵǸ� �ʱ� ��ġ�� ��ġ�ϰ�, 0.5�ʸ� ��ٸ���.
	                catch(Exception e){
	                   bomb.setLocation(0, 40);
	                    try{
	                        //sleep(500);
	                    }
	                    catch(Exception e2){}
	                }
	            }
	        }
	    }
	    
	    class BulletThread extends Thread{
	        JLabel bullet,target, target2, bomb;
	        Thread targetThread, targetThread2, targetThreadB;
	        
	        public BulletThread(JLabel bullet, JLabel target, JLabel target2, JLabel bomb, Thread targetThread, Thread targetThread2, Thread targetThreadB){
	            this.bullet=bullet;
	            this.target=target;
	            this.target2 = target2;
	            this.bomb = bomb;
	            this.targetThread=targetThread;
	            this.targetThread2=targetThread2;
	            this.targetThreadB=targetThreadB;
	        }
	        
	        public void run(){
	            while(running){
	                if(hit()){//Ÿ���� �¾Ҵٸ�
	                   
	                   // ���� ����
	                   score += 50;
	                   scoreLabel.setText("Score: " + score);
	                   scoreLabel.setVisible(true);
	                    targetThread.interrupt();//Ÿ�� �����带 ���δ�.
	                    
	                 // �浹�� ��ġ�� ���� �̹��� ǥ��
	                    int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                    int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                    explosion.setLocation(explosionX, explosionY);
	                    explosion.setVisible(true);

	                    // 0.3�� ���� ���� �̹��� ǥ��
	                    try {
	                        sleep(300);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }

	                    // ���� �̹��� �����
	                    explosion.setVisible(false);
	                    
	                    //�Ѿ��� ���� ��ġ�� �̵�
	                    bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                    return;//�Ѿ� �����嵵 ���δ�.
	                }
	                else{
	                    int x=bullet.getX();
	                    int y=bullet.getY()-5;//5�ȼ��� ���� �̵��Ѵ�.=�Ѿ� �ӵ��� 5�ȼ�
	                    //�Ѿ��� ������ ������ ������ ��
	                    if(y<0){
	                        //�Ѿ� ���� ��ġ�� �̵�
	                        bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                        return;//�Ѿ� �����带 ���δ�.
	                    }
	                    //�Ѿ��� ������ �ȿ� ������ 5�ȼ��� �̵��Ѵ�.
	                    else
	                        bullet.setLocation(x, y);
	                }
	                //0.02�ʸ��� 5�ȼ��� �̵�
	                try{
	                    sleep(20);
	                }
	                
	                catch(Exception e){}
	                
	                if(hit2()){//Ÿ��2�� �¾Ҵٸ�
	                    
	                    // ���� ����
	                    score += 70;
	                    scoreLabel.setText("Score: " + score);
	                    scoreLabel.setVisible(true);
	                     targetThread2.interrupt();//Ÿ�� �����带 ���δ�.
	                     
	                  // �浹�� ��ġ�� ���� �̹��� ǥ��
	                     int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                     int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                     explosion.setLocation(explosionX, explosionY);
	                     explosion.setVisible(true);

	                     // 0.3�� ���� ���� �̹��� ǥ��
	                     try {
	                         sleep(300);
	                     } catch (InterruptedException e) {
	                         e.printStackTrace();
	                     }

	                     // ���� �̹��� �����
	                     explosion.setVisible(false);
	                     
	                     //�Ѿ��� ���� ��ġ�� �̵�
	                     bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                     return;//�Ѿ� �����嵵 ���δ�.
	                 }
	                 else{
	                     int x=bullet.getX();
	                     int y=bullet.getY()-5;//5�ȼ��� ���� �̵��Ѵ�.=�Ѿ� �ӵ��� 5�ȼ�
	                     //�Ѿ��� ������ ������ ������ ��
	                     if(y<0){
	                         //�Ѿ� ���� ��ġ�� �̵�
	                         bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                         return;//�Ѿ� �����带 ���δ�.
	                     }
	                     //�Ѿ��� ������ �ȿ� ������ 5�ȼ��� �̵��Ѵ�.
	                     else
	                         bullet.setLocation(x, y);
	                 }
	                 //0.02�ʸ��� 5�ȼ��� �̵�
	                 try{
	                     sleep(20);
	                 }
	                 
	                 catch(Exception e){}
	                 
	                 if(hitB()){//��ź�� �¾Ҵٸ�
	                     
	                     // ���� ����
	                     score -= 60;
	                     scoreLabel.setText("Score: " + score);
	                     scoreLabel.setVisible(true);
	                      targetThreadB.interrupt();//Ÿ�� �����带 ���δ�.
	                      
	                   // �浹�� ��ġ�� ���� �̹��� ǥ��
	                      int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                      int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                      explosion.setLocation(explosionX, explosionY);
	                      explosion.setVisible(true);

	                      // 0.3�� ���� ���� �̹��� ǥ��
	                      try {
	                          sleep(300);
	                      } catch (InterruptedException e) {
	                          e.printStackTrace();
	                      }

	                      // ���� �̹��� �����
	                      explosion.setVisible(false);
	                      
	                      //�Ѿ��� ���� ��ġ�� �̵�
	                      bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                      return;//�Ѿ� �����嵵 ���δ�.
	                  }
	                  else{
	                      int x=bullet.getX();
	                      int y=bullet.getY()-5;//5�ȼ��� ���� �̵��Ѵ�.=�Ѿ� �ӵ��� 5�ȼ�
	                      //�Ѿ��� ������ ������ ������ ��
	                      if(y<0){
	                          //�Ѿ� ���� ��ġ�� �̵�
	                          bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                          return;//�Ѿ� �����带 ���δ�.
	                      }
	                      //�Ѿ��� ������ �ȿ� ������ 5�ȼ��� �̵��Ѵ�.
	                      else
	                          bullet.setLocation(x, y);
	                  }
	                  //0.02�ʸ��� 5�ȼ��� �̵�
	                  try{
	                      sleep(20);
	                  }
	                  
	                  catch(Exception e){}
	            }
	        }
	        
	        private boolean hit(){
	            int x=bullet.getX();
	            int y=bullet.getY();
	            int w=bullet.getWidth();
	            int h=bullet.getHeight();
	            
	            if(targetContains(x,y)
	                    ||targetContains(x+w-1,y)
	                    ||targetContains(x+w-1,y+h-1)
	                    ||targetContains(x,y+h-1))
	                return true;
	            else
	                return false;
	        }
	        
	        private boolean hit2(){
	            int x=bullet.getX();
	            int y=bullet.getY();
	            int w=bullet.getWidth();
	            int h=bullet.getHeight();
	            
	            if(targetContains2(x,y)
	                    ||targetContains2(x+w-1,y)
	                    ||targetContains2(x+w-1,y+h-1)
	                    ||targetContains2(x,y+h-1))
	                return true;
	            else
	                return false;
	        }
	        
	        private boolean hitB(){
	            int x=bullet.getX();
	            int y=bullet.getY();
	            int w=bullet.getWidth();
	            int h=bullet.getHeight();
	            
	            if(targetContainsB(x,y)
	                    ||targetContainsB(x+w-1,y)
	                    ||targetContainsB(x+w-1,y+h-1)
	                    ||targetContainsB(x,y+h-1))
	                return true;
	            else
	                return false;
	        }
	        
	        private boolean targetContains(int x, int y){
	            //Ÿ���� x��ǥ�� �Ѿ� x��ǥ���� �۰ų� ������ �Ѿ� x��ǥ���� Ÿ�� x��ǥ + Ÿ���� ���� ���̰� ũ�� 
	            if (target.getX()<=x && x<target.getX()+target.getWidth() 
	                    //Ÿ���� y��ǥ�� �Ѿ� y��ǥ���� �۰ų� ������ �Ѿ� y��ǥ���� Ÿ�� y��ǥ + Ÿ���� ���� ���̰� ũ��
	                    && target.getY()<=y && y<target.getY()+target.getHeight())
	                return true;
	            
	            else
	                return false;
	        }
	        
	        private boolean targetContains2(int x, int y){
	           // Ÿ��2�� x��ǥ�� �Ѿ� x��ǥ���� �۰ų� ������ �Ѿ� x��ǥ���� Ÿ��2 x��ǥ + Ÿ��2�� ���� ���̰� ũ��
	            if (target2.getX()<=x && x<target2.getX()+target2.getWidth()
	                  // Ÿ��2�� y��ǥ�� �Ѿ� y��ǥ���� �۰ų� ������ �Ѿ� y��ǥ���� Ÿ��2 y��ǥ + Ÿ��2�� ���� ���̰� ũ��
	                    && target2.getY()<=y && y<target2.getY()+target2.getHeight())
	                return true;
	            
	            else
	                return false;
	        }
	        
	        private boolean targetContainsB(int x, int y){
	           // ��ź�� x��ǥ�� �Ѿ� x��ǥ���� �۰ų� ������ �Ѿ� x��ǥ���� ��ź x��ǥ + ��ź�� ���� ���̰� ũ�� 
	            if (bomb.getX()<=x && x<bomb.getX()+bomb.getWidth()
	                   // ��ź�� y��ǥ�� �Ѿ� y��ǥ���� �۰ų� ������ �Ѿ� y��ǥ���� ��ź y��ǥ + ��ź�� ���� ���̰� ũ��
	                    && bomb.getY()<=y && y<bomb.getY()+bomb.getHeight())
	                return true;
	            
	            else
	                return false;
	        }
	    }
	}

public class JavaApplication30 {
    public static void main(String[] args) {
        new Ex5();
    }
}