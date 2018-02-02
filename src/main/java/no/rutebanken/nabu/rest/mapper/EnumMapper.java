/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *
 */

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
