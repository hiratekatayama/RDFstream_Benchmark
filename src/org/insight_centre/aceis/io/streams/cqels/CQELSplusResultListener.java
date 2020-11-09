package org.insight_centre.aceis.io.streams.cqels;

import com.hp.hpl.jena.sparql.core.Var;
import cqelsplus.execplan.data.IMapping;
import cqelsplus.engine.ContinousListener;
import org.insight_centre.citybench.main.CityBench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CQELSplusResultListener implements ContinousListener {
	private String uri;
	private static final Logger logger = LoggerFactory.getLogger(CQELSplusResultListener.class);
	public static Set<String> capturedObIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	public static Set<String> capturedResults = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	public CQELSplusResultListener(String string) {
		setUri(string);
	}

	@Override
	public void update(IMapping mapping) {
		String result = "";
		try {
			Map<String, Long> latencies = new HashMap<String, Long>();
			// int cnt = 0;
			for (Var var : mapping.getVars()) {
				String varName = var.getName();
				String varStr = CityBench.cqelsplusContext.engine().decode(mapping.getValue(var)).toString();
				if (varName.contains("obId")) {
					if (!capturedObIds.contains(varStr)) {
						capturedObIds.add(varStr);
						long initTime = CityBench.obMap.get(varStr).getSysTimestamp().getTime();
						latencies.put(varStr, (System.currentTimeMillis() - initTime));
					}
				}
				result += " " + varStr;

			}
			// logger.info("CQELS result arrived: " + result);
			if (!capturedResults.contains(result)) {
				capturedResults.add(result);
				// uncomment for testing the completeness, i.e., showing how many observations are captured
				// logger.info("CQELS result arrived " + capturedResults.size() + ", obs size: " + capturedObIds.size()
				// + ", result: " + result);
				CityBench.pm.addResults(getUri(), latencies, 1);
			} else {
				logger.debug("CQELS result discarded: " + result);
			}

		} catch (Exception e) {
			logger.error("CQELS decoding error: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
