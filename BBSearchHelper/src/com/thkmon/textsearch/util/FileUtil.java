package com.thkmon.textsearch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.thkmon.textsearch.data.StringList;
import com.thkmon.textsearch.form.SearchForm;

public class FileUtil {
	public static StringList readFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}

		StringList resultList = null;

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		String charset = SearchForm.defaultCharset;
		if (charset == null || charset.length() == 0) {
			charset = "UTF-8";
		}
		
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);

			String oneLine = null;
			while ((oneLine = bufferedReader.readLine()) != null) {
				if (resultList == null) {
					resultList = new StringList();
				}

				resultList.add(oneLine);
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close(bufferedReader);
			close(inputStreamReader);
			close(fileInputStream);
		}

		return resultList;
	}

	
	public static boolean writeFile(String filePath, StringList stringList, boolean bAppend) {
		if (filePath == null || filePath.length() == 0) {
			return false;
		}

		File file = new File(filePath);

		boolean bWrite = false;

		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		
		try {
			fileOutputStream = new FileOutputStream(file, bAppend);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
			bufferedWriter = new BufferedWriter(outputStreamWriter);

			if (stringList != null && stringList.size() > 0) {
				String oneLine = null;

				int lineCount = stringList.size();
				int lastIndex = lineCount - 1;

				for (int i = 0; i < lineCount; i++) {
					oneLine = stringList.get(i);

					bufferedWriter.write(oneLine, 0, oneLine.length());
					if (i < lastIndex) {
						bufferedWriter.newLine();
					}
				}
			}

			bWrite = true;

		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close(bufferedWriter);
			close(outputStreamWriter);
			close(fileOutputStream);
		}

		return bWrite;
	}
	
	
	private static void close(BufferedWriter bufferedWriter) {
		try {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}

	private static void close(OutputStreamWriter outputStreamWriter) {
		try {
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}

	private static void close(FileOutputStream fileOutputStream) {
		try {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}

	private static void close(FileInputStream fileInputStream) {
		try {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}

	private static void close(InputStreamReader inputStreamReader) {
		try {
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}

	private static void close(BufferedReader bufferedReader) {

		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (Exception e) {
			// 무시
		}
	}
	
	
	public static String getFileExtension(String filePath) {
		if (filePath == null || filePath.length() == 0) {
			return "";
		}
		
		int lastSlashIdx = filePath.lastIndexOf("/");
		int lastBackSlashIdx = filePath.lastIndexOf("\\");
		if (lastSlashIdx > -1 || lastBackSlashIdx > -1) {
			if (lastSlashIdx > lastBackSlashIdx) {
				filePath = filePath.substring(lastSlashIdx + 1);
			} else {
				filePath = filePath.substring(lastBackSlashIdx + 1);
			}
		}
		
		int lastDotIdx = filePath.lastIndexOf(".");
		if (lastDotIdx > -1) {
			return filePath.substring(lastDotIdx  + 1);
		}
		
		return "";
	}
	
	
	/**
	 * 파일을 읽어서 검출된 라인을 가져온다.
	 * 향후 first 라인만 가져올지 multi 라인을 모두 가져올지 옵셔널하게 설정할 수 있었으면 좋겠다.
	 * 
	 * @param file
	 * @param strToFind
	 * @param bIgnoreCase
	 * @return
	 */
	public static String findMultiLineByReadFile(File file, String strToFind, boolean bIgnoreCase) {
		if (file == null || !file.exists()) {
			return "";
		}
		
		if (!file.isFile()) {
			return "";
		}
		
		StringBuffer lineNumberBuff = new StringBuffer();
		int lineNumber = -1;

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		String charset = SearchForm.defaultCharset;
		if (charset == null || charset.length() == 0) {
			charset = "UTF-8";
		}
		
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);

			if (bIgnoreCase) {
				strToFind = strToFind.toLowerCase();
			}
			
			int lineCount = 0;
			int foundIndex = 0;
			
			String oneLine = null;
			while ((oneLine = bufferedReader.readLine()) != null) {
				lineCount++;
				
				if (bIgnoreCase) {
					foundIndex = oneLine.toLowerCase().indexOf(strToFind);
				} else {
					foundIndex = oneLine.indexOf(strToFind);
				}
				
				if (foundIndex > -1) {
					lineNumber = lineCount;
					if (lineNumberBuff.length() > 0) {
						lineNumberBuff.append(", ");
					}
					lineNumberBuff.append(String.valueOf(lineNumber));
					// break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close(bufferedReader);
			close(inputStreamReader);
			close(fileInputStream);
		}

		return lineNumberBuff.toString();
	}
}