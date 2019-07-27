package com.ubiquisoft.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.PartType;

public class TestCarPartValidator {

	@Test
	public void success() throws JAXBException {
		Car car = CarLoader.load("SampleCar.xml");
		
		CarPartValidator validator = new CarPartValidator();
		Map<PartType, Integer> missingParts = validator.getMissingParts(car);

		assertTrue(missingParts.isEmpty());
	}
	
	@Test
	public void noEngine() throws JAXBException {
		Car car = CarLoader.load("missingParts" + File.separator + "NoEngine.xml");
		
		CarPartValidator validator = new CarPartValidator();
		Map<PartType, Integer> missingParts = validator.getMissingParts(car);

		assertFalse(missingParts.isEmpty());
		assertEquals(1, missingParts.size());
		
		Integer count = missingParts.get(PartType.ENGINE);
		assertEquals(1, count.intValue());
	}

	@Test
	public void twoTiresNoFuelFilter() throws JAXBException {
		Car car = CarLoader.load("missingParts" + File.separator + "TwoTiresNoFuelFilter.xml");
		
		CarPartValidator validator = new CarPartValidator();
		Map<PartType, Integer> missingParts = validator.getMissingParts(car);

		assertFalse(missingParts.isEmpty());
		
		//two types of parts missing
		assertEquals(2, missingParts.size());
		
		assertEquals(1, missingParts.get(PartType.FUEL_FILTER).intValue());
		assertEquals(2, missingParts.get(PartType.TIRE).intValue());
	}
	
}
