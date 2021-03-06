package br.com.darkbook.viewhelper.helper.cliente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import br.com.darkbook.dominio.Bandeira;
import br.com.darkbook.dominio.CartaoCredito;
import br.com.darkbook.dominio.Cidade;
import br.com.darkbook.dominio.Cliente;
import br.com.darkbook.dominio.Contato;
import br.com.darkbook.dominio.Endereco;
import br.com.darkbook.dominio.EnderecoEntrega;
import br.com.darkbook.dominio.Estado;
import br.com.darkbook.dominio.Genero;
import br.com.darkbook.dominio.Pais;
import br.com.darkbook.dominio.TipoLogradouro;
import br.com.darkbook.dominio.TipoResidencia;
import br.com.darkbook.dominio.TipoTelefone;
import br.com.darkbook.dominio.Usuario;
import br.com.darkbook.entidade.EntidadeDominio;
import br.com.darkbook.util.Resultado;
import br.com.darkbook.viewhelper.helper.IHelper;

public class AlterarCliente implements IHelper {
	
	@Override
	public void setEntidade(Resultado resultado, HttpServletResponse response) {
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		try {
			if(resultado.getMensagens().length() != 0) {
	    		response.setStatus(400); // Erro 400 (Bad Request): parametros errados ou inexistentes
	    		response.setHeader("Erros", resultado.getMensagens().toString());	// adiciona no header as mensagens de erros
	    		response.getWriter().write(resultado.getMensagens().toString());
	    	} else {
	    		response.getWriter().write("Alterado com Sucesso");
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			resultado.getMensagens().setLength(0);
		}
	}

	@Override
	public EntidadeDominio getEntidade(JSONObject objetoJson) {
		
		JSONObject clienteJson = objetoJson;
		
		System.out.println(clienteJson);
		
		Contato contato = new Contato();     		
 		Usuario usuario = new Usuario();
 		Cliente cliente = new Cliente();
 		Pais pais;
 		Estado estado;
 		Cidade cidade;
 		Endereco enderecoCobranca;
 		EnderecoEntrega enderecoEntrega;
 		CartaoCredito cartaoCredito;
 		TipoResidencia tpr;
  		TipoLogradouro tpl;
 		
    	try {
    		cliente.setCartoes(new ArrayList<>());
 			cliente.setEnderecoEntregas(new ArrayList<>());
 			cliente.setEnderecoCobrancas(new ArrayList<>());
     		
     		// preencher os objetos com os dados da requisição 
     		contato.setTipoTelefone(TipoTelefone.valueOf(clienteJson.getJSONObject("dadosPessoais").get("tipoTelefone").toString()));
     		contato.setNumero(clienteJson.getJSONObject("dadosPessoais").get("telefone").toString());
     		contato.setDdd(clienteJson.getJSONObject("dadosPessoais").get("ddd").toString());
     		contato.setEmail(clienteJson.getJSONObject("dadosPessoais").get("email").toString());
     		
			usuario.setNome(clienteJson.getJSONObject("dadosPessoais").get("nome").toString());
			usuario.setDataNascimento(LocalDate.parse(clienteJson.getJSONObject("dadosPessoais").get("dataNascimento").toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			usuario.setSobrenome(clienteJson.getJSONObject("dadosPessoais").get("sobrenome").toString());
			usuario.setGenero(Genero.valueOf(clienteJson.getJSONObject("dadosPessoais").get("genero").toString()));
			usuario.setSenha(clienteJson.getJSONObject("dadosPessoais").get("senha").toString());
			usuario.setContato(contato);
			
			cliente.setUsuario(usuario);
			cliente.setCpf(clienteJson.getJSONObject("dadosPessoais").get("cpf").toString());
			cliente.setId(Long.parseLong(clienteJson.getJSONObject("dadosPessoais").get("id").toString()));
			
			for(Object ob : clienteJson.getJSONArray("enderecosCobranca")) {
				JSONObject enderecoJson = (JSONObject) ob;
				pais = new Pais();
				estado = new Estado();
				cidade = new Cidade();
				enderecoCobranca = new Endereco();
				
				try {
					enderecoCobranca.setStatus(Boolean.parseBoolean(enderecoJson.get("deletar").toString()));
				}catch (Exception e) {
					enderecoCobranca.setStatus(true);
				}
				
				try {enderecoCobranca.setId(Long.parseLong(enderecoJson.get("id").toString()));} catch (Exception e) {}
				
				pais.setPais(enderecoJson.get("pais").toString());
				estado.setEstado(enderecoJson.get("estado").toString());
				estado.setPais(pais);
				cidade.setCidade(enderecoJson.get("cidade").toString());
				cidade.setEstado(estado);
				
				enderecoCobranca.setBairro(enderecoJson.get("bairro").toString());
				enderecoCobranca.setCep(enderecoJson.get("cep").toString());
				enderecoCobranca.setLogradouro(enderecoJson.get("logradouro").toString());
				enderecoCobranca.setNumero(Integer.parseInt(enderecoJson.get("numero").toString()));
				enderecoCobranca.setObservacao(enderecoJson.get("observacao").toString());
				enderecoCobranca.setFavorito(Boolean.parseBoolean(enderecoJson.get("favorito").toString()));
				
				tpr = new TipoResidencia();
	      		tpr.setNome(enderecoJson.get("tipoResidencia").toString());
	      		
	      		tpl = new TipoLogradouro();
	      		tpl.setNome(enderecoJson.get("tipoLogradouro").toString());
	      		
	      		enderecoCobranca.setTipoLogradouro(tpl);
				enderecoCobranca.setTipoResidencia(tpr);
				enderecoCobranca.setCidade(cidade);
				System.out.println(enderecoCobranca.isStatus());
				cliente.getEnderecoCobrancas().add(enderecoCobranca);
			}
			
			for(Object ob : clienteJson.getJSONArray("enderecosEntrega")) {
				JSONObject enderecoJson = (JSONObject) ob;
				pais = new Pais();
				estado = new Estado();
				cidade = new Cidade();
				enderecoEntrega = new EnderecoEntrega();
				try {
					enderecoEntrega.setStatus(Boolean.parseBoolean(enderecoJson.get("deletar").toString()));
				}catch (Exception e) {
					enderecoEntrega.setStatus(true);
				}
				
				try {enderecoEntrega.setId(Long.parseLong(enderecoJson.get("id").toString()));} catch (Exception e) {}
				
				pais.setPais(enderecoJson.get("pais").toString());
				estado.setEstado(enderecoJson.get("estado").toString());
				estado.setPais(pais);
				cidade.setCidade(enderecoJson.get("cidade").toString());
				cidade.setEstado(estado);
			
				enderecoEntrega.setNomeComposto(enderecoJson.get("nomeComposto").toString());
				enderecoEntrega.setBairro(enderecoJson.get("bairro").toString());
				enderecoEntrega.setCep(enderecoJson.get("cep").toString());
				enderecoEntrega.setLogradouro(enderecoJson.get("logradouro").toString());
				enderecoEntrega.setNumero(Integer.parseInt(enderecoJson.get("numero").toString()));
				enderecoEntrega.setObservacao(enderecoJson.get("observacao").toString());
				enderecoEntrega.setFavorito(Boolean.parseBoolean(enderecoJson.get("favorito").toString()));
				
				tpr = new TipoResidencia();
	      		tpr.setNome(enderecoJson.get("tipoResidencia").toString());
	      		
	      		tpl = new TipoLogradouro();
	      		tpl.setNome(enderecoJson.get("tipoLogradouro").toString());
				
				enderecoEntrega.setTipoLogradouro(tpl);
				enderecoEntrega.setTipoResidencia(tpr);
				enderecoEntrega.setCidade(cidade);
				
				
				cliente.getEnderecoEntregas().add(enderecoEntrega);
			}
			
			for(Object ob : clienteJson.getJSONArray("cartoes")) {
				JSONObject cartaoJson = (JSONObject) ob;
				cartaoCredito = new CartaoCredito();
				Bandeira bandeira = new Bandeira();
				try {
					cartaoCredito.setStatus(Boolean.parseBoolean(cartaoJson.get("deletar").toString()));
				}catch (Exception e) {
					cartaoCredito.setStatus(true);
				}
				try {cartaoCredito.setId(Long.parseLong(cartaoJson.get("id").toString()));}catch (Exception e) {}
				bandeira.setNome(cartaoJson.get("bandeira").toString());
				cartaoCredito.setBandeira(bandeira);
				cartaoCredito.setCodSeguranca(cartaoJson.get("codSeguranca").toString());
				cartaoCredito.setNomeImpresso(cartaoJson.get("nomeImpresso").toString());
				cartaoCredito.setNumero(cartaoJson.get("numero").toString());
				cartaoCredito.setPreferencial(Boolean.parseBoolean(cartaoJson.get("preferencial").toString()));
				cliente.getCartoes().add(cartaoCredito);
			}

		}catch (Exception e) {}
    	
		return cliente;
	}

}
