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

import bpdts.gen.ApiClient;
import bpdts.gen.ApiException;
import bpdts.gen.client.DefaultApi;
import bpdts.gen.model.UserList;

/**
 * This class bridges the gap between the BpdtsAPI and the swagger generated
 * client to the service. It only offers the three basic functions the BpdtsAPI
 * will use.
 * 
 * @author Gabor Kecskemeti
 *
 */
public class DefaultClientAPI implements InjectableClientAPI {
	private final DefaultApi api = new DefaultApi();

	/**
	 * Allows querying the ApiClient which does the actual HTTP communication
	 * towards the RESTful service. This is helpful if the BpdtsAPI needs to
	 * reconfigure the connection between the client's host and the service.
	 * 
	 * @see InjectableClientAPI#getApiClient()
	 */
	@Override
	public ApiClient getApiClient() {
		return api.getApiClient();
	}

	/**
	 * Initiates a synchronous service call to get all users listed in the database.
	 * 
	 * @see InjectableClientAPI#getUsers()
	 */
	@Override
	public UserList getUsers() throws ApiException {
		return api.getUsers();
	}

	/**
	 * Initiates a synchronous service call to get all users listed to reside in a
	 * particular city.
	 * 
	 * @see InjectableClientAPI#getCityUsers(String)
	 */
	@Override
	public UserList getCityUsers(final String city) throws ApiException {
		return api.getCityUsers(city);
	}

}
