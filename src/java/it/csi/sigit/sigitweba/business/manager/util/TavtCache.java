/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright Regione Piemonte - 2021
 *******************************************************************************/
package it.csi.sigit.sigitweba.business.manager.util;

import java.util.HashMap;
import java.util.Hashtable;

import it.csi.csi.pfh.FHResult;
import it.csi.csi.pfh.FunctHandler;
import it.csi.csi.util.Param;
import it.csi.csi.wrapper.CSIException;



public class TavtCache implements FunctHandler {

	static HashMap<SigitWebCallKey, SigitWebCallValue> cache = new HashMap<SigitWebCallKey, SigitWebCallValue>();

	static final long TTL = 8 * 60 * 60 * 1000; // TTL = 1h

	public FHResult doOnInvokeSynch(String methodName, Param[] params,
			FHResult prevres, Hashtable bag, boolean inPreChain)
	throws CSIException {
		if (mustCache(methodName)) {

			SigitWebCallKey call = new SigitWebCallKey(methodName, params);
			if (inPreChain) {
				SigitWebCallValue cachedResult = cache.get(call);
				if (cachedResult != null) {
					if ((System.currentTimeMillis() - cachedResult.birthTime) < TTL) {
						prevres.setResult(cachedResult.result);
						prevres.setSkipInvoke(true);
					} else
						cache.remove(call);
				}
				// else continue and call PA...
			}

			else { // post chain
				if (prevres.getException() == null) {
					// metti in cache il risultato
					SigitWebCallValue newVal = new SigitWebCallValue(
							System.currentTimeMillis(), prevres.getResult());
					cache.put(call, newVal);
				}
			}
		}
		return prevres;
	}

	private boolean mustCache(String methodName) {
		if ("selComuniByProvincia".equals(methodName)
			|| "selProvAttuali".equals(methodName)	
			|| "selProvinceByRegione".equals(methodName)
				)
			return true;
		else
			return false;

	}

	class SigitWebCallValue {
		public long birthTime;
		public Param result;

		public SigitWebCallValue(long bt, Param p) {
			birthTime = bt;
			result = p;
		}
	}

	class SigitWebCallKey {
		public String paramsKey;

		public SigitWebCallKey(String methodName, Param[] params) {
			paramsKey += methodName;
			if (params != null && params.length > 0) {
				paramsKey += ":";
				String id = (String) (params[0].getValue());
				paramsKey += id;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SigitWebCallKey) {
				SigitWebCallKey other = (SigitWebCallKey) obj;
				return paramsKey.equals(other.paramsKey);
			} else
				return false;
		}

		@Override
		public int hashCode() {

			return paramsKey.hashCode();
		}

		@Override
		public String toString() {
			return paramsKey;
		}

	}

}