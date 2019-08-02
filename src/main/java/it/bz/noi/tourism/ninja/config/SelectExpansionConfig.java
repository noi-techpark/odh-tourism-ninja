package it.bz.noi.tourism.ninja.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.noi.tourism.ninja.utils.jsonserializer.JsonIterPostgresSupport;
import it.bz.noi.tourism.ninja.utils.jsonserializer.JsonIterSqlTimestampSupport;
import it.bz.noi.tourism.ninja.utils.querybuilder.QueryBuilder;
import it.bz.noi.tourism.ninja.utils.querybuilder.SelectExpansion;
import it.bz.noi.tourism.ninja.utils.queryexecutor.ColumnMapRowMapper;
import it.bz.noi.tourism.ninja.utils.queryexecutor.QueryExecutor;


@Component
public class SelectExpansionConfig implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private boolean alreadySetup = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		if (alreadySetup) {
            return;
        }

		boolean ignoreNull = true;
		SelectExpansion se = new SelectExpansion();

		se.addColumn("smgpoisopen", "id", "s.data ->> 'Id'");
		se.addColumn("smgpoisopen", "gpsinfo", "s.data ->'GpsInfo'");
		se.addColumn("smgpoisopen", "type", "s.data ->>'Type'");
		se.addColumn("smgpoisopen", "detail", "s.data ->'Detail'->'de'");

		se.addOperator("VALUE", "eq", "= %s");
		se.addOperator("VALUE", "neq", "<> %s");
		se.addOperator("NULL", "eq", "is %s");
		se.addOperator("NULL", "neq", "is not %s");
		se.addOperator("VALUE", "lt", "< %s");
		se.addOperator("VALUE", "gt", "> %s");
		se.addOperator("VALUE", "lteq", "=< %s");
		se.addOperator("VALUE", "gteq", ">= %s");
		se.addOperator("VALUE", "re", "~ %s");
		se.addOperator("VALUE", "ire", "~* %s");
		se.addOperator("VALUE", "nre", "!~ %s");
		se.addOperator("VALUE", "nire", "!~* %s");
		se.addOperator("LIST", "in", "in (%s)", t -> {
			return !(t.getChildCount() == 1 && t.getChild("VALUE").getValue() == null);
		});
		se.addOperator("LIST", "bbi", "&& ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});
		se.addOperator("LIST", "bbc", "@ ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(se);
		QueryExecutor.setup(jdbcTemplate);

		// The API should have a flag to remove null values (what should be default? <-- true)
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		JsonStream.setIndentionStep(4);
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();
	}

}

