package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Region;
import java.util.List;

/**
 * Response with regions and pagination information
 *
 * @param regions List of regions
 * @param page Pagination data
 */
public record RegionResponse(List<Region> regions, PageResponse page) {}
