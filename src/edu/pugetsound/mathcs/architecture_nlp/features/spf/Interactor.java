/*******************************************************************************
 * Copyright (C) 2011 - 2015 Yoav Artzi, All rights reserved.
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package edu.pugetsound.mathcs.architecture_nlp.features.spf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.nlp.spf.base.hashvector.IHashVector;
import edu.cornell.cs.nlp.spf.ccg.lexicon.LexicalEntry;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.collection.IDataCollection;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.sentence.SentenceCollection;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment.Parameters;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.cornell.cs.nlp.spf.parser.IOutputLogger;
import edu.cornell.cs.nlp.spf.parser.IParser;
import edu.cornell.cs.nlp.spf.parser.IParserOutput;
import edu.cornell.cs.nlp.spf.parser.ccg.IWeightedParseStep;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYDerivation;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IDataItemModel;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;
import edu.cornell.cs.nlp.spf.test.stats.ITestingStatistics;
import edu.cornell.cs.nlp.utils.collections.ListUtils;
import edu.cornell.cs.nlp.utils.filter.IFilter;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;

/**
 * Module to allow the interaction of the Model with both singular sentences and with a set of sentences.
 * When given a set of sentences, resulting parses are written out to file
 *
 * @author Yoav Artzi & Jared Polonitza
 * @param <SAMPLE>
 *            Inference data item.
 * @param <MR>
 *            Meaning representation.
 * @param <DI>
 *            Labeled data item.
 */

//THINGS TO ADD: list of sentences translated so far
public class Interactor<SAMPLE extends IDataItem<?>, MR, DI extends IDataItem<SAMPLE>>
		implements IInteract<SAMPLE, MR, DI> {

	public static final ILogger LOG = LoggerFactory
			.create(Interactor.class.getName());

	private IParser<SAMPLE, MR> parser;
	
	private IModelImmutable<SAMPLE,MR> model;

	private IFilter<SAMPLE> skipParsingFilter;
	
	private final IDataCollection<? extends DI> testData;

	private Interactor(IFilter<SAMPLE> skipParsingFilter, IParser<SAMPLE, MR> parser,
						IModelImmutable<SAMPLE, MR> model, IDataCollection<? extends DI> testData) {
		this.skipParsingFilter = skipParsingFilter;
		this.parser = parser;
		this.model = model;
		this.testData = testData;
	}
	
	public IDerivation<MR> interact(final DI dataItem) {
		
		final IDataItemModel<MR> dataItemModel = model
				.createDataItemModel(dataItem.getSample());

		// Try a simple model parse
		final IParserOutput<MR> modelParserOutput = parser
				.parse(dataItem.getSample(), dataItemModel);
		LOG.info("Test parsing time %.2fsec",
				modelParserOutput.getParsingTime() / 1000.0);

		final List<? extends IDerivation<MR>> bestModelParses = modelParserOutput
				.getBestDerivations();
		if (bestModelParses.size() == 1) {
			// Case we have a single parse
			processSingleBestParse(dataItem.getSample(), dataItemModel, modelParserOutput,
					bestModelParses.get(0), false);
			return bestModelParses.get(0);
		} else if (bestModelParses.size() > 1) {
			// Multiple top parses

			// There are more than one equally high scoring
			// logical forms. If this is the case, we abstain
			// from returning a result.
			return bestModelParses.get(0);
		} else {
			// No parses
			LOG.info("no parses");
			System.out.println("Ive got nothing");
			// Potentially re-parse with word skipping
			if (skipParsingFilter.test(dataItem.getSample())) {
				final IParserOutput<MR> parserOutputWithSkipping = parser
						.parse(dataItem.getSample(), dataItemModel, true);
				LOG.info("EMPTY Parsing time %fsec",
						parserOutputWithSkipping.getParsingTime() / 1000.0);
				final List<? extends IDerivation<MR>> bestEmptiesParses = parserOutputWithSkipping
						.getBestDerivations();
				if (bestEmptiesParses.size() == 1) {
					processSingleBestParse(dataItem.getSample(), dataItemModel,
							parserOutputWithSkipping, bestEmptiesParses.get(0),
							true);
				} else if (bestEmptiesParses.isEmpty()) {
					// No parses
					LOG.info("no parses");

				} else {
					// too many parses or no parses
				}
			} else {
				LOG.info("Skipping word-skip parsing due to length");
			}
			return null;
		}
//		System.out.println(bestModelParses.getClass());
		
	}
	
	@Override
	public void conversation() {
		String p = System.getProperty("user.dir");
		File f = new File(p + "/resources");
		String path = f.getAbsolutePath();
		ArrayList<IDerivation<MR>> parses = new ArrayList<>();
		System.out.println(testData.size());
		for (DI d : testData) {
			parses.add(interact(d));
		}
		String str = "";
		for (IDerivation<MR> i : parses) {
			if (i != null) {
				str += i.toString() + "\n";
			}
			else {
				str += "null \n";
			}
		}
		File name = new File(path + "/SpfResources/experiments/data/results");
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(name));
	        writer.write(str);
	        writer.close();
		}
		catch (IOException e) {
			System.out.print(e);
		}
	}

	//Returns a successful parse
	private MR processSingleBestParse(final SAMPLE sample,
			IDataItemModel<MR> dataItemModel,
			final IParserOutput<MR> modelParserOutput,
			final IDerivation<MR> parse, boolean withWordSkipping) {
		final MR label = parse.getSemantics();
		return label;
	}

	public static class Builder<SAMPLE extends IDataItem<?>, MR, DI extends IDataItem<SAMPLE>> {
		private final IModelImmutable<SAMPLE,MR> model;
		
		private final IParser<SAMPLE, MR> parser;
		
		private final IDataCollection<? extends DI> testData;
		

		/** Filters which data items are valid for parsing with word skipping */
		private IFilter<SAMPLE> skipParsingFilter = e -> true;		
		
		public Builder(IParser<SAMPLE, MR> parser,IModelImmutable<SAMPLE, MR> model) {
			this.parser = parser;
			this.model = model;
			testData = null;
		}
		
		public Builder(IParser<SAMPLE, MR> parser,IModelImmutable<SAMPLE, MR> model, IDataCollection<? extends DI> testData) {
			this.parser = parser;
			this.model = model;
			this.testData = testData;
		}

		public Interactor<SAMPLE, MR, DI> build() {
			return new Interactor<SAMPLE, MR, DI>(skipParsingFilter, parser, model,testData);
		}

		public Builder<SAMPLE, MR, DI> setSkipParsingFilter(
				IFilter<SAMPLE> skipParsingFilter) {
			this.skipParsingFilter = skipParsingFilter;
			return this;
		}
	}
}

