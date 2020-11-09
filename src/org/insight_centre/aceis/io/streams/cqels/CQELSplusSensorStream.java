package org.insight_centre.aceis.io.streams.cqels;

import com.hp.hpl.jena.rdf.model.Statement;
import cqelsplus.engine.ExecContext;
import cqelsplus.engine.RDFStream;
//import org.deri.cqels.engine.RDFStream;
import org.insight_centre.aceis.observations.SensorObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.insight_centre.aceis.engine.ACEISEngine;

public abstract class CQELSplusSensorStream extends RDFStream implements Runnable {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public CQELSplusSensorStream(ExecContext context, String uri) {
		super(context, uri);
	}

	protected double rate = 1.0;
	// private int sleep = 1000;
	protected int sleep = 1000;
	protected boolean stop = false;
	protected SensorObservation currentObservation;
	protected List<String> requestedProperties = new ArrayList<String>();

	public List<String> getRequestedProperties() {
		return requestedProperties;
	}

	public void setRequestedProperties(List<String> requestedProperties) {
		this.requestedProperties = requestedProperties;
	}

	public void setRate(Double rate) {
		this.rate = rate;
		if (this.rate != 1.0)
			logger.info("Streamming acceleration rate set to: " + rate);
	}

	public double getRate() {
		return rate;
	}

	public void setFreq(Double freq) {
		sleep = (int) (sleep / freq);
		if (this.rate == 1.0)
			logger.info("Streamming interval set to: " + sleep + " ms");
	}

	public void stop() {
		if (!stop) {
			stop = true;
			logger.info("Stopping stream: " + this.getURI());
		}
		// ACEISEngine.getSubscriptionManager().getStreamMap().remove(this.getURI());
		// SubscriptionManager.
	}

	protected abstract List<Statement> getStatements(SensorObservation so) throws NumberFormatException, IOException;

	protected abstract SensorObservation createObservation(Object data);

	public SensorObservation getCurrentObservation() {
		return this.currentObservation;
	}
}
