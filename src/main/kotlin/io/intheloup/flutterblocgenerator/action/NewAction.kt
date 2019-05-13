package io.intheloup.flutterblocgenerator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import io.intheloup.flutterblocgenerator.builder.TemplateBuilder
import io.intheloup.flutterblocgenerator.model.Bloc
import io.intheloup.flutterblocgenerator.util.FlutterUtils

class NewAction : AnAction() {

  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: throw IllegalStateException("Cannot find project")
    val projectName = FlutterUtils.readProjectName(project)
        ?: throw IllegalStateException("Cannot find flutter project name")

    val name = Messages.showInputDialog(
        "Bloc Name",
        "New Flutter Bloc",
        null,
        null,
        SimpleClassNameInputValidator(project)
    )

    if (name?.isBlank() != false) {
      return
    }

    val bloc = Bloc.build(name, projectName)

    val directory = event.getData(LangDataKeys.PSI_ELEMENT) as PsiDirectory
    if (directory.findSubdirectory(bloc.name) != null) {
      Messages.showErrorDialog("File already exists", "Flutter Bloc")
      return
    }

    WriteCommandAction.runWriteCommandAction(event.project) {
      val blocDirectory = directory.createSubdirectory(bloc.name)
      TemplateBuilder.build(bloc, project, blocDirectory)
    }
  }

  class SimpleClassNameInputValidator(private val project: Project) : InputValidatorEx {
    override fun checkInput(input: String): Boolean {
      return getErrorText(input) == null
    }

    override fun canClose(p0: String?): Boolean {
      return true
    }

    override fun getErrorText(input: String): String? {
      if (input.contains(".") || input.contains(" ") || input.toLowerCase() != input) {
        return "Must only contain lowercase characters and underscores"
      }

      return null
    }
  }
}