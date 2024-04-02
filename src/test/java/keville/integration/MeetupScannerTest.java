package keville.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import keville.background.scanner.ScanReport;
import keville.background.scanner.Meetup.MeetupScanner;
import keville.model.region.Region;

// need to fix this to get only the needed slice of the spring context,
// otherwise the whole application starts up.
@SpringBootTest
public class MeetupScannerTest
{

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MeetupScannerTest.class);
    private MeetupScanner scanner;

    public MeetupScannerTest(@Autowired MeetupScanner meetupScanner) {
      this.scanner = meetupScanner;
    }

    @Test
    public void scan() throws Exception {

      Region region = new Region();
      region.latitude = 40.1784;
      region.longitude = -74.0218;
      region.radius = 6;

      ScanReport report = scanner.scan(region);

      LOG.info(report.toString());

    }

}
