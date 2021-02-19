/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright Regione Piemonte - 2021
 *******************************************************************************/
package it.csi.sigit.sigitweba.business.manager;


import it.csi.csi.porte.InfoPortaDelegata;
import it.csi.csi.porte.proxy.PDProxy;
import it.csi.csi.util.xml.PDConfigReader;
import it.csi.csi.wrapper.CSIException;
import it.csi.sigit.sigitweba.business.manager.util.ServiceException;
import it.csi.sigit.sigitweba.dto.common.CodeDescription;
import it.csi.sigit.sigitweba.util.Constants;
import it.csi.sigit.sigitweba.util.MapDto;
import it.csi.sigit.sigitweba.util.Messages;
import it.csi.sigit.sigitweba.util.mail.Mail;
import it.csi.sigit.sigitweba.util.mail.MailSender;
import it.csi.sitad.tope.exception.TopeNoDataExtractedException;
import it.csi.sitad.tpnm.entity.Via;
import it.csi.sitad.tpnm.interf.ToponomasticaInterface;
import it.csi.tavt.dto.Provincia;
import it.csi.tavt.interf.TavtInterface;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * @author 70140
 * 
 */
public class ServiziMgr {

	protected static Logger log = Logger.getLogger(Constants.APPLICATION_CODE
			+ ".business.manager");
	
	private final String TAVT_RESOURCE = "/META-INF/defpd_tavt.xml";
	private final String TOPE_RESOURCE = "/META-INF/pd_topesv_Topo.xml";
	private final String PROPERTIES_RESOURCE = "/META-INF/sigitweba.properties";
	
	protected DbMgr dbMgr;

	protected TavtInterface getTavt() throws FileNotFoundException {
		log.debug("[ServiziMgr::getTavt] BEGIN");
		InputStream is = getClass().getResourceAsStream(TAVT_RESOURCE);
		if(is != null){
			try{
				InfoPortaDelegata info = PDConfigReader.read(is);
				return(TavtInterface) PDProxy.newInstance(info);
			}
			 catch (Exception e) {
				log.error("[ServiziMgr::getTavt] errore nella parsificazione della configurazione di TAVT:"+ e, e);
				throw new IllegalArgumentException("errore nella parsificazione della configurazione di TAVT");
			}
			 finally{
				 log.debug("[ServiziMgr::getTavt] END");
			 }
		} 
		else {
			log	.error("[ServiziMgr::getTavt] configurazione di TAVT non trovata");
		}
		throw new IllegalArgumentException(	"configurazione di TAVT non trovata");
	}

	protected ToponomasticaInterface getToponomastica() {
		log.debug("[ServiziMgr::getToponomastica] BEGIN");
		InputStream is = getClass().getResourceAsStream(TOPE_RESOURCE);
		if (is != null) {
			try {
				InfoPortaDelegata info = PDConfigReader.read(is);
				return (ToponomasticaInterface) PDProxy.newInstance(info);
			} 
			catch (Exception e) {
				log.error("[ServiziMgr::getToponomastica] errore nella parsificazione della configurazione di TOPE:"+ e, e);
				throw new IllegalArgumentException("errore nella parsificazione della configurazione di TOPE");
			}
		} else{
			log.error("[ServiziMgr::getToponomastica] configurazione di TOPE non trovata");
		}
		throw new IllegalArgumentException("configurazione di TOPE non trovata");
	}
	public void test(){
		log.debug("ServiziMgr RAGGIUNTO CORRETTAMENTE");
	}
	
