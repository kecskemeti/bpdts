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
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * This class focuses on the evaluation of distances between two points.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class TestDistance {
	EarthCentredCoordinates first, second;

	@Before
	public void init() {
		first = new EarthCentredCoordinates(new SphericalCoordinates(30, 30));
		second = new EarthCentredCoordinates(new SphericalCoordinates(40, 40));
	}

	/**
	 * The method tests if the distance calculation is independent from the ordering
	 * of the coordinates passed in to the distance calculation method.
	 */
	@Test(timeout = 100)
	public void distanceCommutativity() {
		var res1 = EarthCentredCoordinates.distance(first, second);
		var res2 = EarthCentredCoordinates.distance(second, first);
		assertEquals("The distance calculation should not depend on the order of the specified coords", res1, res2,
				TestSphericalCoords.doubleAcceptanceLimit);
	}

	/**
	 * This method checks that the distance should not be an Eucledian one as that
	 * is non-realistic (could go through the earth's surface after all..)
	 */
	@Test(timeout = 100)
	public void nonEucledianDistance() {
		var res = EarthCentredCoordinates.distance(first, second);
		var eucledian = Math.sqrt(
				Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2) + Math.pow(first.z - second.z, 2));
		assertNotEquals(
				"The distance calculation should not be based on Eucledian distance as we would cut through Earth's surface",
				eucledian, res, TestSphericalCoords.doubleAcceptanceLimit);
	}

	/**
	 * Checks if we get the distance between opposite points of Earth then we get
	 * half the circumference of earth.
	 */
	@Test(timeout = 100)
	public void halfRound() {
		var halfCircumference = EarthCentredCoordinates.earthRadiusMiles * Math.PI;
		first = new EarthCentredCoordinates(new SphericalCoordinates(0, 0));
		second = new EarthCentredCoordinates(new SphericalCoordinates(0, 180));
		var result = EarthCentredCoordinates.distance(first, second);
		assertEquals("The distance between opposite sides of the equator should be half the circumference of the Earth",
				halfCircumference, result, TestSphericalCoords.doubleAcceptanceLimit);
	}

	/**
	 * Ensures that we can get distance between points exactly at the same location
	 * on the surface of Earth.
	 */
	@Test(timeout = 100)
	public void sameLocation() {
		var result = EarthCentredCoordinates.distance(first, first);
		assertEquals("The distance between the same points should be zero", 0, result,
				TestSphericalCoords.doubleAcceptanceLimit);
	}

	/**
	 * Checks special locations on earth where we only have one Earth centred
	 * coordinate as non-zero.
	 */
	@Test(timeout = 100)
	public void singleCoordShouldBeRadius() {
		var result = new EarthCentredCoordinates(new SphericalCoordinates(0, 0)).x;
		assertEquals("The x coord on (lon 0, lat 0) should be exactly the radius of earth",
				EarthCentredCoordinates.earthRadiusMiles, result, TestSphericalCoords.doubleAcceptanceLimit);
		result = new EarthCentredCoordinates(new SphericalCoordinates(0, 90)).y;
		assertEquals("The y coord on (lon 90, lat 0) should be exactly the radius of earth",
				EarthCentredCoordinates.earthRadiusMiles, result, TestSphericalCoords.doubleAcceptanceLimit);
		result = new EarthCentredCoordinates(new SphericalCoordinates(90, 0)).z;
		assertEquals("The z coord on (lon 0, lat 90) should be exactly the radius of earth",
				EarthCentredCoordinates.earthRadiusMiles, result, TestSphericalCoords.doubleAcceptanceLimit);
	}

}
