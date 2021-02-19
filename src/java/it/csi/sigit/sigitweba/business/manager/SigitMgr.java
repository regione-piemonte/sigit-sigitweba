/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright Regione Piemonte - 2021
 *******************************************************************************/
package it.csi.sigit.sigitweba.business.manager;


import it.csi.sigit.sigitweba.business.dao.sigitweba.dao.SigitTPersonaFisicaDao;
import it.csi.sigit.sigitweba.business.dao.sigitweba.dto.SigitTPersonaFisicaDto;
import it.csi.sigit.sigitweba.business.dao.sigitweba.dto.SigitTPersonaFisicaPk;
import it.csi.sigit.sigitweba.business.dao.sigitweba.exceptions.SigitTPersonaFisicaDaoException;
import it.csi.sigit.sigitweba.dto.accreditamento_tns.PersonaFisica;
import it.csi.sigit.sigitweba.util.Constants;
import it.csi.sigit.sigitweba.util.ConvertUtil;
import it.csi.sigit.sigitweba.util.MapDto;
import it.csi.sigit.sigitweba.util.Messages;
import it.csi.tavt.dto.Comune;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manager generale del sigit, nel caso in cui bisogna chiamare vari servizi o servizi e DB
 * 
 */
public class SigitMgr {

	protected static Logger log = Logger.getLogger(Constants.APPLICATION_CODE + ".business.manager");

}