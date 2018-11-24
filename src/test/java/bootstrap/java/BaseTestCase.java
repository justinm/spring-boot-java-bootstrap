package bootstrap.java;

import orchestrator.builds.Workspace;
import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-test.properties")
public abstract class BaseTestCase {
  public void copyResourceDirectoryToWorkspace( Workspace workspace, String directory ) throws Exception {
    URL module = getClass().getClassLoader().getResource( directory );

    if ( module == null ) {
      throw new Exception( "Could not location tfmodule for testing." );
    }

    FileUtils.copyDirectory( new File( module.getFile() ), new File( workspace.getPath().toString() + "/" + directory ) );
  }

  public String getResourcePath( String resourcePath ) {
    try {
      return getClass().getClassLoader().getResource( "ssh/test.key" ).getFile();
    } catch ( Exception e ) {
      throw new RuntimeException( "Could not determine absolute path for " + resourcePath );
    }
  }

  public String getResourceAsString( String resourcePath ) {
    URL resourceUrl = getClass().getClassLoader().getResource( resourcePath );

    if ( resourceUrl == null ) {
      throw new RuntimeException( "Could not find resource: " + resourcePath );
    }

    try {
      return FileUtils.readFileToString( new File( resourceUrl.toURI() ), UTF_8 );
    } catch ( URISyntaxException | IOException e ) {
      throw new RuntimeException( e );
    }
  }
}
