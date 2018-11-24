package bootstrap.java.builds;

import com.github.dockerjava.api.DockerClient;
import bootstrap.java.BaseTestCase;
import orchestrator.builds.impl.DockerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DockerServiceImplTest extends BaseTestCase {
  @Autowired
  private DockerClient dockerClient;

  @Autowired
  private OrchestratorProperties properties;

  private DockerServiceImpl dockerServiceImpl;

  @Before
  public void setup() {
    dockerServiceImpl = new DockerServiceImpl( dockerClient );
  }

  @Test
  public void run() throws Exception {
    Workspace workspace = new Workspace();
    DockerContainer dockerContainer = dockerServiceImpl.createDockerContainer( workspace, "bash", "latest", true );
    DockerContainer.DockerResult result = dockerContainer.run( Arrays.asList( "echo", "test" ) );

    assertThat( result.getExitCode(), equalTo( 0 ) );
    assertThat( result.getOutput(), equalTo( "test\n" ) );
  }
}
