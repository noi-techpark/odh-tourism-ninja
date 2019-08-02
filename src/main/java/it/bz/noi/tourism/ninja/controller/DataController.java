/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.noi.tourism.ninja.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import it.bz.noi.tourism.ninja.DataFetcher;

/**
 * @author Peter Moser
 */
@RestController
@RequestMapping(value = "/api")
@Api(value = "Data", produces = "application/json")
public class DataController {

	private static final String DEFAULT_LIMIT = "200";
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_SHOWNULL = "false";
	private static final String DEFAULT_DISTINCT = "true";

	@Autowired
	DataFetcher dataFetcher;

	@GetMapping(value = "/pois", produces = "application/json")
	public @ResponseBody String requestActivities(@RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
											    @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
											    @RequestParam(value="select", required=false) String select,
											    @RequestParam(value="where", required=false) String where,
											    @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull,
												@RequestParam(value="distinct", required=false, defaultValue=DEFAULT_DISTINCT) Boolean distinct) {

		dataFetcher.setIgnoreNull(!showNull);
		dataFetcher.setLimit(limit);
		dataFetcher.setOffset(offset);
		dataFetcher.setWhere(where);
		dataFetcher.setSelect(select);
		dataFetcher.setDistinct(distinct);

		List<Map<String, Object>> queryResult = dataFetcher.fetchActivities();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("offset", offset);
		result.put("limit", limit);
		result.put("data", queryResult);

		return DataFetcher.serializeJSON(result);
	}

}
