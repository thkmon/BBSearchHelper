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

	
	private String defaultFolderPath = "";
	private String defaultExtension = "";
	
	
	public SearchForm() {
		
		HashMap<String, String> defaultPropertiesMap = PropertiesUtil.readPropertiesFile("default.properties");
		defaultFolderPath = "";
		defaultExtension = "";
		
		if (defaultPropertiesMap != null) {
			defaultFolderPath = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultFolderPath"), "C:\\");
			defaultExtension = StringUtil.emptyToDefault(defaultPropertiesMap.get("defaultExtension"), "*");
			
		} else {
			defaultFolderPath = "C:\\";
			defaultExtension = "*";
		}
		
		final String dirPath = defaultFolderPath;
				
		int textFieldHeight = 25;
		
		this.setResizable(false);
		this.setTitle("BBSearchHelper_" + CommonConst.VERSION);
		this.setBounds(0, 0, 560, 320);
		this.setLayout(null);
		
		JLabel label1 = new JLabel("Folder Path");
		label1.setBounds(20, 15, 140, textFieldHeight);
		this.getContentPane().add(label1);
		
		final JTextField textField1 = new JTextField();
		textField1.setBounds(20, 40, 450, textFieldHeight);
		textField1.setText(defaultFolderPath);
		this.getContentPane().add(textField1);
		
		JButton fileButton = new JButton("...");
		fileButton.setBounds(480, 40, 40, textFieldHeight);
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
					driveC = new File(dirPath);
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
		
		final JCheckBox checkBox1 = new JCheckBox("Include Subfolders");
		checkBox1.setBounds(20, 65, 500, textFieldHeight);
		checkBox1.setSelected(true);
		this.getContentPane().add(checkBox1);
		
		JLabel label2 = new JLabel("File Extension");
		label2.setBounds(20, 95, 500, textFieldHeight);
		this.getContentPane().add(label2);
		
		final JTextField textField2 = new JTextField();
		textField2.setBounds(20, 120, 500, textFieldHeight);
		textField2.setText(defaultExtension);
		this.getContentPane().add(textField2);
		
		JLabel label3 = new JLabel("Text To Search");
		label3.setBounds(20, 155, 500, textFieldHeight);
		this.getContentPane().add(label3);
		
		final JTextField textField3 = new JTextField();
		textField3.setBounds(20, 180, 500, textFieldHeight);
		this.getContentPane().add(textField3);
		
		final JCheckBox checkBox2 = new JCheckBox("Ignore Case");
		checkBox2.setBounds(20, 205, 500, textFieldHeight);
		checkBox2.setSelected(true);
		this.getContentPane().add(checkBox2);
		
		JButton button = new JButton("Search");
		button.setBounds(20, 235, 500, 30);
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
				if (strToSearch == null || strToSearch.length() == 0) {
					System.out.println("검색어를 입력해주세요");
					return;
				}
				
				SearchController searchCtrl = new SearchController();
				searchCtrl.search(parentDir, bGetSubFolders, extensions, strToSearch, bIgnoreCase);
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
				
				String str1 = "defaultFolderPath=" + defaultFolderPath;
				String str2 = "defaultExtension=" + defaultExtension;
				
//				boolean bModified = false;
				
				String dirPath = textField1.getText();
				if (dirPath != null && dirPath.length() > 0) {
					File dir = new File(dirPath);
					if (dir.exists() && dir.isDirectory()) {
						str1 = "defaultFolderPath=" + dir.getAbsolutePath();
					}
				}
				
				String ext = textField2.getText();
				if (ext != null && ext.trim().length() > 0) {
					str2 = "defaultExtension=" + ext.trim();
				} else {
					str2 = "defaultExtension=*";
				}
				
				StringList strList = new StringList();
				strList.add(str1);
				strList.add(str2);
				
				FileUtil.writeFile("default.properties", strList, false);
				
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
