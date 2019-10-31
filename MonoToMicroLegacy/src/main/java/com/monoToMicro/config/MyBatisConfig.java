/**
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.monoToMicro.config;



import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.monoToMicro.core.repository.mappers.HealthMapper;
import com.monoToMicro.core.repository.mappers.UnicornMapper;
import com.monoToMicro.core.repository.mappers.UserMapper;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

@Configuration
public class MyBatisConfig {

	@Autowired
	private DataSource datasource;

	@Bean
	public SqlSession createSqlSessionService(SqlSessionFactory sqlSessionFactory) {

		return sqlSessionFactory.openSession();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {

		Resource unicornMapperLocations = new ClassPathResource("com/monoToMicro/core/repository/mappers/UnicornMapper.xml");
		Resource userMapperLocations = new ClassPathResource("com/monoToMicro/core/repository/mappers/UserMapper.xml");
		Resource healthMapperLocations = new ClassPathResource("com/monoToMicro/core/repository/mappers/HealthMapper.xml");
		Resource[] r = { unicornMapperLocations, userMapperLocations, healthMapperLocations };

		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(datasource);
		sqlSessionFactory.setMapperLocations(r);

		org.apache.ibatis.session.Configuration configuration = (org.apache.ibatis.session.Configuration) sqlSessionFactory
				.getObject().getConfiguration();
		
		configuration.getTypeHandlerRegistry().register(new DateTimeTypeHandler());
		return sqlSessionFactory.getObject();
	}
		
	@Bean
	public UnicornMapper unicornMapper() throws Exception {
		SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
		UnicornMapper unicornMapper = sessionTemplate.getMapper(UnicornMapper.class);
		return unicornMapper;
	}

	@Bean
	public UserMapper userMapper() throws Exception {
		SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
		sessionTemplate.clearCache();
		UserMapper userMapper = sessionTemplate.getMapper(UserMapper.class);		
		return userMapper;
	}
	
	@Bean
	public HealthMapper healthMapper() throws Exception {
		SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
		HealthMapper healthMapper = sessionTemplate.getMapper(HealthMapper.class);
		return healthMapper;
	}
}
