package com.ecpay.esafebox.repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.utils.Constants;
import com.google.gson.Gson;

@Repository
public class BoxDao {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
    DataSource dataSource;
	
	private static final String PROC_GENERATE_BOX = "generate_box";

	public Map<String, Object> createBoxes(Long logId,Map<String, Object> params) throws SQLException, ESafeboxException {
		Gson gson = new Gson();
		JSONObject jsonParam = new JSONObject(params);
		logger.info("[" + logId + "][BoxDao.createBoxes] execute: " + PROC_GENERATE_BOX + " with params: " + gson.toJson(jsonParam));
		Map<String, Object> out = new HashMap<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName(PROC_GENERATE_BOX)
					.declareParameters(
							new SqlOutParameter("o_manu_id", Types.NUMERIC),
		                    new SqlOutParameter("err_code", Types.VARCHAR),
		                    new SqlOutParameter("err_msg", Types.VARCHAR));
			out = simpleJdbcCall.execute (params);
		} catch (Exception e) {
            logger.error("[" + logId + "] [BoxDao.createBoxes] Execption when executing " + PROC_GENERATE_BOX + ":" + e.getMessage(), e);
            out.put("err_code", e.hashCode());
            out.put("err_msg", e.getMessage());
        } finally {
        	logger.info("[" + logId + "][Duration: " + (System.currentTimeMillis() - logId) + "][BoxDao.createBoxes] response: " + gson.toJson(out));
        }
		
		return out;
	}
}
