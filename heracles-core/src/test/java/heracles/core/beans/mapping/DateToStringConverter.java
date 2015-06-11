package heracles.core.beans.mapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateToStringConverter extends BidirectionalConverter<Date, String> {

	private static Logger logger = LoggerFactory.getLogger(DateToStringConverter.class);

	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public String convertTo(Date source, Type<String> destinationType) {
		return format.format(source);
	}

	@Override
	public Date convertFrom(String source, Type<Date> destinationType) {
		try {
			return format.parse(source);
		} catch (ParseException e) {
			logger.error("conver String to Date error", e);
			return null;
		}
	}

}
