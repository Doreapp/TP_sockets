package stream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Client extends JFrame
{
       JTextField hostname = new JTextField(10);
       JTextField port = new JTextField(10);
       JTextField name = new JTextField(10);
       JTextPane chat = new JTextPane();
       JTextField scanner = new JTextField(50);

       EchoClient echoClient;
       String nickname;

       public Client()
       {
              setTitle("TCP CHAT");
              setVisible(true);
              setSize(800, 600);
              setResizable(false);
              setDefaultCloseOperation(EXIT_ON_CLOSE);
              setLocationRelativeTo(null);

              Container mainContainer = this.getContentPane();

              JPanel topPanel = new JPanel();
              topPanel.setLayout(new FlowLayout(FlowLayout.CENTER,15,5));
              // topPanel.setBackground(Color.ORANGE);

              JLabel labelHostName = new JLabel("HostName : ");
              JLabel labelPort = new JLabel("Port : ");
              JLabel labelName = new JLabel("NickName : ");
              JButton connectButton = new JButton("Connect");
              connectButton.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                            String host = hostname.getText();
                            String p = port.getText();
                            String nom = name.getText();
                            if(host == null || host.length() < 1 || p == null || p.length() < 1 || nom == null || nom.length() < 1){
                              addChat("Bad entries");
                            } else {
                             nickname = nom;
                             try{
                               echoClient = new EchoClient(host,p,nom,chat);
                             }catch(IOException exc){
                             System.out.println(exc);
                             }
                              
                            }
                      }
              });

              topPanel.add(labelHostName);
              topPanel.add(hostname);
              topPanel.add(labelPort);
              topPanel.add(port);
              topPanel.add(labelName);
              topPanel.add(name);
              topPanel.add(connectButton);


              mainContainer.add(topPanel, BorderLayout.NORTH);

              // Middle Panel ====================================

              JPanel middlePanel = new JPanel();
              // middlePanel.setBackground(Color.GREEN);

              chat.setPreferredSize(new Dimension(700, 500));
              chat.setEditable(false);
              // chat.setLineWrap(true);
              // chat.setWrapStyleWord(true);

              JScrollPane scrollPane = new JScrollPane (chat);

              middlePanel.add(scrollPane);

              mainContainer.add(middlePanel);

              // Bottom Panel ===================================

              JPanel bottomPanel = new JPanel();
              // bottomPanel.setBackground(Color.RED);
              

              scanner.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                            echoClient.sendMessage(scanner.getText());
                            scanner.setText("");
                      }
              });

              JButton sendMessage = new JButton("Send");
              sendMessage.addActionListener(new ActionListener()
              {
                      public void actionPerformed(ActionEvent e)
                      {
                            echoClient.sendMessage(scanner.getText());
                            scanner.setText("");
                      }
              });

              bottomPanel.add(scanner);
              bottomPanel.add(sendMessage);

              mainContainer.add(bottomPanel, BorderLayout.SOUTH);

              pack();
       }

       public static void main(String[] args)
       {
             Client t = new Client();
       }

       private void addChat(String message){
       		String fil = chat.getText();
       		fil += '\n'+message;
       		chat.setText(fil);
       }
}