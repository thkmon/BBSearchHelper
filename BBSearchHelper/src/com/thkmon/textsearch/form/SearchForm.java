package com.thkmon.textsearch.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.thkmon.textsearch.common.CommonConst;
import com.thkmon.textsearch.data.StringList;
import com.thkmon.textsearch.search.SearchController;
import com.thkmon.textsearch.util.FileUtil;
import com.thkmon.textsearch.util.PropertiesUtil;
import com.thkmon.textsearch.util.StringUtil;

public class SearchForm extends JFrame {

	
	public static String defaultFolderPath = "";
	public static String defaultPathToExclude = "";
	public static String defaultExtension = "";
	public static String defaultTextToSearch = "";
	public static String defaultCharset = "";
	
	public static JTextField textField1 = null;
	public static JTextField textField2 = null;
	public static JTextField textField3 = null;
	public static JTextField textField4 = null;
	public static JTextField textField5 = null;
	
	public SearchForm() {
		
		HashMap<String, String> defaultPropertiesMap = PropertiesUtil.readPropertiesFile("default.properties");
		defaultFolderPath = "";
		defaultPathToExclude = "";
		defaultExtension = "";
		defaultTextToSearch = "";
		defaultCharset = "";
		
		if (defaultPropertiesMap != null) {
			defaultFolderPath = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultFolderPath"), "C:\\");
			defaultPathToExclude = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultPathToExclude"), "");
			defaultExtension = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultExtension"), "*");
			defaultTextToSearch = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultTextToSearch"), "");
			defaultCharset = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultCharset"), "UTF-8");
			
		} else {
			defaultFolderPath = "C:\\";
			defaultPathToExclude = "";
			defaultExtension = "*";
			defaultTextToSearch = "";
			defaultCharset = "UTF-8";
		}
		
		final String dirPath1 = defaultFolderPath;
				
		int textFieldHeight = 25;
		
		this.setResizable(false);
		this.setTitle("BBSearchHelper_" + CommonConst.VERSION);
		this.setBounds(0, 0, 560, 390);
		this.setLayout(null);
		
		int marginTop = 15;
		
		JLabel label1 = new JLabel("Folder Path");
		label1.setBounds(20, marginTop, 140, textFieldHeight);
		this.getContentPane().add(label1);
		
		marginTop += 25;
		
		textField1 = new JTextField();
		textField1.setBounds(20, marginTop, 450, textFieldHeight);
		textField1.setText(defaultFolderPath);
		this.getContentPane().add(textField1);
		
		JButton fileButton = new JButton("...");
		fileButton.setBounds(480, marginTop, 40, textFieldHeight);
		this.getContentPane().add(fileButton);
		
		fileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 파일 선택 다이얼로그 정의
				JFileChooser fileDialog = new JFileChooser();

				// 폴더만 선택
				fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				// 파일만 선택
				// fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

				// 기본 폴더 위치 변경
				// 텍스트 필드 값을 기본 폴더 위치로 다이얼로그 열고, 여의치 않다면 기본 폴더 위치로 다이얼로그를 열자.
				File driveC = new File(textField1.getText());
				if (!driveC.exists()) {
					driveC = new File(dirPath1);
				}
				
				if (driveC.exists()) {
					fileDialog.setCurrentDirectory(driveC);
				}

				// 파일 선택 다이얼로그 열기
				int returnVal = fileDialog.showOpenDialog(null);
				if (returnVal == 0) {
					// 파일 선택
					textField1.setText(fileDialog.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		marginTop += 25;
		
		final JCheckBox checkBox1 = new JCheckBox("Include Subfolders");
		checkBox1.setBounds(20, marginTop, 500, textFieldHeight);
		checkBox1.setSelected(true);
		this.getContentPane().add(checkBox1);
		
		marginTop += 30;
		
		
		JLabel label5 = new JLabel("Path To Exclude");
		label5.setBounds(20, marginTop, 500, textFieldHeight);
		this.getContentPane().add(label5);
		
		marginTop += 25;
		
		textField5 = new JTextField();
		textField5.setBounds(20, marginTop, 500, textFieldHeight);
		textField5.setText(defaultPathToExclude);
		this.getContentPane().add(textField5);
		
		marginTop += 30;
		
		JLabel label2 = new JLabel("File Extension");
		label2.setBounds(20, marginTop, 390, textFieldHeight);
		this.getContentPane().add(label2);
		
		textField2 = new JTextField();
		textField2.setBounds(20, marginTop + 25, 390, textFieldHeight);
		textField2.setText(defaultExtension);
		this.getContentPane().add(textField2);
		
		JLabel label4 = new JLabel("Charset");
		label4.setBounds(420, marginTop, 100, textFieldHeight);
		this.getContentPane().add(label4);
		
		textField4 = new JTextField();
		textField4.setBounds(420, marginTop + 25, 100, textFieldHeight);
		textField4.setText(defaultCharset);
		this.getContentPane().add(textField4);
		
		marginTop += 55;
		
		JLabel label3 = new JLabel("Text To Search");
		label3.setBounds(20, marginTop, 500, textFieldHeight);
		this.getContentPane().add(label3);
		
		textField3 = new JTextField();
		textField3.setBounds(20, marginTop + 25, 500, textFieldHeight);
		textField3.setText(defaultTextToSearch);
		this.getContentPane().add(textField3);
		
		marginTop += 50;
		
		final JCheckBox checkBox2 = new JCheckBox("Ignore Case");
		checkBox2.setBounds(20, marginTop, 500, textFieldHeight);
		checkBox2.setSelected(true);
		this.getContentPane().add(checkBox2);
		
		marginTop += 30;
		
		JButton button = new JButton("Search");
		button.setBounds(20, marginTop, 500, 30);
		this.getContentPane().add(button);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = textField1.getText();
				if (path == null || path.trim().length() == 0) {
					System.out.println("폴더 경로를 입력해주세요.");
					return;
				} else {
					path = path.trim();
				}
				
				File parentDir = new File(path);
				if (!parentDir.exists()) {
					System.out.println("존재하지 않는 경로입니다. [" + parentDir.getAbsolutePath() + "]");
					return;
				}
				
				boolean bGetSubFolders = checkBox1.isSelected();
				String extensions = textField2.getText();
				
				boolean bIgnoreCase = checkBox2.isSelected();
				String strToSearch = textField3.getText();
				
				// 검색어 없이도 파일 목록 출력할 수 있도록 수정.
				if (strToSearch == null) {
					strToSearch = "";
				}
				
				String pathToExclude = textField5.getText();
				
				SearchController searchCtrl = new SearchController();
				searchCtrl.search(parentDir, bGetSubFolders, extensions, strToSearch, bIgnoreCase, pathToExclude);
			}
		});
		
		this.setVisible(true);
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				System.out.println("사용자 명령으로 종료합니다.");
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}
}