	public ArrayList<CodeDescription> getProvincePiemonte() throws ServiceException{
		log.debug("[ServiziMgr::getProvince] BEGIN");
		try{
			Provincia[] provRegione = getTavt().selProvinceByRegione(Constants.COD_ISTAT_PIEMONTE);
			return MapDto.mapSiglaProvince(provRegione);
		}
		catch (Exception e) {
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		finally{
			log.debug("[ServiziMgr::getProvince] END");
		}
	}

	public ArrayList<CodeDescription> getProvinceItalia() throws ServiceException{
		log.debug("[ServiziMgr::getProvince] BEGIN");
		try{
			Provincia[] provRegione = getTavt().selProvAttuali();
			return MapDto.mapSiglaProvince(provRegione);
		}
		catch (Exception e) {
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		finally{
			log.debug("[ServiziMgr::getProvince] END");
		}
	}
	
	public ArrayList<CodeDescription> getComuniByIstatProvincia(String istatProvincia) throws ServiceException {
		log.debug("[ServiziMgr::getComuniByIstatProvincia] BEGIN");
		try{
			return MapDto.mapComuni(getTavt().selComuniByProvincia(istatProvincia));
		}
		catch (Exception e) {
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		finally{
			log.debug("[ServiziMgr::getComuniByIstatProvincia] END");
		}
	}
	
	public ArrayList<CodeDescription> getListaVieValide(String nomeVia, String codIstatComune) throws ServiceException {
		log.debug("[ServiziMgr::getListaVieValide] BEGIN");
		try {
			return topeGetViaByNome(nomeVia, codIstatComune, Constants.COD_STATO_VIA_VALIDA);
		}
		catch(Exception e) {
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		finally {
			log.debug("[ServiziMgr::getListaVieValide] END");
		}
	}

	public ArrayList<CodeDescription> getVia(String idVia) throws ServiceException {
		log.debug("[ServiziMgr::getListaVieValide] BEGIN");
		try {
			return topeGetViaById(idVia);
		}
		catch(Exception e) {
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		finally {
			log.debug("[ServiziMgr::getListaVieValide] END");
		}
	}
	
	private ArrayList<CodeDescription> topeGetViaByNome(String ilNomeDellaVia, String codIstat, String codStatoVia) throws ServiceException {
		log.debug("[ServiziMgr::topeGetViaByNome] BEGIN");
		ArrayList<CodeDescription> result = new ArrayList<CodeDescription>();
		Via[] leVie = null; 
		try {
			log.debug("---> codIstat *" + codIstat+"*");
			log.debug("---> ilNomeDellaVia *" + ilNomeDellaVia+"*");
			//leVie = getToponomastica().cercaVieByNomeAndIstat(ilNomeDellaVia, new String[]{codIstat}, 4, false, "A",0);
			leVie = getToponomastica().cercaVieByNomeAndIstat(ilNomeDellaVia, new String[]{codIstat}, 4, false, codStatoVia, 0);
			if(leVie!=null){
				log.debug("---> Entro in for ");
				for (int i = 0; i < leVie.length; i++) {
					log.debug("####################################");
					log.debug("---> IDL2 via "+leVie[i].getIdL2());
					log.debug("---> Nome via "+leVie[i].getIndirizzo().getNomeVia());
					log.debug("---> Localita via "+leVie[i].getLocalita().getNome());
					
					
					log.debug("---> Localita oggetto "+leVie[i].getLocalita());
					log.debug("---> Localita.getComune() oggetto "+leVie[i].getLocalita().getComune());
					
					log.debug("---> Localita.getComune().getToponimo() oggetto "+leVie[i].getLocalita().getComune().getToponimo());
					
					log.debug("---> Stato via "+leVie[i].getStatoVia());
					log.debug("####################################");
					
					
					result.add(MapDto.mapToCodeDescription(leVie[i]));
				}
			}
			else{
				log.debug("---> Non Entro in for ");
			}
		
			
		}catch (TopeNoDataExtractedException e) {
			log.info("Nessun dato restituito");
			return result;
		}  
		catch (RemoteException e) {
			log.fatal("Si e' verificato un problema in getViaByNome");
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		} catch (CSIException e) {
			log.fatal("Si e' verificato un problema in getViaByNome");
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		log.debug("[ServiziMgr::topeGetViaByNome] END");
		return result;
	}

	private ArrayList<CodeDescription> topeGetViaById(String idVia) throws ServiceException {
		log.debug("[ServiziMgr::topeGetViaByCode] BEGIN");
		ArrayList<CodeDescription> result = new ArrayList<CodeDescription>();
		try {
			Via via = getToponomastica().cercaViaById(Integer.valueOf(idVia));
			result.add(MapDto.mapToCodeDescription(via));
			
		}catch (TopeNoDataExtractedException e) {
			log.info("Nessun dato restituito");
			return result;
		}  
		catch (RemoteException e) {
			log.fatal("Si e' verificato un problema in getViaByCode");
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		} catch (CSIException e) {
			log.fatal("Si e' verificato un problema in getViaByCode");
			throw new ServiceException(e, Messages.ERROR_RECUPERO_SERVIZIO);
		}
		log.debug("[ServiziMgr::topeGetViaByCode] END");
		return result;
	}
	
	public void sendMail(String destinatario) throws ServiceException {
		MailSender sender = new MailSender();
		Mail email = new Mail();

		
		try {
			System.out.println("INVIO EMAIL");
			// Destinatario
			email.setDestinatario(destinatario);
			
			email.setHost(getProperties().getProperty(Constants.MAIL_HOST));
			email.setPort(getProperties().getProperty(Constants.MAIL_PORT));
			
			String mittente = getDbMgr().cercaConfigValueCarattere(Constants.WEB_MAIL_IND_MITT);
			email.setMittente(mittente);
			System.out.println("parametro da DB: "+mittente);
			email.setOggetto("Registrazione al CIT:  conferma indirizzo e-mail");
			String emailBodyHtml = "CIT - Catasto degli Impianti Termici, indirizzo e-mail scritto correttamente; tutte le notifiche future saranno mandate a questo indirizzo.<br/>";
			emailBodyHtml += "Si ricorda di eseguire il salvataggio dell'informazione a video.<br/><br/>";
			emailBodyHtml+="N.B. Questo e' un messaggio inviato automaticamente. Si prega di non rispondere a questa email.";
			email.setHtml(emailBodyHtml);
			String emailBodyTxt = "CIT - Catasto degli Impianti Termici, indirizzo e-mail scritto correttamente; tutte le notifiche future saranno mandate a questo indirizzo.\n";
			emailBodyTxt += "Si ricorda di eseguire il salvataggio dell'informazione a video.\n\n";
			emailBodyTxt+="N.B. Questo e' un messaggio inviato automaticamente. Si prega di non rispondere a questa email.";
			email.setTesto(emailBodyTxt);
		
			sender.sendMail(email);
			System.out.println("mail inviata");
		} catch (Exception e) {
			log.error(e);
			throw new ServiceException(e, Messages.ERROR_INVIO_MAIL);
		}

	}
	
	protected Properties getProperties() {
		log.debug("[ServiziMgr::getProperties] BEGIN");
		InputStream is = getClass().getResourceAsStream(PROPERTIES_RESOURCE);
		if (is != null) {
			try {
				 Properties properties = new Properties();
	             properties.load(is);
	              
				return properties;
			} 
			catch (Exception e) {
				log.error("[ServiziMgr::getProperties] errore nella parsificazione della configurazione delle PROPERTIES:"+ e, e);
				throw new IllegalArgumentException("errore nella parsificazione della configurazione delle PROPERTIES");
			}
		} else{
			log.error("[ServiziMgr::getProperties] configurazione delle PROPERTIES non trovata");
		}
		throw new IllegalArgumentException("configurazione delle PROPERTIES non trovata");
	}

	public DbMgr getDbMgr() {
		return dbMgr;
	}

	public void setDbMgr(DbMgr dbMgr) {
		this.dbMgr = dbMgr;
	}
	
}