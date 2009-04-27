package org.springframework.jdbc.datasource.embedded;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

public class EmbeddedDatabaseFactoryBeanTests {
	
	@Test
	public void testFactoryBeanLifecycle() throws Exception {
		EmbeddedDatabaseFactoryBean bean = new EmbeddedDatabaseFactoryBean();
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("db-schema.sql", getClass()));
		populator.addScript(new ClassPathResource("db-test-data.sql", getClass()));
		bean.setDatabasePopulator(populator);
		bean.afterPropertiesSet();
		DataSource ds = bean.getObject();
		JdbcTemplate template = new JdbcTemplate(ds);
		assertEquals("Keith", template.queryForObject("select NAME from T_TEST", String.class));
		bean.destroy();
	}
}