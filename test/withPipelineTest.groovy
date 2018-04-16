import org.junit.Before
import uk.gov.hmcts.contino.MockBuilder

import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString
import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static org.assertj.core.api.Assertions.assertThat
import static uk.gov.hmcts.contino.ProjectSource.projectSource
import com.lesfurets.jenkins.unit.BasePipelineTest
import uk.gov.hmcts.contino.MockJenkins
import org.junit.Test

class withPipelineTest extends BasePipelineTest {

  // get the 'project' directory
  String projectDir = (new File(this.getClass().getResource("examplePipeline.jenkins").toURI())).parentFile.parentFile.parentFile.parentFile

  @Override
  @Before
  void setUp() {

    super.setUp()
    binding.setVariable("scm", null)
    binding.setVariable("env", [BRANCH_NAME:"master"])
    binding.setVariable( "Jenkins", [instance: new MockJenkins()])
  }

  @Test
  void PipelineBuildsSuccesfully() {

    def library = library()
      .name('Infrastructure')
      .targetPath(projectDir)
      .retriever(projectSource(projectDir))
      .defaultVersion("master")
      .allowOverride(true)
      .implicit(false)
      .build()
    helper.registerSharedLibrary(library)
    helper.registerAllowedMethod("deleteDir", {})
    helper.registerAllowedMethod("stash", [Map.class], {})
    helper.registerAllowedMethod("unstash", [String.class], {})
    helper.registerAllowedMethod("withEnv", [List.class, Closure.class], {})
    helper.registerAllowedMethod("ansiColor", [String, Closure], {})
    helper.registerAllowedMethod("withCredentials", [LinkedHashMap, Closure], {})
    helper.registerAllowedMethod("sh", [Map.class], { return "" })
    helper.registerAllowedMethod("timestamps", [Closure], { println 'Printing timestamp' })
    runScript("testResources/examplePipeline.jenkins")
    printCallStack()
  }

  @Test
  void testBuildIsCalled() {


    def library = library()
      .name('Infrastructure')
      .targetPath(projectDir)
      .retriever(projectSource(projectDir))
      .defaultVersion("master")
      .allowOverride(true)
      .implicit(false)
      .build()
    helper.registerSharedLibrary(library)
    helper.registerAllowedMethod("deleteDir", {})
    helper.registerAllowedMethod("stash", [Map.class], {})
    helper.registerAllowedMethod("unstash", [String.class], {})
    helper.registerAllowedMethod("withEnv", [List.class, Closure.class], {})
    helper.registerAllowedMethod("ansiColor", [String, Closure], {})
    helper.registerAllowedMethod("withCredentials", [LinkedHashMap, Closure.class], {})
    helper.registerAllowedMethod("sh", [Map.class], { return "" })
    helper.registerAllowedMethod( 'fileExists', [String.class], { c -> true })
    helper.registerAllowedMethod("timestamps", [Closure.class], { c -> c.call() })
    helper.registerAllowedMethod("timeout", [Map.class, Closure.class], { c, d -> d.call() })
    runScript("testResources/examplePipeline.jenkins")
    printCallStack()


    assertThat(helper.callStack.any { call ->
      call.methodName == "sectionBuildAndTest.stage(Test, groovy.lang.Closure)"
    }).isTrue()
  }
}
