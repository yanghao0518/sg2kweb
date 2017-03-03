package com.sg2k.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;

final public class JUtil
{

	public static List<Integer> splitToListInteger(String fieldIdStr) throws Exception
	{
		return splitToListInteger(fieldIdStr, ",");
	}

	// 字符串转换成整形集合
	public static List<Integer> splitToListInteger(String fieldIdStr, String character) throws Exception
	{
		String[] chars = fieldIdStr.split(character);
		List<Integer> listChars = new ArrayList<Integer>();
		for (String cha : chars) {
			Integer i = strToInt(cha);
			listChars.add(i);
		}
		return listChars;
	}

	public static List<String> splitToListString(String fieldIdStr)
	{
		return splitToListString(fieldIdStr, ",");
	}
	
	public static List<String> splitToListString(String fieldIdStr,String replace,String torepacle)
	{
		return splitToListString(fieldIdStr, ",",replace,torepacle);
	}

	/**
	 * 字符串转换成list
	 * 
	 * @param fieldIdStr
	 * @param character
	 * @return
	 */
	public static List<String> splitToListString(String fieldIdStr, String character)
	{
		String[] chars = fieldIdStr.split(character);
		List<String> listChars = new ArrayList<String>();
		for (String cha : chars) {
			listChars.add(cha);
		}
		return listChars;
	}
	
	
	public static List<String> splitToListString(String fieldIdStr, String character,String replace,String torepacle)
	{
		String[] chars = fieldIdStr.split(character);
		List<String> listChars = new ArrayList<String>();
		for (String cha : chars) {
			listChars.add(cha.replace(replace, torepacle).replace(replace.toUpperCase(), torepacle));
		}
		return listChars;
	}

	public static String splitListToString(List<String> strs)
	{
		return splitListToString(strs, ",");
	}

	public static String splitListToString(String[] strs)
	{
		return splitListToString(Arrays.asList(strs));
	}

	public static String splitListIntToString(List<Integer> strs)
	{
		return splitListIntToString(strs, ",");
	}

	/**
	 * 整形集合转换成string
	 * 
	 * @param ints
	 * @param character
	 * @return
	 */
	public static String splitListIntToString(List<Integer> ints, String character)
	{
		StringBuilder sbui = new StringBuilder();
		int size = ints.size();
		int index = 0;
		for (Integer i : ints) {
			sbui.append(i);
			if ((++index) < size) {
				sbui.append(",");
			}
		}
		return sbui.toString();
	}

	public static String splitListToString(List<String> strs, String character)
	{
		StringBuilder sbui = new StringBuilder();
		int size = strs.size();
		int index = 0;
		for (String str : strs) {
			sbui.append(str);
			if ((++index) < size) {
				sbui.append(",");
			}
		}
		return sbui.toString();
	}

	public static String getSuffix(String fileName, String c)
	{
		if (null != fileName) {
			int lastindex = fileName.lastIndexOf(c);
			if (lastindex > -1) {
				return fileName.substring(fileName.lastIndexOf(c) + 1, fileName.length());
			}
		}
		return fileName;
	}

	public static String uuid()
	{
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String uuid12()
	{
		return uuid().substring(0, 12);
	}

	/**
	 * 字符串转整型
	 * 
	 * @param strIn
	 * @return
	 */
	public static Integer strToInt(String strIn) throws Exception
	{
		Integer ret = 0;
		if (null == strIn || strIn.isEmpty()) {
			throw new RuntimeException("转换成整形的字符为空！");
		}
		try {
			ret = Integer.parseInt(strIn);
			return ret;
		} catch (NumberFormatException nfe) {
			throw nfe;
		}
	}

	/**
	 * 字符串转整数
	 * 
	 * @param strIn
	 * @param defaultValue
	 * @return
	 */
	public static int strToInt(String strIn, int defaultValue)
	{
		int ret = defaultValue;
		if (strIn != null && !strIn.isEmpty()) {
			try {
				ret = Integer.parseInt(strIn);
			} catch (NumberFormatException nfe) {
				ret = defaultValue;
			}
		}
		return ret;
	}

	/**
	 * 获取某个区间整数
	 */
	public static int getRandom(int max)
	{
		Random rand = new Random();
		int i = rand.nextInt(); // int范围类的随机数
		i = rand.nextInt(max); // 生成0-max以内的随机数
		return i;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	public static String getFormetFileSize(long fileS, String split)
	{// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "0" + split + "M";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + split + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + split + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + split + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + split + "G";
		}
		return fileSizeString;
	}

	public static double formatDouble(double f)
	{
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
		return f1;
	}

	public static String UUNUM(int num)
	{
		StringBuilder strb = new StringBuilder();
		Random random = new Random();
		for (int j = 0; j < num; j++) {
			strb.append(Integer.toString(random.nextInt(10)));
		}
		return strb.toString();
	}
	
	public static void cloneJSONObject(JSONObject old,JSONObject newjson){
		Set<String> keys = old.keySet();
		for(String key:keys){
			newjson.put(key, old.get(key));
		}
	}

}
