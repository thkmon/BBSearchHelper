package com.thkmon.textsearch.search;

import java.io.File;

import com.thkmon.textsearch.data.StringList;
import com.thkmon.textsearch.log.FileLogManager;
import com.thkmon.textsearch.util.FileUtil;
import com.thkmon.textsearch.util.StringUtil;

public class SearchController {
	
	private FileLogManager logger = new FileLogManager("log");
	
	public void search(File parentDir, boolean bGetSubFolders, String extensions, String strToSearch, boolean bIgnoreCase) {
		if (parentDir == null) {
			return;
		}
		
		if (!parentDir.exists()) {
			return;
		}
		
		logger.debug("시작");
		logger.debug("대상 폴더 : " + parentDir.getAbsolutePath());
		logger.debug("하위 폴더 검색여부 : " + (bGetSubFolders ? "YES" : "NO"));
		
		StringList extensionList = null;
		if (extensions != null && extensions.trim().length() > 0 && !extensions.equals("*")) {
			extensionList = StringUtil.splitWithTrim(extensions, ",");
		}
		
		logger.debug("대상 확장자 : " + (extensionList == null ? "*" : extensionList));
		
		StringList pathList = new StringList();
		addChildrenPaths(pathList, parentDir, bGetSubFolders, extensionList);
		
		if (pathList == null || pathList.size() == 0) {
			logger.debug("대상 파일 없음");
			return;
		}
		
		int fileCount = pathList.size();
		logger.debug("대상 파일 개수 : " + fileCount);
		// logger.debug("대상 파일 패스 : " + pathList);
		
		int printCount = 0;
		int percent = 0;
		System.out.println("진행율 : 0%");
		
		int lineNumber = 0;
		String onePath = "";
		for (int i=0; i<fileCount; i++) {
			onePath = pathList.get(i);
			
			float pp = (float) (i+1) / fileCount * 100;
			if (Math.round(pp) > percent) {
				percent = Math.round(pp);
				System.out.println("진행율 : " + percent + "%");
			}
			
			lineNumber = FileUtil.findLineByReadFile(new File(onePath), strToSearch, bIgnoreCase);
			if (lineNumber > -1) {
				printCount++;
				// logger.debug(onePath + " => 라인 " + lineNumber);
				logger.debug(onePath);
			}
		}
		
		logger.debug("총 " + fileCount + " 개 중 " + printCount + " 개 출력");
		logger.debug("끝");
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
}