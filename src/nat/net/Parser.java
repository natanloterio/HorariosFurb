package nat.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Parser {

	public static void main(String[] args) {
		Parser par = new Parser();
		try {
			par.logar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void entrarNoSistemaFurb() {/*
										 * 
										 * Connection.Response loginForm = Jsoup
										 * .connect(
										 * "https://www.furb.br/academico/servicosAcademicos"
										 * )
										 * .method(Connection.Method.POST).execute
										 * ();
										 * 
										 * Connection.Response resposta = Jsoup
										 * .connect(
										 * "https://www.furb.br/academico/uHorario"
										 * )
										 * .cookies(loginForm.cookies()).method
										 * (Connection.Method.POST) .execute();
										 * 
										 * Document document = Jsoup.connect(
										 * "https://www.furb.br/academico/validaLogon"
										 * ).get();
										 * 
										 * System.out.println(document.toString()
										 * );
										 */
	}

	public void logar() throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(
					"https://www.furb.br/academico/validaLogon");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("nm_login", "nloterio"));
			nvps.add(new BasicNameValuePair("ds_senha", "112713"));
			nvps.add(new BasicNameValuePair("nome_servlet",
					"https://www.furb.br/academico/servicosAcademicos"));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			httpPost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
			CloseableHttpResponse response2 = httpclient.execute(httpPost);

			try {
				HttpEntity entity2 = response2.getEntity();
				byte[] result = EntityUtils.toByteArray(entity2);
				String str = new String(result, "UTF-8");
				System.out.println(str);
			} finally {
				response2.close();
			}
		} finally {
			httpclient.close();
		}
	}
}
