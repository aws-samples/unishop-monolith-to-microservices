## Note

* Looking for a new maintainer for this project. I no longer use Joda or Mybatis at my job, so my drive to update this project is low. Contact me if interested.

## Comments

* Your mileage may vary. Working with time zones can yield strange results
* All databases (AFAIK) store dates in UTC (think epoch)
* As such, joda-time-mybatis sets all dates to UTC (for Joda Instants, doesn't apply to Joda Partial classes of courses)
* For consistent results, your JVM should either
    * be run in UTC (-Dtimezone=UTC)
	* be in the same timezone as the database server

## Example

SQL (PostgreSQL in this case)

	CREATE TABLE foo
	(
		localdate date,
		datetime timestamp with time zone
	);

POJO/VO

	public class FooVO
	{
		private LocalDate localdate;
		private DateTime datetime;
		...

myBatis XML

	<resultMap id="fooResultMap" class="FooVO" >
		<result column="localdate" property="localdate" javaType="org.joda.time.LocalDate" typeHandler="org.joda.time.mybatis.handlers.LocalDateTypeHandler" />
		<result column="datetime" property="datetime" javaType="org.joda.time.DateTime" typeHandler="org.joda.time.mybatis.handlers.DateTimeTypeHandler" />
	</resultMap>

	<select id="loadFoo" resultMap="fooResultMap">
		SELECT	*
		FROM	foo
	</select>

	<insert id="insertFoo" parameterType="FooVO">
		INSERT INTO foo(localdate, datetime)
		VALUES(#{localdate,typeHandler=intouch.joda.mybatis.JodaLocalDateTypeHandler}, #{datetime,typeHandler=intouch.joda.mybatis.JodaDateTimeTypeHandler})
	</insert>

Usage

	DateTimeZone zone = DateTimeZone.forID("America/Vancouver");

	FooVO fooVO = new FooVO();
	fooVO.setLocaldate(new LocalDate(2000, 5, 10));
	fooVO.setDatetime(new DateTime(zone));
	logger.debug("LD: " + fooVO.getLocaldate());
	logger.debug("DT: " + fooVO.getDatetime());
	coreDAO.saveFoo(fooVO);

	FooVO loadedFooVO = coreDAO.loadFoo();
	logger.debug("LD: " + loadedFooVO.getLocaldate());
	logger.debug("DT @ Def: " + loadedFooVO.getDatetime());
	logger.debug("DT @ Van: " + loadedFooVO.getDatetime().withZone(zone));

Output (JVM running in UTC)

	LD: 2000-05-10
	DT: 2012-03-27T07:29:26.748-07:00
	LD: 2000-05-10
	DT @ Def: 2012-03-27T14:29:26.748Z
	DT @ Van: 2012-03-27T07:29:26.748-07:00
