package project1;
 
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
 
import javax.swing.*;
 
public class Client {
	private JFrame clientFrame;
	private JLabel IPLabel;
	private JLabel PortLabel;
	private JLabel sayLabel;
	private JLabel nicknameLabel;
	private JTextField IPText;
	private JTextField PortText;
	private JTextField nicknameText;
	private JTextField sayText;
	private JButton connectButton;
	private JButton nicknameButton;
	private JButton sayButton;
	private JPanel jPanelNorth;
	private JPanel jPanelSouth0;
	private JPanel jPanelSouth1;
	private JPanel jPanelSouth2;
	private JTextArea clientTextArea;
	private JScrollPane scroller;
	private BufferedReader reader;
	private PrintWriter writer;
	private String nickname;
 
	public static void main(String args[]) {
		Client aClient = new Client();
		aClient.startUp();
	}
 
	// 初始化组件
	public Client() {
		nickname = "客户端";
 
		clientFrame = new JFrame();
		jPanelNorth = new JPanel();
		IPLabel = new JLabel("服务器IP", JLabel.LEFT);
		IPText = new JTextField(10);
		PortLabel = new JLabel("服务器端口", JLabel.LEFT);
		PortText = new JTextField(10);
		connectButton = new JButton("连接");
		clientTextArea = new JTextArea();
		scroller = new JScrollPane(clientTextArea);
		jPanelSouth0 = new JPanel();
		jPanelSouth1 = new JPanel();
		jPanelSouth2 = new JPanel();
		nicknameLabel = new JLabel("昵称", JLabel.LEFT);
		nicknameText = new JTextField(nickname, 30);
		nicknameButton = new JButton("确认");
		sayLabel = new JLabel("消息", JLabel.LEFT);
		sayText = new JTextField(30);
		sayButton = new JButton("确认");
	}
 
	// 构建GUI
	private void buildGUI() {
		// 窗口的设置
		clientFrame.setTitle("客户端");
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.setSize(550, 550);
 
		// 北区的组件
		jPanelNorth.add(IPLabel);
		jPanelNorth.add(IPText);
		jPanelNorth.add(PortLabel);
		jPanelNorth.add(PortText);
		jPanelNorth.add(connectButton);
		clientFrame.getContentPane().add(BorderLayout.NORTH, jPanelNorth);
 
		// 中间的组件
		clientTextArea.setFocusable(false);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		clientFrame.getContentPane().add(BorderLayout.CENTER, scroller);
 
		// 南区的组件
		jPanelSouth1.add(nicknameLabel);
		jPanelSouth1.add(nicknameText);
		jPanelSouth1.add(nicknameButton);
		jPanelSouth2.add(sayLabel);
		jPanelSouth2.add(sayText);
		jPanelSouth2.add(sayButton);
		jPanelSouth0.setLayout(new BoxLayout(jPanelSouth0, BoxLayout.Y_AXIS));
		jPanelSouth0.add(jPanelSouth1);
		jPanelSouth0.add(jPanelSouth2);
		clientFrame.getContentPane().add(BorderLayout.SOUTH, jPanelSouth0);
 
		// 设置窗口可见
		clientFrame.setVisible(true);
	}
 
	// 客户端运行
	public void startUp() {
		buildGUI();
 
		// 接收服务器消息的线程
		Runnable incomingReader = new Runnable() {
			@Override
			public void run() {
				String message;
				try {
					while ((message = reader.readLine()) != null) {
						clientTextArea.append(message + "\n");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
 
		// 监听Connect按钮，实现服务器的连接
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aServerIP = IPText.getText();
				String aServerPort = PortText.getText();
 
				if (aServerIP.equals("") || aServerPort.equals("")) {
					JOptionPane.showMessageDialog(clientFrame, "请输入 完整的 IP和端口！");
				} else {
					try {
						@SuppressWarnings("resource")
						Socket clientSocket = new Socket(aServerIP, Integer.parseInt(aServerPort));
						InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
						reader = new BufferedReader(streamReader);
						writer = new PrintWriter(clientSocket.getOutputStream());
 
						clientTextArea.append("服务器已连接...\n");
 
						Thread readerThread = new Thread(incomingReader);
						readerThread.start();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(clientFrame, "连接不上服务器!\n请确认 IP 和 端口 输入正确。");
					}
				}
			}
		});
 
		// 监听nickname，设置昵称
		ActionListener nicknameListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aText = nicknameText.getText();
				if (!aText.equals("")) {
					nickname = aText;
				}
			}
		};
		nicknameButton.addActionListener(nicknameListener);
		nicknameText.addActionListener(nicknameListener);
		nicknameText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
 
			@Override
			public void focusLost(FocusEvent e) {
				String aText = nicknameText.getText();
				if (!aText.equals("")) {
					nickname = aText;
				}
			}
		});
 
		// 发送消息到服务器
		ActionListener SayListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aText = sayText.getText();
				if (aText.equals("")) {
					JOptionPane.showMessageDialog(clientFrame, "内容不能为空！");
				} else {
					try {
						writer.println(nickname + "：" + aText);
						writer.flush();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					sayText.setText("");
				}
			}
		};
		sayButton.addActionListener(SayListener);
		sayText.addActionListener(SayListener);
 
	}
 
}
