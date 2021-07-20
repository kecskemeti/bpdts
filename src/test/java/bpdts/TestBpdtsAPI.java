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

import org.junit.Before;
import org.junit.Test;

import bpdts.gen.ApiClient;
import bpdts.gen.ApiException;
import bpdts.gen.model.User;
import bpdts.gen.model.UserList;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

/**
 * Evaluates the BpdtsAPI by mocking the restful service's behaviour
 * 
 * @author Gabor Kecskemeti
 *
 */
public class TestBpdtsAPI {
	InjectableClientAPI apiInt;
	ApiClient client;
	BpdtsAPI bpdtsApi;
	public static final String expectedPath = "https://localhost";
	public static final SphericalCoordinates cityLoc = new SphericalCoordinates(20, 30);

	public static final String testCityName = "TestCity";

	/**
	 * Initiates the two mocked objects and sets their basic behaviours
	 */
	@Before
	public void prepMock() {
		apiInt = mock(InjectableClientAPI.class);
		client = mock(ApiClient.class);
		when(apiInt.getApiClient()).thenReturn(client);
	}

	/**
	 * Prepares the BpdtsAPI object ({@link #bpdtsApi}) so it knows about the to be
	 * tested city's information and the accepted farthest distance that can be
	 * still considered near to the city.
	 */
	private void prepBPDTS() {
		bpdtsApi = new BpdtsAPI(apiInt, expectedPath);
		bpdtsApi.setCityToCheckAgainst(new City(testCityName, new SphericalCoordinates[] { cityLoc }));
		bpdtsApi.setMaxDistanceInMiles(10);
	}

	/**
	 * Checks if the BpdsAPI passes the connection URL to the swagger codegen
	 * generated HTTP client.
	 */
	@Test(timeout = 100)
	public void pathReceiver() {
		prepBPDTS();
		verify(client).setBasePath(expectedPath);
	}

	/**
	 * Checks if the users who live in a particular city are properly identified by
	 * the API.
	 * 
	 * @throws ApiException
	 */
	@Test(timeout = 100)
	public void citizenCheck() throws ApiException {
		// Outputs of the "service"
		var testCityUsers = new UserList();
		var a = new User();
		a.setId(1);
		a.setFirstName("Ted");
		var b = new User();
		b.setId(2);
		b.setFirstName("Det");
		testCityUsers.add(a);
		testCityUsers.add(b);

		// Custom mocking
		when(apiInt.getCityUsers(testCityName)).thenReturn(testCityUsers);
		when(apiInt.getUsers()).thenReturn(testCityUsers);

		// Behaviour evaluation
		var theApi = new BpdtsAPI(apiInt, "");
		theApi.setCityToCheckAgainst(
				new City(testCityName, new SphericalCoordinates[] { new SphericalCoordinates(20, 30) }));
		var ret = theApi.getUsersOfCity();
		var same = true;
		for (int i = 0; i < ret.size() && same; i++) {
			same &= testCityUsers.get(i) == ret.get(i);
		}
		assertTrue("Should return all testcity users", same);
		verify(apiInt).getCityUsers(testCityName);
	}

	/**
	 * Checks if the nearby users are properly identified by the API.
	 * 
	 * @throws ApiException
	 */
	@Test(timeout = 100)
	public void areaCheck() throws ApiException {
		// Outputs of the "service"
		var closeUserName = "CloseUser";
		var allUsers = new UserList();
		var a = new User();
		a.setFirstName(closeUserName);
		a.setId(1);
		a.setLatitude(new BigDecimal(cityLoc.latitude));
		a.setLongitude(new BigDecimal(cityLoc.longitude + 0.01));
		var b = new User();
		b.setFirstName("FarUser");
		b.setId(2);
		b.setLatitude(new BigDecimal(-30));
		b.setLongitude(new BigDecimal(-120));
		allUsers.add(a);
		allUsers.add(b);

		// Custom mocking
		when(apiInt.getUsers()).thenReturn(allUsers);

		// Behaviour checks
		prepBPDTS();

		var ret = bpdtsApi.getUsersNearCity();
		assertTrue("Should only return one user who is nearby",
				ret.size() == 1 && closeUserName.equals(ret.get(0).getFirstName()));
		verify(apiInt).getUsers();
	}

	/**
	 * Checks if the network errors are properly passed back to the user of the API.
	 * 
	 * @throws ApiException
	 */
	@Test(timeout = 100, expected = ApiException.class)
	public void networkErrorPropagate1() throws ApiException {
		when(apiInt.getUsers()).thenThrow(new ApiException());
		prepBPDTS();
		bpdtsApi.getUsersNearCity();
	}

	/**
	 * Checks if the network errors are properly passed back to the user of the API.
	 * 
	 * @throws ApiException
	 */
	@Test(timeout = 100, expected = ApiException.class)
	public void networkErrorPropagate2() throws ApiException {
		when(apiInt.getCityUsers(testCityName)).thenThrow(new ApiException());
		prepBPDTS();
		bpdtsApi.getUsersOfCity();
	}

	/**
	 * Checks if the network errors are properly passed back to the user of the API.
	 * 
	 * @throws ApiException
	 */
	@Test(timeout = 100, expected = NullPointerException.class)
	public void errorPropagateIfNotAPI() throws ApiException {
		prepBPDTS();
		bpdtsApi.getUsersOfCity();
	}
}
