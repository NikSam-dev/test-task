package com.example.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.example.demo.StaticVariable.*;

public class Rest {

		private List<String> cookies;
		private HttpsURLConnection conn;

		public void main() throws Exception {

			Rest http = new Rest();

			CookieHandler.setDefault(new CookieManager());

			// GET-запрос получения страницы авторизации
			String page = http.GetPageContent(LOGIN_PAGE);
			System.out.println(page);
			String postParams = http.getFormParams(page, LOGIN, PASSWORD);

			// POST-запрос аутентификации
			http.sendPost(postParams);

			// Выполнение запроса под авторизованным пользователем
			String result = http.GetPageContent(ANY_PAGE);
			System.out.println(result);
		}

		private void sendPost(String postParams) throws Exception {

			URL obj = new URL(StaticVariable.LOGIN_PAGE);
			conn = (HttpsURLConnection) obj.openConnection();

			// Работа с REQUEST PROPERTY
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", "accounts.google.com");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Referer", LOGIN_PAGE);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

			conn.setDoOutput(true);
			conn.setDoInput(true);

			// Отправка post request
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();

			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + StaticVariable.LOGIN_PAGE);
			System.out.println("Post parameters : " + postParams);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in =
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}

		private String GetPageContent(String url) throws Exception {

			URL obj = new URL(url);
			conn = (HttpsURLConnection) obj.openConnection();

			// default is GET
			conn.setRequestMethod("GET");

			conn.setUseCaches(false);

			// act like a browser
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			if (cookies != null) {
				for (String cookie : this.cookies) {
					conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
				}
			}
			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in =
					new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Get the response cookies
			setCookies(conn.getHeaderFields().get("Set-Cookie"));

			return response.toString();

		}

		public String getFormParams(String html, String username, String password)
				throws UnsupportedEncodingException {

			System.out.println("Extracting form's data...");

			Document doc = Jsoup.parse(html);

			Element loginform = doc.getElementById(LOGIN_FORM_ID);
			Elements inputElements = loginform.getElementsByTag("input");
			List<String> paramList = new ArrayList<String>();
			for (Element inputElement : inputElements) {
				String key = inputElement.attr("name");
				String value = inputElement.attr("value");
				System.out.println("key = " + key + " , val = " + value);
				if (key.equals("email")) {
					value = username;
				} else if (key.equals("password")) {
					value = password;
				}
				paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
			}

			// build parameters list
			StringBuilder result = new StringBuilder();
			for (String param : paramList) {
				if (result.length() == 0) {
					result.append(param);
				} else {
					result.append("&" + param);
				}
			}
			return result.toString();
		}

		public List<String> getCookies() {
			return cookies;
		}

		public void setCookies(List<String> cookies) {
			this.cookies = cookies;
		}

	}