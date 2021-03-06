package br.com.darkbook.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import br.com.darkbook.dao.conexao.Conexao;
import br.com.darkbook.dominio.TipoLogradouro;
import br.com.darkbook.entidade.EntidadeDominio;

public class TipoLogradouroDAO implements IDAO {
	
	private Connection conexao;

	public TipoLogradouroDAO() throws ClassNotFoundException, SQLException {
		super();
	}

	@Override
	public void salvar(EntidadeDominio entidade) {
		throw new UnsupportedOperationException("Em desenvolvimento.");
	}

	@Override
	public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
		this.conexao = (Connection) Conexao.getConexao();
		List<EntidadeDominio> tiposLogradouro = new ArrayList<>();
		ResultSet resultados;
		
		String tabelaDadosTiposLogradouro = "SELECT * FROM tipo_logradouro;";
		
		
			try {
				resultados = this.conexao.prepareStatement(tabelaDadosTiposLogradouro).executeQuery();
				while(resultados.next()) {
					TipoLogradouro tipoLogradouro = new TipoLogradouro();
					tipoLogradouro.setId(resultados.getLong("tpl_id"));
					tipoLogradouro.setNome(resultados.getString("tpl_nome"));
					tiposLogradouro.add(tipoLogradouro);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Conexao.fechar(conexao);
			}
		
		return tiposLogradouro;
	}

	@Override
	public void alterar(EntidadeDominio entidade) {
		throw new UnsupportedOperationException("Em desenvolvimento.");
	}

	@Override
	public void excluir(EntidadeDominio entidade) {
		throw new UnsupportedOperationException("Em desenvolvimento.");
	}

}
