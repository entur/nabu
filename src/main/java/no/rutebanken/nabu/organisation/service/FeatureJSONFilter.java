package no.rutebanken.nabu.organisation.service;

import org.apache.commons.lang.ObjectUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FeatureJSONFilter {

	private InputStream sourceStream;

	private String correlationProperty;

	private String targetFilePath;
	private String comparatorProperty;


	private final Map<Object, SimpleFeature> map = new HashMap<>();

	public FeatureJSONFilter(InputStream sourceStream, String targetFilePath, String correlationProperty, String comparatorProperty) {
		this.sourceStream = sourceStream;
		this.targetFilePath = targetFilePath;
		this.correlationProperty = correlationProperty;
		this.comparatorProperty = comparatorProperty;
	}


	public void filter() {
		try {

			FeatureJSON fJson = new FeatureJSON();
			FeatureIterator<SimpleFeature> itr = fJson.streamFeatureCollection(sourceStream);
			while (itr.hasNext()) {
				SimpleFeature simpleFeature = itr.next();
				Object id = getProperty(simpleFeature, correlationProperty);
				SimpleFeature existing = map.get(id);

				if (existing == null || shouldNewReplaceExisting(simpleFeature, existing)) {
					map.put(id, simpleFeature);
				}
			}
			itr.close();

			DefaultFeatureCollection filteredCollection = new DefaultFeatureCollection();
			filteredCollection.addAll(map.values());

			fJson.writeFeatureCollection(filteredCollection, new FileOutputStream(targetFilePath));

		} catch (IOException ioE) {
			throw new RuntimeException("Filtering failed for featureJSON input stream:" + ioE.getMessage());
		}
	}


	protected boolean shouldNewReplaceExisting(SimpleFeature newF, SimpleFeature existingF) {
		Comparable existingCmpVal = getProperty(existingF, comparatorProperty);
		Comparable newCmpVal = getProperty(newF, comparatorProperty);

		return ObjectUtils.compare(newCmpVal, existingCmpVal) > 0;
	}


	protected <T> T getProperty(Feature feature, String propertyName) {
		Property property = feature.getProperty(propertyName);
		if (property == null) {
			return null;
		}
		return (T) property.getValue();
	}
}