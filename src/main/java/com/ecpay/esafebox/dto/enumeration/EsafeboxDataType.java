package com.ecpay.esafebox.dto.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

import static com.ecpay.entities.common.EcConstants.MAPPER;

import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.TimeUtils;
import com.fasterxml.jackson.core.type.TypeReference;

@Getter
@AllArgsConstructor
public enum EsafeboxDataType {

    STRING {
		@Override
		public boolean isValid(Object content) {
			return true;
		}
	},
    INTEGER {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			try {
				Integer.parseInt(content.toString());
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
	},
	LONG {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			try {
				Long.parseLong(content.toString());
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
	},
	DATE {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			return TimeUtils.convertString2LocalDate(content.toString()) == null ? false : true;
		}
	},
	DATE_TIME {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			return TimeUtils.convertString2LocalDateTime(content.toString()) == null ? false : true;
		}
	},
	TIMESTAMP {
		@Override
		public boolean isValid(Object content) {
			return true;
		}
	},
	JSON {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			try {
				MAPPER.readTree(content.toString());
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	},
	OBJECT {
		@Override
		public boolean isValid(Object content) {
			return true;
		}
	},
	ARRAY {
		@Override
		public boolean isValid(Object content) {
			return true;
		}
	},
	ARRAY_OF_LONG {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			try {
				Constants.MAPPER.readValue(content.toString(), new TypeReference<List<Long>>() {});
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	},
	ARRAY_OF_STRING {
		@Override
		public boolean isValid(Object content) {
			if (content == null) {
				return true;
			}

			if (content instanceof List) {
				return true;
			}

			return false;
		}
	};
	public abstract boolean isValid(Object content);

}
