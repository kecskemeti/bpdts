/*
 *  ========================================================================
 *  BPDTS
 *  ========================================================================
 *  
 *  This file is part of BPDTS.
 *  
 *  BPDTS is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *  
 *  BPDTS is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with BPDTS.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2021, Gabor Kecskemeti (kecskemeti@iit.uni-miskolc.hu)
 */

package bpdts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import bpdts.gen.model.User;
import static bpdts.Belonging.*;

/**
 * 
 * Evaluates the city class and its main methods
 * 
 * @author Gabor Kecskemeti
 *
 */
public class TestCity {
	private static final double[] coords = { 0.2, 0.2, 0.3, 0.3, 0.25, 0.25 };
	private static final String cityName = "Fictional";
	private static final String asJson;
	private static final String jsonPrefix = "CITYCHECKER", jsonPostFix = "json";
	private City testCity;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\t\"name\" : \"");
		sb.append(cityName);
		sb.append("\",\n\t\"perimeter\" : [");
		Arrays.stream(coords).forEach(d -> {
			sb.append(" \"");
			sb.append(d);
			sb.append("\",");
		});
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" ]\n}");
		asJson = sb.toString();
	}

	/**
	 * Prepares a city with very basic details suitable for most evaluations
	 */
	@Before
	public void init() {
		var scs = new SphericalCoordinates[coords.length / 2];
		for (int i = 0; i < coords.length; i += 2) {
			scs[i / 2] = new SphericalCoordinates(coords[i], coords[i + 1]);
		}
		testCity = new City(cityName, scs);
	}

	/**
	 * Helper to allow comparison of floating point values with a set tolerance
	 * 
	 * @param a
	 * @param b
	 * @return true if a and b are close enough so we can call them equal
	 */
	public static boolean checkDoubleEq(double a, double b) {
		return Math.abs(a - b) < TestSphericalCoords.doubleAcceptanceLimit;
	}

	/**
	 * This method fosters the testing of the file reading capabilities of the City
	 * class. The main purpose here is to generate a json file based on a single
	 * input string. The json file is written as a temporary file and erased right
	 * before terminating the jvm.
	 * 
	 * @param data To write to the json file.
	 * @return The path of the json file written.
	 * @throws IOException If there were write errors.
	 */
	public static Path createTempFileWithData(String data) throws IOException {
		var file = File.createTempFile(jsonPrefix, jsonPostFix);
		file.deleteOnExit();
		Files.writeString(file.toPath(), data);
		return file.toPath();
	}

	/**
	 * Checks if we specify the same data for the two constructors, we get the same
	 * data in the city class's data members as well.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 500)
	public void constructorEquivalence() throws IOException {
		var cityFromJson = new City(createTempFileWithData(asJson));
		var equal = testCity.name.equals(cityFromJson.name);
		equal &= testCity.perimeter.size() == cityFromJson.perimeter.size();
		for (int i = 0; i < testCity.perimeter.size() && equal; i++) {
			var fromCode = testCity.perimeter.get(i);
			var fromJson = cityFromJson.perimeter.get(i);
			equal &= checkDoubleEq(fromCode.x, fromJson.x) && checkDoubleEq(fromCode.y, fromJson.y)
					&& checkDoubleEq(fromCode.z, fromJson.z);
		}
		assertTrue("Data loaded from file should not differ from data loaded from code", equal);
	}

	/**
	 * Checks the reaction of the city class to incorrect json input
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 500, expected = IOException.class)
	public void erraticInput() throws IOException {
		new City(createTempFileWithData("{\n\t\"name\" : \"a\"\n}"));
	}

	/**
	 * Checks the reaction of the city class to incorrect json input
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 500, expected = IOException.class)
	public void erraticPerimeterLen() throws IOException {
		new City(createTempFileWithData("{\n\t\"name\" : \"a\",\n\t\"perimeter\": [ \"3\" ]\n }"));
	}

	/**
	 * Checks the reaction of the city class to incorrect json input
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 500, expected = IOException.class)
	public void erraticLatLon() throws IOException {
		new City(createTempFileWithData("{\n\t\"name\" : \"a\",\n\t\"perimeter\": [ \"3\", \"foo\" ]\n }"));
	}

	/**
	 * Checks if the distance is really measured from the point of the perimeter
	 * closest to the user
	 */
	@Test(timeout = 100)
	public void distanceChecker() {
		var locadjust = 0.000001;
		var test = new User();
		test.setLatitude(new BigDecimal(coords[0]));
		test.setLongitude(new BigDecimal(coords[1] + locadjust));
		var uDist = testCity.distanceOfUser(test);
		var coordDist = EarthCentredCoordinates.distance(
				new EarthCentredCoordinates(new SphericalCoordinates(coords[0], coords[1])),
				new EarthCentredCoordinates(new SphericalCoordinates(coords[0], coords[1] + locadjust)));
		assertEquals(
				"Distance of user from city should be the same as the distance of user to the closest point on the perimeter",
				coordDist, uDist, TestSphericalCoords.doubleAcceptanceLimit);
	}

	/**
	 * Checks if we can get all return values about queries on city citizenship.
	 */
	@Test(timeout = 100)
	public void belongingCheck() {
		var userWithCorrectCity = new User();
		userWithCorrectCity.setCity(cityName);
		var userWithIncorrectCity = new User();
		userWithIncorrectCity.setCity("NotReally" + cityName);
		var userWithNoCity = new User();
		assertEquals("User with the same city should be correctly identified as citizen", CITIZEN,
				testCity.userBelongsToCity(userWithCorrectCity));
		assertEquals("User with a different city should be correctly identified as alien", ALIEN,
				testCity.userBelongsToCity(userWithIncorrectCity));
		assertEquals("User with uninitialised city should be not classed as alien or citizen", UNKNOWN,
				testCity.userBelongsToCity(userWithNoCity));

	}

}
