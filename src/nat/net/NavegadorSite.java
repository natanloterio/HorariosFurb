package nat.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author marcelo
 */
public class NavegadorSite {

	private static final int COLUNA_NOME_MATERIA = 1;
	private static final int COLUNA_SEGUNDA_FEIRA = 5;
	private static final int COLUNA_TERCA_FEIRA = 6;
	private static final int COLUNA_QUARTA_FEIRA = 7;
	private static final int COLUNA_QUINTA_FEIRA = 8;
	private static final int COLUNA_SEXTA_FEIRA = 9;
	private static final int COLUNA_SABADO = 10;
	private final DefaultHttpClient client = new DefaultHttpClient();

	/**
	 * Efetua login no site
	 * 
	 * @param url
	 *            - URL de Login do site
	 * @param user
	 *            - usuario
	 * @param password
	 *            - senha
	 * @return true - login ok | false - login fail
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public boolean login(final String url, final String user,
			final String password) throws UnsupportedEncodingException,
			IOException {

		/* Método POST */
		final HttpPost post = new HttpPost(url);
		boolean result = false;

		/* Configura os parâmetros do POST */
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("nm_login", user));
		nameValuePairs.add(new BasicNameValuePair("ds_senha", password));
		nameValuePairs.add(new BasicNameValuePair("nome_servlet",
				"https://www.furb.br/academico/servicosAcademicos"));

		/*
		 * Codifica os parametros.
		 * 
		 * Antes do encoder: fulano@email.com Depois do enconder:
		 * fulano%40email.com
		 */
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

		/* Define navegador */
		post.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:18.0) Gecko/20100101 Firefox/18.0");

		/* Efetua o POST */
		HttpResponse response = client.execute(post);

		/*
		 * Resposta HTTP: Sempre imprimirá “HTTP/1.1 302 Object moved” (no caso
		 * da devmedia)
		 */
		System.out.println("Login form get: " + response.getStatusLine());

		/*
		 * Consome o conteúdo retornado pelo servidor Necessário esvaziar o
		 * response antes de usar o httpClient novamente
		 */
		EntityUtils.consume(response.getEntity());

		/*
		 * Testar se o login funcionou.
		 * 
		 * Estratégia: acessar uma página que só está disponível quando se está
		 * logado Em caso de erro, o servidor irá redirecionar para a página de
		 * login A pagina de login contem uma string: "Login DevMedia" Se esta
		 * String estiver presente, significa que o login não foi efetuado com
		 * sucesso
		 */
		/*
		 * final HttpGet get = new HttpGet(
		 * "https://www.furb.br/academico/uHorario"); response =
		 * client.execute(get);
		 * 
		 * 
		 * Verifica se a String: "Login DevMedia" está presente
		 * 
		 * if (checkSuccess(response)) {
		 * System.out.println("Conexao Estabelecida!"); result = true; } else {
		 * System.out.println("Login não-efetuado!"); }
		 */

		return true;
	}

	/**
	 * Abre página
	 * 
	 * @param url
	 *            - Página a acessar
	 * @throws IOException
	 */
	public void openPage(final String url) throws IOException {
		final HttpGet get = new HttpGet(url);
		final HttpResponse response = client.execute(get);
		getHTLM(response);
	}

	/**
	 * Encerra conexão
	 */
	public void close() {
		client.getConnectionManager().shutdown();
	}

	/**
	 * Busca por String que indica se o usuário está logado ou não
	 * 
	 * @param response
	 * @return true - Não achou String | false - Achou String
	 * @throws IOException
	 */
	private boolean checkSuccess(final HttpResponse response)
			throws IOException {
		final BufferedReader rd = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String line;
		boolean found = false;
		/*
		 * Deixa correr todo o laco, mesmo achando a String, para consumir o
		 * content
		 */
		while ((line = rd.readLine()) != null) {
			if (line.contains("Login DevMedia")) {
				found = true;
			}
		}
		return !found;
	}

	/**
	 * Salva a página
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private String getHTLM(final HttpResponse response) throws IOException {
		final BufferedReader rd = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	/**
	 * Roda aplicação
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		NavegadorSite navegador = new NavegadorSite();

		try {
			// Tenta efetuar login
			boolean ok = navegador.login(
					"https://www.furb.br/academico/validaLogon", "", "");
			if (ok) {
				// Acessa página restrita
				navegador.openPage("https://www.furb.br/academico/uHorario");
				navegador.listaHorarios();

			}
			navegador.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void listaHorarios() throws Exception {
		/* Método POST */
		final HttpPost post = new HttpPost(
				"https://www.furb.br/academico/userHorario");
		boolean result = false;

		/* Configura os parâmetros do POST */
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("cd_aluno", "102926"));

		/*
		 * Codifica os parametros.
		 * 
		 * Antes do encoder: fulano@email.com Depois do enconder:
		 * fulano%40email.com
		 */
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));

		/* Define navegador */
		post.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:18.0) Gecko/20100101 Firefox/18.0");

		/* Efetua o POST */
		HttpResponse response = client.execute(post);

		lerTabelaDeHorarios(response);

	}

	private void lerTabelaDeHorarios(HttpResponse response) throws Exception {
		Document doc = parseXML(getHTLM(response));
		String[][] array = null;
		Elements tables = doc.select("table");
		array = converterParaArray(tables);

		for (int posLinha = 0; posLinha < array.length; posLinha++) {
			String[] linha = array[posLinha];
			if (linha.length <= 1) {
				continue;
			}
			String nomeMaterioa = linha[COLUNA_NOME_MATERIA];
			String horarioSegunda = linha[COLUNA_SEGUNDA_FEIRA];
			String horarioTerca = linha[COLUNA_TERCA_FEIRA];
			String horarioQuarta = linha[COLUNA_QUARTA_FEIRA];
			String horarioQuinta = linha[COLUNA_QUINTA_FEIRA];
			String horarioSexta = linha[COLUNA_SEXTA_FEIRA];
			String horarioSabado = linha[COLUNA_SABADO];
			
			if(horarioSegunda.trim().length()>1){
				adicionarMateriaSegundaFeira(nomeMaterioa,horarioSegunda);
			}
			
			if(horarioTerca.trim().length()>1){
				adicionarMateriaTercaFeira(nomeMaterioa,horarioTerca);
			}
			
			if(horarioQuarta.trim().length()>1){
				adicionarMateriaQuartaFeira(nomeMaterioa,horarioQuarta);
			}
			
			if(horarioQuinta.trim().length()>1){
				adicionarMateriaQuintaFeira(nomeMaterioa,horarioQuinta);
			}
			
			if(horarioSexta.trim().length()>1){
				adicionarMateriaSextaFeira(nomeMaterioa,horarioSexta);
			}
			
			if(horarioSabado.trim().length()>1){
				adicionarMateriaSabado(nomeMaterioa,horarioSabado);
			}
			System.out.println(nomeMaterioa);

		}
	}

	private void adicionarMateriaSabado(String nomeMaterioa,
			String horarioSabado) {
		// TODO Auto-generated method stub
		
	}

	private void adicionarMateriaSextaFeira(String nomeMaterioa,
			String horarioSexta) {
		// TODO Auto-generated method stub
		
	}

	private void adicionarMateriaQuintaFeira(String nomeMaterioa,
			String horarioQuinta) {
		// TODO Auto-generated method stub
		
	}

	private void adicionarMateriaQuartaFeira(String nomeMaterioa,
			String horarioQuarta) {
		// TODO Auto-generated method stub
		
	}

	private void adicionarMateriaTercaFeira(String nomeMaterioa,
			String horarioTerca) {
		// TODO Auto-generated method stub
		
	}

	private void adicionarMateriaSegundaFeira(String nomeMaterioa,
			String horarioSegunda) {
		// TODO Auto-generated method stub
		
	}

	private String[][] converterParaArray(Elements tables) {
		for (Element table : tables) {
			Elements trs = table.select("tr");
			String[][] trtd = new String[trs.size()][];
			for (int i = 0; i < trs.size(); i++) {
				Elements tds = trs.get(i).select("td");
				trtd[i] = new String[tds.size()];
				for (int j = 0; j < tds.size(); j++) {
					trtd[i][j] = tds.get(j).text();
				}
			}
			return trtd;
		}
		return null;
	}

	private Document parseXML(String contents) throws Exception {
		Document doc = Jsoup.parse(contents);
		return doc;
	}

}