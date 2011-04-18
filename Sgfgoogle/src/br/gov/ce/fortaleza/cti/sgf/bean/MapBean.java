package br.gov.ce.fortaleza.cti.sgf.bean;

import java.util.Date;
import java.util.List;

import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.gov.ce.fortaleza.cti.sgf.entity.Transmissao;
import br.gov.ce.fortaleza.cti.sgf.entity.Veiculo;
import br.gov.ce.fortaleza.cti.sgf.service.BaseService;
import br.gov.ce.fortaleza.cti.sgf.service.TransmissaoService;
import br.gov.ce.fortaleza.cti.sgf.service.VeiculoService;
import br.gov.ce.fortaleza.cti.sgf.util.DateUtil;
import br.gov.ce.fortaleza.cti.sgf.util.JSFUtil;
import br.gov.ce.fortaleza.cti.sgf.util.dto.MapDTO;

@Scope("session")
@Component("mapBean")
public class MapBean extends EntityBean<Integer, MapDTO>  {

	public String pontos = new String("");
	private Date horaInicial;
	private Date horaFinal;
	public Date start;
	public Date end;
	public Integer tempoConsulta;

	@Autowired
	private VeiculoService veiculoService;

	@Autowired
	private TransmissaoService transmissaoService;

	private Veiculo veiculo;
	private String route = new String("");

	@Override
	protected MapDTO createNewEntity() {
		return new MapDTO();
	}

	@Override
	protected Integer retrieveEntityId(MapDTO entity) {
		throw new IllegalStateException("Não implementado");
	}

	@Override
	protected BaseService<Integer, MapDTO> retrieveEntityService() {
		throw new IllegalStateException("Não implementado");
	}

	public String searchAddress() {
		setCurrentState(MapDTO.SEARCH_ADDRESS);
		setCurrentBean(currentBeanName());
		this.entities = null;
		return SUCCESS;
	}

	public String veiculosMap() {
		setCurrentState(MapDTO.VEICULOS_MAP);
		setCurrentBean(currentBeanName());
		this.entities = null;
		return SUCCESS;
	}

	public String veiculosRoute() {
		setCurrentState(MapDTO.VEICULOS_ROUTE);
		setCurrentBean(currentBeanName());
		this.entities = null;
		showVehiclesOnMap();
		return SUCCESS;
	}

	public boolean isSearchAddress() {
		return MapDTO.SEARCH_ADDRESS.equals(getCurrentState());
	}

	public boolean isVeiculosMapState(){
		return MapDTO.VEICULOS_MAP.equals(getCurrentState());
	}

	public boolean isVeiculosRouteState(){
		return MapDTO.VEICULOS_ROUTE.equals(getCurrentState());
	}

	public String showVehiclesOnMap(){		
		List<Veiculo> veiculos = veiculoService.veiculosRastreados();
		String line = new String("");

		for (Veiculo v : veiculos) {
			Float odometro = v.getOdometro() != null ? v.getOdometro() : 0F;
			Float dist = v.getDistancia() != null ? v.getDistancia() : 0F;
			line += ((Point)v.getGeometry()).y + "##" +  ((Point)v.getGeometry()).x + "##" + v.getId() + "##" + v.getPlaca() + "##" + v.getVelocidade() + "##"
			+ odometro + "##" + v.getPontoProximo().getDescricao() + "##" + 																																																																																																																																																																	dist + "##" + DateUtil.parseAsString("dd/MM/yyyy HH:mm", v.getDataTransmissao()) + "##$##";
		}
		this.pontos = line;
		return SUCCESS;
	}

	/**
	 * monta uma string de saida para cosntrução da rota de uma veículo
	 * @return
	 */
	public String showVehiclesRouteOnMap(){
		String line = "";
		this.route = "";
		if(this.veiculo != null){
			Veiculo v = veiculoService.retrieve(veiculo.getId());

			Date current = new Date();
			Date initial = DateUtil.adicionarOuDiminuir(current, -tempoConsulta*DateUtil.HOUR_IN_MILLIS);
			List<Transmissao> transmissoes = transmissaoService.retrieveByVeiculo(veiculo.getId(), initial, current);
			if(transmissoes.size() > 0){
				for (Transmissao transmissao : transmissoes) {
					Float vel = transmissao.getVelocidade() == null ? 0F : transmissao.getVelocidade();
					Float odometro = transmissao.getOdometro() == null ? 0F : transmissao.getOdometro();
					Double x = ((Point)transmissao.getGeometry()).x;
					Double y = ((Point)transmissao.getGeometry()).y;
					String placa = v.getPlaca();
					String pprox = transmissao.getPonto() != null ? transmissao.getPonto().getDescricao() : "";
					Float dist = transmissao.getDistancia() == null ? 0F : transmissao.getDistancia();
					line += y + "##" + x + "##" + transmissao.getVeiculoId() + "##" +  placa + "##" + vel + "##" + odometro + "##" 
					+ pprox + "##" + dist + "##" + DateUtil.parseAsString("dd/MM/yyyy HH:mm", transmissao.getDataTransmissao())  + "##$##";
				}
			} else {
				JSFUtil.getInstance().addErrorMessage("msg.error.veiculo.sem.rota");
				return FAIL;
			}
		} else {
			return FAIL;
		}
		this.route = line;
		return SUCCESS;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getPontos() {
		return pontos;
	}

	public void setPontos(String pontos) {
		this.pontos = pontos;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}

	public Date getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}

	public Integer getTempoConsulta() {
		return tempoConsulta;
	}

	public void setTempoConsulta(Integer tempoConsulta) {
		this.tempoConsulta = tempoConsulta;
	}
}