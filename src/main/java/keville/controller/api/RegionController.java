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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.netty.handler.codec.http.HttpResponseStatus;

import org.springframework.security.core.Authentication;

import keville.dto.CreateRegionDTO;
import keville.dto.RegionDTO;
import keville.exceptions.AuthorizationException;
import keville.exceptions.ResourceNotFoundException;
import keville.model.region.Region;
import keville.services.region.RegionService;
import keville.util.auth.AuthUtil;

@RestController
@RequestMapping("/api/regions")
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

  @GetMapping("")
  public Collection<RegionDTO> getRegions(
      @Autowired Authentication authentication) {

      // Return the regions applicable to the requester

      Integer principalId = AuthUtil.getPrincipalUserId(authentication);

      try {

        Collection<Region> regions = regionService.getUserRegions(principalId);
        Collection<RegionDTO> regionDTOs = regions.stream().map( x -> mapper.map(x,RegionDTO.class)).collect(Collectors.toSet());

        return regionDTOs;

      } catch (AuthorizationException ex) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }

    }

  @PostMapping("")
  public ResponseEntity<RegionDTO> createRegion(
      @RequestBody CreateRegionDTO createRegionDTO,
      @Autowired Authentication authentication) {

      Integer principalId = AuthUtil.getPrincipalUserId(authentication);

      try {

        //should this decoration be in the service method? (probably...)
        Region region = mapper.map(createRegionDTO,Region.class);
        region.owner = principalId;

        Region newRegion = regionService.createRegion(region);
        RegionDTO newRegionDTO = mapper.map(newRegion,RegionDTO.class);

        return new ResponseEntity<RegionDTO>(newRegionDTO,HttpStatus.CREATED);

      } catch (AuthorizationException ex) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }

  }


  @PutMapping("/{regionId}")
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

  @DeleteMapping("/{regionId}")
  public ResponseEntity deleteRegion(
      @PathVariable("regionId") Integer regionId
    ) {

      try {

        regionService.deleteRegion(regionId);
        return new ResponseEntity(HttpStatus.OK);

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


}
