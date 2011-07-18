import play.Play
import play.templates.TemplateExecutionError
import play.templates.Html
import play.modules.coffee.CoffeePlugin

import org.jcoffeescript.JCoffeeScriptCompileException
import org.jcoffeescript.JCoffeeScriptCompiler

package tags {
  object coffee {
    val className = this.getClass().getName()

    def inline()(body:Html): Html = {
      try {
	Html("<script type=\"text/javascript\">" +
	     CoffeePlugin.getCompiler().compile(body.toString) +
	     "</script>")
      } catch {
	case e: JCoffeeScriptCompileException =>
	  play.Logger.error(e, "Coffee compilation error")
	val stack = e.getStackTrace
	val callingStackTraceElement = stack(stack.indexWhere(ste => ste.getClassName == className) + 1)
	val callingClassName = callingStackTraceElement.getClassName
        throw new TemplateExecutionError(
	  Play.classes.getApplicationClass(callingClassName).javaFile,
	  e.getMessage(),
	  CoffeePlugin.getLineNumber(e) + callingStackTraceElement.getLineNumber - 1);
      }
    }
  }
}
