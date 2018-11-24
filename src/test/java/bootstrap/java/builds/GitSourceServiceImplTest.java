package bootstrap.java.builds;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import com.palantir.docker.compose.execution.DockerComposeExecArgument;
import com.palantir.docker.compose.execution.DockerComposeExecOption;
import bootstrap.java.BaseTestCase;
import orchestrator.builds.impl.GitSourceServiceImpl;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class GitSourceServiceImplTest extends BaseTestCase {
  private String repoName;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  GitSourceServiceImpl gitSourceService;

  private File gitDirectory;

  @ClassRule
  public static DockerComposeRule docker = DockerComposeRule.builder()
      .file( "src/test/resources/ssh/docker-compose.yml" )
      .waitingForService( "git", HealthChecks.toHaveAllPortsOpen() )
      .build();

  @Before
  public void setup() throws Exception {
    String publicKey = getResourceAsString( "ssh/test.key.pub" ).trim();

    repoName = UUID.randomUUID().toString();

    docker.exec( DockerComposeExecOption.noOptions(), "git", DockerComposeExecArgument.arguments( "addkey", publicKey ) );
    docker.exec( DockerComposeExecOption.noOptions(), "git", DockerComposeExecArgument.arguments( "repo", "create", repoName ) );

    final File srcDirectory = new File( getClass().getClassLoader().getResource( "tfmodule" ).getFile() );
    final Path repoPath = Files.createTempDirectory( "git-" );
    gitDirectory = new File( repoPath.toUri() );

    Git git = Git.init()
        .setDirectory( srcDirectory )
        .setGitDir( gitDirectory )
        .call();

    URIish remoteUrl = new URIish( docker.containers().container( "git" ).port( 22 ).inFormat( "ssh://git@$HOST:$EXTERNAL_PORT/opt/git/" + repoName + ".git" ) );

    git.remoteAdd().setName( "origin" ).setUri( remoteUrl ).call();
    git.add().addFilepattern( "." ).call();
    git.commit().setMessage( "Test" ).setAuthor( "test", "test@example.com" ).call();


    git.push().setTransportConfigCallback( new TransportConfigCallback() {
      @Override
      public void configure( Transport transport ) {
        SshTransport sshTransport = (SshTransport) transport;

        sshTransport.setSshSessionFactory( new JschConfigSessionFactory() {
          @Override
          protected void configure( OpenSshConfig.Host hc, Session session ) {
            session.setConfig( "StrictHostKeyChecking", "no" );
          }

          @Override
          protected JSch createDefaultJSch( FS fs ) throws JSchException {
            JSch jSch = super.createDefaultJSch( fs );

            jSch.addIdentity( "127.0.0.1", getResourceAsString( "ssh/test.key" ).getBytes(), getResourceAsString( "ssh/test.key.pub" ).getBytes(), null );

            return jSch;
          }
        } );
      }
    } ).call();
  }

  @After
  public void cleanup() throws Exception {
    FileUtils.deleteDirectory( gitDirectory );
  }

  @Test
  public void checkoutIfNeeded_isNeeded() throws Exception {
    GitCredentials gitCredentials = new GitCredentials()
        .setSshPrivateKey( getResourceAsString( "ssh/test.key" ) )
        .setSshPublicKey( getResourceAsString( "ssh/test.key.pub" ) );
    GitSource source = new GitSource().setUri( getPathToRepo( repoName ) ).setCredentials( gitCredentials );
    Workspace workspace = new Workspace().setRequiresCheckout( true ).setSource( source );
    Build build = new Build().setWorkspace( workspace ).setBranch( "master" );

    assertTrue( gitSourceService.checkoutIfNeeded( build ).get() );
    assertTrue( "main.tf does not exist in workspace", Files.exists( Paths.get( workspace.getPath().toString(), "main.tf" ) ) );
  }

  @Test
  public void checkoutIfNeeded_invalidUrl() throws Throwable {
    GitCredentials gitCredentials = new GitCredentials()
        .setSshPrivateKey( getResourceAsString( "ssh/test.key" ) )
        .setSshPublicKey( getResourceAsString( "ssh/test.key.pub" ) );
    GitSource source = new GitSource().setUri( getPathToRepo( "unknown" ) ).setCredentials( gitCredentials );
    Workspace workspace = new Workspace().setRequiresCheckout( true ).setSource( source );
    Build build = new Build().setWorkspace( workspace ).setBranch( "master" );

    expectedException.expect( GitAPIException.class );
    try {
      gitSourceService.checkoutIfNeeded( build ).get();
    } catch ( ExecutionException e ) {
      throw e.getCause();
    }
  }

  @Test
  public void checkoutIfNeeded_invalidAuth() throws Throwable {
    GitCredentials gitCredentials = new GitCredentials()
        .setSshPrivateKey( getResourceAsString( "ssh/test.key.pub" ) )
        .setSshPublicKey( getResourceAsString( "ssh/test.key.pub" ) );
    GitSource source = new GitSource().setUri( getPathToRepo( repoName ) ).setCredentials( gitCredentials );
    Workspace workspace = new Workspace().setRequiresCheckout( true ).setSource( source );
    Build build = new Build().setWorkspace( workspace ).setBranch( "master" );

    expectedException.expect( GitAPIException.class );
    try {
      gitSourceService.checkoutIfNeeded( build ).get();
    } catch ( ExecutionException e ) {
      throw e.getCause();
    }
  }

  @Test
  public void checkoutIfNeeded_isNotNeeded() throws Exception {
    Workspace workspace = new Workspace().setRequiresCheckout( false );
    Build build = new Build().setWorkspace( workspace );

    gitSourceService.checkoutIfNeeded( build ).get();
  }

  private URIish getPathToRepo( String repoName ) throws Exception {
    return new URIish( docker.containers().container( "git" ).port( 22 ).inFormat( "ssh://git@$HOST:$EXTERNAL_PORT/opt/git/" + repoName + ".git" ) );
  }
}
