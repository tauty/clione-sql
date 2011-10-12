package tetz42.old.clione.parsar;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tetz42.old.clione.parsar.ParsarUtil.*;

import org.junit.Test;


public class ParsarUtilTest {

	@Test
	public void indexSize(){
		int size = calcIndent("	 	  	   	    	");
		assertThat(size, is(24));
	}
}
