package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Region;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Response with Regions and pagination information
 *
 * @param regions List of regions
 * @param paginatedRegions List of paginated regions
 */
public record RegionResponse(List<Region> regions, Page<Region> paginatedRegions) {}
