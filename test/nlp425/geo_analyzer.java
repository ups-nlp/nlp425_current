/**
 * 
 */
package nlp425;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Test;

import edu.pugetsound.mathcs.nlp.lang.Utterance;
import edu.pugetsound.nlp.geoanalyzer.GeoAnalyzer;

/**
 * @author brian
 *
 */
public class geo_analyzer {

	@Test
	public void EnsureAnalyzeReturnsUtterance() {
		assert(GeoAnalyzer.geoAnalyze("This is a sentance") instanceof Utterance);
	}
}
