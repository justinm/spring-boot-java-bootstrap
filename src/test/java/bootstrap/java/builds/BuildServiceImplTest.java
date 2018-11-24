package bootstrap.java.builds;

import bootstrap.java.BaseTestCase;
import orchestrator.builds.impl.BuildServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BuildServiceImplTest extends BaseTestCase {
  @Autowired
  BuildServiceImpl buildService;

  @Test
  public void plan() throws Exception {
    Workspace workspace = new Workspace();
    Build build = new Build().setWorkspace( workspace );

    workspace.setPathToModule( "tfmodule" );
    workspace.setRequiresCheckout( false );

    copyResourceDirectoryToWorkspace( workspace, "tfmodule" );

    CompletableFuture<Build> results = buildService.plan( build );

    Build plan = results.get();

    assertThat( "Plan failed: " + plan.getOutput(), plan.getStatus(), equalTo( Build.Status.SUCCESS ) );
  }
}
