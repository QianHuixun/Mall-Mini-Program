package cn.tofocus.lejia.util.wx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "xml")
public class PayResult {
	

	@JacksonXmlProperty(localName = "return_code")
	private String returnCode;
	
	@JacksonXmlProperty(localName = "return_msg")
	private String returnMsg;
}
