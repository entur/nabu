/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.domain.event;

/**
 * Sub action types for TimeTable job domain.
 */
public enum TimeTableAction implements EventAction {
    FILE_TRANSFER, FILE_CLASSIFICATION, IMPORT, EXPORT, PREVALIDATION, VALIDATION_LEVEL_1, VALIDATION_LEVEL_2, CLEAN, DATASPACE_TRANSFER, BUILD_GRAPH, OTP2_BUILD_GRAPH, EXPORT_NETEX, EXPORT_NETEX_POSTVALIDATION, EXPORT_NETEX_MERGED_POSTVALIDATION


}
