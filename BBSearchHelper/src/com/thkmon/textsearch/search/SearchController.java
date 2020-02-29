package com.thkmon.textsearch.search;

import java.io.File;

import com.thkmon.textsearch.data.StringList;
import com.thkmon.textsearch.form.AlertForm;
import com.thkmon.textsearch.form.SearchForm;
import com.thkmon.textsearch.log.FileLogManager;
import com.thkmon.textsearch.util.FileUtil;
import com.thkmon.textsearch.util.StringUtil;

public class SearchController {
	
	public void search(File parentDir, boolean bGetSubFolders, String extensions, String strToSearch, boolean bIgnoreCase, String pathToExclude) {
		if (parentDir == null) {
			return;
		}
		
		if (!parentDir.exists()) {
			return;
		}
		
		FileLogManager logger = null;
		StringBuffer resultBuff = null;
		
		try {
			logger = new FileLogManager("log");
			resultBuff = new StringBuffer();
			
			logger.debug("시작", resultBuff);
			logger.debug("대상 폴더 : " + parentDir.getAbsolutePath(), resultBuff);
			logger.debug("하위 폴더 검색여부 : " + (bGetSubFolders ? "YES" : "NO"), resultBuff);
			
			// 제외할 패스를 얻는다.
			StringList excludingPathList = getExcludingPathList(pathToExclude);
			int excludingPathCount = 0;
			if (excludingPathList != null && excludingPathList.size() > 0) {
				logger.debug("검색제외할 경로 : " + excludingPathList.toString(), resultBuff);
				excludingPathCount = excludingPathList.size();
			}
			
			StringList extensionList = null;
			if (extensions != null && extensions.trim().length() > 0 && !extensions.equals("*")) {
				extensionList = StringUtil.splitWithTrim(extensions, ",");
			}
			
			logger.debug("대상 확장자 : " + (extensionList == null ? "*" : extensionList), resultBuff);
			
			StringList pathList = new StringList();
			addChildrenPaths(pathList, parentDir, bGetSubFolders, extensionList);
			
			if (pathList == null || pathList.size() == 0) {
				logger.debug("대상 파일 없음", resultBuff);
				return;
			}
			
			int fileCount = pathList.size();
			logger.debug("대상 파일 개수 : " + fileCount, resultBuff);
			// logger.debug("대상 파일 패스 : " + pathList);
			
			int printCount = 0;
			int percent = 0;
			System.out.println("진행율 : 0%");
			
			String lineNumber = "";
			String onePath = "";
			for (int i=0; i<fileCount; i++) {
				onePath = pathList.get(i);
				
				float pp = (float) (i+1) / fileCount * 100;
				if (Math.round(pp) > percent) {
					percent = Math.round(pp);
					System.out.println("진행율 : " + percent + "%");
				}
				
				boolean bContinue = false;
				if (excludingPathCount > 0) {
					for (int k=0; k<excludingPathCount; k++) {
						if (onePath != null) {
							// 빠른 비교를 위해 경로상의 역슬래시를 슬래시로 통일한다.
							String tempPath = onePath.replace("\\", "/");
							
							if (tempPath.matches(excludingPathList.get(k))) {
								bContinue = true;
								break;
							}
						}
					}
				}
				
				if (bContinue) {
					continue;
				}
				
				if (strToSearch != null && strToSearch.length() > 0) {
					// 제목검색 또는 제목 및 내용검색
					if (SearchForm.titleButton.isSelected() || SearchForm.titleContButton.isSelected()) {
						if (bIgnoreCase) {
							if (onePath.toLowerCase().indexOf(strToSearch.toLowerCase()) > -1) {
								printCount++;
								logger.debug(onePath, resultBuff);
								continue;
							}
							
						} else {
							if (onePath.indexOf(strToSearch) > -1) {
								printCount++;
								logger.debug(onePath, resultBuff);
								continue;
							}
						}
					}
					
					// 내용검색 또는 제목 및 내용검색
					if (SearchForm.contentButton.isSelected() || SearchForm.titleContButton.isSelected()) {
						lineNumber = FileUtil.findMultiLineByReadFile(new File(onePath), strToSearch, bIgnoreCase);
						
						if (lineNumber != null && lineNumber.length() > 0) {
							printCount++;
							logger.debug(onePath + " // line : " + lineNumber, resultBuff);
							// logger.debug(onePath);
							continue;
						}
					}
					
				} else {
					printCount++;
					logger.debug(onePath, resultBuff);
				}
			}
			
			logger.debug("총 " + fileCount + " 개 중 " + printCount + " 개 파일 검색됨", resultBuff);
			logger.debug("끝", resultBuff);
			
			AlertForm.open(resultBuff.toString(), 500, 500);
			
			try {
				writeDefaultProperies();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			logger.closeLogFile();
		}
	}
	
	public void addChildrenPaths(StringList listToAdd, File parentDir, boolean bGetSubFolders, StringList extensionList) {
		if (listToAdd == null) {
			return;
		}
		
		if (!parentDir.exists()) {
			return;
		}
		
		if (!parentDir.isDirectory()) {
			return;
		}
		
		File[] fileArr = parentDir.listFiles();
		if (fileArr == null || fileArr.length == 0) {
			return;
		}
		
		String onePath = "";
		String oneExt = "";
		File oneFile = null;
		int count = fileArr.length;
		for (int i=0; i<count; i++) {
			oneFile = fileArr[i];
			if (!oneFile.exists()) {
				continue;
			}

			if (oneFile.isFile()) {
				onePath = oneFile.getAbsolutePath();
				if (onePath == null || onePath.length() == 0) {
					continue;
				}
				
				oneExt = FileUtil.getFileExtension(onePath);
				
				if (extensionList == null || extensionList.findIgnoreCase(oneExt)) {
					listToAdd.add(onePath);
				}
				
			} else if (oneFile.isDirectory()) {
				if (bGetSubFolders) {
					addChildrenPaths(listToAdd, oneFile, bGetSubFolders, extensionList);
				}
			}
		}
	}
	
	private StringList getExcludingPathList(String pathToExclude) {
		if (pathToExclude == null || pathToExclude.trim().length() == 0) {
			return null;
		}
		
		pathToExclude = pathToExclude.trim();
		
		// 파일 이름에 올 수 없는 문자열을 제거한다.
		pathToExclude = pathToExclude.replace(":", "");
		pathToExclude = pathToExclude.replace("?", "");
		pathToExclude = pathToExclude.replace("<", "");
		pathToExclude = pathToExclude.replace(">", "");
		pathToExclude = pathToExclude.replace("|", "");
		pathToExclude = pathToExclude.replace("\"", ""); // 쌍따옴표 제거
		
		// 정규식에 문제될 것 같은 문자열을 제거한다.
		pathToExclude = pathToExclude.replace("'", "");
		pathToExclude = pathToExclude.replace("!", "");
		pathToExclude = pathToExclude.replace("[", "");
		pathToExclude = pathToExclude.replace("]", "");
		pathToExclude = pathToExclude.replace("{", "");
		pathToExclude = pathToExclude.replace("}", "");
		
		// 빠른 비교를 위해 경로상의 역슬래시를 슬래시로 통일한다.
		pathToExclude = pathToExclude.replace("\\", "/");
		
		// 혹시 슬래시 2개가 연속되어 있다면 슬래시 1개로 바꾼다.
		while (pathToExclude.indexOf("//") > -1) {
			pathToExclude = pathToExclude.replace("//", "/");
		}
		
		// 정규식을 위해, 점을 역슬래시 점으로 바꾼다.
		pathToExclude = pathToExclude.replace(".", "\\.");
		
		// 정규식을 위해, 별을 점+별로 바꾼다.
		pathToExclude = pathToExclude.replace("*", ".*");
		
		if (pathToExclude == null || pathToExclude.trim().length() == 0) {
			return null;
		}
		
		StringList excludingPathList = new StringList();
		if (pathToExclude.indexOf(",") > -1) {
			String[] pathArr = pathToExclude.split(",");
			String onePath = "";
			int pathCount= pathArr.length;
			for (int i=0; i<pathCount; i++) {
				onePath = pathArr[i];
				if (onePath != null && onePath.trim().length() > 0) {
					excludingPathList.add(onePath.trim());
				}
			}
			
		} else {
			if (pathToExclude.trim().length() > 0) {
				excludingPathList.add(pathToExclude.trim());
			}
		}
		
		return excludingPathList;
	}
	
	private void writeDefaultProperies() {
		System.out.println("writeDefaultProperies start");
		
		String str1 = "defaultFolderPath=" + SearchForm.defaultFolderPath;
		String strPathToExclude = "defaultPathToExclude=" + SearchForm.defaultFolderPath;
		String str2 = "defaultExtension=" + SearchForm.defaultExtension;
		String str3 = "defaultTextToSearch=" + SearchForm.defaultTextToSearch;
		String str4 = "defaultCharset=" + SearchForm.defaultCharset;
		
		String folderPath = SearchForm.textField1.getText();
		if (folderPath != null && folderPath.length() > 0) {
			File dir = new File(folderPath);
			if (dir.exists() && dir.isDirectory()) {
				str1 = "defaultFolderPath=" + dir.getAbsolutePath();
			}
		}
		
		String pathToExclude = SearchForm.textField5.getText();
		if (pathToExclude != null && pathToExclude.trim().length() > 0) {
			strPathToExclude = "defaultPathToExclude=" + pathToExclude.trim();
		} else {
			strPathToExclude = "defaultPathToExclude=";
		}
		
		String extension = SearchForm.textField2.getText();
		if (extension != null && extension.trim().length() > 0) {
			str2 = "defaultExtension=" + extension.trim();
		} else {
			str2 = "defaultExtension=*";
		}
		
		String textToSearch = SearchForm.textField3.getText();
		if (textToSearch != null && textToSearch.trim().length() > 0) {
			str3 = "defaultTextToSearch=" + textToSearch.trim();
		} else {
			str3 = "defaultTextToSearch=";
		}
		
		String charset = SearchForm.textField4.getText();
		if (charset != null && charset.trim().length() > 0) {
			str4 = "defaultCharset=" + charset.trim();
		} else {
			str4 = "defaultCharset=UTF-8";
		}
		
		StringList strList = new StringList();
		strList.add(str1);
		strList.add(strPathToExclude);
		strList.add(str2);
		strList.add(str3);
		strList.add(str4);
		
		try {
			FileUtil.writeFile("default.properties", strList, false);
		} catch (Exception e2) {
			System.err.println("Fail to writeFile(default.properties)");
			e2.printStackTrace();
		}
		
		System.out.println("writeDefaultProperies end");
	}
}