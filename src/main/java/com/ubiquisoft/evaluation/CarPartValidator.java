package com.ubiquisoft.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

public class CarPartValidator {

	private static final Map<PartType, Integer> REQUIRED_PARTS = new HashMap<PartType, Integer>();

	static {
		REQUIRED_PARTS.put(PartType.ENGINE, 1);
		REQUIRED_PARTS.put(PartType.ELECTRICAL, 1);
		REQUIRED_PARTS.put(PartType.FUEL_FILTER, 1);
		REQUIRED_PARTS.put(PartType.OIL_FILTER, 1);

		REQUIRED_PARTS.put(PartType.TIRE, 4);
	}

	/**
	 * Returns map of the part types missing.
	 *
	 * Each car requires one of each of the following types:
	 *      ENGINE, ELECTRICAL, FUEL_FILTER, OIL_FILTER
	 * and four of the type: TIRE
	 *
	 * Example: a car only missing three of the four tires should return a map like this:
	 *
	 *      {
	 *          "TIRE": 3
	 *      }
	 *      
	 * @return missing parts
	 */
	public Map<PartType, Integer> getMissingParts(Car car) {
		Map<PartType, Integer> missingParts = new HashMap<PartType, Integer>();

		Map<PartType, Integer> partCount = getPartCount(car);

		//determine if actual part count matches requirements
		for (Map.Entry<PartType, Integer> entry : REQUIRED_PARTS.entrySet()) {
			PartType type = entry.getKey();
			Integer requiredCount = entry.getValue();
			
			//if requirements not met then return actual count
			Integer actualCount = partCount.get(type);
			
			if (actualCount == null) {
				actualCount = 0;
			}
			
			if (actualCount < requiredCount) {
				missingParts.put(type, requiredCount - actualCount);
			}
		}

		return missingParts;
	}

	private Map<PartType, Integer> getPartCount(Car car) {
		Map<PartType, Integer> partCount = new HashMap<PartType, Integer>();

		List<Part> parts = car.getParts();
		for (Part part : parts) {
			PartType type = part.getType();

			Integer count = partCount.get(type);
			if (count == null) {
				partCount.put(type, 1);
			} else {
				partCount.put(type, count + 1);
			}			
		}

		return partCount;
	}

}
