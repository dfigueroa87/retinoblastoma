package ar.com.retinoblastoma;



import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RestTests {
	
	@Test
	public void testFileUpload() {

	    String url = "http://localhost:8080/retinoblastoma/file";

	    Resource resource = new ClassPathResource("images/file.xxx");

	    MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
	    mvm.add("file", resource);
	}

	   
}
