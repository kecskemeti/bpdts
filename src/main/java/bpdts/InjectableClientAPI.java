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
import bpdts.gen.model.UserList;

/**
 * This interface fosters the replacement of the backend API behind the
 * BpdtsAPI. Thus if we need to contact the service differently or with a
 * different client this interface needs to be implemented and passed on to the
 * BpdtsAPI.
 * 
 * @author Gabor Kecskemeti
 *
 */
public interface InjectableClientAPI {
	/**
	 * Allows querying the ApiClient which does the actual HTTP communication
	 * towards the RESTful service. This is helpful if the BpdtsAPI needs to
	 * reconfigure the connection between the client's host and the service.
	 */
	ApiClient getApiClient();

	/**
	 * Initiates a synchronous service call to get all users listed in the database.
	 * 
	 * @return All users in the db.
	 * @throws ApiException if there was a network connectivity issue.
	 */
	UserList getUsers() throws ApiException;

	/**
	 * Initiates a synchronous service call to get all users listed to reside in a
	 * particular city.
	 * 
	 * @param city The name of the city for which we are looking for its citizens.
	 * @return Citizens of a particular city.
	 * @throws ApiException if there was a network connectivity issue.
	 */
	UserList getCityUsers(String city) throws ApiException;
}
