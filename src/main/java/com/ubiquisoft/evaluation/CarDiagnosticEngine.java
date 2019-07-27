package com.ubiquisoft.evaluation;

import com.ubiquisoft.evaluation.domain.Car;
import com.ubiquisoft.evaluation.domain.ConditionType;
import com.ubiquisoft.evaluation.domain.Part;
import com.ubiquisoft.evaluation.domain.PartType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CarDiagnosticEngine {

	public void executeDiagnostics(Car car) throws DiagnosticFailedException {
		/*
		 * Implement basic diagnostics and print results to console.
		 *
		 * The purpose of this method is to find any problems with a car's data or parts.
		 *
		 * Diagnostic Steps:
		 *      First   - Validate the 3 data fields are present, if one or more are
		 *                then print the missing fields to the console
		 *                in a similar manner to how the provided methods do.
		 *
		 *      Second  - Validate that no parts are missing using the 'getMissingPartsMap' method in the Car class,
		 *                if one or more are then run each missing part and its count through the provided missing part method.
		 *
		 *      Third   - Validate that all parts are in working condition, if any are not
		 *                then run each non-working part through the provided damaged part method.
		 *
		 *      Fourth  - If validation succeeds for the previous steps then print something to the console informing the user as such.
		 * A damaged part is one that has any condition other than NEW, GOOD, or WORN.
		 *
		 * Important:
		 *      If any validation fails, complete whatever step you are actively one and end diagnostics early.
		 *
		 * Treat the console as information being read by a user of this application. Attempts should be made to ensure
		 * console output is as least as informative as the provided methods.
		 */
		
		// first - data fields are present
		validateDataFieldsPresent(car);
		
		// second - no parts are missing
		validateNoPartsMissing(car);
		
		// third - all parts are in working condition
		validateWorkingCondition(car);
		
		// fourth - validation succeeded
		String msg = String.format("Validation succeeded for %s", car.toString());
		System.out.println(msg);
	}

	private void validateWorkingCondition(Car car) throws DiagnosticFailedException {
		boolean valid = true;
		
		List<Part> parts = car.getParts();
	
		int damagedCount = 0;
		for (Part part : parts) {
			if (!part.isInWorkingCondition()) {
				valid = false;
				
				printDamagedPart(part.getType(), part.getCondition());
				damagedCount++;
			}
		}
		
		if (!valid) {
			String msg = String.format("found %s damaged parts", damagedCount);
			throw new DiagnosticFailedException(msg);
		}
	}

	private void validateNoPartsMissing(Car car) throws DiagnosticFailedException {

		Map<PartType, Integer> missingParts = car.getMissingPartsMap();
		if (!missingParts.isEmpty()) {
			//found missing parts, invalid
			
			int missingCount = 0;

			for (Map.Entry<PartType, Integer> entry : missingParts.entrySet()) {
				printMissingPart(entry.getKey(), entry.getValue());
				missingCount+= entry.getValue();
			}
			
			String msg = String.format("found %s missing parts", missingCount);
			throw new DiagnosticFailedException(msg);
		}
	}

	private void validateDataFieldsPresent(Car car) throws DiagnosticFailedException {
		boolean valid = true;
		List<String> missingFields = new LinkedList<String>();
		
		if (isBlank(car.getYear())) {
			valid = false;
			missingFields.add("year");
		}
		
		if (isBlank(car.getMake())) {
			valid = false;
			missingFields.add("make");
		}
		
		if (isBlank(car.getModel())) {
			valid = false;
			missingFields.add("model");
		}
		
		if (!valid) {
			String fields = String.join(",", missingFields);
			String msg = String.format("missing fields: %s", fields);
			
			throw new DiagnosticFailedException(msg);
		}
	}

	//note could have used Apache StringUtils here instead
	private boolean isBlank(String value) {
		if(value==null || value.isEmpty()){
		    return true;
		} else return false;
	}
	
	private void printMissingPart(PartType partType, Integer count) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (count == null || count <= 0) throw new IllegalArgumentException("Count must be greater than 0");

		System.out.println(String.format("Missing Part(s) Detected: %s - Count: %s", partType, count));
	}

	private void printDamagedPart(PartType partType, ConditionType condition) {
		if (partType == null) throw new IllegalArgumentException("PartType must not be null");
		if (condition == null) throw new IllegalArgumentException("ConditionType must not be null");

		System.out.println(String.format("Damaged Part Detected: %s - Condition: %s", partType, condition));
	}

	public static void main(String[] args) throws JAXBException {

		// Load classpath resource
		InputStream xml = ClassLoader.getSystemResourceAsStream("SampleCar.xml");

		// Verify resource was loaded properly
		if (xml == null) {
			System.err.println("An error occurred attempting to load SampleCar.xml");

			System.exit(1);
		}

		// Build JAXBContext for converting XML into an Object
		JAXBContext context = JAXBContext.newInstance(Car.class, Part.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Car car = (Car) unmarshaller.unmarshal(xml);

		// Build new Diagnostics Engine and execute on deserialized car object.

		CarDiagnosticEngine diagnosticEngine = new CarDiagnosticEngine();

		try {
			diagnosticEngine.executeDiagnostics(car);
			
		} catch (DiagnosticFailedException e) {
			String msg = String.format("Validation failed: %s", e.getMessage());
			System.out.println(msg);
		}

	}

}
