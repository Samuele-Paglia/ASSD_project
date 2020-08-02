package artemisourceconnector;

import it.unisannio.artemisourceconnector.ArtemisConnectorConfig;
import org.junit.Test;

public class ArtemisConnectorConfigTest {
  @Test
  public void doc() {
    System.out.println(ArtemisConnectorConfig.baseConfigDef().toEnrichedRst());
  }
}