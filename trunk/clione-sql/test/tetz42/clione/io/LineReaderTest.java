package tetz42.clione.io;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class LineReaderTest {

	@Test
	public void join_by_blank() throws IOException {
		LineReader reader = new LineReader(new StringReader("tako -- \r\nika"));
		assertThat(reader.readLine(), is("tako -- \r\nika"));
	}

	@Test
	public void join_by_tab() throws IOException {
		LineReader reader = new LineReader(new StringReader(
				"tako --  \t \r\nika"));
		assertThat(reader.readLine(), is("tako --  \t \r\nika"));
	}

	@Test
	public void no_join_by_normal_comment() throws IOException {
		LineReader reader = new LineReader(new StringReader(
				"tako --  \t aaa\r\nika"));
		assertThat(reader.readLine(), is("tako --  \t aaa"));
	}

}
