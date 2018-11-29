package br.com.darkbook.strategy;

import br.com.darkbook.dominio.Cliente;
import br.com.darkbook.entidade.EntidadeDominio;

public class ValidarSenha implements IStrategy{

	@Override
	public String processar(EntidadeDominio entidade) {
		Cliente cliente = (Cliente) entidade;
		
		String senha = cliente.getUsuario().getSenha();
		
		if(senha.length() <= 6) {
			return "A senha tem que ter mais de 6 caracteres";
		} 
		return null;
	}

}
