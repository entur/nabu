package no.rutebanken.nabu.rest.mapper;


import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class EnumMapper {

	public static <T extends Enum<T>, O extends Enum<O>> List<T> convertEnums(List<O> org, Class<T> toEnum) {
		List<T> converted = new ArrayList<>();
		if (!CollectionUtils.isEmpty(org)) {
			org.forEach(orgVal -> converted.add(T.valueOf(toEnum, orgVal.name())));
		}
		return converted;

	}

	public static <O extends Enum<O>> List<String> toString(List<O> org) {
		List<String> converted = new ArrayList<>();
		if (!CollectionUtils.isEmpty(org)) {
			org.forEach(orgVal -> converted.add(orgVal.name()));
		}
		return converted;

	}

}
