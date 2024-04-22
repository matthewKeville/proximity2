package keville.server.runners;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Component;

import keville.model.region.Region;
import keville.model.user.User;
import keville.repository.RegionRepository;
import keville.repository.UserRepository;

@Component
public class SampleDataRunner implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SampleDataRunner.class);

  @Autowired
  private RegionRepository regionRepository;
  @Autowired
  private UserRepository userRepository;

  @Override
  public void run(String... args) {

      boolean skipCreateDevData = true;

      for ( String arg : args ) {

        String[] argParts = arg.split("=");

        if (argParts.length != 2) {
          LOG.warn("invalid argument : " + arg);
          continue;
        }

        try {

          String prop = argParts[0];
          String value = argParts[1];

          if ( prop.equals("--create-dev-data") ) {
            skipCreateDevData = !Boolean.parseBoolean(value);
          }

        } catch (Exception e)  {

          LOG.error("error processing argument " + arg);
          continue;

        }

      }

      if ( skipCreateDevData ) {
        LOG.info("skipping dev data");
        return;
      }

      User dev = new User("dev","dev@email.com","{noop}dev");
      dev = userRepository.save(dev);

      Region belmar = new Region();
      belmar.owner = dev.id;
      belmar.name = "belmar";
      belmar.latitude = 40.1784;
      belmar.longitude = -74.0218;
      belmar.radius = 5.0;
      belmar.lastScan = LocalDateTime.ofInstant(Instant.EPOCH,ZoneOffset.UTC);
      regionRepository.save(belmar);

      Region philly = new Region();
      philly.owner = dev.id;
      philly.name = "philly";
      philly.latitude = 39.9525;
      philly.longitude = -75.1652;
      philly.radius = 5.0;
      philly.lastScan = LocalDateTime.ofInstant(Instant.EPOCH,ZoneOffset.UTC);
      philly.isDisabled = false;
      regionRepository.save(philly);


      Region chicago = new Region();
      chicago.owner = dev.id;
      chicago.name = "chicago";
      chicago.latitude = 41.8781;
      chicago.longitude = -87.6298;
      chicago.radius = 5.0;
      chicago.lastScan = LocalDateTime.ofInstant(Instant.EPOCH,ZoneOffset.UTC);
      chicago.isDisabled = true;
      regionRepository.save(chicago);

      User dev2 = new User("dev2","dev2@email.com","{noop}dev2");
      dev2 = userRepository.save(dev2);

      Region chicago2 = new Region();
      chicago.owner = dev2.id;
      chicago.name = "chicago 2 electric boogalo";
      chicago.latitude = 41.8781;
      chicago.longitude = -87.6298;
      chicago.radius = 3.0;
      chicago.lastScan = LocalDateTime.ofInstant(Instant.EPOCH,ZoneOffset.UTC);
      chicago.isDisabled = true;
      regionRepository.save(chicago2);
      
      System.exit(0);

    }

}
