package com.salem.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class NotesBackendApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void it_is_Works() {

		calculator calculator = new calculator();
		int result ;
		// org.assertj.core.api.Assertions.assertThat(result).isEqualTo(5);
		
		assertThat( calculator.add(2, 3)).isEqualTo(5);
		
	}


	@Test
	void TestThrows() {

		//given
		calculator calculator = new calculator();
		//when
		//then
		assertThatThrownBy(() -> {
			throw new IllegalArgumentException("Invalid argument");
		}).isInstanceOf(IllegalArgumentException.class)
		  .hasMessageContaining("Invalid argument");
	}




}


class calculator {
	 int add(int a, int b) {
		return a + b;
	}
}
