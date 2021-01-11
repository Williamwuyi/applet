/**
 * jdm-v1.0
 * 2020年06月02日
 * @copyright 2020 www.jbx.cn 湖南捷报信息技术有限公司 All rights reserved.
 * @author Kunkka
 *//*

package com.jiebao.platfrom.common.briefing;

import com.jfinal.kit.PathKit;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtil {

	private static String tempDoc = PathKit.getRootClassPath() + "\\tempDoc.doc";
	private static String styleDoc = PathKit.getRootClassPath() + "\\styleDoc.doc";期

	public void build(String filename, Map<String, Object> params) throws IOException {
		FileInputStream tempInputStream = new FileInputStream(tempDoc);
		FileInputStream styleInputStream = new FileInputStream(styleDoc);

		XWPFDocument tempXdf = new XWPFDocument(tempInputStream);
		XWPFDocument styleXdf = new XWPFDocument(styleInputStream);

		this.replaceText(tempXdf, params);
		this.setStyle(styleXdf, tempXdf);

		OutputStream outputStream = new FileOutputStream(filename);
		tempXdf.write(outputStream);

		this.close(outputStream);
		this.close(tempInputStream);
		this.close(styleInputStream);
	}


	*/
/**
	 * 替换文档中的参数（word）
	 *
	 * @param doc
	 * @param params
	 *//*

	private void replaceText(XWPFDocument doc, Map<String, Object> params) {
		Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
		XWPFParagraph para;
		while (iterator.hasNext()) {
			para = iterator.next();
			this.replaceInPara(para, params);
		}
	}

	*/
/**
	 * 替换段落中的参数（word）
	 *
	 * @param para
	 * @param params
	 *//*

	private void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
		List<XWPFRun> runs;
		Matcher matcher;
		if (matcher(para.getParagraphText()).find()) {
			runs = para.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun run = runs.get(i);
				String runText = run.toString();
				matcher = matcher(runText);
				if (matcher.find()) {
					while ((matcher = matcher(runText)).find()) {
						runText = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
					}
					para.removeRun(i);
					//重新插入run里内容格式可能与原来模板的格式不一致
					para.insertNewRun(i).setText(runText);
				}
			}
		}
	}

	private Matcher matcher(String str) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher;
	}


	*/
/**
	 * 处理文档替换内容的格式问题（word）
	 *
	 * @param tempDoc
	 * @param doc
	 *//*

	private void setStyle(XWPFDocument tempDoc, XWPFDocument doc) {
		Iterator<XWPFParagraph> iterator = tempDoc.getParagraphsIterator();
		Iterator<XWPFParagraph> iterator2 = doc.getParagraphsIterator();
		XWPFParagraph para;
		XWPFParagraph para2;
		while (iterator.hasNext() && iterator2.hasNext()) {
			para = iterator.next();
			para2 = iterator2.next();
			this.setStyleInPara(para, para2);
		}
	}

	*/
/**
	 * 处理段落替换内容的格式问题（word）
	 *
	 * @param para
	 * @param para2
	 *//*

	private void setStyleInPara(XWPFParagraph para, XWPFParagraph para2) {
		List<XWPFRun> runs;
		List<XWPFRun> runs2;
		Matcher matcher;
		if (matcher(para.getParagraphText()).find()) {
			runs = para.getRuns();
			runs2 = para2.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun run = runs.get(i);
				XWPFRun run2 = runs2.get(i);
				String runText = run.toString();
				matcher = matcher(runText);
				if (matcher.find()) {
					//按模板文件格式设置格式
					run2.getCTR().setRPr(run.getCTR().getRPr());
				}
			}
		}
	}

	*/
/**
	 * 关闭输入流
	 * @param is
	 *//*

	private void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	*/
/**
	 * 关闭输出流
	 * @param os
	 *//*

	private void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
*/
