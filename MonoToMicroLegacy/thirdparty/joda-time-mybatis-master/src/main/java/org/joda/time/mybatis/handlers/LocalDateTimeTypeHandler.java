//Copyright 2012 Lucas Libraro
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

//package org.joda.time.mybatis.handlers;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.TypeHandler;
//import org.joda.time.DateTimeZone;
//import org.joda.time.LocalDateTime;
//
//public class LocalDateTimeTypeHandler implements TypeHandler
//{
//    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException
//    {
//        ps.setTimestamp(i, new java.sql.Timestamp(((LocalDateTime) parameter).toDateTime(DateTimeZone.UTC).toDate()
//                .getTime()));
//    }
//
//    public Object getResult(ResultSet rs, String columnName) throws SQLException
//    {
//        return new LocalDateTime(rs.getTimestamp(columnName).getTime(), DateTimeZone.UTC);
//    }
//
//    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException
//    {
//        return new LocalDateTime(cs.getTimestamp(columnIndex).getTime(), DateTimeZone.UTC);
//    }
//}
