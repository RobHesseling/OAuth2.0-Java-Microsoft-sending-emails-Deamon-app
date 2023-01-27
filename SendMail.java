

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.internal.http.HttpMethod;

public class SendMail {
	
	/* Dependecies used (ivy):
	 * <dependency org="com.google.code.gson" name="gson" rev="2.8.2"/>
	 * <dependency org="com.squareup.okhttp" name="okhttp" rev="2.7.5"/>
	 * */
	
	public static String tenantID 	= "***"; //Your Azure Active Directory organizations tenantID. 
	public static String clientID 	= "***"; //Your Azure Active Directory app its clientID.
	public static String secret 	= "***"; //Your Azure Active Directory app its secret.
	public static String userID 	= "***"; //A userid from a user of your organization. The mail will be send on behalf of this user. It can also be found in the Azure Active Directory of your organization. 

	public static void main(String[] args) throws IOException {
		
		String accessToken = getAccess_Token(tenantID, clientID, secret);
		sendEmail(accessToken, userID);
	}

	private static void sendEmail(String accessToken, String userID) throws IOException {
		String SEND_MAIL_URL = "https://graph.microsoft.com/v1.0/users/"+userID+"/microsoft.graph.sendMail";
		String send_mail_body = "{'message': {'subject': 'Test','body': {'contentType': 'Text','content': 'This is a test mail!'},'toRecipients': [{'emailAddress': {'address': 'someone@someCompany.com'}}]}, 'saveToSentItems': 'true'}";
		
		Properties headers = new Properties();
		RequestBody requestBody = null;
		if(HttpMethod.permitsRequestBody("POST")) {
			MediaType type = MediaType.parse("application/json; charset=utf-8");
			requestBody = RequestBody.create(type, send_mail_body);
		}
		
		Request.Builder requestbuilder = new Request.Builder()
				.method("POST", requestBody)
				.url(SEND_MAIL_URL)
				.header("User-Agent", "Mozilla/5.0");
		

		requestbuilder.addHeader("Authorization","Bearer "+ accessToken);
		
		if(headers != null) {
			Set<Object> keyset = headers.keySet();
			for(Object key : keyset) {
				requestbuilder.addHeader((String) key, headers.getProperty((String) key));
			}
		}
		
		OkHttpClient client = new OkHttpClient();
		Request request = requestbuilder.build();
		
		Call call = client.newCall(request);
		Response respons = call.execute();

		ResponseBody responseBody = respons.body();
		String responseContent = responseBody.string();
		
		System.out.println(responseContent);
	}

	private static String getAccess_Token(String tenantID, String clientId, String secret) throws IOException {
			URL url = new URL("https://login.microsoftonline.com/"+tenantID+"/oauth2/v2.0/token");

			Map<String, String> params = new LinkedHashMap<>();
			params.put("client_id", clientId);
			params.put("scope", "https://graph.microsoft.com/.default");
			params.put("client_secret", secret);
			params.put("grant_type", "client_credentials");

			StringBuilder postData = new StringBuilder();
			byte[] postDataBytes = null;

			for (Map.Entry<String, String> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
			}
			postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);


			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder response = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				response.append((char) c);

			/* Get only the accesstoken from response. 
			 * com.google.code.gson dependency needed 
			 * https://mvnrepository.com/artifact/com.google.code.gson/gson */
			String json = response.toString();
			JsonObject object = new Gson().fromJson(json, JsonObject.class);
			String access_token = object.get("access_token").getAsString();

			return access_token;
		}
	
}
