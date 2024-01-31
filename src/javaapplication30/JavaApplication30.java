package javaapplication30;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

class Ex5 extends JFrame{
   
   GamePanel p;   // 스테이지1
   GamePanel2 p2;   // 스테이지2
   JLabel timeLabel;   // 시간을 표시할 레이블
    Timer timer;   // 타이머
    JLabel resultLabel;   // 결과화면
    JButton startButton;   // 시작버튼
    JButton homeButton;   // 홈버튼
    JButton nextStageButton = new JButton("Next Stage");   // 다음 스테이지 버튼
   
    Ex5(){
        this.setTitle("사격 게임");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        
        // start 버튼
        ImageIcon startImg = new ImageIcon("start.png");
        startButton = new JButton(startImg);
        startButton.setBounds(0, 0, startImg.getIconWidth(), startImg.getIconHeight());
        this.add(startButton);
        
        // 게임 패널
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
              startTimer();   // 타이머 시작
           }
        });
        
        timeLabel = new JLabel("Time: 30");   // 초기 시간 설정
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

    
    // 타이머 시작 메소드
    public void startTimer() {
       int timeLimit = 30;   // 타이머 제한 시간 설정(초 단위)
       
       timer = new Timer(1000, new ActionListener() {
          int time = timeLimit;
          
          @Override
          public void actionPerformed(ActionEvent e) {
             if (time > 0) {
                time--;
                timeLabel.setText("Time: " + time);
                timeLabel.setVisible(true);   // 레이블을 보이도록 설정
             }
             else {
                timer.stop();   // 제한 시간이 종료되면 타이머 중지
                timeLabel.setText("Time: 0");
                timeLabel.setVisible(false);
                
                // 점수를 확인하고 적절한 이미지 표시
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

                
                // 게임 멈추기
                p.stopGame();
                p2.stopGame();
             }
          }
       });
       
       timer.start();   // 타이머 시작
    }
    
    
    // Next Stage 버튼 생성
    public JButton createNextStageButton() {
       nextStageButton = new JButton("Next Stage");
       nextStageButton.setBounds(150, 180, 100, 30);
       
       nextStageButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             // 다음 스테이지로 이동하는 코드 추가
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
    
    // Home 버튼 생성
    public JButton createHomeButton() {
       homeButton = new JButton("Home");
       homeButton.setBounds(150, 220, 100, 30);
       
       homeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             // 홈으로 이동하는 코드
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
      ImageIcon back = new ImageIcon("background.png");   // 배경 이미지
      g.drawImage(back.getImage(), 0, 0, d.width, d.height, null);
   }
   
    Ex5 parentFrame;   // 부모 프레임
    TargetThread targetThread;
    JLabel base;   // 슈팅기계
    JLabel bullet;   // 총알
    JLabel target;   // 목표물
    JLabel explosion;   // 폭발 효과
    
    int score;   // 점수변수
    JLabel scoreLabel;   // 점수를 표시할 레이블
    boolean running;   // 게임 실행 여부를 저장하는 변수
    
    GamePanel(){
       
       score = 0;   // 점수 초기화
       running = false;   // 게임 실행 여부 초기화
       
       // 점수를 표시할 레이블 생성
       scoreLabel = new JLabel("Score: 0");
        scoreLabel.setBounds(10, 240, 80, 20);
        this.add(scoreLabel);
       
       
       // 슈팅 기계 이미지
        this.setLayout(null);
        ImageIcon img3 = new ImageIcon("roundShot.png");
        base = new JLabel(img3);
        base.setSize(img3.getIconWidth(), img3.getIconHeight());
        //base.setOpaque(true);
        //base.setBackground(Color.black);
        
        // 타겟 이미지
        ImageIcon img = new ImageIcon("red monster.png");
        target = new JLabel(img);
        //이미지 크기만큼 레이블 크기 설정
        target.setSize(img.getIconWidth(),img.getIconHeight());
        
        // 총알 이미지
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
        explosion.setVisible(false); // 초기에는 폭발 이미지를 보이지 않도록 설정
        this.add(explosion);
        //URL url = Ex5.class.getResource("LASER.wav");
        //sound = Applet.newAudioClip(url);
    }
    
    public void startGame(){
       running = true;   // 게임 실행 상태로 변경
       
        base.setLocation(this.getWidth()/2-17, this.getHeight()-75);
        bullet.setLocation(this.getWidth()/2-5, this.getHeight()-90);
        target.setLocation(0, 0);
        //타겟을 움직이는 스레드
        targetThread = new TargetThread(target);
        targetThread.start();
        
        //베이스에 초점을 두고 엔터키 입력에 따라 bullet스레드 실행
        base.requestFocus();
        base.addKeyListener(new KeyListener(){
            BulletThread bulletThread = null;
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyChar()==KeyEvent.VK_ENTER){
                    //스레드가 죽어있는 상태인지 확인
                    if(bulletThread==null || !bulletThread.isAlive()){
                        //sound.play();
                        //총알로 타겟을 맞췄는지 확인하기 위해 2개의 레이블과 타겟스레드를 넘겨준다.
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
       running = false;   // 게임 실행 중지 상태로 변경
    }
    
    class TargetThread extends Thread{
        JLabel target;
        
        TargetThread(JLabel target){
            this.target=target;
            target.setLocation(0, 0);
        }
        public void run(){
            while(running){
                int x=target.getX()+5;//5픽셀씩 이동
                int y=target.getY();
                
                //프레임 밖으로 나갈경우
                if(x>GamePanel.this.getWidth())
                    target.setLocation(0, 0);
                //프레임 안에 있을경우 5픽셀씩 이동
                else
                    target.setLocation(x, y);
                
                //0.02초마다 이동
                try{
                    sleep(20);
                }
                //스레드가 죽게되면 초기 위치에 위치하고, 0.5초를 기다린다.
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
                if(hit()){//타겟이 맞았다면
                   
                   // 점수 갱신
                   score += 50;
                   scoreLabel.setText("Score: " + score);
                   scoreLabel.setVisible(true);
                    targetThread.interrupt();//타겟 스레드를 죽인다.
                    
                 // 충돌한 위치에 폭발 이미지 표시
                    int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
                    int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
                    explosion.setLocation(explosionX, explosionY);
                    explosion.setVisible(true);

                    // 0.3초 동안 폭발 이미지 표시
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 폭발 이미지 숨기기
                    explosion.setVisible(false);
                    
                    //총알은 원래 위치로 이동
                    bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
                    return;//총알 스레드도 죽인다.
                }
                else{
                    int x=bullet.getX();
                    int y=bullet.getY()-5;//5픽셀씩 위로 이동한다.=총알 속도가 5픽셀
                    //총알이 프레임 밖으로 나갔을 때
                    if(y<0){
                        //총알 원래 위치로 이동
                        bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
                        return;//총알 스레드를 죽인다.
                    }
                    //총알이 프레임 안에 있으면 5픽셀씩 이동한다.
                    else
                        bullet.setLocation(x, y);
                }
                //0.02초마다 5픽셀씩 이동
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
            //타겟의 x좌표가 총알 x좌표보다 작거나 같으며 총알 x좌표보다 타겟 x좌표 + 타겟의 가로 길이가 크고 
            if (target.getX()<=x && x<target.getX()+target.getWidth() 
                    //타겟의 y좌표가 총알 y좌표보다 작거나 같으며 총알 y좌표보다 타겟 y좌표 + 타겟의 세로 길이가 크면
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
	      ImageIcon back = new ImageIcon("background.png");   // 배경 이미지
	      g.drawImage(back.getImage(), 0, 0, d.width, d.height, null);
	   }
	   
	    Ex5 parentFrame;   // 부모 프레임
	    TargetThread targetThread;
	    TargetThread2 targetThread2;
	    TargetThreadB targetThreadB;
	    JLabel base;   // 슈팅기계
	    JLabel bullet;   // 총알
	    JLabel target;   // 목표물
	    JLabel target2;   // 목표물2
	    JLabel bomb;   // 폭탄
	    JLabel explosion;   // 폭발 효과
	    
	    int score;   // 점수변수
	    JLabel scoreLabel;   // 점수를 표시할 레이블
	    boolean running;   // 게임 실행 여부를 저장하는 변수
	    
	    GamePanel2(){
	       
	       score = 0;   // 점수 초기화
	       running = false;   // 게임 실행 여부 초기화
	       
	       // 점수를 표시할 레이블 생성
	       scoreLabel = new JLabel("Score: 0");
	        scoreLabel.setBounds(10, 240, 80, 20);
	        this.add(scoreLabel);
	       
	       
	       // 슈팅 기계 이미지
	        this.setLayout(null);
	        ImageIcon img3 = new ImageIcon("roundShot.png");
	        base = new JLabel(img3);
	        base.setSize(img3.getIconWidth(), img3.getIconHeight());
	        //base.setOpaque(true);
	        //base.setBackground(Color.black);
	        
	        // 타겟 이미지
	        ImageIcon img = new ImageIcon("red monster.png");
	        target = new JLabel(img);
	        //이미지 크기만큼 레이블 크기 설정
	        target.setSize(img.getIconWidth(),img.getIconHeight());
	        
	        // 타겟2 이미지
	        ImageIcon img4 = new ImageIcon("colorful monster.png");
	        target2 = new JLabel(img4);
	        //이미지 크기만큼 레이블 크기 설정
	        target2.setSize(img4.getIconWidth(),img4.getIconHeight());
	        
	        // 폭탄 이미지
	        ImageIcon bombImg = new ImageIcon("bomb.png");
	        bomb = new JLabel(bombImg);
	        bomb.setSize(bombImg.getIconWidth(), bombImg.getIconHeight());
	        
	        // 총알 이미지
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
	        explosion.setVisible(false); // 초기에는 폭발 이미지를 보이지 않도록 설정
	        this.add(explosion);
	        //URL url = Ex5.class.getResource("LASER.wav");
	        //sound = Applet.newAudioClip(url);
	    }
	    
	    public void startGame(){
	       running = true;   // 게임 실행 상태로 변경
	       
	        base.setLocation(this.getWidth()/2-17, this.getHeight()-75);
	        bullet.setLocation(this.getWidth()/2-5, this.getHeight()-90);
	        target.setLocation(0, 0);
	        target2.setLocation(365, 20);
	        bomb.setLocation(0, 40);
	        
	        //타겟을 움직이는 스레드
	        targetThread = new TargetThread(target);
	        targetThread.start();
	        
	        // 타겟2를 움직이는 스레드
	        targetThread2 = new TargetThread2(target2);
	        targetThread2.start();
	        
	        // bomb을 움직이는 스레드
	        targetThreadB = new TargetThreadB(bomb);
	        targetThreadB.start();
	        
	        //베이스에 초점을 두고 엔터키 입력에 따라 bullet스레드 실행
	        base.requestFocus();
	        base.addKeyListener(new KeyListener(){
	            BulletThread bulletThread = null;
	            @Override
	            public void keyTyped(KeyEvent ke) {
	            }

	            @Override
	            public void keyPressed(KeyEvent ke) {
	                if(ke.getKeyChar()==KeyEvent.VK_ENTER){
	                    //스레드가 죽어있는 상태인지 확인
	                    if(bulletThread==null || !bulletThread.isAlive()){
	                        //sound.play();
	                        //총알로 타겟을 맞췄는지 확인하기 위해 2개의 레이블과 타겟스레드를 넘겨준다.
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
	       running = false;   // 게임 실행 중지 상태로 변경
	    }
	    
	    class TargetThread extends Thread{
	        JLabel target;
	        
	        TargetThread(JLabel target){
	            this.target=target;
	            target.setLocation(0, 0);
	        }
	        public void run(){
	            while(running){
	                int x=target.getX()+5;//5픽셀씩 이동
	                int y=target.getY();
	                
	                //프레임 밖으로 나갈경우
	                if(x>GamePanel2.this.getWidth())
	                    target.setLocation(0, 0);
	                //프레임 안에 있을경우 5픽셀씩 이동
	                else
	                    target.setLocation(x, y);
	                
	                //0.02초마다 이동
	                try{
	                    sleep(20);
	                }
	                //스레드가 죽게되면 초기 위치에 위치하고, 0.5초를 기다린다.
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
	                int x=target2.getX()-7;//-7픽셀씩 이동
	                int y=target2.getY();
	                
	                //프레임 밖으로 나갈경우
	                if(x<0)
	                    target2.setLocation(365, 20);
	                //프레임 안에 있을경우 -7픽셀씩 이동
	                else
	                    target2.setLocation(x, y);
	                
	                //0.02초마다 이동
	                try{
	                    sleep(20);
	                }
	                //스레드가 죽게되면 초기 위치에 위치하고, 0.5초를 기다린다.
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
	                int x=bomb.getX()+9;//9픽셀씩 이동
	                int y=bomb.getY();
	                
	                //프레임 밖으로 나갈경우
	                if(x>GamePanel2.this.getWidth())
	                   bomb.setLocation(0, 40);
	                //프레임 안에 있을경우 9픽셀씩 이동
	                else
	                   bomb.setLocation(x, y);
	                
	                //0.02초마다 이동
	                try{
	                    sleep(20);
	                }
	                //스레드가 죽게되면 초기 위치에 위치하고, 0.5초를 기다린다.
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
	                if(hit()){//타겟이 맞았다면
	                   
	                   // 점수 갱신
	                   score += 50;
	                   scoreLabel.setText("Score: " + score);
	                   scoreLabel.setVisible(true);
	                    targetThread.interrupt();//타겟 스레드를 죽인다.
	                    
	                 // 충돌한 위치에 폭발 이미지 표시
	                    int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                    int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                    explosion.setLocation(explosionX, explosionY);
	                    explosion.setVisible(true);

	                    // 0.3초 동안 폭발 이미지 표시
	                    try {
	                        sleep(300);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }

	                    // 폭발 이미지 숨기기
	                    explosion.setVisible(false);
	                    
	                    //총알은 원래 위치로 이동
	                    bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                    return;//총알 스레드도 죽인다.
	                }
	                else{
	                    int x=bullet.getX();
	                    int y=bullet.getY()-5;//5픽셀씩 위로 이동한다.=총알 속도가 5픽셀
	                    //총알이 프레임 밖으로 나갔을 때
	                    if(y<0){
	                        //총알 원래 위치로 이동
	                        bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                        return;//총알 스레드를 죽인다.
	                    }
	                    //총알이 프레임 안에 있으면 5픽셀씩 이동한다.
	                    else
	                        bullet.setLocation(x, y);
	                }
	                //0.02초마다 5픽셀씩 이동
	                try{
	                    sleep(20);
	                }
	                
	                catch(Exception e){}
	                
	                if(hit2()){//타겟2가 맞았다면
	                    
	                    // 점수 갱신
	                    score += 70;
	                    scoreLabel.setText("Score: " + score);
	                    scoreLabel.setVisible(true);
	                     targetThread2.interrupt();//타겟 스레드를 죽인다.
	                     
	                  // 충돌한 위치에 폭발 이미지 표시
	                     int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                     int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                     explosion.setLocation(explosionX, explosionY);
	                     explosion.setVisible(true);

	                     // 0.3초 동안 폭발 이미지 표시
	                     try {
	                         sleep(300);
	                     } catch (InterruptedException e) {
	                         e.printStackTrace();
	                     }

	                     // 폭발 이미지 숨기기
	                     explosion.setVisible(false);
	                     
	                     //총알은 원래 위치로 이동
	                     bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                     return;//총알 스레드도 죽인다.
	                 }
	                 else{
	                     int x=bullet.getX();
	                     int y=bullet.getY()-5;//5픽셀씩 위로 이동한다.=총알 속도가 5픽셀
	                     //총알이 프레임 밖으로 나갔을 때
	                     if(y<0){
	                         //총알 원래 위치로 이동
	                         bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                         return;//총알 스레드를 죽인다.
	                     }
	                     //총알이 프레임 안에 있으면 5픽셀씩 이동한다.
	                     else
	                         bullet.setLocation(x, y);
	                 }
	                 //0.02초마다 5픽셀씩 이동
	                 try{
	                     sleep(20);
	                 }
	                 
	                 catch(Exception e){}
	                 
	                 if(hitB()){//폭탄이 맞았다면
	                     
	                     // 점수 갱신
	                     score -= 60;
	                     scoreLabel.setText("Score: " + score);
	                     scoreLabel.setVisible(true);
	                      targetThreadB.interrupt();//타겟 스레드를 죽인다.
	                      
	                   // 충돌한 위치에 폭발 이미지 표시
	                      int explosionX = bullet.getX() + bullet.getWidth() / 2 - explosion.getWidth() / 2;
	                      int explosionY = bullet.getY() + bullet.getHeight() / 2 - explosion.getHeight() / 2;
	                      explosion.setLocation(explosionX, explosionY);
	                      explosion.setVisible(true);

	                      // 0.3초 동안 폭발 이미지 표시
	                      try {
	                          sleep(300);
	                      } catch (InterruptedException e) {
	                          e.printStackTrace();
	                      }

	                      // 폭발 이미지 숨기기
	                      explosion.setVisible(false);
	                      
	                      //총알은 원래 위치로 이동
	                      bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                      return;//총알 스레드도 죽인다.
	                  }
	                  else{
	                      int x=bullet.getX();
	                      int y=bullet.getY()-5;//5픽셀씩 위로 이동한다.=총알 속도가 5픽셀
	                      //총알이 프레임 밖으로 나갔을 때
	                      if(y<0){
	                          //총알 원래 위치로 이동
	                          bullet.setLocation(bullet.getParent().getWidth()/2-5, bullet.getParent().getHeight()-90);
	                          return;//총알 스레드를 죽인다.
	                      }
	                      //총알이 프레임 안에 있으면 5픽셀씩 이동한다.
	                      else
	                          bullet.setLocation(x, y);
	                  }
	                  //0.02초마다 5픽셀씩 이동
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
	            //타겟의 x좌표가 총알 x좌표보다 작거나 같으며 총알 x좌표보다 타겟 x좌표 + 타겟의 가로 길이가 크고 
	            if (target.getX()<=x && x<target.getX()+target.getWidth() 
	                    //타겟의 y좌표가 총알 y좌표보다 작거나 같으며 총알 y좌표보다 타겟 y좌표 + 타겟의 세로 길이가 크면
	                    && target.getY()<=y && y<target.getY()+target.getHeight())
	                return true;
	            
	            else
	                return false;
	        }
	        
	        private boolean targetContains2(int x, int y){
	           // 타겟2의 x좌표가 총알 x좌표보다 작거나 같으며 총알 x좌표보다 타겟2 x좌표 + 타겟2의 가로 길이가 크고
	            if (target2.getX()<=x && x<target2.getX()+target2.getWidth()
	                  // 타겟2의 y좌표가 총알 y좌표보다 작거나 같으며 총알 y좌표보다 타겟2 y좌표 + 타겟2의 세로 길이가 크면
	                    && target2.getY()<=y && y<target2.getY()+target2.getHeight())
	                return true;
	            
	            else
	                return false;
	        }
	        
	        private boolean targetContainsB(int x, int y){
	           // 폭탄의 x좌표가 총알 x좌표보다 작거나 같으며 총알 x좌표보다 폭탄 x좌표 + 폭탄의 가로 길이가 크고 
	            if (bomb.getX()<=x && x<bomb.getX()+bomb.getWidth()
	                   // 폭탄의 y좌표가 총알 y좌표보다 작거나 같으며 총알 y좌표보다 폭탄 y좌표 + 폭탄의 세로 길이가 크면
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