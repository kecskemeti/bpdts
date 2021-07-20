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

import java.math.BigDecimal;

import org.junit.Test;

import bpdts.gen.model.User;

/**
 * Tests the basic behaviour of spherical coordinates
 * 
 * @author Gabor Kecskemeti
 *
 */
public class TestSphericalCoords {
	public static final double doubleAcceptanceLimit = 0.0000001;;

	/**
	 * Asserts if the latitude and longitude values are not 1. This is a helper as
	 * 1-1 coords are used multiple times throughout the test code.
	 * 
	 * @param sc
	 */
	private void assertFor11(SphericalCoordinates sc) {
		assertEquals("Unexpected latitude value", 1, sc.latitude, doubleAcceptanceLimit);
		assertEquals("Unexpected longitude value", 1, sc.longitude, doubleAcceptanceLimit);
	}

	/**
	 * Generates a user located at 1,1.
	 * 
	 * @return
	 */
	private User getUser() {
		var u = new User();
		u.setLatitude(BigDecimal.ONE);
		u.setLongitude(BigDecimal.ONE);
		return u;
	}

	/**
	 * Checks the basic constructor's correctness
	 */
	@Test(timeout = 100)
	public void initViaDoubles() {
		assertFor11(new SphericalCoordinates(1, 1));
	}

	/**
	 * Checks if the user-based constructor also behaves the same way as if it was
	 * the basic, double-based, constructor
	 */
	@Test(timeout = 100)
	public void initViaUser() {
		assertFor11(new SphericalCoordinates(getUser()));
	}

	/**
	 * Ensures that non-realistic coordinates are not accepted.
	 */
	@Test(timeout = 100, expected = IllegalArgumentException.class)
	public void outOfRangeOverLat() {
		new SphericalCoordinates(91, 1);
	}

	/**
	 * Ensures that non-realistic coordinates are not accepted.
	 */
	@Test(timeout = 100, expected = IllegalArgumentException.class)
	public void outOfRangeUnderLat() {
		new SphericalCoordinates(-91, 1);
	}

	/**
	 * Ensures that non-realistic coordinates are not accepted.
	 */
	@Test(timeout = 100, expected = IllegalArgumentException.class)
	public void outOfRangeOverLon() {
		new SphericalCoordinates(0, 181);
	}

	/**
	 * Ensures that non-realistic coordinates are not accepted.
	 */
	@Test(timeout = 100, expected = IllegalArgumentException.class)
	public void outOfRangeUnderLon() {
		new SphericalCoordinates(0, -181);
	}

	/**
	 * Checks if the two constructors of lead to the storage of the same data.
	 */
	@Test(timeout = 100)
	public void initEquivalence() {
		var doubleInited = new SphericalCoordinates(1, 1);
		var userInited = new SphericalCoordinates(getUser());
		assertEquals("Latitude values must match between the two different constructors", doubleInited.latitude,
				userInited.latitude, doubleAcceptanceLimit);
		assertEquals("Longitude values must match between the two different constructors", doubleInited.longitude,
				userInited.longitude, doubleAcceptanceLimit);
	}
}
