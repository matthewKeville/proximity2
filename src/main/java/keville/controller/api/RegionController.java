package keville.controller.api;

import java.util.Collection;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import keville.dto.RegionDTO;
import keville.exceptions.AuthorizationException;
import keville.exceptions.ResourceNotFoundException;
import keville.model.region.Region;
import keville.services.region.RegionService;
import keville.util.auth.AuthUtil;

@RestController
@RequestMapping("/api/region")
public class RegionController {

  private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

  private RegionService regionService;
  private ModelMapper mapper;

  public RegionController(
      @Autowired RegionService regionService,
      @Autowired ModelMapper modelMapper
    ) {
    this.regionService = regionService;
    this.mapper = modelMapper;
  }

  @PostMapping("/create")
  public ResponseEntity<RegionDTO> createRegion(
      @RequestBody RegionDTO regionDTO
  ) {

      Region region = mapper.map(regionDTO,Region.class);
      Region newRegion = regionService.createRegion(region);
      RegionDTO newRegionDTO = mapper.map(newRegion,RegionDTO.class);

      return new ResponseEntity<RegionDTO>(newRegionDTO,HttpStatus.CREATED);
  }

  @PostMapping("/{regionId}/update")
  public RegionDTO updateRegion(
      @PathVariable("regionId") Integer regionId,
      @RequestBody RegionDTO regionUpdateDTO
    ) {

      try {

        Region regionUpdate = mapper.map(regionUpdateDTO,Region.class);
        Region region = regionService.updateRegion(regionUpdate);
        RegionDTO regionDTO = mapper.map(region,RegionDTO.class);

        return regionDTO;

      } catch (ResourceNotFoundException | AuthorizationException ex) {
        if ( ex instanceof ResourceNotFoundException ) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,ex.getMessage());
        } else if ( ex instanceof AuthorizationException ) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
  }

  @GetMapping("/user/{userId}")
  public Collection<RegionDTO> getUserRegions(
      @PathVariable("userId") Integer userId,
      @Autowired Authentication authentication) {

      //Only the matching userId should access this route
      Integer principalId = AuthUtil.getPrincipalUserId(authentication);
      if ( userId != principalId ) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }

      try {

        Collection<Region> regions = regionService.getUserRegions(userId);
        Collection<RegionDTO> regionDTOs = regions.stream().map( x -> mapper.map(x,RegionDTO.class)).collect(Collectors.toSet());

        return regionDTOs;

      } catch (AuthorizationException ex) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }

    }

}
