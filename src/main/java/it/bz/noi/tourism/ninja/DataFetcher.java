package it.bz.noi.tourism.ninja;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.noi.tourism.ninja.utils.querybuilder.QueryBuilder;
import it.bz.noi.tourism.ninja.utils.queryexecutor.ColumnMapRowMapper;
import it.bz.noi.tourism.ninja.utils.queryexecutor.QueryExecutor;

@Component
public class DataFetcher {

	private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

	private QueryBuilder query;
	private long limit;
	private long offset;
	private boolean ignoreNull;
	private String select;
	private String where;
	private boolean distinct;

	public List<Map<String, Object>> fetchActivities() {
		log.debug("FETCHING ACTIVITIES");

//		SELECT data ->> 'Id', data ->'GpsInfo', data ->>'Type', data ->'Detail'->'de'
//		FROM public.smgpoisopen;

		long nanoTime = System.nanoTime();
		query = QueryBuilder
				.init(select == null ? "*" : select, where, "smgpoisopen")
				.addSql("select")
				.addSqlIf("distinct", distinct)
//				.addSql("id")
				.expandSelect()
				.addSql("from smgpoisopen s")
				.addSql("where true")
				.expandWhere()
				.addLimit(limit)
				.addOffset(offset);

		log.debug(query.getSql());

		log.debug("build query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.debug(queryResult.toString());

		log.debug("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		return queryResult;
	}

	public static String serializeJSON(Object whatever) {
		long nanoTime = System.nanoTime();
		String serialize = JsonStream.serialize(whatever);
		log.debug("serialize json: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		return serialize;
	}

	public QueryBuilder getQuery() {
		return query;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}
}
