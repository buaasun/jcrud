package net.pdp7.jcrud.petclinic;

import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

import net.pdp7.jcrud.TableController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import schemacrawler.schema.Database;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

@Configuration
@EnableAutoConfiguration
public class Application {

	@Autowired
	public DataSource dataSource;
	
	@Autowired
	public NamedParameterJdbcTemplate jdbcTemplate;
	
	@Bean
	public Schema schema() {
		return database().getSchema("TESTDB.PUBLIC");
	}
	
	@Bean
	@DependsOn("dataSourceAutoConfigurationInitializer")
	public Database database() {
		SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
		schemaCrawlerOptions.setRoutineTypes(Collections.<RoutineType>emptyList());
		try {
			return SchemaCrawlerUtility.getDatabase(dataSource.getConnection(), schemaCrawlerOptions);
		} catch (SchemaCrawlerException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public IndexController indexController() {
		return new IndexController(crudControllers());
	}

	@Bean
	public TableController[] crudControllers() {
		return new TableController[] { vetController(), ownerController(), petTypeController(), visitController() };
	}
	
	@Bean
	public VetController vetController() {
		return new VetController(database().getTable(schema(), "VETS"), jdbcTemplate);
	}

	@Bean
	public OwnerController ownerController() {
		return new OwnerController(database().getTable(schema(), "OWNERS"), jdbcTemplate);
	}

	@Bean
	public PetTypeController petTypeController() {
		return new PetTypeController(database().getTable(schema(), "PET_TYPES"), jdbcTemplate);
	}

	@Bean
	public VisitController visitController() {
		return new VisitController(database().getTable(schema(), "VISITS"), jdbcTemplate);
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
