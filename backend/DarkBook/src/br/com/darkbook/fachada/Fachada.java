package br.com.darkbook.fachada;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.darkbook.dao.BandeiraCartaoDAO;
import br.com.darkbook.dao.ClienteDAO;
import br.com.darkbook.dao.IDAO;
import br.com.darkbook.dao.PaisDAO;
import br.com.darkbook.dao.TipoLogradouroDAO;
import br.com.darkbook.dao.TipoResidenciaDAO;
import br.com.darkbook.dominio.Bandeira;
import br.com.darkbook.dominio.Cliente;
import br.com.darkbook.dominio.Pais;
import br.com.darkbook.dominio.TipoLogradouro;
import br.com.darkbook.dominio.TipoResidencia;
import br.com.darkbook.entidade.EntidadeDominio;
import br.com.darkbook.strategy.StComplementarDataCadastro;
import br.com.darkbook.strategy.IStrategy;
import br.com.darkbook.strategy.StValidarCPF;
import br.com.darkbook.strategy.StValidarDadosObrigatorios;
import br.com.darkbook.strategy.StValidarExistenciaCliente;
import br.com.darkbook.strategy.StValidarMinimoEndereco;
import br.com.darkbook.strategy.StValidarSenha;
import br.com.darkbook.util.Resultado;

public class Fachada implements IFachada {
	
	private Map<String, Map<String, List<IStrategy>>> mapStrategy;
	private Map<String, IDAO> mapDao;
	private Resultado resultado;
	// construtor
	public Fachada() {
		mapStrategy = new HashMap<String, Map<String, List<IStrategy>>>();
		mapDao = new HashMap<String, IDAO>();
		Map<String, List<IStrategy>> strategysCliente = new HashMap<>();
		ArrayList<IStrategy> clienteSalvarStrategy = new ArrayList<>();
		ArrayList<IStrategy> clienteAlterarStrategy = new ArrayList<>();
		
		StValidarDadosObrigatorios stValidarDadosObrigatorios = new StValidarDadosObrigatorios();
		StValidarCPF stValidarCPF = new StValidarCPF();
		StValidarExistenciaCliente stValidarExistenciaCliente = new StValidarExistenciaCliente();
		StValidarSenha stValidarSenha = new StValidarSenha();
		StComplementarDataCadastro stComplementarDataCadastro = new StComplementarDataCadastro();
		StValidarMinimoEndereco stValidarMinimoEndereco = new StValidarMinimoEndereco();
		
		clienteSalvarStrategy.add(stValidarDadosObrigatorios);
		clienteSalvarStrategy.add(stValidarCPF);
		clienteSalvarStrategy.add(stValidarExistenciaCliente);
		clienteSalvarStrategy.add(stValidarSenha);
		clienteSalvarStrategy.add(stComplementarDataCadastro);
		
		strategysCliente.put("SALVAR", clienteSalvarStrategy);
		
		clienteAlterarStrategy.add(stValidarDadosObrigatorios);
		clienteAlterarStrategy.add(stValidarCPF);
		clienteAlterarStrategy.add(stValidarSenha);
		clienteAlterarStrategy.add(stValidarMinimoEndereco);		
		strategysCliente.put("ALTERAR", clienteAlterarStrategy);
		
		mapStrategy.put(Cliente.class.getName(), strategysCliente);
		
		mapDao.put(Cliente.class.getName(), new ClienteDAO());
		try {
			mapDao.put(Pais.class.getName(), new PaisDAO());
			mapDao.put(TipoLogradouro.class.getName(), new TipoLogradouroDAO());
			mapDao.put(TipoResidencia.class.getName(), new TipoResidenciaDAO());
			mapDao.put(Bandeira.class.getName(), new BandeiraCartaoDAO());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Resultado salvar(EntidadeDominio entidade) {
		resultado = new Resultado();
		
		executarStrategys(entidade, mapStrategy.get(entidade.getClass().getName()).get("SALVAR"));
		
    	if(resultado.getMensagens().length() == 0) {
    		IDAO dao = mapDao.get(entidade.getClass().getName());
    		dao.salvar(entidade);
    	}
    	resultado.addEntidade(entidade);
    	return resultado;

	}

	@Override
	public Resultado consultar(EntidadeDominio entidade) {
		
		IDAO dao = mapDao.get(entidade.getClass().getName());
		resultado = new Resultado();
		resultado.setEntidades(dao.consultar(entidade));
		
		return resultado;
		
	}

	@Override
	public Resultado alterar(EntidadeDominio entidade) {
		resultado = new Resultado();
		executarStrategys(entidade, mapStrategy.get(entidade.getClass().getName()).get("ALTERAR"));
		
    	if(resultado.getMensagens().length() == 0) {
    		IDAO dao = mapDao.get(entidade.getClass().getName());
    		dao.alterar(entidade);
    	}
    	
    	resultado.addEntidade(entidade);
    	return resultado;

	}

	@Override
	public Resultado excluir(EntidadeDominio entidade) {
		IDAO dao = mapDao.get(entidade.getClass().getName());
		dao.excluir(entidade);
		
		return new Resultado();
	}
	
	private List<IStrategy> executarStrategys(EntidadeDominio entidade, List<IStrategy> strategys) {
		for(IStrategy str : strategys) {
			String mensagem = str.processar(entidade);
			if(mensagem != null) {
				System.out.println(mensagem);
				resultado.getMensagens().append(mensagem);
			}
		}
		return strategys;
	}
	


}
