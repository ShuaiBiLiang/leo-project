package com.leo.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * NetUtil
 * @author chenjx
 * @Version 1.0.0
 * @time   2018年5月9日
 */
public class NetUtil {
	private final static Logger logger = LoggerFactory.getLogger(NetUtil.class);

	//private final static int MAX_BUFFER = 1024000;
	private final static char CHAR[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

	/**
	 * 取出所有参数
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseEnum(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();

		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			params.put(parameterName, request.getParameter(parameterName));
		}

		return params;
	}

	/**
	 * 请求数据解析
	 * @param request
	 * @return String
	 */
	public static String parse(HttpServletRequest request) {
		String data = "";
		try {
			BufferedReader br = request.getReader();

		    String str;
		    while((str = br.readLine()) != null){
		    	data += str;
		}
		} catch(Exception e) {
			logger.warn(e.getMessage());
			return null;
		}

		return data;
	}

	public final static String parseToken(HttpServletRequest request) {
		String sessionToken = "";
		String data = NetUtil.parse(request);
		if (!StringUtils.isEmpty(data)) {
			JSONObject jsonObject = JSONObject.parseObject(data);
			if (jsonObject != null) {
				sessionToken = (String)jsonObject.get("token");
				if (sessionToken == null) {
					sessionToken = "";
				}
			}
		}
		return sessionToken;
	}


	/**
	 * 产生验证码
	 * @return 验证码
	 */
	public final static String generatorCode(int codeCount) {
		try {
			Random random = new Random();
			StringBuffer sb = new StringBuffer();
			int index;
			int len = CHAR.length;
			for (int i = 0; i < codeCount; i++) {
				index = random.nextInt(len);
				sb.append(CHAR[index]);
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 产生验证码
	 * 以文件流的形式返回（response）
	 * @param response
	 * @return 验证码
	 */
	public final static String generatorCode(HttpServletResponse response) {
		String code = null;
		try {
			BufferedImage img = new BufferedImage(68, 22, BufferedImage.TYPE_3BYTE_BGR);// TYPE_INT_RGB
			// 得到该图片的绘图对象
			Graphics g = img.getGraphics();
			Random r = new Random();
			@SuppressWarnings("unused")
			Color c = g.getColor();
			g.setColor(Color.WHITE);
			// 填充整个图片的颜色
			g.fillRect(0, 0, 68, 22);
			// 向图片中输出数字和字母
			StringBuffer sb = new StringBuffer();
			//char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
			int index, len = CHAR.length;
			for (int i = 0; i < 4; i++) {
				index = r.nextInt(len);
				g.setColor(new Color(r.nextInt(88), r.nextInt(188), r.nextInt(255)));
				g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));// 输出的字体和大小
				g.drawString("" + CHAR[index], (i * 15) + 3, 18);// 写什么数字，在图片的什么位置画
				sb.append(CHAR[index]);
			}
			ImageIO.write(img, "JPG", response.getOutputStream());

			code = sb.toString();
		} catch (Exception e) {
			return null;
		}

		return code;
	}

	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public final static String getIpAddress(HttpServletRequest request) {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

		String ip = request.getHeader("X-Forwarded-For");
//		if (logger.isInfoEnabled()) {
//			logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
//		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
//				if (logger.isInfoEnabled()) {
//					logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
//				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
//				if (logger.isInfoEnabled()) {
//					logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
//				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
//				if (logger.isInfoEnabled()) {
//					logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
//				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//				if (logger.isInfoEnabled()) {
//					logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
//				}
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
//				if (logger.isInfoEnabled()) {
//					logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
//				}
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}


}
