package bootstrap.java.builds;

import com.github.dockerjava.api.DockerClient;
import bootstrap.java.BaseTestCase;
import orchestrator.builds.impl.DockerServiceImpl;
import orchestrator.builds.impl.TerraformServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TerraformServiceImplTest extends BaseTestCase {
  @Autowired
  DockerClient dockerClient;

  @Autowired
  OrchestratorProperties properties;

  @Test
  public void plan() throws Exception {
    TerraformServiceImpl service = new TerraformServiceImpl( new DockerServiceImpl( dockerClient ), properties );

    URL url = getClass().getClassLoader().getResource( "application-test.properties" );

    if ( url == null ) {
      throw new Exception( "Could not locate tfmodule resource" );
    }

    Path path = Paths.get( url.toURI() );
    Workspace workspace = new Workspace();
    Build build = new Build().setWorkspace( workspace );

    workspace.setPathToModule( "tfmodule" );

    copyResourceDirectoryToWorkspace( workspace, "tfmodule" );

    CompletableFuture<TerraformServiceImpl.PlanResults> resultsFuture = service.plan( build );

    TerraformServiceImpl.PlanResults results = resultsFuture.get();

    assertThat( "Plan failed " + results.getOutput(), results.getExitCode(), equalTo( 0 ) );
  }
}
