package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class JdbcEntityManagerTest extends BuilderTest {
  @Test
  @DisplayName("EntityManager를 이용해서 find 합니다.")
  public void findEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 persist 합니다.")
  public void persistEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext);

    jdbcEntityManager.persist(PersonInstances.세번째사람);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 3L);

    assertThat(person).isEqualTo(PersonInstances.세번째사람);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 remove 하고 조회하였을 때, 해당 row가 없습니다.")
  public void removeEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext);

    jdbcEntityManager.remove(PersonInstances.첫번째사람);

    Throwable thrown = catchThrowable(() -> {
      jdbcEntityManager.find(PersonFixtureStep3.class, 1L);
    });
    assertThat(thrown).isInstanceOf(RuntimeException.class);
  }
  @Test
  @DisplayName("find시에 Persistence Context 저장된 entity를 가져온다.")
  public void findEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext);

    jdbcEntityManager.persist(PersonInstances.두번째사람);
    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(person).isEqualTo(PersonInstances.두번째사람);
  }

  @Test
  @DisplayName("persist시에 Persistence Context의 entity와 다르면 더티체킹으로 업데이트한다.")
  public void persistDiffEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);
    person.setName("Sichngpark");

    jdbcEntityManager.persist(person);
    
    PersonFixtureStep3 personInCache = jdbcEntityManager.find(PersonFixtureStep3.class, 2L);

    assertThat(personInCache.getName()).isEqualTo("Sichngpark");
  }
  
}
